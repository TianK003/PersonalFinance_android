package mau.se.personalfinance.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mau.se.personalfinance.ui.screens.AddTransactionScreen
import mau.se.personalfinance.ui.screens.BalanceScreen
import mau.se.personalfinance.ui.screens.ChartScreen
import mau.se.personalfinance.ui.screens.IncomeScreen
import mau.se.personalfinance.ui.screens.OutcomeScreen
import mau.se.personalfinance.ui.screens.SettingsScreen
import mau.se.personalfinance.viewmodels.AddTransactionViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Balance.route,
        modifier = modifier.padding(innerPadding)
    ) {

        /* ---------- add / edit ---------- */
        composable("add_transaction/{type}") { backStackEntry ->
            val transactionType = backStackEntry.arguments?.getString("type") ?: ""
            val viewModel: AddTransactionViewModel = viewModel()
            AddTransactionScreen(
                transactionType = transactionType,
                onBack          = { navController.popBackStack() },
                viewModel       = viewModel
            )
        }

        /* ---------- bottom-nav ---------- */
        composable(BottomNavItem.Balance.route) { BalanceScreen() }

        composable(BottomNavItem.Income.route) {
            IncomeScreen(onAddClick = {
                navController.navigate("add_transaction/income")
            })
        }
        composable(BottomNavItem.Outcome.route) {
            OutcomeScreen(onAddClick = {
                navController.navigate("add_transaction/outcome")
            })
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(BottomNavItem.Chart.route)   { ChartScreen() }
    }
}