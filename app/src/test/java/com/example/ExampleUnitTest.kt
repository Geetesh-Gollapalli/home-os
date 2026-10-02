package com.example

import com.example.ai.VoiceCommandIntent
import com.example.ai.VoiceCommandParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testShoppingIntentParsing() {
    val result = VoiceCommandParser.parse("Add 2 packets of milk to my shopping list")
    assertTrue(result is VoiceCommandIntent.AddShoppingItem)
    val item = result as VoiceCommandIntent.AddShoppingItem
    assertEquals("Milk", item.title)
    assertEquals("2", item.quantity)
    assertEquals("packets", item.unit)
  }

  @Test
  fun testReminderIntentParsing() {
    val result = VoiceCommandParser.parse("Remind me to call Geetesh at 7")
    assertTrue(result is VoiceCommandIntent.CreateReminder)
    val reminder = result as VoiceCommandIntent.CreateReminder
    assertEquals("Call Geetesh", reminder.title)
    assertEquals("7:00 PM", reminder.timeLabel)
  }

  @Test
  fun testExpenseIntentParsing() {
    val result = VoiceCommandParser.parse("I spent 250 on vegetables")
    assertTrue(result is VoiceCommandIntent.AddExpense)
    val expense = result as VoiceCommandIntent.AddExpense
    assertEquals(250.0, expense.amount, 0.01)
    assertEquals("Food", expense.category)
  }

  @Test
  fun testCallContactIntentParsing() {
    val result = VoiceCommandParser.parse("Call Geetesh")
    assertTrue(result is VoiceCommandIntent.CallContact)
    val call = result as VoiceCommandIntent.CallContact
    assertEquals("Geetesh", call.contactName)
  }

  @Test
  fun testViewScheduleIntentParsing() {
    val result = VoiceCommandParser.parse("What's on today?")
    assertTrue(result is VoiceCommandIntent.ViewSchedule)
  }

  @Test
  fun testViewShoppingIntentParsing() {
    val result = VoiceCommandParser.parse("Show my shopping list")
    assertTrue(result is VoiceCommandIntent.ViewShoppingList)
  }

  @Test
  fun testScreenBottomNavItemsNotNull() {
    val items = com.example.ui.navigation.Screen.bottomNavItems
    assertEquals(5, items.size)
    items.forEach { screen ->
      org.junit.Assert.assertNotNull(screen)
      assertTrue(screen.route.isNotBlank())
      assertTrue(screen.title.isNotBlank())
    }
  }

  @Test
  fun testRoomEntityClasses() {
    val user = com.example.data.model.User(name = "Mom")
    assertEquals("Mom", user.name)

    val shoppingItem = com.example.data.model.ShoppingItem(title = "Apples", quantity = "1 kg")
    assertEquals("Apples", shoppingItem.title)

    val task = com.example.data.model.Task(title = "Drink Water", timeLabel = "9:00 AM")
    assertEquals("Drink Water", task.title)

    val expense = com.example.data.model.Expense(amount = 150.0, category = "Food")
    assertEquals(150.0, expense.amount, 0.01)

    val note = com.example.data.model.Note(title = "Ideas", content = "Test note")
    assertEquals("Ideas", note.title)
  }

  @Test
  fun testCommunicationHelperSanitization() {
    val raw = "+91 98765-43210 (Mobile)"
    val clean = com.example.util.CommunicationHelper.sanitizePhoneNumber(raw)
    assertEquals("+919876543210", clean)
  }

  @Test
  fun testOnboardingAndWidgetsRoutes() {
    assertEquals("onboarding", com.example.ui.navigation.Screen.Onboarding.route)
    assertEquals("widgets", com.example.ui.navigation.Screen.Widgets.route)
  }
}
