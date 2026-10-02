package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpenseItem
import com.example.ui.components.MomOSCard
import com.example.ui.components.MomOSCategoryChip
import com.example.ui.components.MomOSEmptyState
import com.example.ui.components.MomOSLargeCard
import com.example.ui.components.MomOSPrimaryButton
import com.example.ui.components.MomOSTextField
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.CategoryBreakdown
import com.example.ui.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CategoryStyle(
    val color: Color,
    val icon: ImageVector
)

fun getCategoryStyle(category: String): CategoryStyle {
    return when (category.lowercase(Locale.ROOT)) {
        "food" -> CategoryStyle(Color(0xFFFF9800), Icons.Default.Restaurant)
        "groceries" -> CategoryStyle(Color(0xFF2E9B68), Icons.Default.ShoppingCart)
        "household" -> CategoryStyle(Color(0xFF5B5BD6), Icons.Default.Home)
        "travel" -> CategoryStyle(Color(0xFF0288D1), Icons.Default.DirectionsCar)
        "health" -> CategoryStyle(Color(0xFFE53935), Icons.Default.LocalHospital)
        else -> CategoryStyle(Color(0xFF78909C), Icons.Default.Receipt)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpensesScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .testTag("expenses_screen_list")
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("expenses_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Expenses",
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    MomOSPrimaryButton(
                        text = "+ Add",
                        onClick = { viewModel.openAddDialog() },
                        testTag = "add_expense_header_button"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Monthly Total Large Card with Stacked Expense Bar & Category Breakdown
                MomOSLargeCard(
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "expense_summary_card"
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "This month",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (uiState.categoryBreakdown.isNotEmpty()) {
                                Text(
                                    text = "${uiState.categoryBreakdown.size} categories",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.0f", uiState.totalThisMonth)}",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 34.sp),
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo
                        )

                        if (uiState.categoryBreakdown.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // 1. Multi-Segment Stacked Visual Bar
                            MultiSegmentExpenseBar(breakdowns = uiState.categoryBreakdown)

                            Spacer(modifier = Modifier.height(18.dp))

                            // 2. Color-coded Category Breakdown Rows
                            uiState.categoryBreakdown.forEach { breakdown ->
                                val isSelected = uiState.selectedCategoryFilter?.equals(breakdown.category, ignoreCase = true) == true
                                val style = getCategoryStyle(breakdown.category)

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.selectCategoryFilter(breakdown.category) }
                                        .padding(vertical = 6.dp, horizontal = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(style.color.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = style.icon,
                                                    contentDescription = breakdown.category,
                                                    tint = style.color,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Text(
                                                text = breakdown.category,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = style.color.copy(alpha = 0.12f)
                                            ) {
                                                Text(
                                                    text = "${(breakdown.percentage * 100).toInt()}%",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                    fontWeight = FontWeight.Bold,
                                                    color = style.color,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "₹${String.format(Locale.getDefault(), "%,.0f", breakdown.total)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Animated Category Progress Bar
                                    val animatedProgress by animateFloatAsState(
                                        targetValue = breakdown.percentage,
                                        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                                        label = "category_progress"
                                    )

                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = style.color,
                                        trackColor = style.color.copy(alpha = 0.12f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section Header & Active Filter Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.selectedCategoryFilter != null)
                            "FILTERED: ${uiState.selectedCategoryFilter?.uppercase(Locale.ROOT)}"
                        else
                            "RECENT EXPENSES",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (uiState.selectedCategoryFilter != null) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { viewModel.selectCategoryFilter(null) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Show All",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear filter",
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.recentExpenses.isEmpty()) {
                item {
                    MomOSEmptyState(
                        title = if (uiState.selectedCategoryFilter != null)
                            "No expenses in ${uiState.selectedCategoryFilter}."
                        else
                            "No expenses recorded yet.",
                        subtitle = "Track daily household spending with simple entries.",
                        actionText = "+ Add expense",
                        onActionClick = { viewModel.openAddDialog() }
                    )
                }
            } else {
                items(uiState.recentExpenses, key = { it.id }) { expense ->
                    ExpenseRow(
                        expense = expense,
                        onDelete = { viewModel.deleteExpense(expense) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Add Expense Dialog
        if (uiState.isAddDialogOpen) {
            AddExpenseDialog(
                onDismiss = { viewModel.closeAddDialog() },
                onAdd = { amount, category, desc ->
                    viewModel.addExpense(amount, category, desc)
                }
            )
        }
    }
}

/**
 * Multi-Segment Stacked Progress Bar showing unified monthly distribution
 */
@Composable
private fun MultiSegmentExpenseBar(
    breakdowns: List<CategoryBreakdown>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            breakdowns.forEach { breakdown ->
                if (breakdown.percentage > 0f) {
                    val style = getCategoryStyle(breakdown.category)
                    val animatedWeight by animateFloatAsState(
                        targetValue = breakdown.percentage,
                        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                        label = "bar_segment_weight"
                    )
                    Box(
                        modifier = Modifier
                            .weight(animatedWeight.coerceAtLeast(0.001f))
                            .fillMaxSize()
                            .background(style.color)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseRow(
    expense: ExpenseItem,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(expense.timestamp))
    val style = getCategoryStyle(expense.category)

    MomOSCard(
        modifier = Modifier.fillMaxWidth(),
        testTag = "expense_item_${expense.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(style.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = style.icon,
                    contentDescription = expense.category,
                    tint = style.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (expense.description.isNotBlank()) expense.description else expense.category,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${expense.category} • $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "₹${String.format(Locale.getDefault(), "%,.0f", expense.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = style.color
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("delete_expense_${expense.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onAdd: (amount: String, category: String, desc: String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Groceries") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("Food", "Groceries", "Household", "Travel", "Health", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Expense",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                MomOSTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    placeholder = "₹ Amount (e.g. 250)",
                    label = "Amount",
                    singleLine = true,
                    testTag = "expense_amount_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        MomOSCategoryChip(
                            text = cat,
                            isSelected = (selectedCategory == cat),
                            onClick = { selectedCategory = cat }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                MomOSTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "e.g. Vegetables from market",
                    label = "Description (optional)",
                    singleLine = true,
                    testTag = "expense_desc_input"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (amount.isNotBlank()) {
                        onAdd(amount, selectedCategory, description)
                    }
                },
                modifier = Modifier.testTag("confirm_add_expense_button")
            ) {
                Text(
                    text = "Save",
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryIndigo
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
