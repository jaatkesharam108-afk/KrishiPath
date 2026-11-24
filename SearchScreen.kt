package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AppLanguage
import com.example.domain.model.PracticeQuestion
import com.example.ui.components.ContentOriginBadge
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.KrishiTopBar
import com.example.ui.components.QuestionPracticeCard
import com.example.ui.components.tr
import com.example.ui.state.SearchResults

@Composable
fun SearchScreen(
    searchResults: SearchResults,
    language: AppLanguage,
    onQueryChange: (String) -> Unit,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onOpenNote: (String) -> Unit,
    onSelectQuestionOption: (PracticeQuestion, Int) -> Unit,
    onToggleQuestionBookmark: (PracticeQuestion) -> Unit,
    onToggleQuestionOffline: (PracticeQuestion) -> Unit
) {
    Scaffold(
        topBar = {
            KrishiTopBar(
                title = tr(language, "नोट्स एवं प्रश्न खोजें", "Search Notes & Questions"),
                subtitle = tr(language, "हिन्दी या अंग्रेज़ी में खोजें", "Search in Hindi or English"),
                language = language,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchResults.query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("global_search_input"),
                placeholder = {
                    Text(
                        tr(
                            language,
                            "उदा. यूरिया, खैरा रोग, C4, मेंडल, NABARD...",
                            "e.g. Urea, Khaira, C4, Mendel, NABARD..."
                        )
                    )
                },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchResults.query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = tr(language, "साफ़ करें", "Clear"))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (searchResults.notes.isEmpty() && searchResults.questions.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Filled.Search,
                            title = tr(language, "कोई परिणाम नहीं मिला", "No Results Found"),
                            subtitle = tr(language, "कृपया दूसरा कीवर्ड आज़माएँ।", "Try another keyword in Hindi or English."),
                            modifier = Modifier.widthIn(max = 840.dp)
                        )
                    }
                } else {
                    if (searchResults.notes.isNotEmpty()) {
                        item {
                            Text(
                                text = tr(
                                    language,
                                    "अध्ययन नोट्स (${searchResults.notes.size})",
                                    "Study Notes (${searchResults.notes.size})"
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .widthIn(max = 840.dp)
                                    .fillMaxWidth()
                            )
                        }
                        items(searchResults.notes, key = { "search_${it.id}" }) { note ->
                            Card(
                                onClick = { onOpenNote(note.id) },
                                modifier = Modifier
                                    .widthIn(max = 840.dp)
                                    .fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    ContentOriginBadge(
                                        isVerified = !note.isSampleContent,
                                        language = language
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = note.title.get(language),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = note.summary.get(language),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (searchResults.questions.isNotEmpty()) {
                        item {
                            Text(
                                text = tr(
                                    language,
                                    "प्रश्न एवं PYQ (${searchResults.questions.size})",
                                    "Questions & PYQs (${searchResults.questions.size})"
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .widthIn(max = 840.dp)
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                            )
                        }
                        itemsIndexed(searchResults.questions, key = { _, q -> "search_q_${q.id}" }) { idx, q ->
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
            }
        }
    }
}
