package com.roadfam.farminventsof.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.roadfam.farminventsof.data.model.ReminderFrequency
import com.roadfam.farminventsof.ui.components.BottomNavItem
import com.roadfam.farminventsof.ui.components.BottomNavigationBar
import com.roadfam.farminventsof.ui.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: ReminderViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val activeReminders by viewModel.activeReminders.collectAsState()
    val completedReminders by viewModel.completedReminders.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var reminderTitle by remember { mutableStateOf("") }
    var reminderDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedFrequency by remember { mutableStateOf(ReminderFrequency.ONCE) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reminders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Reminder") }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = 2,
                items = listOf(
                    BottomNavItem("Inventory", Icons.Default.List) { onNavigateToInventory() },
                    BottomNavItem("Analytics", Icons.Default.Analytics) { onNavigateToAnalytics() },
                    BottomNavItem("Reminders", Icons.Default.Notifications) {},
                    BottomNavItem("Settings", Icons.Default.Settings) { onNavigateToSettings() }
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Active")
                            if (activeReminders.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Badge { Text(activeReminders.size.toString()) }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Completed") }
                )
            }
            
            // Content
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        if (activeReminders.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "🔔", fontSize = 64.sp)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "No active reminders",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Add a reminder to stay on track",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            items(activeReminders) { reminder ->
                                ReminderCard(
                                    title = reminder.title,
                                    description = reminder.description,
                                    date = reminder.date,
                                    frequency = reminder.frequency,
                                    isCompleted = false,
                                    onComplete = { viewModel.completeReminder(reminder) },
                                    onDelete = { viewModel.deleteReminder(reminder) }
                                )
                            }
                        }
                    }
                    1 -> {
                        if (completedReminders.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "✅", fontSize = 64.sp)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "No completed reminders",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            items(completedReminders) { reminder ->
                                ReminderCard(
                                    title = reminder.title,
                                    description = reminder.description,
                                    date = reminder.date,
                                    frequency = reminder.frequency,
                                    isCompleted = true,
                                    onComplete = { viewModel.uncompleteReminder(reminder) },
                                    onDelete = { viewModel.deleteReminder(reminder) }
                                )
                            }
                        }
                    }
                }
                
                // Bottom spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
    
    // Add Reminder Dialog
    if (showAddDialog) {
        Dialog(onDismissRequest = { viewModel.hideAddDialog() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add Reminder",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { viewModel.hideAddDialog() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    
                    // Title field
                    OutlinedTextField(
                        value = reminderTitle,
                        onValueChange = { reminderTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g., Check equipment") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Description field
                    OutlinedTextField(
                        value = reminderDescription,
                        onValueChange = { reminderDescription = it },
                        label = { Text("Description (Optional)") },
                        placeholder = { Text("Add details...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Frequency section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Frequency",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        // FlowRow-like layout for chips
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedFrequency == ReminderFrequency.ONCE,
                                    onClick = { selectedFrequency = ReminderFrequency.ONCE },
                                    label = { Text("Once") },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = selectedFrequency == ReminderFrequency.DAILY,
                                    onClick = { selectedFrequency = ReminderFrequency.DAILY },
                                    label = { Text("Daily") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedFrequency == ReminderFrequency.WEEKLY,
                                    onClick = { selectedFrequency = ReminderFrequency.WEEKLY },
                                    label = { Text("Weekly") },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = selectedFrequency == ReminderFrequency.MONTHLY,
                                    onClick = { selectedFrequency = ReminderFrequency.MONTHLY },
                                    label = { Text("Monthly") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    
                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.hideAddDialog() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (reminderTitle.isNotBlank()) {
                                    viewModel.addReminder(
                                        title = reminderTitle,
                                        description = reminderDescription,
                                        date = selectedDate,
                                        frequency = selectedFrequency
                                    )
                                    reminderTitle = ""
                                    reminderDescription = ""
                                    selectedFrequency = ReminderFrequency.ONCE
                                }
                            },
                            enabled = reminderTitle.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    title: String,
    description: String,
    date: Long,
    frequency: ReminderFrequency,
    isCompleted: Boolean,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onComplete() }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (frequency != ReminderFrequency.ONCE) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = when (frequency) {
                                ReminderFrequency.DAILY -> "Daily"
                                ReminderFrequency.WEEKLY -> "Weekly"
                                ReminderFrequency.MONTHLY -> "Monthly"
                                else -> ""
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

