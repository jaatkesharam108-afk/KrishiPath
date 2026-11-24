package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.AppLanguage
import com.example.domain.model.StudentProfile
import com.example.domain.model.StudentProgressSummary
import com.example.domain.model.StudyNote
import com.example.ui.components.ContentOriginBadge
import com.example.ui.components.KrishiTopBar
import com.example.ui.components.tr
import com.example.ui.navigation.Routes
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.HarvestAmberSoft
import com.example.ui.theme.SoftMint
import com.example.ui.theme.VerifiedTeal
import com.example.ui.theme.VerifiedTealSoft
import com.example.ui.theme.VibrantEmerald

private data class HomeModuleItem(
    val route: String,
    val titleHi: String,
    val titleEn: String,
    val subtitleHi: String,
    val subtitleEn: String,
    val icon: ImageVector,
    val tint: Color,
    val bgTint: Color,
    val testTag: String
)

@Composable
fun HomeScreen(
    profile: StudentProfile,
    progress: StudentProgressSummary,
    featuredNotes: List<StudyNote>,
    onToggleLanguage: () -> Unit,
    onNavigate: (String) -> Unit,
    onOpenNote: (String) -> Unit
) {
    val lang = profile.language

    val modules = listOf(
        HomeModuleItem(
            route = Routes.AGRICULTURE_SUBJECTS,
            titleHi = "कृषि विषय",
            titleEn = "Agriculture Subjects",
            subtitleHi = "शस्य, मृदा, उद्यान, डेयरी",
            subtitleEn = "Agronomy, Soil, Dairy",
            icon = Icons.Filled.Agriculture,
            tint = ForestGreen,
            bgTint = SoftMint,
            testTag = "tile_agriculture_subjects"
        ),
        HomeModuleItem(
            route = Routes.CUET_ICAR,
            titleHi = "CUET व ICAR तैयारी",
            titleEn = "CUET & ICAR Prep",
            subtitleHi = "सिलेबस, अंक योजना व रणनीति",
            subtitleEn = "Syllabus & Exam Pattern",
            icon = Icons.Filled.School,
            tint = VerifiedTeal,
            bgTint = VerifiedTealSoft,
            testTag = "tile_cuet_icar"
        ),
        HomeModuleItem(
            route = Routes.BIO_CHEM,
            titleHi = "जीव व रसायन विज्ञान",
            titleEn = "Biology & Chemistry",
            subtitleHi = "आनुवंशिकी, कार्यिकी, NPK",
            subtitleEn = "Genetics, Physiology, NPK",
            icon = Icons.Filled.Biotech,
            tint = DeepEmerald,
            bgTint = SoftMint,
            testTag = "tile_bio_chem"
        ),
        HomeModuleItem(
            route = Routes.studyNotes(),
            titleHi = "अध्यायवार नोट्स",
            titleEn = "Chapter Study Notes",
            subtitleHi = "ऑफ़लाइन पढ़ने योग्य सार",
            subtitleEn = "Offline Structured Notes",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            tint = ForestGreen,
            bgTint = SoftMint,
            testTag = "tile_study_notes"
        ),
        HomeModuleItem(
            route = Routes.mcqPractice(),
            titleHi = "MCQ अभ्यास",
            titleEn = "MCQ Practice",
            subtitleHi = "उत्तर व विस्तृत व्याख्या",
            subtitleEn = "Answers & Explanations",
            icon = Icons.Filled.Quiz,
            tint = HarvestAmber,
            bgTint = HarvestAmberSoft,
            testTag = "tile_mcq_practice"
        ),
        HomeModuleItem(
            route = Routes.MOCK_TESTS,
            titleHi = "मॉक टेस्ट (टाइमर)",
            titleEn = "Mock Tests",
            subtitleHi = "लाइव टाइमर व स्कोरकार्ड",
            subtitleEn = "Live Timer & Scorecard",
            icon = Icons.Filled.Timer,
            tint = VerifiedTeal,
            bgTint = VerifiedTealSoft,
            testTag = "tile_mock_tests"
        ),
        HomeModuleItem(
            route = Routes.PYQ,
            titleHi = "पिछले वर्षों के प्रश्न",
            titleEn = "Previous Year Qs",
            subtitleHi = "सत्यापित बनाम नमूना टैग",
            subtitleEn = "Verified vs Sample PYQs",
            icon = Icons.Filled.HistoryEdu,
            tint = ForestGreen,
            bgTint = SoftMint,
            testTag = "tile_pyqs"
        ),
        HomeModuleItem(
            route = Routes.BOOKMARKS,
            titleHi = "बुकमार्क व ऑफ़लाइन",
            titleEn = "Bookmarks & Offline",
            subtitleHi = "${progress.bookmarkedNotesCount + progress.bookmarkedQuestionsCount} सहेजे गए",
            subtitleEn = "${progress.bookmarkedNotesCount + progress.bookmarkedQuestionsCount} Saved Items",
            icon = Icons.Filled.Bookmark,
            tint = HarvestAmber,
            bgTint = HarvestAmberSoft,
            testTag = "tile_bookmarks"
        ),
        HomeModuleItem(
            route = Routes.PROFILE,
            titleHi = "छात्र प्रोफ़ाइल व प्रगति",
            titleEn = "Profile & Progress",
            subtitleHi = "सटीकता व लक्ष्य ट्रैकर",
            subtitleEn = "Accuracy & Study Tracker",
            icon = Icons.Filled.Person,
            tint = DeepEmerald,
            bgTint = SoftMint,
            testTag = "tile_profile"
        )
    )

    Scaffold(
        topBar = {
            KrishiTopBar(
                title = "KrishiPath",
                subtitle = tr(lang, "कृषि शिक्षा का नया रास्ता", "A New Path for Agriculture Education"),
                language = lang,
                onToggleLanguage = onToggleLanguage,
                onSearchClick = { onNavigate(Routes.SEARCH) }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isTablet = maxWidth >= 600.dp
            val columnCount = if (maxWidth >= 840.dp) 3 else 2

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Banner
                item {
                    Box(modifier = Modifier.widthIn(max = 960.dp)) {
                        HomeHeroBanner(
                            profile = profile,
                            progress = progress,
                            onSearchClick = { onNavigate(Routes.SEARCH) },
                            onStartMockClick = { onNavigate(Routes.MOCK_TESTS) }
                        )
                    }
                }

                // Quick Search Bar
                item {
                    Surface(
                        modifier = Modifier
                            .widthIn(max = 960.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigate(Routes.SEARCH) }
                            .testTag("home_search_bar"),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = tr(
                                    lang,
                                    "नोट्स, फसलें, MCQ या PYQ खोजें...",
                                    "Search notes, crops, MCQs or PYQs..."
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudDone,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = tr(lang, "ऑफ़लाइन तैयार", "Offline Ready"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Section Title: All Study Modules
                item {
                    Row(
                        modifier = Modifier
                            .widthIn(max = 960.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tr(lang, "अध्ययन मॉड्यूल", "Learning Modules"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tr(lang, "सभी 10 सेक्शन", "All Sections"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Responsive Module Grid (2 columns on phone, 2-3 columns on tablet)
                val rows = modules.chunked(columnCount)
                items(rows) { rowItems ->
                    Row(
                        modifier = Modifier
                            .widthIn(max = 960.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            ModuleGridCard(
                                item = item,
                                language = lang,
                                onClick = { onNavigate(item.route) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size < columnCount) {
                            repeat(columnCount - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Featured Offline Chapters
                if (featuredNotes.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tr(lang, "प्रमुख अध्ययन अध्याय", "Featured Study Chapters"),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tr(lang, "सभी देखें", "View All"),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onNavigate(Routes.studyNotes()) }
                                        .padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(featuredNotes.take(5), key = { it.id }) { note ->
                                    Card(
                                        modifier = Modifier
                                            .width(if (isTablet) 300.dp else 260.dp)
                                            .clickable { onOpenNote(note.id) },
                                        shape = MaterialTheme.shapes.large,
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            ContentOriginBadge(
                                                isVerified = !note.isSampleContent,
                                                language = lang,
                                                extraLabel = "${note.readMinutes} ${tr(lang, "मिनट", "min")}"
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = note.title.get(lang),
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = note.summary.get(lang),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = tr(lang, "नोट्स पढ़ें", "Read Notes"),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun HomeHeroBanner(
    profile: StudentProfile,
    progress: StudentProgressSummary,
    onSearchClick: () -> Unit,
    onStartMockClick: () -> Unit
) {
    val lang = profile.language
    val noteRatio = if (progress.totalNotes > 0) {
        progress.completedNotes.toFloat() / progress.totalNotes.toFloat()
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_agriculture),
                contentDescription = tr(lang, "कृषिपंथ बैनर", "KrishiPath Agriculture Banner"),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                ForestGreen.copy(alpha = 0.92f),
                                DeepEmerald.copy(alpha = 0.78f),
                                ForestGreen.copy(alpha = 0.45f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Eco,
                            contentDescription = null,
                            tint = SoftMint,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(18.dp)
                        )
                    }
                    Text(
                        text = tr(
                            lang,
                            "नमस्ते, ${profile.name} • ${profile.targetExam}",
                            "Welcome, ${profile.name} • ${profile.targetExam}"
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = SoftMint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = tr(lang, "कृषि शिक्षा का नया रास्ता", "KrishiPath: Agriculture Learning Hub"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = tr(
                        lang,
                        "${progress.completedNotes}/${progress.totalNotes} अध्याय पूर्ण • ${progress.correctQuestions}/${progress.attemptedQuestions} सही MCQ",
                        "${progress.completedNotes}/${progress.totalNotes} chapters read • ${progress.correctQuestions}/${progress.attemptedQuestions} correct MCQs"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.92f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { noteRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = HarvestAmberSoft,
                    trackColor = Color.White.copy(alpha = 0.28f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onStartMockClick),
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = tr(lang, "मॉक टेस्ट शुरू करें", "Start Mock Test"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onSearchClick),
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = tr(lang, "खोजें", "Search"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleGridCard(
    item: HomeModuleItem,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag(item.testTag),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.bgTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.tint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = tr(language, item.titleHi, item.titleEn),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = tr(language, item.subtitleHi, item.subtitleEn),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
