package com.example.ui.screens

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.R
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import com.example.widget.HomeOSFamilyDialWidgetProvider
import com.example.widget.HomeOSQuickActionsWidgetProvider
import com.example.widget.HomeOSTasksShoppingWidgetProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home OS Widgets",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Add shortcuts directly to your phone's home screen for instant 1-tap calling, WhatsApp, and shopping without opening the full app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Widget 1: Tasks & Shopping List Collection Widget
            WidgetPreviewCard(
                title = "Tasks & Shopping List (4x3)",
                subtitle = "Scrollable live summary of pending reminders and groceries on your home screen",
                onPinWidget = {
                    requestPinWidget(context, HomeOSTasksShoppingWidgetProvider::class.java)
                }
            ) {
                // Interactive Compose Simulation of the Collection Widget
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF8F9FE))
                        .border(1.dp, Color(0xFFE1E4F2), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryIndigo,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Home OS Tasks & Shopping",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1F2024)
                            )
                        }
                        Text(
                            text = "Home OS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PrimaryIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 1: Task Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEDE7F6)
                        ) {
                            Text(
                                text = "TASK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Evening BP Medicine", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2024))
                            Text("Today 8:00 PM", fontSize = 10.sp, color = Color(0xFF74777F))
                        }
                        Text("•", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 2: Shopping Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "BUY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Fresh Milk & Whole Wheat Bread", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2024))
                            Text("2 packets • Dairy", fontSize = 10.sp, color = Color(0xFF74777F))
                        }
                        Text("•", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Footer Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(8.dp))
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+ Add Task", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(8.dp))
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+ Add Shopping", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Widget 2: Quick Actions Widget
            WidgetPreviewCard(
                title = "Home OS Quick Actions (4x2)",
                subtitle = "Fast access to Phone Call, WhatsApp, Shopping List & Tasks",
                onPinWidget = {
                    requestPinWidget(context, HomeOSQuickActionsWidgetProvider::class.java)
                }
            ) {
                // Interactive Compose Simulation of the Quick Actions Widget
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF8F9FE))
                        .border(1.dp, Color(0xFFE1E4F2), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = PrimaryIndigo,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Home OS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1F2024)
                            )
                        }
                        Text(
                            text = "Today",
                            fontSize = 12.sp,
                            color = Color(0xFF74777F)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WidgetMiniButton(
                            title = "Call",
                            icon = Icons.Default.Call,
                            iconTint = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        WidgetMiniButton(
                            title = "WhatsApp",
                            drawableRes = R.drawable.ic_whatsapp,
                            modifier = Modifier.weight(1f)
                        )
                        WidgetMiniButton(
                            title = "Shopping",
                            icon = Icons.Default.ShoppingCart,
                            iconTint = PrimaryIndigo,
                            modifier = Modifier.weight(1f)
                        )
                        WidgetMiniButton(
                            title = "Tasks",
                            icon = Icons.Default.CheckCircle,
                            iconTint = Color(0xFFE08600),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Widget 2: Family Speed Dial Widget
            WidgetPreviewCard(
                title = "Family Speed Dial (4x2)",
                subtitle = "One-tap direct phone call and WhatsApp to Geetesh & Dad",
                onPinWidget = {
                    requestPinWidget(context, HomeOSFamilyDialWidgetProvider::class.java)
                }
            ) {
                // Interactive Compose Simulation of the Speed Dial Widget
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF8F9FE))
                        .border(1.dp, Color(0xFFE1E4F2), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Family Speed Dial",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1F2024)
                            )
                        }
                        Text(
                            text = "Home OS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PrimaryIndigo
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Contact 1
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Geetesh", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1F2024))
                            Text("Son", fontSize = 11.sp, color = Color(0xFF74777F))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_whatsapp),
                                    contentDescription = "WhatsApp",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Contact 2
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Dad", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1F2024))
                            Text("Family", fontSize = 11.sp, color = Color(0xFF74777F))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_whatsapp),
                                    contentDescription = "WhatsApp",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Instructions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "How to add widgets on Android:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Press and hold an empty space on your phone's Home screen.\n2. Tap 'Widgets' from the popup.\n3. Scroll down to 'Home OS'.\n4. Touch and hold the widget and drag it to your screen.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun WidgetPreviewCard(
    title: String,
    subtitle: String,
    onPinWidget: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            content()

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onPinWidget,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("pin_widget_button_${title.take(6)}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add to Phone Home Screen",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun WidgetMiniButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    drawableRes: Int? = null,
    iconTint: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        } else if (drawableRes != null) {
            Icon(
                painter = painterResource(id = drawableRes),
                contentDescription = title,
                tint = Color.Unspecified,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2024)
        )
    }
}

private fun requestPinWidget(context: Context, providerClass: Class<*>) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val componentName = ComponentName(context, providerClass)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            try {
                val successCallback = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, providerClass),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                appWidgetManager.requestPinAppWidget(componentName, null, successCallback)
                Toast.makeText(context, "Adding widget to home screen...", Toast.LENGTH_SHORT).show()
                return
            } catch (e: Exception) {
                // fall through to toast instructions
            }
        }
    }

    Toast.makeText(
        context,
        "Long-press your phone's Home screen -> Widgets -> Home OS to place widget",
        Toast.LENGTH_LONG
    ).show()
}
