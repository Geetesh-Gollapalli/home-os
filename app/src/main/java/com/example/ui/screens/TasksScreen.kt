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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.TaskItem
import com.example.ui.components.MomOSCard
import com.example.ui.components.MomOSCategoryChip
import com.example.ui.components.MomOSCheckbox
import com.example.ui.components.MomOSEmptyState
import com.example.ui.components.MomOSPrimaryButton
import com.example.ui.components.MomOSTextField
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasksScreen(
    viewModel: TaskViewModel
) {
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
                .testTag("tasks_screen_list")
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    MomOSPrimaryButton(
                        text = "+ Add task",
                        onClick = { viewModel.openAddDialog() },
                        testTag = "add_task_header_button"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Empty state check
            if (uiState.todayTasks.isEmpty() && uiState.upcomingTasks.isEmpty() && uiState.completedTasks.isEmpty()) {
                item {
                    MomOSEmptyState(
                        title = "You're all caught up.",
                        subtitle = "No tasks or reminders. Tap below to create one.",
                        actionText = "+ Add task",
                        onActionClick = { viewModel.openAddDialog() }
                    )
                }
            } else {
                // TODAY SECTION
                if (uiState.todayTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "TODAY (${uiState.todayTasks.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(uiState.todayTasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // UPCOMING SECTION
                if (uiState.upcomingTasks.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "UPCOMING (${uiState.upcomingTasks.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(uiState.upcomingTasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // COMPLETED SECTION
                if (uiState.completedTasks.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "COMPLETED (${uiState.completedTasks.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(uiState.completedTasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Add Reminder Dialog
        if (uiState.isAddDialogOpen) {
            AddReminderDialog(
                onDismiss = { viewModel.closeAddDialog() },
                onSave = { title, preset, exactTime, repeat, note ->
                    viewModel.addTask(title, preset, exactTime, repeat, note)
                }
            )
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    MomOSCard(
        modifier = Modifier.fillMaxWidth(),
        testTag = "task_row_${task.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MomOSCheckbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                testTag = "task_checkbox_${task.id}"
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (task.timeLabel.isNotBlank()) {
                        Text(
                            text = task.timeLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (task.isUrgent) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (task.repeatOption.isNotBlank() && task.repeatOption != "None") {
                        Text(
                            text = " • Repeats ${task.repeatOption.lowercase()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (task.note.isNotBlank()) {
                    Text(
                        text = task.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("delete_task_${task.id}")
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
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, preset: String, exactTime: String, repeat: String, note: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf("Later today") }
    var exactTime by remember { mutableStateOf("") }
    var selectedRepeat by remember { mutableStateOf("None") }
    var note by remember { mutableStateOf("") }

    val presets = listOf("Later today", "This evening", "Tomorrow", "Next week", "Custom")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Reminder",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                MomOSTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "e.g. Call Dad, Buy medicine",
                    label = "What?",
                    singleLine = true,
                    testTag = "task_what_input"
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "When?",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.forEach { preset ->
                        MomOSCategoryChip(
                            text = preset,
                            isSelected = (selectedPreset == preset),
                            onClick = { selectedPreset = preset }
                        )
                    }
                }

                if (selectedPreset == "Custom") {
                    Spacer(modifier = Modifier.height(10.dp))
                    MomOSTextField(
                        value = exactTime,
                        onValueChange = { exactTime = it },
                        placeholder = "e.g. 5:30 PM",
                        label = "Exact time",
                        singleLine = true,
                        testTag = "task_exact_time_input"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                MomOSTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = "Optional extra details",
                    label = "Note (optional)",
                    singleLine = true,
                    testTag = "task_note_input"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, selectedPreset, exactTime, selectedRepeat, note)
                    }
                },
                modifier = Modifier.testTag("save_reminder_button")
            ) {
                Text(
                    text = "Save reminder",
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
