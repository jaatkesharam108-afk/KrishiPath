package com.example.ui.navigation

object Routes {
    const val HOME = "home"
    const val AGRICULTURE_SUBJECTS = "agriculture_subjects"
    const val CUET_ICAR = "cuet_icar"
    const val BIO_CHEM = "bio_chem"
    const val STUDY_NOTES = "study_notes"
    const val STUDY_NOTES_WITH_FILTER = "study_notes?category={category}&subjectId={subjectId}"
    const val NOTE_DETAIL = "note_detail/{noteId}"
    const val MCQ_PRACTICE = "mcq_practice"
    const val MCQ_PRACTICE_WITH_FILTER = "mcq_practice?category={category}&subjectId={subjectId}"
    const val MOCK_TESTS = "mock_tests"
    const val PYQ = "pyq"
    const val BOOKMARKS = "bookmarks"
    const val PROFILE = "profile"
    const val SEARCH = "search"

    fun studyNotes(category: String = "all", subjectId: String = "all"): String =
        "study_notes?category=$category&subjectId=$subjectId"

    fun noteDetail(noteId: String): String = "note_detail/$noteId"

    fun mcqPractice(category: String = "all", subjectId: String = "all"): String =
        "mcq_practice?category=$category&subjectId=$subjectId"
}
