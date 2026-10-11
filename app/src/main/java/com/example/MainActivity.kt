package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppNavTab
import com.example.ui.PulseViewModel
import com.example.ui.components.ObjectionDialog
import com.example.ui.components.TransparencyDialog
import com.example.ui.components.VotingBottomSheet
import com.example.ui.screens.CompareScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MethodologyScreen
import com.example.ui.screens.RankingsScreen
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PublicPulseApp()
            }
        }
    }
}

@Composable
fun PublicPulseApp(
    viewModel: PulseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userNotification) {
        val notif = uiState.userNotification
        if (notif != null) {
            snackbarHostState.showSnackbar(notif)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.selectedDetailCelebrity == null) {
                NavigationBar(
                    containerColor = SlateCard,
                    contentColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .testTag("main_bottom_nav")
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = uiState.currentTab == AppNavTab.FEED,
                        onClick = { viewModel.onTabSelected(AppNavTab.FEED) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.currentTab == AppNavTab.FEED)
                                    Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                contentDescription = "Gündem"
                            )
                        },
                        label = { Text("Gündem", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PopularityCyan,
                            selectedTextColor = PopularityCyan,
                            indicatorColor = PopularityCyan.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_feed")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppNavTab.RANKINGS,
                        onClick = { viewModel.onTabSelected(AppNavTab.RANKINGS) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.currentTab == AppNavTab.RANKINGS)
                                    Icons.Filled.Leaderboard else Icons.Outlined.Leaderboard,
                                contentDescription = "Listeler"
                            )
                        },
                        label = { Text("Listeler", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LikeabilityPurple,
                            selectedTextColor = LikeabilityPurple,
                            indicatorColor = LikeabilityPurple.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_rankings")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppNavTab.COMPARE,
                        onClick = { viewModel.onTabSelected(AppNavTab.COMPARE) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.currentTab == AppNavTab.COMPARE)
                                    Icons.Filled.CompareArrows else Icons.Outlined.CompareArrows,
                                contentDescription = "Kıyasla"
                            )
                        },
                        label = { Text("Kıyasla", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PopularityCyan,
                            selectedTextColor = PopularityCyan,
                            indicatorColor = PopularityCyan.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_compare")
                    )

                    NavigationBarItem(
                        selected = uiState.currentTab == AppNavTab.METHODOLOGY,
                        onClick = { viewModel.onTabSelected(AppNavTab.METHODOLOGY) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.currentTab == AppNavTab.METHODOLOGY)
                                    Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                contentDescription = "Metodoloji"
                            )
                        },
                        label = { Text("Metodoloji", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF10B981),
                            selectedTextColor = Color(0xFF10B981),
                            indicatorColor = Color(0xFF10B981).copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_methodology")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main content switching
            if (uiState.selectedDetailCelebrity != null) {
                DetailScreen(
                    celebrity = uiState.selectedDetailCelebrity!!,
                    viewModel = viewModel
                )
            } else {
                when (uiState.currentTab) {
                    AppNavTab.FEED -> HomeScreen(uiState = uiState, viewModel = viewModel)
                    AppNavTab.RANKINGS -> RankingsScreen(uiState = uiState, viewModel = viewModel)
                    AppNavTab.COMPARE -> CompareScreen(uiState = uiState, viewModel = viewModel)
                    AppNavTab.METHODOLOGY -> MethodologyScreen()
                }
            }
        }
    }

    // Modal Overlays
    uiState.transparencyCelebrity?.let { celebrity ->
        TransparencyDialog(
            celebrity = celebrity,
            onDismiss = { viewModel.showTransparencyDialog(null) }
        )
    }

    uiState.votingCelebrity?.let { celebrity ->
        VotingBottomSheet(
            celebrity = celebrity,
            alreadyVoted = viewModel.hasUserVoted(celebrity.id),
            onDismiss = { viewModel.showVotingSheet(null) },
            onSubmitVote = { knows, rating, tags ->
                viewModel.submitVote(celebrity.id, knows, rating, tags)
            }
        )
    }

    uiState.objectionCelebrity?.let { celebrity ->
        ObjectionDialog(
            celebrity = celebrity,
            onDismiss = { viewModel.showObjectionDialog(null) },
            onSubmitObjection = { rep, subj, stmt ->
                viewModel.submitObjection(celebrity.id, rep, subj, stmt)
            }
        )
    }
}
