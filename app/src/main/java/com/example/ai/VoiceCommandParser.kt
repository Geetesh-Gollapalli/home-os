package com.example.ai

import java.util.Locale

object VoiceCommandParser {

    /**
     * Interprets free-form natural voice or text into a structured VoiceCommandIntent.
     * Offline-first, fast, deterministic and safe.
     */
    fun parse(input: String): VoiceCommandIntent {
        val clean = input.trim().lowercase(Locale.ROOT)
        if (clean.isEmpty()) return VoiceCommandIntent.Unknown("")

        // 1. Check for Shopping View intent
        if (clean.contains("shopping") && (clean.contains("show") || clean.contains("open") || clean.contains("view") || clean.contains("see"))) {
            return VoiceCommandIntent.ViewShoppingList
        }

        // 2. Check for Schedule View intent
        if (clean.contains("what's on") || clean.contains("whats on") ||
            clean.contains("today's schedule") || clean.contains("my schedule") ||
            clean.contains("what do i have today") || clean.contains("what do i have tomorrow") ||
            clean.contains("view schedule") || clean.contains("show schedule")
        ) {
            return VoiceCommandIntent.ViewSchedule
        }

        // 3. Check for Call intent ("call Geetesh", "phone Dad")
        if (clean.startsWith("call ") || clean.startsWith("phone ") || clean.startsWith("ring ")) {
            val contactName = clean.replaceFirst(Regex("^(call|phone|ring)\\s+"), "")
                .replace("please", "")
                .trim()
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase(Locale.ROOT) } }
            if (contactName.isNotEmpty()) {
                return VoiceCommandIntent.CallContact(contactName)
            }
        }

        // 4. Check for Reminder / Task intent ("remind me to...", "remember to...")
        val reminderMatch = extractReminder(clean)
        if (reminderMatch != null) {
            return reminderMatch
        }

        // 5. Check for Shopping Add intent ("add milk to shopping list", "buy 2 kg sugar", "add apples")
        val shoppingMatch = extractShoppingItem(clean)
        if (shoppingMatch != null) {
            return shoppingMatch
        }

        // 6. Check for Expense intent ("spent 250 on vegetables", "add ₹350 for groceries", "expense 100")
        val expenseMatch = extractExpense(clean)
        if (expenseMatch != null) {
            return expenseMatch
        }

        // 7. Check for Note intent ("note: doctor prescription", "save note ...")
        if (clean.startsWith("note:") || clean.startsWith("note ") || clean.startsWith("write note ") || clean.startsWith("take note ")) {
            val content = clean.replaceFirst(Regex("^(note:|note|write note|take note)\\s*"), "")
                .trim()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            if (content.isNotEmpty()) {
                val title = if (content.length > 30) content.take(30) + "..." else content
                return VoiceCommandIntent.CreateNote(title, content)
            }
        }

        return VoiceCommandIntent.Unknown(input)
    }

    private fun extractExpense(text: String): VoiceCommandIntent.AddExpense? {
        // Must clearly indicate an expense via keywords or currency
        val isExplicitExpense = text.startsWith("spent") || text.startsWith("i spent") ||
            text.startsWith("paid") || text.startsWith("cost") || text.startsWith("expense") ||
            text.contains("₹") || text.contains("rs") || text.contains("rupees")

        if (!isExplicitExpense) return null

        val regex = Regex("(?:spent|add|paid|cost|expense)?\\s*(?:rs\\.?|inr|₹|rupees)?\\s*([0-9]+(?:\\.[0-9]+)?)\\s*(?:rs\\.?|inr|₹|rupees)?\\s*(?:on|for|in)?\\s*([a-zA-Z\\s]+)?")
        val match = regex.find(text)
        if (match != null) {
            val amountStr = match.groupValues[1]
            val rawCategory = match.groupValues.getOrNull(2)?.trim() ?: ""
            val amount = amountStr.toDoubleOrNull() ?: return null

            val category = when {
                rawCategory.contains("veg") || rawCategory.contains("fruit") -> "Food"
                rawCategory.contains("food") || rawCategory.contains("dinner") || rawCategory.contains("lunch") -> "Food"
                rawCategory.contains("grocery") || rawCategory.contains("groceries") || rawCategory.contains("milk") -> "Groceries"
                rawCategory.contains("medicine") || rawCategory.contains("doctor") || rawCategory.contains("health") -> "Health"
                rawCategory.contains("cab") || rawCategory.contains("auto") || rawCategory.contains("petrol") || rawCategory.contains("travel") -> "Travel"
                rawCategory.contains("cleaning") || rawCategory.contains("soap") || rawCategory.contains("detergent") || rawCategory.contains("house") -> "Household"
                else -> if (rawCategory.isNotEmpty()) rawCategory.replaceFirstChar { it.titlecase(Locale.ROOT) } else "Other"
            }

            val description = if (rawCategory.isNotEmpty()) {
                rawCategory.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            } else "General expense"

            return VoiceCommandIntent.AddExpense(amount, category, description)
        }
        return null
    }

    private fun extractReminder(text: String): VoiceCommandIntent.CreateReminder? {
        // e.g. "remind me to call Dad at 6", "remind me tomorrow to take medicine"
        if (text.contains("remind me") || text.startsWith("reminder") || text.startsWith("remember to")) {
            var body = text.replaceFirst(Regex("^(remind me to|remind me|reminder to|reminder|remember to)\\s+"), "")
            var timeLabel = ""
            val isToday = !body.contains("tomorrow")

            // Look for time indication like "at 7", "at 5:30 pm", "at 10 am", "in the evening"
            val timeRegex = Regex("\\s+at\\s+([0-9]{1,2}(?::[0-9]{2})?\\s*(?:am|pm)?)")
            val timeMatch = timeRegex.find(body)
            if (timeMatch != null) {
                var rawTime = timeMatch.groupValues[1].trim()
                if (!rawTime.contains("am") && !rawTime.contains("pm")) {
                    val hour = rawTime.toIntOrNull() ?: 12
                    rawTime = if (hour in 1..7) "$hour:00 PM" else "$hour:00 AM"
                }
                timeLabel = rawTime.uppercase(Locale.ROOT)
                body = body.replace(timeRegex, "")
            } else if (body.contains("evening")) {
                timeLabel = "6:00 PM"
                body = body.replace("this evening", "").replace("in the evening", "")
            } else if (body.contains("morning")) {
                timeLabel = "9:00 AM"
                body = body.replace("tomorrow morning", "").replace("this morning", "")
            } else if (body.contains("afternoon")) {
                timeLabel = "2:00 PM"
                body = body.replace("this afternoon", "")
            }

            body = body.replace("tomorrow", "").replace("today", "").trim()
            val title = body.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase(Locale.ROOT) } }
            return VoiceCommandIntent.CreateReminder(
                title = if (title.isNotEmpty()) title else "Reminder",
                timeLabel = if (timeLabel.isNotEmpty()) timeLabel else "5:00 PM",
                isToday = isToday
            )
        }
        return null
    }

    private fun extractShoppingItem(text: String): VoiceCommandIntent.AddShoppingItem? {
        // e.g. "add milk", "add 2 packets of milk to shopping list", "buy tomatoes", "need rice"
        val prefixRegex = Regex("^(add|buy|get|need)\\s+")
        if (prefixRegex.containsMatchIn(text) || text.contains("shopping list") || text.contains("grocery")) {
            var cleaned = text.replaceFirst(prefixRegex, "")
                .replace("to my shopping list", "")
                .replace("to shopping list", "")
                .replace("in shopping list", "")
                .replace("to list", "")
                .trim()

            if (cleaned.isEmpty()) return null

            // Detect quantity and unit e.g. "2 packets of milk", "4 tomatoes", "5 kg rice"
            var qty = "1"
            var unit = ""
            val qtyRegex = Regex("^([0-9]+)\\s*([a-zA-Z]+)?\\s*(?:of\\s+)?(.*)")
            val match = qtyRegex.find(cleaned)

            val itemTitle: String
            if (match != null) {
                qty = match.groupValues[1]
                val possibleUnit = match.groupValues[2].trim()
                val knownUnits = listOf("packet", "packets", "kg", "g", "liter", "liters", "ltr", "bottle", "bottles", "pack", "pcs", "piece", "pieces")
                if (knownUnits.contains(possibleUnit)) {
                    unit = possibleUnit
                    itemTitle = match.groupValues[3].trim()
                } else if (possibleUnit.isNotEmpty()) {
                    itemTitle = "$possibleUnit ${match.groupValues[3]}".trim()
                } else {
                    itemTitle = match.groupValues[3].trim()
                }
            } else {
                itemTitle = cleaned
            }

            val category = when {
                itemTitle.contains("tomato") || itemTitle.contains("potato") || itemTitle.contains("onion") ||
                    itemTitle.contains("spinach") || itemTitle.contains("vegetable") || itemTitle.contains("carrot") -> "Vegetables"
                itemTitle.contains("soap") || itemTitle.contains("detergent") || itemTitle.contains("paper") ||
                    itemTitle.contains("cleaner") || itemTitle.contains("brush") -> "Household"
                itemTitle.contains("shampoo") || itemTitle.contains("cream") || itemTitle.contains("lotion") -> "Personal"
                else -> "Groceries"
            }

            val formattedTitle = itemTitle.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase(Locale.ROOT) } }
            return VoiceCommandIntent.AddShoppingItem(
                title = formattedTitle,
                quantity = qty,
                unit = unit,
                category = category
            )
        }
        return null
    }
}
