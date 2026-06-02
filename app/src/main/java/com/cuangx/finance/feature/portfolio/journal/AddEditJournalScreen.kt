package com.cuangx.finance.feature.portfolio.journal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.runtime.mutableStateOf
import com.cuangx.finance.core.ui.components.CalculatorDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material.icons.filled.DateRange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.domain.model.AssetType
import com.cuangx.finance.domain.model.JournalAction

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditJournalScreen(
    journalId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddEditJournalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    var showDeleteDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var calculatorTarget by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(journalId) {
        if (journalId != null && journalId > 0) {
            viewModel.loadJournal(journalId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is AddEditJournalEvent.SaveSuccess -> onNavigateBack()
                is AddEditJournalEvent.ShowError -> {
                    android.widget.Toast.makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Journal" else "Tambah Journal") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
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
                Text("Aksi", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    JournalAction.entries.forEach { action ->
                        FilterChip(
                            selected = uiState.action == action,
                            onClick = { viewModel.updateAction(action) },
                            label = { Text(action.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (action) {
                                    JournalAction.BUY -> MaterialTheme.colorScheme.primaryContainer
                                    JournalAction.SELL -> MaterialTheme.colorScheme.errorContainer
                                    JournalAction.DIVIDEND -> MaterialTheme.colorScheme.tertiaryContainer
                                }
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Tipe Aset", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    AssetType.entries.forEach { type ->
                        FilterChip(
                            selected = uiState.assetType == type,
                            onClick = { viewModel.updateAssetType(type) },
                            label = { Text(type.displayName) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                if (uiState.assetType.hasTicker) {
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.ticker,
                            onValueChange = viewModel::updateTicker,
                            label = { Text("Ticker (e.g. BBCA.JK, BTC-USD, GC=F)") },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            singleLine = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                        )

                        val distinctHoldings = uiState.existingHoldings.distinctBy { it.ticker }
                        if (distinctHoldings.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                distinctHoldings.forEach { holding ->
                                    if (!holding.ticker.isNullOrBlank()) {
                                        DropdownMenuItem(
                                            text = { Text(holding.ticker) },
                                            onClick = {
                                                viewModel.updateTicker(holding.ticker)
                                                viewModel.updateName(holding.name)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text("Nama Aset") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.quantity,
                    onValueChange = viewModel::updateQuantity,
                    label = { Text(if (uiState.assetType == com.cuangx.finance.domain.model.AssetType.GOLD) "Gram" else "Jumlah (lot/unit)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        IconButton(onClick = { calculatorTarget = "quantity" }) {
                            Icon(Icons.Default.Calculate, contentDescription = "Kalkulator")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.price,
                    onValueChange = viewModel::updatePrice,
                    label = { Text("Harga per unit") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        IconButton(onClick = { calculatorTarget = "price" }) {
                            Icon(Icons.Default.Calculate, contentDescription = "Kalkulator")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.fee,
                    onValueChange = viewModel::updateFee,
                    label = { Text("Fee (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        IconButton(onClick = { calculatorTarget = "fee" }) {
                            Icon(Icons.Default.Calculate, contentDescription = "Kalkulator")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                Text("Sumber Dana (Account)", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    uiState.accounts.forEach { account ->
                        FilterChip(
                            selected = uiState.accountId == account.id,
                            onClick = { viewModel.updateAccountId(account.id) },
                            label = { Text(account.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CalmCard(modifier = Modifier.fillMaxWidth()) {
                var showDatePicker by remember { mutableStateOf(false) }
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                OutlinedTextField(
                    value = dateFormat.format(Date(uiState.date)),
                    onValueChange = {},
                    label = { Text("Tanggal") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                        }
                    }
                )

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.date)
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { viewModel.updateDate(it) }
                                showDatePicker = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.reason,
                    onValueChange = viewModel::updateReason,
                    label = { Text("Alasan (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.tags,
                    onValueChange = viewModel::updateTags,
                    label = { Text("Tags (pisahkan koma)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = viewModel::updateNote,
                    label = { Text("Catatan") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Journal") },
            text = { Text("Apakah Anda yakin ingin menghapus journal ini? Transaksi terkait akan dihapus dan saldo akun akan disesuaikan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteJournal()
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    calculatorTarget?.let { target ->
        CalculatorDialog(
            initialValue = when (target) {
                "quantity" -> uiState.quantity
                "price" -> uiState.price
                "fee" -> uiState.fee
                else -> ""
            },
            onDismiss = { calculatorTarget = null },
            onConfirm = { result ->
                when (target) {
                    "quantity" -> viewModel.updateQuantity(result)
                    "price" -> viewModel.updatePrice(result)
                    "fee" -> viewModel.updateFee(result)
                }
                calculatorTarget = null
            }
        )
    }
}
