package com.example.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.KrishiPathDatabase
import com.example.data.local.PreferencesManager
import com.example.data.repository.KrishiPathRepository
import com.example.domain.model.AppLanguage
import com.example.domain.model.MockTestAttempt
import com.example.domain.model.MockTestBlueprint
import com.example.domain.model.PracticeQuestion
import com.example.domain.model.StudentProfile
import com.example.domain.model.StudentProgressSummary
import com.example.domain.model.StudyNote
import com.example.domain.model.SubjectCategory
import com.example.domain.model.SubjectItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MockTestSessionState(
    val blueprint: MockTestBlueprint? = null,
    val questions: List<PracticeQuestion> = emptyList(),
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val markedForReview: Set<String> = emptySet(),
    val currentIndex: Int = 0,
    val remainingSeconds: Int = 0,
    val isPaused: Boolean = false,
    val isSubmitted: Boolean = false,
    val latestResult: MockTestAttempt? = null
)

data class SearchResults(
    val query: String = "",
    val notes: List<StudyNote> = emptyList(),
    val questions: List<PracticeQuestion> = emptyList()
)

class KrishiPathViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KrishiPathRepository = KrishiPathRepository(
        database = KrishiPathDatabase.getInstance(application),
        preferences = PreferencesManager(application)
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    val profile: StateFlow<StudentProfile> = repository.profileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StudentProfile(
            name = "कृषि विद्यार्थी",
            targetExam = "ICAR AIEEA / CUET-UG",
            language = AppLanguage.HINDI,
            dailyGoalMinutes = 45
        )
    )

    val notes: StateFlow<List<StudyNote>> = repository.notesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val questions: StateFlow<List<PracticeQuestion>> = repository.questionsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val agricultureSubjects: StateFlow<List<SubjectItem>> = repository.agricultureSubjectsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val mockBlueprints: List<MockTestBlueprint> = repository.mockBlueprints

    val mockAttempts: StateFlow<List<MockTestAttempt>> = repository.mockAttemptsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val progressSummary: StateFlow<StudentProgressSummary> = repository.progressSummaryFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StudentProgressSummary(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<SearchResults> = combine(
        _searchQuery,
        notes,
        questions
    ) { query, allNotes, allQuestions ->
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            SearchResults(query = query, notes = allNotes, questions = allQuestions)
        } else {
            val matchedNotes = allNotes.filter { note ->
                note.title.hi.contains(trimmed, ignoreCase = true) ||
                    note.title.en.contains(trimmed, ignoreCase = true) ||
                    note.summary.hi.contains(trimmed, ignoreCase = true) ||
                    note.summary.en.contains(trimmed, ignoreCase = true) ||
                    note.content.hi.contains(trimmed, ignoreCase = true) ||
                    note.content.en.contains(trimmed, ignoreCase = true)
            }
            val matchedQuestions = allQuestions.filter { q ->
                q.questionText.hi.contains(trimmed, ignoreCase = true) ||
                    q.questionText.en.contains(trimmed, ignoreCase = true) ||
                    q.explanation.hi.contains(trimmed, ignoreCase = true) ||
                    q.explanation.en.contains(trimmed, ignoreCase = true) ||
                    q.examName.contains(trimmed, ignoreCase = true)
            }
            SearchResults(query = query, notes = matchedNotes, questions = matchedQuestions)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchResults()
    )

    private val _mockSession = MutableStateFlow(MockTestSessionState())
    val mockSession: StateFlow<MockTestSessionState> = _mockSession.asStateFlow()

    private var timerJob: Job? = null

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleLanguage() {
        val next = if (profile.value.language == AppLanguage.HINDI) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.HINDI
        }
        viewModelScope.launch { repository.setLanguage(next) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { repository.setLanguage(language) }
    }

    fun updateProfile(name: String, targetExam: String, dailyGoalMinutes: Int) {
        viewModelScope.launch {
            repository.updateProfile(name, targetExam, dailyGoalMinutes)
        }
    }

    fun toggleNoteBookmark(note: StudyNote) {
        viewModelScope.launch {
            repository.toggleNoteBookmark(note.id, note.isBookmarked)
        }
    }

    fun toggleNoteOffline(note: StudyNote) {
        viewModelScope.launch {
            repository.toggleNoteOffline(note.id, note.isSavedOffline)
        }
    }

    fun toggleNoteCompleted(note: StudyNote) {
        viewModelScope.launch {
            repository.toggleNoteCompleted(note.id, note.isCompleted)
        }
    }

    fun toggleQuestionBookmark(question: PracticeQuestion) {
        viewModelScope.launch {
            repository.toggleQuestionBookmark(question.id, question.isBookmarked)
        }
    }

    fun toggleQuestionOffline(question: PracticeQuestion) {
        viewModelScope.launch {
            repository.toggleQuestionOffline(question.id, question.isSavedOffline)
        }
    }

    fun recordMcqAnswer(question: PracticeQuestion, selectedOptionIndex: Int) {
        val isCorrect = selectedOptionIndex == question.correctIndex
        viewModelScope.launch {
            repository.recordQuestionAttempt(question.id, isCorrect)
        }
    }

    fun startMockTest(blueprint: MockTestBlueprint) {
        val all = questions.value
        val pool = when (blueprint.categoryFilter) {
            null -> all
            SubjectCategory.BIOLOGY -> all.filter {
                it.category == SubjectCategory.BIOLOGY || it.category == SubjectCategory.CHEMISTRY
            }
            else -> all.filter { it.category == blueprint.categoryFilter }
        }.ifEmpty { all }

        val selected = pool.shuffled().take(blueprint.questionCount.coerceAtMost(pool.size))
        val totalSecs = blueprint.durationMinutes * 60

        timerJob?.cancel()
        _mockSession.value = MockTestSessionState(
            blueprint = blueprint,
            questions = selected,
            selectedAnswers = emptyMap(),
            markedForReview = emptySet(),
            currentIndex = 0,
            remainingSeconds = totalSecs,
            isPaused = false,
            isSubmitted = false,
            latestResult = null
        )
        startTimerLoop()
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _mockSession.value
                if (current.blueprint == null || current.isSubmitted) break
                if (!current.isPaused) {
                    if (current.remainingSeconds <= 1) {
                        submitMockTest()
                        break
                    } else {
                        _mockSession.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
                    }
                }
            }
        }
    }

    fun togglePauseMockTest() {
        _mockSession.update { it.copy(isPaused = !it.isPaused) }
    }

    fun selectMockAnswer(questionId: String, optionIndex: Int) {
        _mockSession.update { session ->
            if (session.isSubmitted) session
            else session.copy(selectedAnswers = session.selectedAnswers + (questionId to optionIndex))
        }
    }

    fun clearMockAnswer(questionId: String) {
        _mockSession.update { session ->
            if (session.isSubmitted) session
            else session.copy(selectedAnswers = session.selectedAnswers - questionId)
        }
    }

    fun toggleMarkForReview(questionId: String) {
        _mockSession.update { session ->
            val updated = if (questionId in session.markedForReview) {
                session.markedForReview - questionId
            } else {
                session.markedForReview + questionId
            }
            session.copy(markedForReview = updated)
        }
    }

    fun goToMockQuestion(index: Int) {
        _mockSession.update { session ->
            val safe = index.coerceIn(0, (session.questions.size - 1).coerceAtLeast(0))
            session.copy(currentIndex = safe)
        }
    }

    fun submitMockTest() {
        timerJob?.cancel()
        val session = _mockSession.value
        val blueprint = session.blueprint ?: return
        if (session.isSubmitted) return

        var correct = 0
        var wrong = 0
        var skipped = 0
        session.questions.forEach { q ->
            val answer = session.selectedAnswers[q.id]
            when {
                answer == null -> skipped++
                answer == q.correctIndex -> correct++
                else -> wrong++
            }
        }

        val score = (correct * blueprint.positiveMarks) - (wrong * blueprint.negativeMarks)
        val maxScore = session.questions.size * blueprint.positiveMarks
        val totalAllocated = blueprint.durationMinutes * 60
        val taken = (totalAllocated - session.remainingSeconds).coerceAtLeast(1)

        val result = MockTestAttempt(
            id = 0L,
            testId = blueprint.id,
            testTitleHi = blueprint.title.hi,
            testTitleEn = blueprint.title.en,
            totalQuestions = session.questions.size,
            correctCount = correct,
            wrongCount = wrong,
            skippedCount = skipped,
            score = score,
            maxScore = maxScore,
            timeTakenSeconds = taken,
            completedAtMillis = System.currentTimeMillis()
        )

        _mockSession.update { it.copy(isSubmitted = true, isPaused = false, latestResult = result) }

        viewModelScope.launch {
            repository.saveMockAttempt(
                blueprint = blueprint,
                correctCount = correct,
                wrongCount = wrong,
                skippedCount = skipped,
                score = score,
                maxScore = maxScore,
                timeTakenSeconds = taken
            )
            session.questions.forEach { q ->
                session.selectedAnswers[q.id]?.let { ans ->
                    repository.recordQuestionAttempt(q.id, ans == q.correctIndex)
                }
            }
        }
    }

    fun exitMockSession() {
        timerJob?.cancel()
        _mockSession.value = MockTestSessionState()
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
