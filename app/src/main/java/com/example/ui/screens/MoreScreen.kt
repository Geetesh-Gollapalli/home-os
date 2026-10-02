package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.MomOSCard
import com.example.ui.components.MomOSPrimaryButton
import com.example.ui.components.MomOSTextField
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.SettingsViewModel

@Composable
fun MoreScreen(
    viewModel: SettingsViewModel,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isEditNameOpen by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(uiState.userProfile.name) }
    var isHelpDialogOpen by remember { mutableStateOf(false) }

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
                .testTag("more_screen_list")
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "More",
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Profile Header Card
                MomOSCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        tempName = uiState.userProfile.name
                        isEditNameOpen = true
                    },
                    testTag = "edit_name_card"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PrimaryIndigo),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.userProfile.name.take(1).ifBlank { "U" }.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.userProfile.name.ifBlank { "User" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap to change display name",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Edit name",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: FAMILY SYNC & CONNECT DEVICES
                Text(
                    text = "FAMILY SYNC & CONNECT DEVICES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                MomOSCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        MoreItemRow(
                            icon = Icons.Default.Share,
                            title = "Connect Family Devices",
                            subtitle = "Sync Shopping List & Tasks in real time with family",
                            onClick = { viewModel.openConnectDevicesDialog() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: UTILITIES
                Text(
                    text = "UTILITIES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                MomOSCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        MoreItemRow(
                            icon = Icons.Default.PhoneAndroid,
                            title = "Home Screen Widgets",
                            subtitle = "Speed dial & quick action shortcuts for your phone screen",
                            onClick = { onNavigate("widgets") }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        MoreItemRow(
                            icon = Icons.Default.Description,
                            title = "Notes & Prescriptions",
                            subtitle = "Simple personal notes and recipes",
                            onClick = { onNavigate("notes") }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        MoreItemRow(
                            icon = Icons.Default.AttachMoney,
                            title = "Expenses & Spending",
                            subtitle = "Monthly budget summary and entries",
                            onClick = { onNavigate("expenses") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: PREFERENCES & SETTINGS
                Text(
                    text = "PREFERENCES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                MomOSCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        // Dark Theme Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Appearance",
                                        tint = PrimaryIndigo,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Dark Mode",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (uiState.userProfile.isDarkMode) "Enabled" else "Standard Light Theme",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = uiState.userProfile.isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryIndigo
                                ),
                                modifier = Modifier.testTag("dark_mode_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Notifications Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Reminders",
                                        tint = PrimaryIndigo,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Task Reminders",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (uiState.userProfile.notificationsEnabled) "Active" else "Muted",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = uiState.userProfile.notificationsEnabled,
                                onCheckedChange = { viewModel.toggleNotifications(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryIndigo
                                ),
                                modifier = Modifier.testTag("notifications_switch")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: DATA & STORAGE
                Text(
                    text = "DATA & BACKUP",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                MomOSCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        MoreItemRow(
                            icon = Icons.Default.Backup,
                            title = "Export Local Backup",
                            subtitle = "Save a secure copy of your lists, tasks & notes",
                            onClick = { viewModel.generateBackup() }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        MoreItemRow(
                            icon = Icons.Default.DeleteForever,
                            title = "Reset All Data",
                            subtitle = "Clear lists, tasks and restore clean state",
                            tint = ErrorRed,
                            onClick = { viewModel.openClearDataDialog() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: ABOUT
                Text(
                    text = "ABOUT",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                MomOSCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        MoreItemRow(
                            icon = Icons.Default.WavingHand,
                            title = "Welcome Tour & Setup",
                            subtitle = "Revisit the initial setup and features walkthrough",
                            onClick = { onNavigate("onboarding") }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        MoreItemRow(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            title = "How to use Home OS",
                            subtitle = "Everyday voice commands and simple guide",
                            onClick = { isHelpDialogOpen = true }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "About",
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Home OS v1.0",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Personal Family Operating System",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Signature
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Build by Geetesh with '❤️'",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Edit Name Dialog
        if (isEditNameOpen) {
            AlertDialog(
                onDismissRequest = { isEditNameOpen = false },
                title = { Text("What is your name?") },
                text = {
                    MomOSTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        placeholder = "Name",
                        singleLine = true,
                        testTag = "user_name_input"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                viewModel.updateUserName(tempName)
                                isEditNameOpen = false
                            }
                        }
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isEditNameOpen = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Connect Family Devices Dialog
        if (uiState.isConnectDevicesDialogOpen) {
            var inputCode by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { viewModel.closeConnectDevicesDialog() },
                title = {
                    Text(
                        text = "Connect Family Devices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Connect another phone so everyone shares the exact same Shopping List & Tasks in real time.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Box showing current Family Join Code
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimaryIndigo.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "YOUR FAMILY GROUP CODE",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = uiState.familyGroupCode,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Share this code with family members to connect their app.",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Or join an existing Family Code:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MomOSTextField(
                                value = inputCode,
                                onValueChange = { inputCode = it },
                                placeholder = "e.g. HOME-9821",
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                testTag = "join_family_code_input"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            MomOSPrimaryButton(
                                text = "Join",
                                onClick = {
                                    if (inputCode.isNotBlank()) {
                                        viewModel.joinFamilyGroup(inputCode)
                                        inputCode = ""
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Connected Family Members:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Real User's Device (Host)
                        val realUserName = uiState.userProfile.name.ifBlank { "User" }
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• ", style = MaterialTheme.typography.bodyMedium, color = SuccessGreen)
                            Text("$realUserName (This Device - Host)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }

                        // Other Joined Family Devices (if any)
                        if (uiState.otherConnectedDevices.isNotEmpty()) {
                            uiState.otherConnectedDevices.forEach { device ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("• ", style = MaterialTheme.typography.bodyMedium, color = SuccessGreen)
                                    Text(device, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No other devices connected yet. Share code ${uiState.familyGroupCode} with family members to connect.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.closeConnectDevicesDialog() }) {
                        Text("Done", fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    }
                }
            )
        }

        // Help Dialog
        if (isHelpDialogOpen) {
            AlertDialog(
                onDismissRequest = { isHelpDialogOpen = false },
                title = {
                    Text(
                        text = "Voice Commands Guide",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Column {
                        Text("Home OS understands natural language everyday requests:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• \"Add milk to my shopping list\"", style = MaterialTheme.typography.bodySmall)
                        Text("• \"Remind me to call Dad at 6\"", style = MaterialTheme.typography.bodySmall)
                        Text("• \"I spent 250 on vegetables\"", style = MaterialTheme.typography.bodySmall)
                        Text("• \"What's on today?\"", style = MaterialTheme.typography.bodySmall)
                        Text("• \"Call Geetesh\"", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All core features work 100% offline securely on your device.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { isHelpDialogOpen = false }) {
                        Text("Got it", fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    }
                }
            )
        }

        // Export Dialog
        if (uiState.isExportDialogOpen) {
            AlertDialog(
                onDismissRequest = { viewModel.closeExportDialog() },
                title = { Text("Home OS Local Backup") },
                text = {
                    Text("Your Home OS data (Shopping items, Tasks, Reminders, Family Contacts, and Notes) is safely stored in encrypted Room SQLite storage on this device.")
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.closeExportDialog() }) {
                        Text("Close", color = PrimaryIndigo)
                    }
                }
            )
        }

        // Clear Data Dialog
        if (uiState.isClearDataDialogOpen) {
            AlertDialog(
                onDismissRequest = { viewModel.closeClearDataDialog() },
                title = { Text("Reset all Home OS data?") },
                text = {
                    Text("This will remove all current tasks, shopping items, and notes from this device.")
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearAllData() }) {
                        Text("Reset", color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeClearDataDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun MoreItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color = PrimaryIndigo
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
        )
    }
}
