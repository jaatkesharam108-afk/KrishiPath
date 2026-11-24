package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM study_notes ORDER BY categoryId, chapterNumber ASC")
    fun observeAllNotes(): Flow<List<StudyNoteEntity>>

    @Query("SELECT COUNT(*) FROM study_notes")
    suspend fun getNoteCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(notes: List<StudyNoteEntity>)

    @Query("UPDATE study_notes SET isBookmarked = :bookmarked WHERE id = :noteId")
    suspend fun updateBookmark(noteId: String, bookmarked: Boolean)

    @Query("UPDATE study_notes SET isSavedOffline = :saved WHERE id = :noteId")
    suspend fun updateOfflineSaved(noteId: String, saved: Boolean)

    @Query("UPDATE study_notes SET isCompleted = :completed WHERE id = :noteId")
    suspend fun updateCompleted(noteId: String, completed: Boolean)

    @Query("UPDATE study_notes SET isCompleted = 0")
    suspend fun resetNoteProgress()
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY isPyq DESC, examYear DESC, id ASC")
    fun observeAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(questions: List<QuestionEntity>)

    @Query("UPDATE questions SET isBookmarked = :bookmarked WHERE id = :questionId")
    suspend fun updateBookmark(questionId: String, bookmarked: Boolean)

    @Query("UPDATE questions SET isSavedOffline = :saved WHERE id = :questionId")
    suspend fun updateOfflineSaved(questionId: String, saved: Boolean)

    @Query("UPDATE questions SET lastAttemptCorrect = :isCorrect WHERE id = :questionId")
    suspend fun updateAttemptResult(questionId: String, isCorrect: Boolean)

    @Query("UPDATE questions SET lastAttemptCorrect = NULL")
    suspend fun resetQuestionProgress()
}

@Dao
interface MockTestAttemptDao {
    @Query("SELECT * FROM mock_test_attempts ORDER BY completedAtMillis DESC")
    fun observeAllAttempts(): Flow<List<MockTestAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: MockTestAttemptEntity)

    @Query("DELETE FROM mock_test_attempts")
    suspend fun clearAllAttempts()
}
