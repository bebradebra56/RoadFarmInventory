package com.roadfam.farminventsof.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roadfam.farminventsof.ui.components.CategoryCard
import com.roadfam.farminventsof.ui.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: CategoryViewModel,
    onNavigateBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val editingCategory by viewModel.editingCategory.collectAsState()
    
    var categoryName by remember { mutableStateOf("") }
    var categoryIcon by remember { mutableStateOf("") }
    var categoryColor by remember { mutableStateOf("#F4B400") }
    
    LaunchedEffect(showAddDialog, editingCategory) {
        if (showAddDialog) {
            if (editingCategory != null) {
                categoryName = editingCategory!!.name
                categoryIcon = editingCategory!!.icon
                categoryColor = editingCategory!!.color
            } else {
                categoryName = ""
                categoryIcon = ""
                categoryColor = "#F4B400"
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Category") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Manage your inventory categories",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            if (categories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "📂", style = MaterialTheme.typography.displayLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No categories yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { viewModel.editCategory(category) }
                    )
                }
            }
        }
    }
    
    // Add/Edit Category Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAddDialog() },
            title = { Text(if (editingCategory != null) "Edit Category" else "Add Category") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("Category Name") },
                        placeholder = { Text("e.g., Tools, Feed, etc.") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = categoryIcon,
                        onValueChange = { categoryIcon = it },
                        label = { Text("Icon (Emoji)") },
                        placeholder = { Text("e.g., 🪣, 🌾, etc.") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Text(
                        text = "Common icons: 🪣 🌾 💡 🚜 📦 🔧 ⚙️ 🛠️ 📋",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (categoryName.isNotBlank() && categoryIcon.isNotBlank()) {
                            viewModel.saveCategory(categoryName, categoryIcon, categoryColor)
                        }
                    },
                    enabled = categoryName.isNotBlank() && categoryIcon.isNotBlank()
                ) {
                    Text(if (editingCategory != null) "Save" else "Add")
                }
            },
            dismissButton = {
                if (editingCategory != null) {
                    TextButton(
                        onClick = {
                            viewModel.deleteCategory(editingCategory!!)
                            viewModel.hideAddDialog()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = { viewModel.hideAddDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

