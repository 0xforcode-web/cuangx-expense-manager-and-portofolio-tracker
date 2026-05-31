package com.cuangx.finance.feature.expense.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.core.ui.components.SectionHeader
import com.cuangx.finance.core.ui.theme.CuangXSpacing
import com.cuangx.finance.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddCategory: (Long?) -> Unit,
    onNavigateToEditCategory: (Long) -> Unit,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val expandedCategories by viewModel.expandedCategories.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kategori") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddCategory(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(CuangXSpacing.xs),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(CuangXSpacing.md)
        ) {
            item {
                SectionHeader(title = "Pengeluaran")
                Spacer(modifier = Modifier.height(CuangXSpacing.xs))
            }
            items(uiState.expenseCategories, key = { it.category.id }) { categoryWithChildren ->
                CategoryGroupItem(
                    categoryWithChildren = categoryWithChildren,
                    isExpanded = expandedCategories.contains(categoryWithChildren.category.id),
                    onToggleExpand = { viewModel.toggleExpanded(categoryWithChildren.category.id) },
                    onAddSubCategory = { onNavigateToAddCategory(categoryWithChildren.category.id) },
                    onEditCategory = { onNavigateToEditCategory(it.id) },
                    onDeleteCategory = { viewModel.deleteCategory(it) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(CuangXSpacing.md))
                SectionHeader(title = "Pemasukan")
                Spacer(modifier = Modifier.height(CuangXSpacing.xs))
            }
            items(uiState.incomeCategories, key = { it.category.id }) { categoryWithChildren ->
                CategoryGroupItem(
                    categoryWithChildren = categoryWithChildren,
                    isExpanded = expandedCategories.contains(categoryWithChildren.category.id),
                    onToggleExpand = { viewModel.toggleExpanded(categoryWithChildren.category.id) },
                    onAddSubCategory = { onNavigateToAddCategory(categoryWithChildren.category.id) },
                    onEditCategory = { onNavigateToEditCategory(it.id) },
                    onDeleteCategory = { viewModel.deleteCategory(it) }
                )
            }
        }
    }
}

@Composable
private fun CategoryGroupItem(
    categoryWithChildren: CategoryWithChildren,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onAddSubCategory: () -> Unit,
    onEditCategory: (Category) -> Unit,
    onDeleteCategory: (Category) -> Unit
) {
    var expandedMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    CalmCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(categoryWithChildren.category.color).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryWithChildren.category.name.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(categoryWithChildren.category.color)
                    )
                }
                Spacer(modifier = Modifier.width(CuangXSpacing.sm))
                Text(
                    text = categoryWithChildren.category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                if (categoryWithChildren.children.isNotEmpty()) {
                    Text(
                        text = "${categoryWithChildren.children.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(CuangXSpacing.xxs))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Box {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(androidx.compose.material.icons.filled.MoreVert, contentDescription = "More options")
                    }
                    androidx.compose.material3.DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Edit Kategori") },
                            onClick = {
                                expandedMenu = false
                                onEditCategory(categoryWithChildren.category)
                            },
                            leadingIcon = { Icon(androidx.compose.material.icons.filled.Edit, contentDescription = null) }
                        )
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Hapus Kategori", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                expandedMenu = false
                                onDeleteCategory(categoryWithChildren.category)
                            },
                            leadingIcon = { Icon(androidx.compose.material.icons.filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                        )
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Tambah Sub-Kategori") },
                            onClick = {
                                expandedMenu = false
                                onAddSubCategory()
                            },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(start = 52.dp, end = 0.dp, bottom = 0.dp)
                ) {
                    categoryWithChildren.children.forEach { child ->
                        var childMenuExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(child.color))
                            )
                            Spacer(modifier = Modifier.width(CuangXSpacing.xs))
                            Text(
                                text = child.name,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Box {
                                IconButton(onClick = { childMenuExpanded = true }, modifier = Modifier.size(32.dp)) {
                                    Icon(androidx.compose.material.icons.filled.MoreVert, contentDescription = "More options", modifier = Modifier.size(16.dp))
                                }
                                androidx.compose.material3.DropdownMenu(
                                    expanded = childMenuExpanded,
                                    onDismissRequest = { childMenuExpanded = false }
                                ) {
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("Edit") },
                                        onClick = {
                                            childMenuExpanded = false
                                            onEditCategory(child)
                                        },
                                        leadingIcon = { Icon(androidx.compose.material.icons.filled.Edit, contentDescription = null) }
                                    )
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("Hapus", color = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            childMenuExpanded = false
                                            onDeleteCategory(child)
                                        },
                                        leadingIcon = { Icon(androidx.compose.material.icons.filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onAddSubCategory)
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(CuangXSpacing.xs))
                        Text(
                            text = "Tambah sub-kategori",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
