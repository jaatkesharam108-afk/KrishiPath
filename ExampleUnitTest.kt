package com.example

import com.example.data.local.SeedNotes
import com.example.data.local.SeedQuestions
import com.example.domain.model.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun seedNotes_areValidAndBilingual() {
        assertTrue(SeedNotes.notes.size >= 14)
        SeedNotes.notes.forEach { note ->
            assertTrue("Note ${note.id} missing Hindi title", note.titleHi.isNotBlank())
            assertTrue("Note ${note.id} missing English title", note.titleEn.isNotBlank())
            assertTrue("Note ${note.id} missing Hindi content", note.contentHi.isNotBlank())
            assertTrue("Note ${note.id} missing English content", note.contentEn.isNotBlank())
            assertTrue("Note ${note.id} should be saved offline by default", note.isSavedOffline)
        }
    }

    @Test
    fun seedQuestions_andPyqs_haveValidOptionsAndVerificationTags() {
        val questions = SeedQuestions.questions
        assertTrue(questions.size >= 25)
        val verifiedPyqs = questions.filter { it.isPyq && it.isVerified }
        val samplePyqs = questions.filter { it.isPyq && !it.isVerified }
        assertTrue("Expected verified PYQs", verifiedPyqs.isNotEmpty())
        assertTrue("Expected sample PYQs", samplePyqs.isNotEmpty())

        questions.forEach { q ->
            val domain = q.toDomain()
            assertEquals(4, domain.options(AppLanguage.HINDI).size)
            assertEquals(4, domain.options(AppLanguage.ENGLISH).size)
            assertTrue("Invalid correctIndex for ${q.id}", q.correctIndex in 0..3)
            assertTrue("Missing Hindi explanation for ${q.id}", q.explanationHi.isNotBlank())
            assertTrue("Missing English explanation for ${q.id}", q.explanationEn.isNotBlank())
        }
    }

    @Test
    fun mockTests_haveValidBlueprints() {
        assertEquals(4, SeedQuestions.mockTests.size)
        SeedQuestions.mockTests.forEach { bp ->
            assertTrue(bp.durationMinutes > 0)
            assertTrue(bp.questionCount > 0)
            assertTrue(bp.positiveMarks > 0)
        }
    }
}
