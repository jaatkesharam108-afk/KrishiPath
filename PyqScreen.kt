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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
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
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.KrishiTopBar
import com.example.ui.components.QuestionPracticeCard
import com.example.ui.components.tr
import com.example.ui.theme.HarvestAmberSoft
import com.example.ui.theme.HarvestAmberText
import com.example.ui.theme.VerifiedTeal
import com.example.ui.theme.VerifiedTealSoft

@Composable
fun PyqScreen(
    questions: List<PracticeQuestion>,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onSelectOption: (PracticeQuestion, Int) -> Unit,
    onToggleBookmark: (PracticeQuestion) -> Unit,
    onToggleOffline: (PracticeQuestion) -> Unit
) {
    // Include PYQs and optionally allow filtering Verified vs Sample
    var originFilter by rememberSaveable { mutableStateOf("all") } // all, verified, sample
    var searchText by rememberSaveable { mutableStateOf("") }

    val pyqList = questions.filter { it.isPyq }.filter { q ->
        val matchesOrigin = when (originFilter) {
            "verified" -> q.isVerified
            "sample" -> !q.isVerified
            else -> true
        }
        val t = searchText.trim()
        val matchesSearch = t.isEmpty() ||
            q.questionText.hi.contains(t, ignoreCase = true) ||
            q.questionText.en.contains(t, ignoreCase = true) ||
            q.examName.contains(t, ignoreCase = true) ||
            q.examYear.contains(t, ignoreCase = true)
        matchesOrigin && matchesSearch
    }

    Scaffold(
        topBar = {
            KrishiTopBar(
                title = tr(language, "पिछले वर्षों के प्रश्न (PYQ)", "Previous Year Questions"),
                subtitle = tr(language, "सत्यापित बनाम नमूना स्पष्ट पहचान", "Clear Verified vs. Sample Labeling"),
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
            // Transparent Verification Notice
            Surface(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                color = VerifiedTealSoft.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, VerifiedTeal.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = VerifiedTeal
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = tr(
                            language,
                            "पारदर्शिता सूचना: 'सत्यापित PYQ' (हरा बैज) मानक ICAR/CUET परीक्षा संदर्भ प्रश्न हैं, जबकि 'नमूना सामग्री' (पीला बैज) अभ्यास हेतु नमूना प्रश्न हैं।",
                            "Transparency Notice: 'Verified PYQ' (teal badge) marks standard historical ICAR/CUET exam items, whereas 'Sample Content' (amber badge) marks practice-style sample questions."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
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
                    Text(tr(language, "परीक्षा, वर्ष (2024) या प्रश्न खोजें...", "Search exam, year (2024) or keyword..."))
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
                        selected = originFilter == "all",
                        onClick = { originFilter = "all" },
                        label = { Text(tr(language, "सभी PYQ", "All PYQs")) }
                    )
                }
                item {
                    FilterChip(
                        selected = originFilter == "verified",
                        onClick = { originFilter = "verified" },
                        leadingIcon = {
                            Icon(Icons.Filled.Verified, contentDescription = null, tint = VerifiedTeal)
                        },
                        label = { Text(tr(language, "सत्यापित PYQ", "Verified PYQ")) }
                    )
                }
                item {
                    FilterChip(
                        selected = originFilter == "sample",
                        onClick = { originFilter = "sample" },
                        label = {
                            Text(
                                text = tr(language, "नमूना PYQ", "Sample PYQ"),
                                color = if (originFilter == "sample") HarvestAmberText else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (pyqList.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Filled.HistoryEdu,
                            title = tr(language, "कोई PYQ नहीं मिला", "No matching PYQs"),
                            subtitle = tr(language, "कृपया अन्य फ़िल्टर चुनें।", "Try selecting another filter."),
                            modifier = Modifier.widthIn(max = 840.dp)
                        )
                    }
                } else {
                    itemsIndexed(pyqList, key = { _, q -> q.id }) { idx, question ->
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
