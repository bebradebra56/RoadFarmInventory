package com.roadfam.farminventsof.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roadfam.farminventsof.data.model.ItemStatus
import com.roadfam.farminventsof.ui.components.BottomNavItem
import com.roadfam.farminventsof.ui.components.BottomNavigationBar
import com.roadfam.farminventsof.ui.theme.SecondaryOrange
import com.roadfam.farminventsof.ui.theme.StatusGreen
import com.roadfam.farminventsof.ui.theme.StatusRed
import com.roadfam.farminventsof.ui.viewmodel.AnalyticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val categoryStats by viewModel.categoryStatistics.collectAsState()
    val statusStats by viewModel.statusStatistics.collectAsState()
    val totalItems by viewModel.totalItemCount.collectAsState()
    val workingItems by viewModel.workingItemsCount.collectAsState()
    val needsRepair by viewModel.needsRepairCount.collectAsState()
    val brokenItems by viewModel.brokenItemsCount.collectAsState()
    val itemsThisWeek by viewModel.itemsAddedThisWeek.collectAsState()
    val itemsThisMonth by viewModel.itemsAddedThisMonth.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Analytics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedIndex = 1,
                items = listOf(
                    BottomNavItem("Inventory", Icons.Default.List) { onNavigateToInventory() },
                    BottomNavItem("Analytics", Icons.Default.Analytics) {},
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
            // Overview stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Items",
                        value = totalItems.toString(),
                        icon = "📦",
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "This Week",
                        value = itemsThisWeek.toString(),
                        icon = "📊",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            
            // Status Distribution Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Equipment Status",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        if (totalItems > 0) {
                            DonutChart(
                                data = listOf(
                                    ChartData("Working", workingItems, StatusGreen),
                                    ChartData("Needs Repair", needsRepair, SecondaryOrange),
                                    ChartData("Broken", brokenItems, StatusRed)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                ChartLegendItem(
                                    label = "Working",
                                    value = workingItems,
                                    percentage = if (totalItems > 0) (workingItems.toFloat() / totalItems * 100).toInt() else 0,
                                    color = StatusGreen
                                )
                                ChartLegendItem(
                                    label = "Needs Repair",
                                    value = needsRepair,
                                    percentage = if (totalItems > 0) (needsRepair.toFloat() / totalItems * 100).toInt() else 0,
                                    color = SecondaryOrange
                                )
                                ChartLegendItem(
                                    label = "Broken",
                                    value = brokenItems,
                                    percentage = if (totalItems > 0) (brokenItems.toFloat() / totalItems * 100).toInt() else 0,
                                    color = StatusRed
                                )
                            }
                        } else {
                            Text(
                                text = "No data available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 32.dp)
                            )
                        }
                    }
                }
            }
            
            // Category Distribution
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Items by Category",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (categoryStats.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                categoryStats.forEach { stat ->
                                    CategoryBar(
                                        icon = stat.category.icon,
                                        name = stat.category.name,
                                        count = stat.itemCount,
                                        percentage = stat.percentage,
                                        color = Color(android.graphics.Color.parseColor(stat.category.color))
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "No categories with items",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }
            
            // Activity Stats
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Recent Activity",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Last 30 days",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$itemsThisMonth",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "items added this month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class ChartData(val label: String, val value: Int, val color: Color)

@Composable
private fun DonutChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.value }.toFloat()
    if (total == 0f) return
    
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "chart_animation"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val strokeWidth = 35.dp.toPx()
            var startAngle = -90f
            
            data.forEach { item ->
                val sweepAngle = (item.value / total) * 360f * animatedProgress
                
                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(size.width, size.height)
                )
                
                startAngle += sweepAngle
            }
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = total.toInt().toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Total Items",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChartLegendItem(
    label: String,
    value: Int,
    percentage: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$value ($percentage%)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
private fun CategoryBar(
    icon: String,
    name: String,
    count: Int,
    percentage: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

