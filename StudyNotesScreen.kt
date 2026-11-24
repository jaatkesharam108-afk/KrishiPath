package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AppLanguage
import com.example.domain.model.StudyNote
import com.example.domain.model.SubjectCategory
import com.example.ui.components.ContentOriginBadge
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.KrishiTopBar
import com.example.ui.components.OfflineStatusChip
import com.example.ui.components.tr
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.SoftMint
import com.example.ui.theme.VibrantEmerald

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudyNotesScreen(
    notes: List<StudyNote>,
    initialCategory: String,
    initialSubjectId: String,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onOpenNote: (String) -> Unit,
    onToggleBookmark: (StudyNote) -> Unit,
    onToggleOffline: (StudyNote) -> Unit
) {
    var selectedCategory by rememberSaveable(initialCategory) { mutableStateOf(initialCategory) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var subjectFilter by rememberSaveable(initialSubjectId) { mutableStateOf(initialSubjectId) }

    val filtered = notes.filter { note ->
        val matchesCategory = selectedCategory == "all" || note.category.id == selectedCategory
        val matchesSubject = subjectFilter == "all" || note.subjectId == subjectFilter
        val q = searchText.trim()
        val matchesSearch = q.isEmpty() ||
            note.title.hi.contains(q, ignoreCase = true) ||
            note.title.en.contains(q, ignoreCase = true) ||
            note.summary.hi.contains(q, ignoreCase = true) ||
            note.summary.en.contains(q, ignoreCase = true) ||
            note.content.hi.contains(q, ignoreCase = true) ||
            note.content.en.contains(q, ignoreCase = true)
        matchesCategory && matchesSubject && matchesSearch
    }

    Scaffold(
        topBar = {
            KrishiTopBar(
                title = tr(language, "अध्यायवार अध्ययन नोट्स", "Chapter-wise Study Notes"),
                subtitle = tr(language, "${filtered.size} ऑफ़लाइन अध्याय", "${filtered.size} Offline Chapters"),
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
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = {
                    Text(tr(language, "अध्याय, फसल या विषय खोजें...", "Search chapters, crops or topics..."))
                },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            LazyRow(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == "all" && subjectFilter == "all",
                        onClick = {
                            selectedCategory = "all"
                            subjectFilter = "all"
                        },
                        label = { Text(tr(language, "सभी", "All")) }
                    )
                }
                items(SubjectCategory.entries) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat.id,
                        onClick = {
                            selectedCategory = cat.id
                            subjectFilter = "all"
                        },
                        label = { Text(tr(language, cat.titleHi, cat.titleEn)) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (filtered.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            title = tr(language, "कोई अध्याय नहीं मिला", "No matching notes found"),
                            subtitle = tr(language, "कृपया दूसरा खोज शब्द या फ़िल्टर आज़माएँ।", "Try a different keyword or clear filters."),
                            modifier = Modifier.widthIn(max = 840.dp)
                        )
                    }
                } else {
                    items(filtered, key = { it.id }) { note ->
                        Card(
                            onClick = { onOpenNote(note.id) },
                            modifier = Modifier
                                .widthIn(max = 840.dp)
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    FlowRow(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = tr(language, note.category.titleHi, note.category.titleEn),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        ContentOriginBadge(
                                            isVerified = !note.isSampleContent,
                                            language = language,
                                            extraLabel = "${note.readMinutes} ${tr(language, "मिनट", "min")}"
                                        )
                                        OfflineStatusChip(
                                            isSavedOffline = note.isSavedOffline,
                                            language = language,
                                            onClick = { onToggleOffline(note) }
                                        )
                                    }
                                    IconButton(onClick = { onToggleBookmark(note) }) {
                                        Icon(
                                            imageVector = if (note.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = tr(language, "बुकमार्क", "Bookmark"),
                                            tint = if (note.isBookmarked) HarvestAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = note.title.get(language),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (note.isCompleted) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = tr(language, "पूर्ण", "Completed"),
                                            tint = VibrantEmerald
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = note.summary.get(language),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteDetailScreen(
    note: StudyNote?,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onToggleBookmark: (StudyNote) -> Unit,
    onToggleOffline: (StudyNote) -> Unit,
    onToggleCompleted: (StudyNote) -> Unit
) {
    Scaffold(
        topBar = {
            KrishiTopBar(
                title = note?.title?.get(language) ?: tr(language, "अध्ययन नोट्स", "Study Notes"),
                language = language,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        if (note == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmptyStateCard(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    title = tr(language, "अध्याय उपलब्ध नहीं है", "Chapter not found"),
                    subtitle = tr(language, "कृपया वापस जाकर दूसरा अध्याय चुनें।", "Please go back and select another chapter.")
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ContentOriginBadge(
                            isVerified = !note.isSampleContent,
                            language = language
                        )
                        OfflineStatusChip(
                            isSavedOffline = note.isSavedOffline,
                            language = language,
                            onClick = { onToggleOffline(note) }
                        )
                    }

                    Text(
                        text = note.title.get(language),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = note.summary.get(language),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Main Content Card
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = tr(language, "मुख्य अध्ययन बिंदु", "Detailed Study Notes"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = note.content.get(language),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // Key Formulas Card
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = SoftMint.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Functions,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = tr(language, "महत्वपूर्ण सूत्र व तथ्य", "Key Formulas & Facts"),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = note.keyFormulas.get(language),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Exam Tip Card
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f),
                        border = BorderStroke(1.dp, HarvestAmber.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = null,
                                tint = HarvestAmber
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = tr(language, "परीक्षा विशेष टिप", "Exam Focus Tip"),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note.examTip.get(language),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onToggleCompleted(note) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (note.isCompleted) VibrantEmerald else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = if (note.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                tr(
                                    language,
                                    if (note.isCompleted) "अध्याय पूर्ण ✓" else "पूर्ण चिह्नित करें",
                                    if (note.isCompleted) "Completed ✓" else "Mark Complete"
                                )
                            )
                        }
                        OutlinedButton(
                            onClick = { onToggleBookmark(note) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (note.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = if (note.isBookmarked) HarvestAmber else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                tr(
                                    language,
                                    if (note.isBookmarked) "सहेजा गया" else "बुकमार्क करें",
                                    if (note.isBookmarked) "Bookmarked" else "Bookmark"
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
