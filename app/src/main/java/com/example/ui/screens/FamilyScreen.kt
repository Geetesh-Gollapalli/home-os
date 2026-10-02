package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.FamilyContact
import com.example.ui.components.MomOSCard
import com.example.ui.components.MomOSEmptyState
import com.example.ui.components.MomOSPrimaryButton
import com.example.ui.components.MomOSTextField
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.FamilyViewModel
import com.example.util.CommunicationHelper

@Composable
fun FamilyScreen(
    viewModel: FamilyViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedContactForWa by remember { mutableStateOf<FamilyContact?>(null) }
    var customWaMessage by remember { mutableStateOf("") }

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
                .testTag("family_screen_list")
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Family & Friends",
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "1-tap phone calls and WhatsApp",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    MomOSPrimaryButton(
                        text = "+ Add",
                        onClick = { viewModel.openAddDialog() },
                        testTag = "add_family_member_button"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (uiState.contacts.isEmpty()) {
                item {
                    MomOSEmptyState(
                        title = "No family contacts added yet.",
                        subtitle = "Add your loved ones for one-tap calling and WhatsApp.",
                        actionText = "+ Add member",
                        onActionClick = { viewModel.openAddDialog() }
                    )
                }
            } else {
                items(uiState.contacts, key = { it.id }) { contact ->
                    FamilyContactCard(
                        contact = contact,
                        onCall = {
                            CommunicationHelper.dialPhoneNumber(context, contact.phoneNumber)
                        },
                        onWhatsApp = {
                            selectedContactForWa = contact
                            customWaMessage = "Hello ${contact.name}! How are you?"
                        },
                        onMessage = {
                            CommunicationHelper.sendSmsMessage(context, contact.phoneNumber)
                        },
                        onDelete = { viewModel.deleteContact(contact) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // WhatsApp Quick Message Dialog
        val waContact = selectedContactForWa
        if (waContact != null) {
            AlertDialog(
                onDismissRequest = { selectedContactForWa = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_whatsapp),
                            contentDescription = "WhatsApp",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WhatsApp ${waContact.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Send a message via WhatsApp to ${waContact.phoneNumber}:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        MomOSTextField(
                            value = customWaMessage,
                            onValueChange = { customWaMessage = it },
                            placeholder = "Type your message...",
                            label = "Message",
                            singleLine = false,
                            testTag = "whatsapp_message_input"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Quick options:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Please call me", "At home now", "Love you!").forEach { quickMsg ->
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { customWaMessage = quickMsg }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = quickMsg,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val msgToSend = customWaMessage
                            val targetPhone = waContact.phoneNumber
                            selectedContactForWa = null
                            CommunicationHelper.openWhatsAppChat(context, targetPhone, msgToSend)
                        },
                        modifier = Modifier.testTag("send_whatsapp_button")
                    ) {
                        Text(
                            text = "Open WhatsApp",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF25D366)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedContactForWa = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Add Family Member Dialog
        if (uiState.isAddDialogOpen) {
            AddFamilyDialog(
                onDismiss = { viewModel.closeAddDialog() },
                onAdd = { name, relationship, phone ->
                    viewModel.addContact(name, relationship, phone)
                }
            )
        }
    }
}

@Composable
private fun FamilyContactCard(
    contact: FamilyContact,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onMessage: () -> Unit,
    onDelete: () -> Unit
) {
    MomOSCard(
        modifier = Modifier.fillMaxWidth(),
        testTag = "family_card_${contact.id}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Avatar with Initials
            val avatarColor = try {
                Color(android.graphics.Color.parseColor(contact.avatarColorHex))
            } catch (_: Exception) {
                PrimaryIndigo
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = contact.relationship,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Quick Call Button (Real Phone)
            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen.copy(alpha = 0.12f))
                    .testTag("call_contact_${contact.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call ${contact.name}",
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Quick WhatsApp Button (Real WhatsApp)
            IconButton(
                onClick = onWhatsApp,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF25D366).copy(alpha = 0.12f))
                    .testTag("whatsapp_contact_${contact.id}")
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_whatsapp),
                    contentDescription = "WhatsApp ${contact.name}",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Quick SMS Button (Real SMS)
            IconButton(
                onClick = onMessage,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryIndigo.copy(alpha = 0.12f))
                    .testTag("message_contact_${contact.id}")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Message,
                    contentDescription = "SMS ${contact.name}",
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Delete
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("delete_contact_${contact.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AddFamilyDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, relationship: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Family Member",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                MomOSTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "e.g. Geetesh, Dad",
                    label = "Name",
                    singleLine = true,
                    testTag = "family_name_input"
                )

                Spacer(modifier = Modifier.height(12.dp))

                MomOSTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    placeholder = "e.g. Son, Husband, Sister",
                    label = "Relationship",
                    singleLine = true,
                    testTag = "family_relationship_input"
                )

                Spacer(modifier = Modifier.height(12.dp))

                MomOSTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "e.g. +91 98765 43210",
                    label = "Phone Number",
                    singleLine = true,
                    testTag = "family_phone_input"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onAdd(name, relationship, phone)
                    }
                },
                modifier = Modifier.testTag("confirm_add_family_button")
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
