package com.cuangx.finance.feature.expense.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditCategoryScreen(
    categoryId: Long? = null,
    parentId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddEditCategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val isSubCategoryMode = uiState.expectedParentId != null || uiState.parentId != null

    LaunchedEffect(categoryId, parentId) {
        if (categoryId != null && categoryId > 0) {
            viewModel.loadCategory(categoryId)
        }
        if (parentId != null && parentId > 0) {
            viewModel.loadParentCategory(parentId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is AddEditCategoryEvent.SaveSuccess -> onNavigateBack()
                is AddEditCategoryEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Kategori" else if (isSubCategoryMode) "Tambah Sub-Kategori" else "Tambah Kategori") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Text("Tipe", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    TransactionType.entries.filter { it != TransactionType.TRANSFER }.forEach { type ->
                        FilterChip(
                            selected = uiState.type == type,
                            onClick = { viewModel.updateType(type) },
                            enabled = !isSubCategoryMode,
                            label = { Text(type.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text("Nama Kategori") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (uiState.parentId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sub-kategori dari: ${uiState.parentName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "Menyimpan..." else "Simpan")
            }
        }
    }
}
