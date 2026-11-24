package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.BilingualText
import com.example.domain.model.MockTestAttempt
import com.example.domain.model.PracticeQuestion
import com.example.domain.model.StudyNote
import com.example.domain.model.SubjectCategory

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val categoryId: String,
    val chapterNumber: Int,
    val titleHi: String,
    val titleEn: String,
    val summaryHi: String,
    val summaryEn: String,
    val contentHi: String,
    val contentEn: String,
    val keyFormulasHi: String,
    val keyFormulasEn: String,
    val examTipHi: String,
    val examTipEn: String,
    val readMinutes: Int,
    val isSampleContent: Boolean = true,
    val isBookmarked: Boolean = false,
    val isSavedOffline: Boolean = true,
    val isCompleted: Boolean = false
) {
    fun toDomain(): StudyNote = StudyNote(
        id = id,
        subjectId = subjectId,
        category = SubjectCategory.entries.firstOrNull { it.id == categoryId } ?: SubjectCategory.AGRICULTURE,
        chapterNumber = chapterNumber,
        title = BilingualText(titleHi, titleEn),
        summary = BilingualText(summaryHi, summaryEn),
        content = BilingualText(contentHi, contentEn),
        keyFormulas = BilingualText(keyFormulasHi, keyFormulasEn),
        examTip = BilingualText(examTipHi, examTipEn),
        readMinutes = readMinutes,
        isSampleContent = isSampleContent,
        isBookmarked = isBookmarked,
        isSavedOffline = isSavedOffline,
        isCompleted = isCompleted
    )
}

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val categoryId: String,
    val questionHi: String,
    val questionEn: String,
    val optionAHi: String,
    val optionBHi: String,
    val optionCHi: String,
    val optionDHi: String,
    val optionAEn: String,
    val optionBEn: String,
    val optionCEn: String,
    val optionDEn: String,
    val correctIndex: Int,
    val explanationHi: String,
    val explanationEn: String,
    val isPyq: Boolean,
    val examName: String,
    val examYear: String,
    val isVerified: Boolean,
    val isBookmarked: Boolean = false,
    val isSavedOffline: Boolean = true,
    val lastAttemptCorrect: Boolean? = null
) {
    fun toDomain(): PracticeQuestion = PracticeQuestion(
        id = id,
        subjectId = subjectId,
        category = SubjectCategory.entries.firstOrNull { it.id == categoryId } ?: SubjectCategory.AGRICULTURE,
        questionText = BilingualText(questionHi, questionEn),
        optionsHi = listOf(optionAHi, optionBHi, optionCHi, optionDHi),
        optionsEn = listOf(optionAEn, optionBEn, optionCEn, optionDEn),
        correctIndex = correctIndex,
        explanation = BilingualText(explanationHi, explanationEn),
        isPyq = isPyq,
        examName = examName,
        examYear = examYear,
        isVerified = isVerified,
        isBookmarked = isBookmarked,
        isSavedOffline = isSavedOffline,
        lastAttemptCorrect = lastAttemptCorrect
    )
}

@Entity(tableName = "mock_test_attempts")
data class MockTestAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testId: String,
    val testTitleHi: String,
    val testTitleEn: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val score: Int,
    val maxScore: Int,
    val timeTakenSeconds: Int,
    val completedAtMillis: Long = System.currentTimeMillis()
) {
    fun toDomain(): MockTestAttempt = MockTestAttempt(
        id = id,
        testId = testId,
        testTitleHi = testTitleHi,
        testTitleEn = testTitleEn,
        totalQuestions = totalQuestions,
        correctCount = correctCount,
        wrongCount = wrongCount,
        skippedCount = skippedCount,
        score = score,
        maxScore = maxScore,
        timeTakenSeconds = timeTakenSeconds,
        completedAtMillis = completedAtMillis
    )
}
