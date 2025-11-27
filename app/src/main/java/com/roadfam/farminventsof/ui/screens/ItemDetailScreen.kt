package com.roadfam.farminventsof.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roadfam.farminventsof.data.model.ItemStatus
import com.roadfam.farminventsof.ui.theme.SecondaryOrange
import com.roadfam.farminventsof.ui.theme.StatusGreen
import com.roadfam.farminventsof.ui.theme.StatusRed
import com.roadfam.farminventsof.ui.viewmodel.ItemDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    viewModel: ItemDetailViewModel,
    itemId: Long,
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit
) {
    val item by viewModel.item.collectAsState()
    val history by viewModel.history.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }
    
    val statusColor = when (item?.status) {
        ItemStatus.WORKING -> StatusGreen
        ItemStatus.NEEDS_REPAIR -> SecondaryOrange
        ItemStatus.BROKEN -> StatusRed
        else -> Color.Gray
    }
    
    val statusText = when (item?.status) {
        ItemStatus.WORKING -> "Working"
        ItemStatus.NEEDS_REPAIR -> "Needs Repair"
        ItemStatus.BROKEN -> "Broken"
        else -> "Unknown"
    }
    
    val statusIcon = when (item?.status) {
        ItemStatus.WORKING -> "🟢"
        ItemStatus.NEEDS_REPAIR -> "🟠"
        ItemStatus.BROKEN -> "🔴"
        else -> "⚪"
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item?.let { currentItem ->
                // Header card with item name
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📦",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = currentItem.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                
                // Status card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = statusColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = statusIcon,
                                fontSize = 40.sp,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Status",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                            FilledTonalButton(
                                onClick = { showStatusMenu = true },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = statusColor.copy(alpha = 0.2f),
                                    contentColor = statusColor
                                )
                            ) {
                                Text("Change")
                            }
                        }
                    }
                }
                
                // Details section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Details",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            DetailRow(
                                icon = Icons.Default.Inventory,
                                label = "Quantity",
                                value = currentItem.quantity.toString()
                            )
                            
                            DetailRow(
                                icon = Icons.Default.CalendarToday,
                                label = "Last Used",
                                value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(Date(currentItem.lastUsedDate))
                            )
                            
                            DetailRow(
                                icon = Icons.Default.DateRange,
                                label = "Added",
                                value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(Date(currentItem.createdAt))
                            )
                            
                            if (currentItem.note.isNotEmpty()) {
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.Notes,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Notes",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentItem.note,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // History section
                if (history.isNotEmpty()) {
                    item {
                        Text(
                            text = "History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(history) { historyItem ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = historyItem.action,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (historyItem.details.isNotEmpty()) {
                                        Text(
                                            text = historyItem.details,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = SimpleDateFormat("MMM dd", Locale.getDefault())
                                        .format(Date(historyItem.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } ?: item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
    
    // Status change menu
    if (showStatusMenu) {
        AlertDialog(
            onDismissRequest = { showStatusMenu = false },
            title = { Text("Change Status") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChangeOption(
                        icon = "🟢",
                        title = "Working",
                        color = StatusGreen,
                        onClick = {
                            viewModel.updateItemStatus(ItemStatus.WORKING)
                            showStatusMenu = false
                        }
                    )
                    StatusChangeOption(
                        icon = "🟠",
                        title = "Needs Repair",
                        color = SecondaryOrange,
                        onClick = {
                            viewModel.updateItemStatus(ItemStatus.NEEDS_REPAIR)
                            showStatusMenu = false
                        }
                    )
                    StatusChangeOption(
                        icon = "🔴",
                        title = "Broken",
                        color = StatusRed,
                        onClick = {
                            viewModel.updateItemStatus(ItemStatus.BROKEN)
                            showStatusMenu = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusMenu = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Item?") },
            text = { Text("Are you sure you want to delete this item? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteItem()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatusChangeOption(
    icon: String,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

