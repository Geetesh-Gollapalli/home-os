package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ShoppingItem
import com.example.ui.components.MomOSCard
import com.example.ui.components.MomOSCategoryChip
import com.example.ui.components.MomOSCheckbox
import com.example.ui.components.MomOSEmptyState
import com.example.ui.components.MomOSPrimaryButton
import com.example.ui.components.MomOSTextField
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.ShoppingViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShoppingScreen(
    viewModel: ShoppingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSearchVisible by remember { mutableStateOf(false) }

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
                .testTag("shopping_screen_list")
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Screen Header: "Shopping" with Search Icon and "+ Add item"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shopping",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isSearchVisible = !isSearchVisible },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("search_shopping_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        MomOSPrimaryButton(
                            text = "+ Add item",
                            onClick = { viewModel.openAddDialog() },
                            modifier = Modifier.testTag("add_shopping_item_button")
                        )
                    }
                }

                // Optional Search bar
                if (isSearchVisible) {
                    Spacer(modifier = Modifier.height(12.dp))
                    MomOSTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = "Search items...",
                        leadingIcon = Icons.Default.Search,
                        singleLine = true,
                        testTag = "shopping_search_input"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Chips Filter
                val categories = listOf("All", "Groceries", "Vegetables", "Household", "Personal", "Other")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        MomOSCategoryChip(
                            text = cat,
                            isSelected = (uiState.selectedCategory == cat),
                            onClick = { viewModel.selectCategory(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Active items
            if (uiState.activeItems.isEmpty() && uiState.completedItems.isEmpty()) {
                item {
                    MomOSEmptyState(
                        title = "Your shopping list is empty.",
                        subtitle = "Add items using the button above or ask Home OS.",
                        actionText = "+ Add item",
                        onActionClick = { viewModel.openAddDialog() }
                    )
                }
            } else {
                items(uiState.activeItems, key = { it.id }) { item ->
                    ShoppingItemRow(
                        item = item,
                        onToggle = { viewModel.toggleItem(item) },
                        onDelete = { viewModel.deleteItem(item) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Completed items section
                if (uiState.completedItems.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COMPLETED (${uiState.completedItems.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = { viewModel.clearCompleted() },
                                modifier = Modifier.testTag("clear_completed_button")
                            ) {
                                Text(
                                    text = "Clear completed",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(uiState.completedItems, key = { it.id }) { item ->
                        ShoppingItemRow(
                            item = item,
                            onToggle = { viewModel.toggleItem(item) },
                            onDelete = { viewModel.deleteItem(item) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Add Item Dialog
        if (uiState.isAddDialogOpen) {
            AddShoppingItemDialog(
                onDismiss = { viewModel.closeAddDialog() },
                onAdd = { title, qty, unit, cat ->
                    viewModel.addItem(title, qty, unit, cat)
                }
            )
        }
    }
}

@Composable
private fun ShoppingItemRow(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    MomOSCard(
        modifier = Modifier.fillMaxWidth(),
        testTag = "shopping_item_${item.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MomOSCheckbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggle() },
                testTag = "shopping_checkbox_${item.id}"
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                val quantityText = buildString {
                    if (item.quantity.isNotBlank() && item.quantity != "1") {
                        append("× ${item.quantity}")
                    }
                    if (item.unit.isNotBlank()) {
                        append(" ${item.unit}")
                    }
                    if (item.category.isNotBlank() && item.category != "Groceries") {
                        if (isNotEmpty()) append(" • ")
                        append(item.category)
                    }
                }
                if (quantityText.isNotBlank()) {
                    Text(
                        text = quantityText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("delete_shopping_item_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddShoppingItemDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, quantity: String, unit: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Groceries") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Item",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                MomOSTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Item name (e.g. Milk, Apples)",
                    label = "What to buy?",
                    singleLine = true,
                    testTag = "add_item_title_input"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MomOSTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        placeholder = "1",
                        label = "Quantity",
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        testTag = "add_item_qty_input"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    MomOSTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        placeholder = "packets, kg",
                        label = "Unit (optional)",
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        testTag = "add_item_unit_input"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                val categories = listOf("Groceries", "Vegetables", "Household", "Personal", "Other")
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, quantity, unit, selectedCategory)
                    }
                },
                modifier = Modifier.testTag("confirm_add_shopping_button")
            ) {
                Text(
                    text = "Add",
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
