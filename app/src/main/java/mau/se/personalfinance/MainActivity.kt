package mau.se.personalfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mau.se.personalfinance.ui.components.FinanceBottomBar
import mau.se.personalfinance.ui.components.FinanceTopBar
import mau.se.personalfinance.ui.navigation.AppNavHost
import mau.se.personalfinance.ui.navigation.BottomNavItem
import mau.se.personalfinance.ui.theme.PersonalFinanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalFinanceTheme {
                FinanceApp()
            }
        }
    }
}

@Composable
fun FinanceApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val selectedTab = when (currentDestination?.route) {
        BottomNavItem.Income.route -> BottomNavItem.Income
        BottomNavItem.Outcome.route -> BottomNavItem.Outcome
        BottomNavItem.Balance.route -> BottomNavItem.Balance
        BottomNavItem.Chart.route -> BottomNavItem.Chart
        else -> null
    }

    Scaffold(
        topBar = {
            FinanceTopBar(
                isSettings = currentDestination?.route == "settings",
                onSettingsClick = { navController.navigate("settings") }
            )
        },
        bottomBar = {
            selectedTab?.let {
                FinanceBottomBar(selectedTab) { newTab ->
                    if (newTab.route != currentDestination?.route) {
                        navController.navigate(newTab.route) {
                            // Proper back stack management
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding
        )
    }
}

@Preview(showBackground = true, name = "App Preview")
@Composable
fun FinanceAppPreview() {
    PersonalFinanceTheme {
        FinanceApp()
    }
}