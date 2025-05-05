package mau.se.personalfinance.ui.components
import mau.se.personalfinance.R
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "salary" -> ImageVector.vectorResource(R.drawable.ic_salary)
        "food" -> ImageVector.vectorResource(R.drawable.ic_food)
        "leisure" -> ImageVector.vectorResource(R.drawable.ic_leisure)
        "travel" -> ImageVector.vectorResource(R.drawable.ic_travel)
        "accommodation" -> ImageVector.vectorResource(R.drawable.ic_accommodation)
        else -> ImageVector.vectorResource(R.drawable.ic_other)
    }
}

@Composable
fun TransactionRow(
    description: String,
    amount: Double,
    isIncome: Boolean,
    date: Date,
    category: String,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false,
    onDelete: () -> Unit = {}
) {
    var isExpanded by rememberSaveable { mutableStateOf(initialExpanded) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = getCategoryIcon(category),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isIncome) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = ( (if (isIncome) "+" else "-") + "%.2f€".format(amount)),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isIncome)
                        MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.error
                )
            }
            if (isExpanded) {
                val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            "Date: ${sdf.format(date)}",
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text("Category: $category")
                    }

                    // Delete button with confirmation
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd)
                            .clickable { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_delete),
                            contentDescription = "Delete transaction",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Confirm Delete") },
                    text = { Text("Are you sure you want to delete this transaction? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                onDelete()
                            }
                        ) { Text("Delete") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) { Text("Cancel") }
                    }
                )
            }
        }
    }
}
