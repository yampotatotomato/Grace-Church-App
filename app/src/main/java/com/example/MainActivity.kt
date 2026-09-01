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
            "devotional" -> {
                viewModel.completeOnboarding()
                viewModel.selectTab(ChurchTab.DEVOTION)
            }
            "profile" -> {
                viewModel.completeOnboarding()
                viewModel.selectTab(ChurchTab.PROFILE)
            }
            "pastors" -> {
                viewModel.completeOnboarding()
                viewModel.selectTab(ChurchTab.SERMONS)
            }
            "companion" -> {
                viewModel.completeOnboarding()
                viewModel.selectTab(ChurchTab.COMPANION)
            }
            "announcements" -> {
                viewModel.completeOnboarding()
                viewModel.selectTab(ChurchTab.HOME)
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
        if (!uiState.isOnboardingCompleted || uiState.isOnboardingReviewOpen) {
            WelcomeCarouselScreen(
                viewModel = viewModel,
                isReviewMode = uiState.isOnboardingReviewOpen,
                onGetStarted = { targetTab ->
                    viewModel.completeOnboarding(targetTab)
                },
                onDismissReview = {
                    viewModel.closeOnboardingReview()
                }
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
                            ChurchTab.PROFILE -> ProfileScreen(
                                viewModel = viewModel,
                                onNavigateToScripture = { viewModel.selectTab(ChurchTab.SCRIPTURE) },
                                onNavigateToDevotional = { viewModel.selectTab(ChurchTab.DEVOTION) },
                                onNavigateToSettings = { viewModel.openNotificationSettings() },
                                onNavigateTab = { viewModel.selectTab(it) }
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
                            ChurchTab.COMPANION -> CompanionScreen(
                                viewModel = viewModel,
                                onNavigateTab = { viewModel.selectTab(it) }
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
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isHome = selectedTab == ChurchTab.HOME
            val isScripture = selectedTab == ChurchTab.SCRIPTURE
            val isDevotion = selectedTab == ChurchTab.DEVOTION || selectedTab == ChurchTab.JOURNAL
            val isCommunity = selectedTab == ChurchTab.COMMUNITY || selectedTab == ChurchTab.SERMONS
            val isProfile = selectedTab == ChurchTab.PROFILE || selectedTab == ChurchTab.COMPANION

            BottomNavItem(
                title = "Today",
                icon = if (isHome) CupertinoIcons.HouseFill else CupertinoIcons.House,
                isSelected = isHome,
                onClick = { onTabSelected(ChurchTab.HOME) },
                testTag = "tab_home",
                modifier = Modifier.weight(1f)
            )
            BottomNavItem(
                title = "Bible",
                icon = CupertinoIcons.Book,
                isSelected = isScripture,
                onClick = { onTabSelected(ChurchTab.SCRIPTURE) },
                testTag = "tab_scripture",
                modifier = Modifier.weight(1f)
            )
            BottomNavItem(
                title = "Devotion",
                icon = if (isDevotion) CupertinoIcons.HeartFill else CupertinoIcons.Heart,
                isSelected = isDevotion,
                onClick = { onTabSelected(ChurchTab.DEVOTION) },
                testTag = "tab_devotion",
                modifier = Modifier.weight(1f)
            )
            BottomNavItem(
                title = "Community",
                icon = if (isCommunity) CupertinoIcons.Person2Fill else CupertinoIcons.Person2,
                isSelected = isCommunity,
                onClick = { onTabSelected(ChurchTab.COMMUNITY) },
                testTag = "tab_community",
                modifier = Modifier.weight(1f)
            )
            BottomNavItem(
                title = "Profile",
                icon = if (isProfile) CupertinoIcons.PersonFill else CupertinoIcons.Person,
                isSelected = isProfile,
                onClick = { onTabSelected(ChurchTab.PROFILE) },
                testTag = "tab_profile",
                modifier = Modifier.weight(1f)
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
    testTag: String,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "nav_icon_scale"
    )
    val tint by animateColorAsState(
        targetValue = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "nav_tint"
    )
    val pillBgColor by animateColorAsState(
        targetValue = if (isSelected) activeColor.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = tween(200),
        label = "nav_pill_bg"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(14.dp))
            .background(pillBgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp)
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
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
