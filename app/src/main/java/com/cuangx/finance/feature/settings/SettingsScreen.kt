package com.cuangx.finance.feature.settings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.FileProvider
import com.cuangx.finance.core.util.BackupManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cuangx.finance.core.ui.components.CalmCard
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDarkModeDialog by remember { mutableStateOf(false) }
    var showStartDayDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showPasscodeDialog by remember { mutableStateOf(false) }
    var showWipeConfirmDialog by remember { mutableStateOf(false) }
    var tempPasscode by remember { mutableStateOf("") }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.restoreData(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is SettingsEvent.BackupSuccess -> {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        event.file
                    )
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Simpan Backup"))
                }
                is SettingsEvent.RestoreSuccess -> {
                    Toast.makeText(context, "Data berhasil di-restore!", Toast.LENGTH_LONG).show()
                }
                is SettingsEvent.WipeSuccess -> {
                    Toast.makeText(context, "Semua data telah dihapus!", Toast.LENGTH_LONG).show()
                }
                is SettingsEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalmCard {
                Text(
                    text = "Security",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SettingsSwitch(
                    title = "Passcode Lock",
                    subtitle = "Require passcode to open app",
                    checked = uiState.passcodeEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled && uiState.passcodeValue.isEmpty()) {
                            showPasscodeDialog = true
                        } else {
                            viewModel.setPasscodeEnabled(enabled)
                        }
                    }
                )

                if (uiState.passcodeEnabled) {
                    SettingsItem(
                        title = "Change Passcode",
                        subtitle = "Update your 4-digit security code",
                        onClick = {
                            tempPasscode = ""
                            showPasscodeDialog = true
                        }
                    )
                }

                SettingsSwitch(
                    title = "Biometric Lock",
                    subtitle = "Use fingerprint or face unlock",
                    checked = uiState.biometricEnabled,
                    onCheckedChange = viewModel::setBiometricEnabled
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CalmCard {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SettingsItem(
                    title = "Dark Mode",
                    subtitle = when (uiState.darkMode) {
                        "dark" -> "Dark"
                        "light" -> "Light"
                        else -> "System"
                    },
                    onClick = { showDarkModeDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CalmCard {
                Text(
                    text = "Finance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SettingsItem(
                    title = "Month Start Day",
                    subtitle = "Day ${uiState.startDay}",
                    onClick = { showStartDayDialog = true }
                )

                SettingsItem(
                    title = "Default Currency",
                    subtitle = uiState.defaultCurrency,
                    onClick = { showCurrencyDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CalmCard {
                Text(
                    text = "Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SettingsItem(
                    title = "Backup Data",
                    subtitle = "Ekspor semua data ke file JSON",
                    onClick = viewModel::backupData
                )

                SettingsItem(
                    title = "Restore from Backup",
                    subtitle = "Impor data dari file backup JSON",
                    onClick = { restoreLauncher.launch("application/json") }
                )

                SettingsItem(
                    title = "Wipe All Data",
                    subtitle = "Hapus semua transaksi, akun, dan pengaturan",
                    onClick = { showWipeConfirmDialog = true },
                    titleColor = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CalmCard {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SettingsItem(
                    title = "Version",
                    subtitle = "1.0.0",
                    onClick = {}
                )

                SettingsItem(
                    title = "Developer",
                    subtitle = "CuangX-by-fachriceg",
                    onClick = {}
                )
            }
        }
    }

    // Passcode Dialog
    if (showPasscodeDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasscodeDialog = false
                tempPasscode = ""
            },
            title = { Text(if (uiState.passcodeValue.isEmpty()) "Set Passcode" else "Change Passcode") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Enter a 4-digit passcode")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(4) { index ->
                            val isFilled = index < tempPasscode.length
                            Surface(
                                modifier = Modifier.size(16.dp),
                                shape = MaterialTheme.shapes.small,
                                color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ) {}
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Simple numeric keypad inside dialog
                    val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "DEL")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        digits.chunked(3).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { digit ->
                                    if (digit.isEmpty()) {
                                        Box(modifier = Modifier.size(48.dp))
                                    } else {
                                        OutlinedButton(
                                            onClick = {
                                                if (digit == "DEL") {
                                                    if (tempPasscode.isNotEmpty()) tempPasscode = tempPasscode.dropLast(1)
                                                } else if (tempPasscode.length < 4) {
                                                    tempPasscode += digit
                                                    if (tempPasscode.length == 4) {
                                                        viewModel.setPasscodeValue(tempPasscode)
                                                        viewModel.setPasscodeEnabled(true)
                                                        showPasscodeDialog = false
                                                        tempPasscode = ""
                                                    }
                                                }
                                            },
                                            modifier = Modifier.size(48.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(digit)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { 
                    showPasscodeDialog = false
                    tempPasscode = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dark Mode Dialog
    if (showDarkModeDialog) {
        AlertDialog(
            onDismissRequest = { showDarkModeDialog = false },
            title = { Text("Dark Mode") },
            text = {
                Column {
                    listOf("system" to "System", "light" to "Light", "dark" to "Dark").forEach { (value, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDarkMode(value)
                                    showDarkModeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.darkMode == value,
                                onClick = {
                                    viewModel.setDarkMode(value)
                                    showDarkModeDialog = false
                                }
                            )
                            Text(
                                text = label,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDarkModeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Start Day Dialog
    if (showStartDayDialog) {
        var selectedDay by remember { mutableStateOf(uiState.startDay.toFloat()) }
        AlertDialog(
            onDismissRequest = { showStartDayDialog = false },
            title = { Text("Month Start Day") },
            text = {
                Column {
                    Text("Day: ${selectedDay.toInt()}")
                    Slider(
                        value = selectedDay,
                        onValueChange = { selectedDay = it },
                        valueRange = 1f..28f,
                        steps = 26
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setStartDay(selectedDay.toInt())
                    showStartDayDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDayDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Currency Dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Default Currency") },
            text = {
                Column {
                    listOf("IDR", "USD", "EUR", "GBP", "JPY", "SGD").forEach { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDefaultCurrency(currency)
                                    showCurrencyDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.defaultCurrency == currency,
                                onClick = {
                                    viewModel.setDefaultCurrency(currency)
                                    showCurrencyDialog = false
                                }
                            )
                            Text(
                                text = currency,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Wipe Confirmation Dialog
    if (showWipeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWipeConfirmDialog = false },
            title = { Text("Hapus Semua Data?") },
            text = { Text("Tindakan ini tidak dapat dibatalkan. Seluruh riwayat transaksi, daftar akun, portofolio, dan pengaturan aplikasi akan dihapus secara permanen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showWipeConfirmDialog = false
                        viewModel.wipeAllData()
                    }
                ) {
                    Text("Hapus Permanen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
