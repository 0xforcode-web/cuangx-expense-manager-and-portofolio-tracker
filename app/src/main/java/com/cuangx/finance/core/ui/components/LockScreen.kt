package com.cuangx.finance.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.cuangx.finance.core.util.BiometricUtils

@Composable
fun LockScreen(
    isBiometricEnabled: Boolean,
    isPasscodeEnabled: Boolean,
    savedPasscode: String,
    onUnlock: () -> Unit
) {
    val context = LocalContext.current
    var enteredPasscode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun triggerBiometric() {
        val activity = context as? FragmentActivity
        activity?.let {
            BiometricUtils.showBiometricPrompt(
                activity = it,
                onSuccess = {
                    onUnlock()
                },
                onError = { error ->
                    errorMessage = error
                },
                onFailed = {
                    errorMessage = "Authentication failed"
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        if (isBiometricEnabled && BiometricUtils.isBiometricAvailable(context)) {
            triggerBiometric()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "CuangX Finance",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isPasscodeEnabled) "Enter Passcode" else "Authenticate to continue",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (isPasscodeEnabled) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { index ->
                            val isFilled = index < enteredPasscode.length
                            Surface(
                                modifier = Modifier.size(16.dp),
                                shape = MaterialTheme.shapes.small,
                                color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ) {}
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            if (isPasscodeEnabled) {
                PasscodeKeypad(
                    onDigitClick = { digit ->
                        if (enteredPasscode.length < 4) {
                            enteredPasscode += digit
                            errorMessage = null
                            if (enteredPasscode.length == 4) {
                                if (enteredPasscode == savedPasscode) {
                                    onUnlock()
                                } else {
                                    errorMessage = "Wrong passcode"
                                    enteredPasscode = ""
                                }
                            }
                        }
                    },
                    onDeleteClick = {
                        if (enteredPasscode.isNotEmpty()) {
                            enteredPasscode = enteredPasscode.dropLast(1)
                        }
                    },
                    onBiometricClick = if (isBiometricEnabled && BiometricUtils.isBiometricAvailable(context)) {
                        { triggerBiometric() }
                    } else null
                )
            } else if (isBiometricEnabled && BiometricUtils.isBiometricAvailable(context)) {
                Button(
                    onClick = { triggerBiometric() },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp)
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unlock with Biometric")
                }
            }
        }
    }
}

@Composable
fun PasscodeKeypad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometricClick: (() -> Unit)? = null
) {
    val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    
    Column(
        modifier = Modifier.padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.width(280.dp)
        ) {
            items(digits) { digit ->
                KeypadButton(text = digit, onClick = { onDigitClick(digit) })
            }
            
            item {
                if (onBiometricClick != null) {
                    KeypadIconButton(icon = Icons.Default.Fingerprint, onClick = onBiometricClick)
                } else {
                    Box(modifier = Modifier.size(64.dp))
                }
            }
            
            item {
                KeypadButton(text = "0", onClick = { onDigitClick("0") })
            }
            
            item {
                KeypadIconButton(icon = Icons.Default.Backspace, onClick = onDeleteClick)
            }
        }
    }
}

@Composable
fun KeypadButton(text: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun KeypadIconButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(28.dp))
    }
}
