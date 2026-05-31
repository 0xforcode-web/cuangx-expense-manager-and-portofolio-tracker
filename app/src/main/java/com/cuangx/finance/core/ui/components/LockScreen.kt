package com.cuangx.finance.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.cuangx.finance.core.util.BiometricUtils

@Composable
fun LockScreen(
    isBiometricEnabled: Boolean,
    isPasscodeEnabled: Boolean,
    onUnlock: () -> Unit
) {
    val context = LocalContext.current
    var isAuthenticated by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (isBiometricEnabled && BiometricUtils.isBiometricAvailable(context)) {
            // Will trigger biometric prompt on button click
        } else if (!isPasscodeEnabled) {
            // No lock enabled, unlock immediately
            isAuthenticated = true
            onUnlock()
        }
    }

    if (isAuthenticated) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Lock",
            modifier = Modifier.size(80.dp),
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
            text = "Authenticate to continue",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isBiometricEnabled && BiometricUtils.isBiometricAvailable(context)) {
            Button(
                onClick = {
                    val activity = context as? FragmentActivity
                    activity?.let {
                        BiometricUtils.showBiometricPrompt(
                            activity = it,
                            onSuccess = {
                                isAuthenticated = true
                                onUnlock()
                            },
                            onError = { error ->
                                errorMessage = error
                            },
                            onFailed = {
                                errorMessage = "Authentication failed. Try again."
                            }
                        )
                    }
                }
            ) {
                Text("Unlock with Biometric")
            }
        }

        if (isPasscodeEnabled) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Or enter your passcode",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Passcode input would go here
            // For now, just a simple unlock button
            Button(
                onClick = {
                    isAuthenticated = true
                    onUnlock()
                }
            ) {
                Text("Enter Passcode")
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}
