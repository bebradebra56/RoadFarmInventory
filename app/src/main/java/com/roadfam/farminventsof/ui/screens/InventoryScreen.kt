package com.roadfam.farminventsof.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roadfam.farminventsof.data.model.ItemStatus
import com.roadfam.farminventsof.ui.components.BottomNavItem
import com.roadfam.farminventsof.ui.components.BottomNavigationBar
import com.roadfam.farminventsof.ui.components.CategoryCard
import com.roadfam.farminventsof.ui.components.ItemCard
import com.roadfam.farminventsof.ui.theme.SecondaryOrange
import com.roadfam.farminventsof.ui.theme.StatusGreen
import com.roadfam.farminventsof.ui.theme.StatusRed
import com.roadfam.farminventsof.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onAddItem: () -> Unit,
    onItemClick: (Long) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val workingCount by viewModel.workingItemsCount.collectAsState()
    val needsRepairCount by viewModel.needsRepairCount.collectAsState()
    val brokenCount by viewModel.brokenItemsCount.collectAsState()
    
    var showFilterMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Road Farm Inventory",
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
                onClick = onAddItem,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Item") }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = 0,
                items = listOf(
                    BottomNavItem("Inventory", Icons.Default.List) {},
                    BottomNavItem("Analytics", Icons.Default.Analytics) { onNavigateToAnalytics() },
                    BottomNavItem("Reminders", Icons.Default.Notifications) { onNavigateToReminders() },
                    BottomNavItem("Settings", Icons.Default.Settings) { onNavigateToSettings() }
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
            // Status Overview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatusItem(
                            icon = "🟢",
                            label = "Working",
                            count = workingCount,
                            color = StatusGreen
                        )
                        StatusItem(
                            icon = "🟠",
                            label = "Attention",
                            count = needsRepairCount,
                            color = SecondaryOrange
                        )
                        StatusItem(
                            icon = "🔴",
                            label = "Broken",
                            count = brokenCount,
                            color = StatusRed
                        )
                    }
                }
            }
            
            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search items...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        Row {
                            if (searchQuery.isNotEmpty() || selectedStatus != null || selectedCategoryId != null) {
                                IconButton(onClick = { viewModel.clearFilters() }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                            IconButton(onClick = { showFilterMenu = !showFilterMenu }) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = if (selectedStatus != null || selectedCategoryId != null) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true
                )
            }
            
            // Filter chips
            item {
                AnimatedVisibility(visible = showFilterMenu) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Status filter
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = selectedStatus == null,
                                    onClick = { viewModel.setStatusFilter(null) },
                                    label = { Text("All") }
                                )
                            }
                            items(ItemStatus.values()) { status ->
                                FilterChip(
                                    selected = selectedStatus == status,
                                    onClick = { viewModel.setStatusFilter(status) },
                                    label = {
                                        Text(
                                            when (status) {
                                                ItemStatus.WORKING -> "Working"
                                                ItemStatus.NEEDS_REPAIR -> "Needs Repair"
                                                ItemStatus.BROKEN -> "Broken"
                                            }
                                        )
                                    }
                                )
                            }
                        }
                        
                        // Category filter
                        if (categories.isNotEmpty()) {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = selectedCategoryId == null,
                                        onClick = { viewModel.setCategoryFilter(null) },
                                        label = { Text("All") }
                                    )
                                }
                                items(categories) { category ->
                                    FilterChip(
                                        selected = selectedCategoryId == category.id,
                                        onClick = { viewModel.setCategoryFilter(category.id) },
                                        label = { Text("${category.icon} ${category.name}") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Categories section (if no filters active)
            if (searchQuery.isEmpty() && selectedStatus == null && selectedCategoryId == null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                items(categories.take(6)) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { viewModel.setCategoryFilter(category.id) }
                    )
                }
            }
            
            // Items section
            item {
                Text(
                    text = if (searchQuery.isNotEmpty() || selectedStatus != null || selectedCategoryId != null)
                        "Results (${filteredItems.size})"
                    else
                        "All Items (${filteredItems.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (filteredItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "📦",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No items found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add your first item to get started",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredItems) { item ->
                    val category = viewModel.getCategoryById(item.categoryId)
                    ItemCard(
                        item = item,
                        categoryName = category?.name ?: "Unknown",
                        categoryIcon = category?.icon ?: "📦",
                        onClick = { onItemClick(item.id) }
                    )
                }
            }
            
            // Bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun StatusItem(
    icon: String,
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 32.sp
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

