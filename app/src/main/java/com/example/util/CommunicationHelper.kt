package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object CommunicationHelper {

    /**
     * Clean phone number for tel/sms/whatsapp
     */
    fun sanitizePhoneNumber(rawPhone: String): String {
        return rawPhone.replace(Regex("[^0-9+]"), "").trim()
    }

    /**
     * Connect to real phone dialer with phone number
     */
    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        val cleanNumber = sanitizePhoneNumber(phoneNumber)
        if (cleanNumber.isEmpty()) {
            Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open phone dialer: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Connect to real SMS / messaging app
     */
    fun sendSmsMessage(context: Context, phoneNumber: String, message: String = "") {
        val cleanNumber = sanitizePhoneNumber(phoneNumber)
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$cleanNumber")
                if (message.isNotBlank()) {
                    putExtra("sms_body", message)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:$cleanNumber")
                    if (message.isNotBlank()) {
                        putExtra("sms_body", message)
                    }
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Could not open messaging app: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Connect to real WhatsApp for chat or pre-filled message
     */
    fun openWhatsAppChat(context: Context, phoneNumber: String, message: String = "") {
        var cleanNumber = sanitizePhoneNumber(phoneNumber)
        // Remove leading '+' for WhatsApp api url
        if (cleanNumber.startsWith("+")) {
            cleanNumber = cleanNumber.substring(1)
        }

        val encodedMessage = Uri.encode(message)
        val url = if (cleanNumber.isNotEmpty()) {
            if (encodedMessage.isNotBlank()) {
                "https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage"
            } else {
                "https://api.whatsapp.com/send?phone=$cleanNumber"
            }
        } else {
            "https://api.whatsapp.com/send?text=$encodedMessage"
        }

        val targetUri = Uri.parse(url)

        // First attempt: Direct WhatsApp package
        try {
            val waIntent = Intent(Intent.ACTION_VIEW, targetUri).apply {
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(waIntent)
            return
        } catch (_: Exception) {
            // WhatsApp main app not found or failed, try WhatsApp Business
        }

        try {
            val waBusinessIntent = Intent(Intent.ACTION_VIEW, targetUri).apply {
                setPackage("com.whatsapp.w4b")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(waBusinessIntent)
            return
        } catch (_: Exception) {
            // Try generic browser/handler
        }

        try {
            val webIntent = Intent(Intent.ACTION_VIEW, targetUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "WhatsApp is not installed on this device.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
