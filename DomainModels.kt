package com.example.domain.model

enum class AppLanguage(val code: String, val displayName: String) {
    HINDI("hi", "हिन्दी"),
    ENGLISH("en", "English")
}

enum class SubjectCategory(
    val id: String,
    val titleHi: String,
    val titleEn: String
) {
    AGRICULTURE("agriculture", "कृषि विज्ञान", "Agriculture"),
    BIOLOGY("biology", "जीव विज्ञान", "Biology"),
    CHEMISTRY("chemistry", "रसायन विज्ञान", "Chemistry"),
    CUET_ICAR("cuet_icar", "CUET एवं ICAR", "CUET & ICAR")
}

enum class ContentOrigin(val isVerified: Boolean) {
    VERIFIED(true),
    SAMPLE(false)
}

data class BilingualText(
    val hi: String,
    val en: String
) {
    fun get(language: AppLanguage): String = when (language) {
        AppLanguage.HINDI -> hi
        AppLanguage.ENGLISH -> en
    }
}

data class SubjectItem(
    val id: String,
    val category: SubjectCategory,
    val title: BilingualText,
    val subtitle: BilingualText,
    val iconKey: String,
    val totalChapters: Int,
    val completedChapters: Int
)

data class StudyNote(
    val id: String,
    val subjectId: String,
    val category: SubjectCategory,
    val chapterNumber: Int,
    val title: BilingualText,
    val summary: BilingualText,
    val content: BilingualText,
    val keyFormulas: BilingualText,
    val examTip: BilingualText,
    val readMinutes: Int,
    val isSampleContent: Boolean,
    val isBookmarked: Boolean,
    val isSavedOffline: Boolean,
    val isCompleted: Boolean
)

data class PracticeQuestion(
    val id: String,
    val subjectId: String,
    val category: SubjectCategory,
    val questionText: BilingualText,
    val optionsHi: List<String>,
    val optionsEn: List<String>,
    val correctIndex: Int,
    val explanation: BilingualText,
    val isPyq: Boolean,
    val examName: String,
    val examYear: String,
    val isVerified: Boolean,
    val isBookmarked: Boolean,
    val isSavedOffline: Boolean,
    val lastAttemptCorrect: Boolean?
) {
    fun options(language: AppLanguage): List<String> = when (language) {
        AppLanguage.HINDI -> optionsHi
        AppLanguage.ENGLISH -> optionsEn
    }
}

data class MockTestBlueprint(
    val id: String,
    val title: BilingualText,
    val description: BilingualText,
    val categoryFilter: SubjectCategory?,
    val durationMinutes: Int,
    val questionCount: Int,
    val positiveMarks: Int,
    val negativeMarks: Int,
    val isSample: Boolean = true
)

data class MockTestAttempt(
    val id: Long,
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
    val completedAtMillis: Long
)

data class StudentProgressSummary(
    val totalNotes: Int,
    val completedNotes: Int,
    val totalQuestions: Int,
    val attemptedQuestions: Int,
    val correctQuestions: Int,
    val bookmarkedNotesCount: Int,
    val bookmarkedQuestionsCount: Int,
    val offlineSavedCount: Int,
    val mockTestsTaken: Int,
    val bestMockAccuracyPercent: Int
)

data class StudentProfile(
    val name: String,
    val targetExam: String,
    val language: AppLanguage,
    val dailyGoalMinutes: Int
)
