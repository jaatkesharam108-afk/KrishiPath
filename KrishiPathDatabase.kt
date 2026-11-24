package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        StudyNoteEntity::class,
        QuestionEntity::class,
        MockTestAttemptEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KrishiPathDatabase : RoomDatabase() {
    abstract fun studyNoteDao(): StudyNoteDao
    abstract fun questionDao(): QuestionDao
    abstract fun mockTestAttemptDao(): MockTestAttemptDao

    companion object {
        @Volatile
        private var INSTANCE: KrishiPathDatabase? = null

        fun getInstance(context: Context): KrishiPathDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KrishiPathDatabase::class.java,
                    "krishipath_offline.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
