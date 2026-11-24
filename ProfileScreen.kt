package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AppLanguage
import com.example.domain.model.StudentProfile
import com.example.domain.model.StudentProgressSummary
import com.example.domain.model.SubjectItem
import com.example.ui.components.KrishiTopBar
import com.example.ui.components.tr
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.SoftMint

@Composable
fun ProfileScreen(
    profile: StudentProfile,
    progress: StudentProgressSummary,
    subjects: List<SubjectItem>,
    onToggleLanguage: () -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onUpdateProfile: (String, String, Int) -> Unit,
    onResetProgress: () -> Unit,
    onBackClick: () -> Unit
) {
    val lang = profile.language
    var showEditDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val mcqAccuracy = if (progress.attemptedQuestions > 0) {
        (progress.correctQuestions * 100) / progress.attemptedQuestions
    } else 0

    Scaffold(
        topBar = {
            KrishiTopBar(
                title = tr(language = lang, hi = "छात्र प्रोफ़ाइल एवं प्रगति", en = "Profile & Progress"),
                subtitle = tr(lang, "स्थानीय रूप से सहेजी गई प्रगति", "Saved Locally in Room"),
                language = lang,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header Card
            item {
                Card(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = ForestGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(SoftMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = tr(
                                    lang,
                                    "लक्ष्य: ${profile.targetExam} • ${profile.dailyGoalMinutes} मिनट/दिन",
                                    "Target: ${profile.targetExam} • ${profile.dailyGoalMinutes} min/day"
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftMint
                            )
                        }
                        Surface(
                            onClick = { showEditDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = tr(lang, "प्रोफ़ाइल संपादित करें", "Edit Profile"),
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tr(lang, "बदलें", "Edit"),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Progress Stats Grid
            item {
                Card(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = tr(lang, "अध्ययन प्रगति सारांश", "Study Progress Summary"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProgressStatTile(
                                value = "${progress.completedNotes}/${progress.totalNotes}",
                                label = tr(lang, "अध्याय पूर्ण", "Notes Read"),
                                modifier = Modifier.weight(1f)
                            )
                            ProgressStatTile(
                                value = "$mcqAccuracy%",
                                label = tr(lang, "MCQ सटीकता", "MCQ Accuracy"),
                                modifier = Modifier.weight(1f)
                            )
                            ProgressStatTile(
                                value = "${progress.mockTestsTaken}",
                                label = tr(lang, "मॉक टेस्ट", "Mock Tests"),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ProgressStatTile(
                                value = "${progress.bestMockAccuracyPercent}%",
                                label = tr(lang, "श्रेष्ठ टेस्ट स्कोर", "Best Mock %"),
                                modifier = Modifier.weight(1f)
                            )
                            ProgressStatTile(
                                value = "${progress.bookmarkedNotesCount + progress.bookmarkedQuestionsCount}",
                                label = tr(lang, "बुकमार्क", "Bookmarks"),
                                modifier = Modifier.weight(1f)
                            )
                            ProgressStatTile(
                                value = "${progress.offlineSavedCount}",
                                label = tr(lang, "ऑफ़लाइन सहेजे", "Saved Offline"),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Subject Mastery
            item {
                Card(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = tr(lang, "कृषि विषयवार प्रगति", "Subject-wise Progress"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        subjects.forEach { subj ->
                            val pct = if (subj.totalChapters > 0) {
                                subj.completedChapters.toFloat() / subj.totalChapters.toFloat()
                            } else 0f
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = subj.title.get(lang),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${subj.completedChapters}/${subj.totalChapters}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { pct },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    }
                }
            }

            // Language Preference Card
            item {
                Card(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tr(lang, "ऐप भाषा चुनें (App Language)", "App Language"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilterChip(
                                selected = lang == AppLanguage.HINDI,
                                onClick = { onSetLanguage(AppLanguage.HINDI) },
                                label = { Text("हिन्दी (Default)") }
                            )
                            FilterChip(
                                selected = lang == AppLanguage.ENGLISH,
                                onClick = { onSetLanguage(AppLanguage.ENGLISH) },
                                label = { Text("English") }
                            )
                        }
                    }
                }
            }

            // Reset Progress
            item {
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tr(lang, "अध्ययन प्रगति रीसेट करें", "Reset Study Progress"))
                }
            }
        }
    }

    if (showEditDialog) {
        var nameInput by remember { mutableStateOf(profile.name) }
        var examInput by remember { mutableStateOf(profile.targetExam) }
        var goalInput by remember { mutableStateOf(profile.dailyGoalMinutes.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(tr(lang, "छात्र प्रोफ़ाइल संपादित करें", "Edit Student Profile")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(tr(lang, "आपका नाम", "Your Name")) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = examInput,
                        onValueChange = { examInput = it },
                        label = { Text(tr(lang, "लक्ष्य परीक्षा", "Target Exam")) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it.filter { c -> c.isDigit() } },
                        label = { Text(tr(lang, "दैनिक लक्ष्य (मिनट)", "Daily Goal (minutes)")) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val mins = goalInput.toIntOrNull() ?: 45
                    onUpdateProfile(nameInput, examInput, mins)
                    showEditDialog = false
                }) {
                    Text(tr(lang, "सहेजें", "Save"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(tr(lang, "रद्द करें", "Cancel"))
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(tr(lang, "प्रगति रीसेट करें?", "Reset Progress?")) },
            text = {
                Text(
                    tr(
                        lang,
                        "इससे पूर्ण किए गए अध्याय, MCQ प्रयास और मॉक टेस्ट इतिहास हट जाएंगे। बुकमार्क सुरक्षित रहेंगे।",
                        "This clears completed chapters, MCQ attempt history, and mock test scores. Your bookmarks will stay intact."
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text(tr(lang, "रीसेट करें", "Reset"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(tr(lang, "रद्द करें", "Cancel"))
                }
            }
        )
    }
}

@Composable
private fun ProgressStatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
