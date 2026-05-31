/*
 * Copyright (c) 2026 CuangX-by-fachriceg
 * All rights reserved.
 */

package com.cuangx.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cuangx.finance.core.datastore.UserPreferences
import com.cuangx.finance.core.ui.components.LockScreen
import com.cuangx.finance.core.ui.navigation.AppNavHost
import com.cuangx.finance.core.ui.navigation.BottomNavBar
import com.cuangx.finance.core.ui.navigation.Screen
import com.cuangx.finance.core.ui.theme.CuangXFinanceTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkModePreference by userPreferences.darkMode.collectAsStateWithLifecycle(initialValue = "system")
            val biometricEnabled by userPreferences.biometricEnabled.collectAsStateWithLifecycle(initialValue = false)
            val passcodeEnabled by userPreferences.passcodeEnabled.collectAsStateWithLifecycle(initialValue = false)

            var isUnlocked by remember { mutableStateOf(!biometricEnabled && !passcodeEnabled) }

            CuangXFinanceTheme(darkModePreference = darkModePreference) {
                if (!isUnlocked) {
                    LockScreen(
                        isBiometricEnabled = biometricEnabled,
                        isPasscodeEnabled = passcodeEnabled,
                        onUnlock = { isUnlocked = true }
                    )
                } else {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val showBottomBar = currentRoute in listOf(
                        Screen.Dashboard.route,
                        Screen.TransactionList.route,
                        Screen.JournalList.route,
                        Screen.PortfolioOverview.route,
                        Screen.More.route
                    )

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            if (showBottomBar) {
                                BottomNavBar(navController = navController)
                            }
                        }
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
