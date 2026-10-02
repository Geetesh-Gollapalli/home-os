package com.example.ai

sealed class VoiceCommandIntent {
    data class AddShoppingItem(
        val title: String,
        val quantity: String = "1",
        val unit: String = "",
        val category: String = "Groceries"
    ) : VoiceCommandIntent()

    data class CreateReminder(
        val title: String,
        val timeLabel: String = "",
        val isToday: Boolean = true
    ) : VoiceCommandIntent()

    data class AddExpense(
        val amount: Double,
        val category: String = "Groceries",
        val description: String = ""
    ) : VoiceCommandIntent()

    data object ViewSchedule : VoiceCommandIntent()

    data object ViewShoppingList : VoiceCommandIntent()

    data class CallContact(
        val contactName: String
    ) : VoiceCommandIntent()

    data class CreateNote(
        val title: String,
        val content: String
    ) : VoiceCommandIntent()

    data class Unknown(
        val rawText: String
    ) : VoiceCommandIntent()
}

data class CommandExecutionResult(
    val success: Boolean,
    val feedbackMessage: String,
    val requiresConfirmation: Boolean = false,
    val confirmationPrompt: String? = null,
    val intent: VoiceCommandIntent? = null
)
