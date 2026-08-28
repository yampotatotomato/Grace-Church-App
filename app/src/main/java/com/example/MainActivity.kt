package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.ChurchTab
import com.example.ui.ChurchViewModel
import com.example.ui.components.CupertinoIcons
import com.example.ui.components.ToastBanner
import com.example.ui.onboarding.WelcomeCarouselScreen
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: ChurchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle navigation deep links from notifications
        val target = intent?.getStringExtra("EXTRA_NAV_TARGET")
        when (target) {
            "scripture" -> {
                viewModel.completeOnboarding()
                viewModel.selectTab(ChurchTab.SCRIPTURE)
            }
            "pastors" -> {
                viewModel.completeOnboarding()
                viewModel.selectTab(ChurchTab.SERMONS)
            }
            "prayer_groups" -> {
                viewModel.completeOnboarding()
                viewModel.selectTab(ChurchTab.COMMUNITY)
            }
            "journal" -> {
                viewModel.completeOnboarding()
                viewModel.selectTab(ChurchTab.JOURNAL)
            }
        }

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MyApplicationTheme(
                themeMode = uiState.themeMode,
                accentTheme = uiState.accentTheme
            ) {
                // Request Notification Permission on Android 13+
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ ->
                    // Notification permission state updated
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                ChurchApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ChurchApp(viewModel: ChurchViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!uiState.isOnboardingCompleted) {
            WelcomeCarouselScreen(
                onGetStarted = { viewModel.completeOnboarding() }
            )
        } else if (uiState.isNotificationSettingsOpen) {
            SettingsNotificationsScreen(
                viewModel = viewModel,
                onBack = { viewModel.closeNotificationSettings() }
            )
        } else {
            Scaffold(
                bottomBar = {
                    Column {
                        // Floating Mini Audio Bar if playing and not on Sermons tab
                        if (uiState.isAudioPlaying && uiState.selectedTab != ChurchTab.SERMONS && uiState.activeSermon != null) {
                            MiniAudioPlayerBar(
                                title = uiState.activeSermon!!.title,
                                pastor = uiState.activeSermon!!.pastorName,
                                isPlaying = uiState.isAudioPlaying,
                                onToggle = { viewModel.togglePlayPause() },
                                onClick = { viewModel.selectTab(ChurchTab.SERMONS) }
                            )
                        }

                        IosBottomNavigationBar(
                            selectedTab = uiState.selectedTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = uiState.selectedTab,
                        transitionSpec = {
                            val initialOrdinal = initialState.ordinal
                            val targetOrdinal = targetState.ordinal
                            if (targetOrdinal > initialOrdinal) {
                                (slideInHorizontally(
                                    animationSpec = spring(
                                        stiffness = Spring.StiffnessMediumLow,
                                        dampingRatio = Spring.DampingRatioLowBouncy
                                    ),
                                    initialOffsetX = { fullWidth -> fullWidth / 3 }
                                ) + fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) + scaleIn(
                                    initialScale = 0.96f,
                                    animationSpec = tween(280)
                                )).togetherWith(
                                    slideOutHorizontally(
                                        animationSpec = tween(240, easing = FastOutLinearInEasing),
                                        targetOffsetX = { fullWidth -> -fullWidth / 3 }
                                    ) + fadeOut(animationSpec = tween(200)) + scaleOut(
                                        targetScale = 0.96f,
                                        animationSpec = tween(200)
                                    )
                                )
                            } else {
                                (slideInHorizontally(
                                    animationSpec = spring(
                                        stiffness = Spring.StiffnessMediumLow,
                                        dampingRatio = Spring.DampingRatioLowBouncy
                                    ),
                                    initialOffsetX = { fullWidth -> -fullWidth / 3 }
                                ) + fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) + scaleIn(
                                    initialScale = 0.96f,
                                    animationSpec = tween(280)
                                )).togetherWith(
                                    slideOutHorizontally(
                                        animationSpec = tween(240, easing = FastOutLinearInEasing),
                                        targetOffsetX = { fullWidth -> fullWidth / 3 }
                                    ) + fadeOut(animationSpec = tween(200)) + scaleOut(
                                        targetScale = 0.96f,
                                        animationSpec = tween(200)
                                    )
                                )
                            }
                        },
                        label = "tab_transition"
                    ) { targetTab ->
                        when (targetTab) {
                            ChurchTab.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateTab = { viewModel.selectTab(it) }
                            )
                            ChurchTab.SCRIPTURE -> ScriptureScreen(
                                viewModel = viewModel
                            )
                            ChurchTab.DEVOTION -> DevotionScreen(
                                viewModel = viewModel
                            )
                            ChurchTab.JOURNAL -> JournalScreen(
                                viewModel = viewModel
                            )
                            ChurchTab.SERMONS -> PastorsScreen(
                                viewModel = viewModel
                            )
                            ChurchTab.COMMUNITY -> PrayerGroupsScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }

        // Top Toast Banner for user notifications and feedback
        ToastBanner(
            message = uiState.userToastMessage,
            onDismiss = { viewModel.clearToast() }
        )
    }
}

@Composable
fun IosBottomNavigationBar(
    selectedTab: ChurchTab,
    onTabSelected: (ChurchTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                title = "Today",
                icon = if (selectedTab == ChurchTab.HOME) CupertinoIcons.HouseFill else CupertinoIcons.House,
                isSelected = selectedTab == ChurchTab.HOME,
                onClick = { onTabSelected(ChurchTab.HOME) },
                testTag = "tab_home"
            )
            BottomNavItem(
                title = "Scripture",
                icon = CupertinoIcons.Book,
                isSelected = selectedTab == ChurchTab.SCRIPTURE,
                onClick = { onTabSelected(ChurchTab.SCRIPTURE) },
                testTag = "tab_scripture"
            )
            BottomNavItem(
                title = "Devotion",
                icon = if (selectedTab == ChurchTab.DEVOTION) CupertinoIcons.HeartFill else CupertinoIcons.Heart,
                isSelected = selectedTab == ChurchTab.DEVOTION,
                onClick = { onTabSelected(ChurchTab.DEVOTION) },
                testTag = "tab_devotion"
            )
            BottomNavItem(
                title = "Journal",
                icon = CupertinoIcons.SquareAndPencil,
                isSelected = selectedTab == ChurchTab.JOURNAL,
                onClick = { onTabSelected(ChurchTab.JOURNAL) },
                testTag = "tab_journal"
            )
            BottomNavItem(
                title = "Sermons",
                icon = CupertinoIcons.Headphones,
                isSelected = selectedTab == ChurchTab.SERMONS,
                onClick = { onTabSelected(ChurchTab.SERMONS) },
                testTag = "tab_sermons"
            )
            BottomNavItem(
                title = "Prayer",
                icon = if (selectedTab == ChurchTab.COMMUNITY) CupertinoIcons.Person2Fill else CupertinoIcons.Person2,
                isSelected = selectedTab == ChurchTab.COMMUNITY,
                onClick = { onTabSelected(ChurchTab.COMMUNITY) },
                testTag = "tab_community"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "nav_icon_scale"
    )
    val tint by animateColorAsState(
        targetValue = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
        animationSpec = tween(220),
        label = "nav_tint"
    )
    val pillBgColor by animateColorAsState(
        targetValue = if (isSelected) activeColor.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = tween(220),
        label = "nav_pill_bg"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(pillBgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier
                .size(22.dp)
                .scale(iconScale)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = tint,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

@Composable
fun MiniAudioPlayerBar(
    title: String,
    pastor: String,
    isPlaying: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("mini_audio_player_bar"),
        color = RoyalNavy,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ChurchGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CupertinoIcons.Sparkles,
                        contentDescription = null,
                        tint = RoyalNavy,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = pastor,
                        style = MaterialTheme.typography.labelSmall,
                        color = ChurchGoldLight,
                        maxLines = 1
                    )
                }
            }

            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) CupertinoIcons.PauseFill else CupertinoIcons.PlayFill,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }
        }
    }
}
