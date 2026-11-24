package com.example.data.repository

import com.example.data.local.KrishiPathDatabase
import com.example.data.local.MockTestAttemptEntity
import com.example.data.local.PreferencesManager
import com.example.data.local.SeedNotes
import com.example.data.local.SeedQuestions
import com.example.domain.model.AppLanguage
import com.example.domain.model.BilingualText
import com.example.domain.model.MockTestAttempt
import com.example.domain.model.MockTestBlueprint
import com.example.domain.model.PracticeQuestion
import com.example.domain.model.StudentProfile
import com.example.domain.model.StudentProgressSummary
import com.example.domain.model.StudyNote
import com.example.domain.model.SubjectCategory
import com.example.domain.model.SubjectItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class KrishiPathRepository(
    private val database: KrishiPathDatabase,
    private val preferences: PreferencesManager
) {
    private val noteDao = database.studyNoteDao()
    private val questionDao = database.questionDao()
    private val mockAttemptDao = database.mockTestAttemptDao()

    suspend fun ensureSeeded() {
        if (noteDao.getNoteCount() == 0) {
            noteDao.insertAllIgnore(SeedNotes.notes)
        }
        if (questionDao.getQuestionCount() == 0) {
            questionDao.insertAllIgnore(SeedQuestions.questions)
        }
    }

    val profileFlow: Flow<StudentProfile> = preferences.profileFlow

    val notesFlow: Flow<List<StudyNote>> = noteDao.observeAllNotes().map { list ->
        list.map { it.toDomain() }
    }

    val questionsFlow: Flow<List<PracticeQuestion>> = questionDao.observeAllQuestions().map { list ->
        list.map { it.toDomain() }
    }

    val mockAttemptsFlow: Flow<List<MockTestAttempt>> = mockAttemptDao.observeAllAttempts().map { list ->
        list.map { it.toDomain() }
    }

    val mockBlueprints: List<MockTestBlueprint> = SeedQuestions.mockTests

    val agricultureSubjectsFlow: Flow<List<SubjectItem>> = notesFlow.map { notes ->
        val specs = listOf(
            Triple("agronomy", BilingualText("शस्य विज्ञान", "Agronomy"), BilingualText("फसलें, ऋतुएँ व फसल चक्र", "Crops, Seasons & Cropping")),
            Triple("soil_science", BilingualText("मृदा विज्ञान", "Soil Science"), BilingualText("मृदा प्रकार, pH व सुधार", "Soil Types, pH & Reclamation")),
            Triple("horticulture", BilingualText("उद्यान विज्ञान", "Horticulture"), BilingualText("फल, सब्जियाँ व प्रवर्धन", "Fruits, Vegetables & Grafting")),
            Triple("plant_protection", BilingualText("पादप रोग एवं कीट विज्ञान", "Plant Pathology & Entomology"), BilingualText("रोग, कीट व IPM नियंत्रण", "Diseases, Pests & IPM")),
            Triple("agri_economics", BilingualText("कृषि अर्थशास्त्र एवं प्रसार", "Agri Economics & Extension"), BilingualText("MSP, नाबार्ड व प्रसार विधियाँ", "MSP, NABARD & Extension")),
            Triple("animal_husbandry", BilingualText("पशुपालन एवं डेयरी विज्ञान", "Animal Husbandry & Dairy"), BilingualText("नस्लें, पोषण व दुग्ध विज्ञान", "Breeds, Ration & Dairy"))
        )
        specs.map { (id, title, sub) ->
            val matching = notes.filter { it.subjectId == id }
            SubjectItem(
                id = id,
                category = SubjectCategory.AGRICULTURE,
                title = title,
                subtitle = sub,
                iconKey = id,
                totalChapters = matching.size.coerceAtLeast(1),
                completedChapters = matching.count { it.isCompleted }
            )
        }
    }

    val progressSummaryFlow: Flow<StudentProgressSummary> = combine(
        notesFlow,
        questionsFlow,
        mockAttemptsFlow
    ) { notes, questions, attempts ->
        val attempted = questions.filter { it.lastAttemptCorrect != null }
        val correct = attempted.count { it.lastAttemptCorrect == true }
        val bestMock = attempts.maxOfOrNull {
            if (it.totalQuestions > 0) (it.correctCount * 100) / it.totalQuestions else 0
        } ?: 0

        StudentProgressSummary(
            totalNotes = notes.size,
            completedNotes = notes.count { it.isCompleted },
            totalQuestions = questions.size,
            attemptedQuestions = attempted.size,
            correctQuestions = correct,
            bookmarkedNotesCount = notes.count { it.isBookmarked },
            bookmarkedQuestionsCount = questions.count { it.isBookmarked },
            offlineSavedCount = notes.count { it.isSavedOffline } + questions.count { it.isSavedOffline },
            mockTestsTaken = attempts.size,
            bestMockAccuracyPercent = bestMock
        )
    }

    suspend fun setLanguage(language: AppLanguage) = preferences.setLanguage(language)

    suspend fun updateProfile(name: String, targetExam: String, dailyGoalMinutes: Int) =
        preferences.updateProfile(name, targetExam, dailyGoalMinutes)

    suspend fun toggleNoteBookmark(noteId: String, currentlyBookmarked: Boolean) =
        noteDao.updateBookmark(noteId, !currentlyBookmarked)

    suspend fun toggleNoteOffline(noteId: String, currentlySaved: Boolean) =
        noteDao.updateOfflineSaved(noteId, !currentlySaved)

    suspend fun toggleNoteCompleted(noteId: String, currentlyCompleted: Boolean) =
        noteDao.updateCompleted(noteId, !currentlyCompleted)

    suspend fun toggleQuestionBookmark(questionId: String, currentlyBookmarked: Boolean) =
        questionDao.updateBookmark(questionId, !currentlyBookmarked)

    suspend fun toggleQuestionOffline(questionId: String, currentlySaved: Boolean) =
        questionDao.updateOfflineSaved(questionId, !currentlySaved)

    suspend fun recordQuestionAttempt(questionId: String, isCorrect: Boolean) =
        questionDao.updateAttemptResult(questionId, isCorrect)

    suspend fun saveMockAttempt(
        blueprint: MockTestBlueprint,
        correctCount: Int,
        wrongCount: Int,
        skippedCount: Int,
        score: Int,
        maxScore: Int,
        timeTakenSeconds: Int
    ) {
        mockAttemptDao.insertAttempt(
            MockTestAttemptEntity(
                testId = blueprint.id,
                testTitleHi = blueprint.title.hi,
                testTitleEn = blueprint.title.en,
                totalQuestions = correctCount + wrongCount + skippedCount,
                correctCount = correctCount,
                wrongCount = wrongCount,
                skippedCount = skippedCount,
                score = score,
                maxScore = maxScore,
                timeTakenSeconds = timeTakenSeconds
            )
        )
    }

    suspend fun resetAllProgress() {
        noteDao.resetNoteProgress()
        questionDao.resetQuestionProgress()
        mockAttemptDao.clearAllAttempts()
    }
}
