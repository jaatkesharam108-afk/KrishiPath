package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AppLanguage
import com.example.domain.model.PracticeQuestion
import com.example.domain.model.StudyNote
import com.example.ui.components.ContentOriginBadge
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.KrishiTopBar
import com.example.ui.components.OfflineStatusChip
import com.example.ui.components.QuestionPracticeCard
import com.example.ui.components.tr
import com.example.ui.theme.HarvestAmber

@Composable
fun BookmarksScreen(
    notes: List<StudyNote>,
    questions: List<PracticeQuestion>,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onOpenNote: (String) -> Unit,
    onToggleNoteBookmark: (StudyNote) -> Unit,
    onToggleNoteOffline: (StudyNote) -> Unit,
    onSelectQuestionOption: (PracticeQuestion, Int) -> Unit,
    onToggleQuestionBookmark: (PracticeQuestion) -> Unit,
    onToggleQuestionOffline: (PracticeQuestion) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val bookmarkedNotes = notes.filter { it.isBookmarked }
    val bookmarkedQuestions = questions.filter { it.isBookmarked }
    val offlineNotes = notes.filter { it.isSavedOffline }

    Scaffold(
        topBar = {
            KrishiTopBar(
                title = tr(language, "बुकमार्क एवं ऑफ़लाइन", "Bookmarks & Offline"),
                subtitle = tr(language, "बिना इंटरनेट के त्वरित पुनरावृत्ति", "Instant Offline Revision"),
                language = language,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(tr(language, "नोट्स (${bookmarkedNotes.size})", "Notes (${bookmarkedNotes.size})"))
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(tr(language, "प्रश्न (${bookmarkedQuestions.size})", "Questions (${bookmarkedQuestions.size})"))
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(tr(language, "ऑफ़लाइन (${offlineNotes.size})", "Offline (${offlineNotes.size})"))
                    }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (selectedTab) {
                    0 -> {
                        if (bookmarkedNotes.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    icon = Icons.AutoMirrored.Filled.MenuBook,
                                    title = tr(language, "कोई बुकमार्क नोट नहीं है", "No Bookmarked Notes"),
                                    subtitle = tr(language, "किसी भी अध्याय के बुकमार्क आइकन को दबाकर यहाँ सहेजें।", "Tap the bookmark icon on any chapter to save it here."),
                                    modifier = Modifier.widthIn(max = 840.dp)
                                )
                            }
                        } else {
                            items(bookmarkedNotes, key = { it.id }) { note ->
                                BookmarkedNoteRow(
                                    note = note,
                                    language = language,
                                    onOpen = { onOpenNote(note.id) },
                                    onToggleBookmark = { onToggleNoteBookmark(note) },
                                    onToggleOffline = { onToggleNoteOffline(note) },
                                    modifier = Modifier.widthIn(max = 840.dp)
                                )
                            }
                        }
                    }
                    1 -> {
                        if (bookmarkedQuestions.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    icon = Icons.Filled.Quiz,
                                    title = tr(language, "कोई बुकमार्क प्रश्न नहीं है", "No Bookmarked Questions"),
                                    subtitle = tr(language, "MCQ या PYQ पर बुकमार्क आइकन दबाकर यहाँ सहेजें।", "Tap the bookmark icon on any MCQ or PYQ to review it here."),
                                    modifier = Modifier.widthIn(max = 840.dp)
                                )
                            }
                        } else {
                            itemsIndexed(bookmarkedQuestions, key = { _, q -> q.id }) { idx, q ->
                                QuestionPracticeCard(
                                    question = q,
                                    indexNumber = idx + 1,
                                    language = language,
                                    onSelectOption = { opt -> onSelectQuestionOption(q, opt) },
                                    onToggleBookmark = { onToggleQuestionBookmark(q) },
                                    onToggleOffline = { onToggleQuestionOffline(q) },
                                    modifier = Modifier.widthIn(max = 840.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        if (offlineNotes.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    icon = Icons.Filled.CloudDone,
                                    title = tr(language, "कोई ऑफ़लाइन नोट नहीं", "No Offline Notes"),
                                    subtitle = tr(language, "ऑफ़लाइन पढ़ने के लिए नोट्स पर 'ऑफ़लाइन सहेजें' चुनें।", "Enable offline saving on notes to access them anytime."),
                                    modifier = Modifier.widthIn(max = 840.dp)
                                )
                            }
                        } else {
                            items(offlineNotes, key = { it.id }) { note ->
                                BookmarkedNoteRow(
                                    note = note,
                                    language = language,
                                    onOpen = { onOpenNote(note.id) },
                                    onToggleBookmark = { onToggleNoteBookmark(note) },
                                    onToggleOffline = { onToggleNoteOffline(note) },
                                    modifier = Modifier.widthIn(max = 840.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkedNoteRow(
    note: StudyNote,
    language: AppLanguage,
    onOpen: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onOpen,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ContentOriginBadge(isVerified = !note.isSampleContent, language = language)
                    OfflineStatusChip(
                        isSavedOffline = note.isSavedOffline,
                        language = language,
                        onClick = onToggleOffline
                    )
                }
                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = tr(language, "बुकमार्क हटाएँ", "Remove bookmark"),
                        tint = if (note.isBookmarked) HarvestAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note.title.get(language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.summary.get(language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
