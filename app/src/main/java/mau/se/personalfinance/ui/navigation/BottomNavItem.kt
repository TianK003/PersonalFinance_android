package mau.se.personalfinance.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import mau.se.personalfinance.R

enum class BottomNavItem(
    val route: String,
    val iconRes: Int,
    val contentDescription: String
) {
    Balance("balance", R.drawable.ic_balance, "Balance"),
    Income("income", R.drawable.ic_income, "Income"),
    Outcome("outcome", R.drawable.ic_outcome, "Outcome"),
    Chart("chart", R.drawable.ic_chart, "Chart");

    companion object {
        val items: List<BottomNavItem> = entries.toList()
    }
}