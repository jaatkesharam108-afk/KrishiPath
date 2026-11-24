package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AppLanguage
import com.example.domain.model.MockTestAttempt
import com.example.domain.model.MockTestBlueprint
import com.example.ui.components.ContentOriginBadge
import com.example.ui.components.KrishiTopBar
import com.example.ui.components.tr
import com.example.ui.state.MockTestSessionState
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ErrorRedSoft
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.HarvestAmberSoft
import com.example.ui.theme.SoftMint
import com.example.ui.theme.VibrantEmerald
import java.util.Locale

@Composable
fun MockTestScreen(
    blueprints: List<MockTestBlueprint>,
    attempts: List<MockTestAttempt>,
    session: MockTestSessionState,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onStartTest: (MockTestBlueprint) -> Unit,
    onTogglePause: () -> Unit,
    onSelectAnswer: (String, Int) -> Unit,
    onClearAnswer: (String) -> Unit,
    onToggleReview: (String) -> Unit,
    onGoToQuestion: (Int) -> Unit,
    onSubmitTest: () -> Unit,
    onExitSession: () -> Unit
) {
    when {
        session.blueprint != null && !session.isSubmitted -> {
            ActiveMockTestRunner(
                session = session,
                language = language,
                onTogglePause = onTogglePause,
                onSelectAnswer = onSelectAnswer,
                onClearAnswer = onClearAnswer,
                onToggleReview = onToggleReview,
                onGoToQuestion = onGoToQuestion,
                onSubmitTest = onSubmitTest,
                onExitSession = onExitSession
            )
        }
        session.blueprint != null && session.isSubmitted && session.latestResult != null -> {
            MockTestResultScreen(
                session = session,
                result = session.latestResult,
                language = language,
                onExitSession = onExitSession,
                onRetake = { onStartTest(session.blueprint) }
            )
        }
        else -> {
            MockTestLobbyScreen(
                blueprints = blueprints,
                attempts = attempts,
                language = language,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick,
                onStartTest = onStartTest
            )
        }
    }
}

@Composable
private fun MockTestLobbyScreen(
    blueprints: List<MockTestBlueprint>,
    attempts: List<MockTestAttempt>,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onStartTest: (MockTestBlueprint) -> Unit
) {
    Scaffold(
        topBar = {
            KrishiTopBar(
                title = tr(language, "मॉक टेस्ट एवं मूल्यांकन", "Mock Tests & Scoring"),
                subtitle = tr(language, "लाइव टाइमर व वास्तविक अंक योजना", "Live Timer & Exam Marking"),
                language = language,
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(blueprints, key = { it.id }) { bp ->
                Card(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth()
                        .testTag("mock_card_${bp.id}"),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ContentOriginBadge(
                            isVerified = !bp.isSample,
                            language = language,
                            extraLabel = "${bp.questionCount} Q • ${bp.durationMinutes} ${tr(language, "मिनट", "min")}"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = bp.title.get(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bp.description.get(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onStartTest(bp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_mock_${bp.id}"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tr(language, "टेस्ट शुरू करें", "Start Timed Test"))
                        }
                    }
                }
            }

            if (attempts.isNotEmpty()) {
                item {
                    Text(
                        text = tr(language, "पिछले मॉक टेस्ट परिणाम", "Recent Test Attempts"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .widthIn(max = 840.dp)
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                items(attempts.take(6), key = { it.id }) { attempt ->
                    val accuracy = if (attempt.totalQuestions > 0) {
                        (attempt.correctCount * 100) / attempt.totalQuestions
                    } else 0
                    Card(
                        modifier = Modifier
                            .widthIn(max = 840.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tr(language, attempt.testTitleHi, attempt.testTitleEn),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tr(
                                        language,
                                        "सही: ${attempt.correctCount} • गलत: ${attempt.wrongCount} • छूटे: ${attempt.skippedCount}",
                                        "Correct: ${attempt.correctCount} • Wrong: ${attempt.wrongCount} • Skipped: ${attempt.skippedCount}"
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SoftMint
                            ) {
                                Text(
                                    text = "${attempt.score}/${attempt.maxScore} ($accuracy%)",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
private fun ActiveMockTestRunner(
    session: MockTestSessionState,
    language: AppLanguage,
    onTogglePause: () -> Unit,
    onSelectAnswer: (String, Int) -> Unit,
    onClearAnswer: (String) -> Unit,
    onToggleReview: (String) -> Unit,
    onGoToQuestion: (Int) -> Unit,
    onSubmitTest: () -> Unit,
    onExitSession: () -> Unit
) {
    var showSubmitConfirm by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }

    BackHandler { showExitConfirm = true }

    val question = session.questions.getOrNull(session.currentIndex)
    val mins = session.remainingSeconds / 60
    val secs = session.remainingSeconds % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", mins, secs)

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { showExitConfirm = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = tr(language, "बाहर निकलें", "Exit test")
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (session.remainingSeconds <= 60) ErrorRedSoft else SoftMint,
                        modifier = Modifier.testTag("mock_timer_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = if (session.remainingSeconds <= 60) ErrorRed else ForestGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timeFormatted,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (session.remainingSeconds <= 60) ErrorRed else ForestGreen
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onTogglePause) {
                            Icon(
                                imageVector = if (session.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                contentDescription = tr(language, "रोकें/चलाएँ", "Pause or Resume")
                            )
                        }
                        Button(
                            onClick = { showSubmitConfirm = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("submit_mock_button")
                        ) {
                            Text(tr(language, "सबमिट", "Submit"))
                        }
                    }
                }
            }
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
            // Question Palette
            item {
                Card(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = tr(
                                language,
                                "प्रश्न पैलेट (${session.selectedAnswers.size}/${session.questions.size} उत्तरित)",
                                "Question Palette (${session.selectedAnswers.size}/${session.questions.size} answered)"
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            session.questions.forEachIndexed { idx, q ->
                                val isCurrent = idx == session.currentIndex
                                val isAnswered = session.selectedAnswers.containsKey(q.id)
                                val isMarked = q.id in session.markedForReview

                                val bg = when {
                                    isCurrent -> ForestGreen
                                    isMarked -> HarvestAmberSoft
                                    isAnswered -> SoftMint
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                                val fg = when {
                                    isCurrent -> Color.White
                                    isMarked -> HarvestAmber
                                    isAnswered -> ForestGreen
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }

                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(bg)
                                        .clickable { onGoToQuestion(idx) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${idx + 1}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = fg
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (session.isPaused) {
                item {
                    Card(
                        modifier = Modifier
                            .widthIn(max = 840.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = HarvestAmberSoft)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = tr(language, "टेस्ट रोका गया है", "Test Paused"),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(onClick = onTogglePause) {
                                Text(tr(language, "पुनः शुरू करें", "Resume Test"))
                            }
                        }
                    }
                }
            } else if (question != null) {
                item {
                    val chosen = session.selectedAnswers[question.id]
                    val isMarked = question.id in session.markedForReview
                    val options = question.options(language)
                    val letters = listOf("A", "B", "C", "D")

                    Card(
                        modifier = Modifier
                            .widthIn(max = 840.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tr(
                                        language,
                                        "प्रश्न ${session.currentIndex + 1} / ${session.questions.size}",
                                        "Question ${session.currentIndex + 1} of ${session.questions.size}"
                                    ),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                TextButton(onClick = { onToggleReview(question.id) }) {
                                    Icon(
                                        imageVector = if (isMarked) Icons.Filled.Flag else Icons.Outlined.Flag,
                                        contentDescription = null,
                                        tint = if (isMarked) HarvestAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        tr(
                                            language,
                                            if (isMarked) "समीक्षा हेतु चिह्नित" else "समीक्षा हेतु रखें",
                                            if (isMarked) "Marked" else "Mark Review"
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = question.questionText.get(language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            options.forEachIndexed { idx, opt ->
                                val isSelected = chosen == idx
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSelectAnswer(question.id, idx) }
                                        .testTag("mock_option_$idx"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) SoftMint else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    border = BorderStroke(
                                        1.4.dp,
                                        if (isSelected) VibrantEmerald else MaterialTheme.colorScheme.outlineVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${letters.getOrElse(idx) { "" }}.",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) ForestGreen else MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = opt,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = { onClearAnswer(question.id) },
                                    enabled = chosen != null
                                ) {
                                    Text(tr(language, "उत्तर हटाएँ", "Clear"))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onGoToQuestion(session.currentIndex - 1) },
                                        enabled = session.currentIndex > 0
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                    Button(
                                        onClick = {
                                            if (session.currentIndex < session.questions.size - 1) {
                                                onGoToQuestion(session.currentIndex + 1)
                                            } else {
                                                showSubmitConfirm = true
                                            }
                                        }
                                    ) {
                                        Text(
                                            if (session.currentIndex < session.questions.size - 1) {
                                                tr(language, "अगला", "Next")
                                            } else {
                                                tr(language, "सबमिट करें", "Finish")
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSubmitConfirm) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirm = false },
            title = { Text(tr(language, "मॉक टेस्ट सबमिट करें?", "Submit Mock Test?")) },
            text = {
                Text(
                    tr(
                        language,
                        "आपने ${session.selectedAnswers.size} / ${session.questions.size} प्रश्नों के उत्तर दिए हैं। क्या आप परिणाम देखना चाहते हैं?",
                        "You have answered ${session.selectedAnswers.size} of ${session.questions.size} questions. Submit to calculate your score?"
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    showSubmitConfirm = false
                    onSubmitTest()
                }) {
                    Text(tr(language, "हाँ, सबमिट करें", "Yes, Submit"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirm = false }) {
                    Text(tr(language, "वापस जाएँ", "Cancel"))
                }
            }
        )
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text(tr(language, "टेस्ट छोड़ें?", "Leave Mock Test?")) },
            text = {
                Text(tr(language, "वर्तमान टेस्ट का प्रगति डेटा सेव नहीं होगा।", "Your current unsubmitted test session will be discarded."))
            },
            confirmButton = {
                Button(onClick = {
                    showExitConfirm = false
                    onExitSession()
                }) {
                    Text(tr(language, "बाहर निकलें", "Leave"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text(tr(language, "जारी रखें", "Continue Test"))
                }
            }
        )
    }
}

@Composable
private fun MockTestResultScreen(
    session: MockTestSessionState,
    result: MockTestAttempt,
    language: AppLanguage,
    onExitSession: () -> Unit,
    onRetake: () -> Unit
) {
    BackHandler { onExitSession() }
    val accuracy = if (result.totalQuestions > 0) (result.correctCount * 100) / result.totalQuestions else 0
    val mins = result.timeTakenSeconds / 60
    val secs = result.timeTakenSeconds % 60

    Scaffold(
        topBar = {
            KrishiTopBar(
                title = tr(language, "टेस्ट स्कोरकार्ड", "Test Scorecard"),
                language = language,
                onToggleLanguage = {},
                onBackClick = onExitSession
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = ForestGreen)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = SoftMint,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${result.score} / ${result.maxScore}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = tr(
                                language,
                                "सटीकता: $accuracy% • समय: ${mins}m ${secs}s",
                                "Accuracy: $accuracy% • Time: ${mins}m ${secs}s"
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftMint
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ScorePill(tr(language, "सही", "Correct"), "${result.correctCount}")
                            ScorePill(tr(language, "गलत", "Wrong"), "${result.wrongCount}")
                            ScorePill(tr(language, "छूटे", "Skipped"), "${result.skippedCount}")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = onRetake,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(tr(language, "पुनः प्रयास करें", "Retake Test"))
                            }
                            OutlinedButton(
                                onClick = onExitSession,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(tr(language, "सभी टेस्ट", "All Tests"), color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = tr(language, "उत्तर कुंजी एवं व्याख्या", "Answer Key & Solutions"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth()
                )
            }

            itemsIndexed(session.questions, key = { _, q -> q.id }) { idx, q ->
                val userAns = session.selectedAnswers[q.id]
                val opts = q.options(language)
                val isCorrect = userAns == q.correctIndex

                Card(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.dp,
                        when {
                            userAns == null -> MaterialTheme.colorScheme.outlineVariant
                            isCorrect -> VibrantEmerald
                            else -> ErrorRed
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Q${idx + 1}. ${q.questionText.get(language)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = tr(
                                language,
                                "आपका उत्तर: ${userAns?.let { opts.getOrNull(it) } ?: "अनुत्तरित"}",
                                "Your Answer: ${userAns?.let { opts.getOrNull(it) } ?: "Skipped"}"
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCorrect) ForestGreen else ErrorRed
                        )
                        Text(
                            text = tr(
                                language,
                                "सही उत्तर: ${opts.getOrElse(q.correctIndex) { "" }}",
                                "Correct Answer: ${opts.getOrElse(q.correctIndex) { "" }}"
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = q.explanation.get(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScorePill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.16f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = SoftMint)
        }
    }
}
