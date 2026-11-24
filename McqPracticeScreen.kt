package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.domain.model.PracticeQuestion
import com.example.domain.model.SubjectCategory
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.KrishiTopBar
import com.example.ui.components.QuestionPracticeCard
import com.example.ui.components.tr

@Composable
fun McqPracticeScreen(
    questions: List<PracticeQuestion>,
    initialCategory: String,
    initialSubjectId: String,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onSelectOption: (PracticeQuestion, Int) -> Unit,
    onToggleBookmark: (PracticeQuestion) -> Unit,
    onToggleOffline: (PracticeQuestion) -> Unit
) {
    var selectedCategory by rememberSaveable(initialCategory) { mutableStateOf(initialCategory) }
    var subjectFilter by rememberSaveable(initialSubjectId) { mutableStateOf(initialSubjectId) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val filtered = questions.filter { q ->
        val matchesCategory = selectedCategory == "all" || q.category.id == selectedCategory
        val matchesSubject = subjectFilter == "all" || q.subjectId == subjectFilter
        val text = searchText.trim()
        val matchesSearch = text.isEmpty() ||
            q.questionText.hi.contains(text, ignoreCase = true) ||
            q.questionText.en.contains(text, ignoreCase = true) ||
            q.explanation.hi.contains(text, ignoreCase = true) ||
            q.explanation.en.contains(text, ignoreCase = true)
        matchesCategory && matchesSubject && matchesSearch
    }

    val attemptedCount = filtered.count { it.lastAttemptCorrect != null }
    val correctCount = filtered.count { it.lastAttemptCorrect == true }

    Scaffold(
        topBar = {
            KrishiTopBar(
                title = tr(language, "MCQ अभ्यास", "MCQ Practice"),
                subtitle = tr(
                    language,
                    "उत्तर चुनें व तुरंत व्याख्या देखें",
                    "Select answers for instant explanations"
                ),
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
            // Accuracy Strip
            Surface(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tr(
                            language,
                            "कुल प्रश्न: ${filtered.size}",
                            "Questions: ${filtered.size}"
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = tr(
                            language,
                            "सही: $correctCount / $attemptedCount",
                            "Correct: $correctCount / $attemptedCount"
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = {
                    Text(tr(language, "प्रश्न या विषय खोजें...", "Search questions or topics..."))
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
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (filtered.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Filled.Quiz,
                            title = tr(language, "कोई MCQ नहीं मिला", "No matching MCQs found"),
                            subtitle = tr(language, "कृपया दूसरा फ़िल्टर या खोज शब्द चुनें।", "Try clearing filters or changing search text."),
                            modifier = Modifier.widthIn(max = 840.dp)
                        )
                    }
                } else {
                    itemsIndexed(filtered, key = { _, q -> q.id }) { idx, question ->
                        QuestionPracticeCard(
                            question = question,
                            indexNumber = idx + 1,
                            language = language,
                            onSelectOption = { opt -> onSelectOption(question, opt) },
                            onToggleBookmark = { onToggleBookmark(question) },
                            onToggleOffline = { onToggleOffline(question) },
                            modifier = Modifier.widthIn(max = 840.dp)
                        )
                    }
                }
            }
        }
    }
}
