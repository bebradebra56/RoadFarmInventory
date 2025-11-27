package com.roadfam.farminventsof.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roadfam.farminventsof.data.model.ItemStatus
import com.roadfam.farminventsof.ui.theme.SecondaryOrange
import com.roadfam.farminventsof.ui.theme.StatusGreen
import com.roadfam.farminventsof.ui.theme.StatusRed
import com.roadfam.farminventsof.ui.viewmodel.AddEditItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    viewModel: AddEditItemViewModel,
    itemId: Long? = null,
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    
    var showCategoryDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(itemId) {
        if (itemId != null) {
            viewModel.loadItem(itemId)
        }
    }
    
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onNavigateBack()
            viewModel.resetSaveSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Item" else "Add New Item") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Name field
            item {
                OutlinedTextField(
                    value = formState.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g., Tractor, Seeds, etc.") },
                    isError = formState.nameError != null,
                    supportingText = formState.nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
            
            // Category selector
            item {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryDialog = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Category *",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (formState.categoryError != null)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (formState.categoryId != null) {
                                val category = categories.find { it.id == formState.categoryId }
                                Text(
                                    text = "${category?.icon ?: ""} ${category?.name ?: "Unknown"}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "Select a category",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            formState.categoryError?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Quantity
            item {
                Column {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(
                            onClick = { viewModel.updateQuantity(formState.quantity - 1) },
                            enabled = formState.quantity > 0
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        
                        Text(
                            text = formState.quantity.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        FilledIconButton(
                            onClick = { viewModel.updateQuantity(formState.quantity + 1) }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }
            
            // Status
            item {
                Column {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusOptionCard(
                            icon = "🟢",
                            title = "Working",
                            description = "Item is in good condition",
                            selected = formState.status == ItemStatus.WORKING,
                            color = StatusGreen,
                            onClick = { viewModel.updateStatus(ItemStatus.WORKING) }
                        )
                        StatusOptionCard(
                            icon = "🟠",
                            title = "Needs Repair",
                            description = "Requires attention or maintenance",
                            selected = formState.status == ItemStatus.NEEDS_REPAIR,
                            color = SecondaryOrange,
                            onClick = { viewModel.updateStatus(ItemStatus.NEEDS_REPAIR) }
                        )
                        StatusOptionCard(
                            icon = "🔴",
                            title = "Broken",
                            description = "Out of order or depleted",
                            selected = formState.status == ItemStatus.BROKEN,
                            color = StatusRed,
                            onClick = { viewModel.updateStatus(ItemStatus.BROKEN) }
                        )
                    }
                }
            }
            
            // Note
            item {
                OutlinedTextField(
                    value = formState.note,
                    onValueChange = { viewModel.updateNote(it) },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("Add any additional information...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5
                )
            }
            
            // Save button
            item {
                Button(
                    onClick = { viewModel.saveItem() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditMode) "Save Changes" else "Add Item",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
    
    // Category selection dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Select Category") },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateCategory(category.id)
                                    showCategoryDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (formState.categoryId == category.id)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.icon,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun StatusOptionCard(
    icon: String,
    title: String,
    description: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = if (selected) CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = androidx.compose.ui.graphics.SolidColor(color)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (selected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = color
                )
            }
        }
    }
}

