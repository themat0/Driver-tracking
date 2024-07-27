package com.example.drivertracking

import androidx.annotation.StringRes
import CameraPreview
import com.example.drivertracking.features.settings.view.SettingsScreen
import com.example.drivertracking.features.logs.view.LogsScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.drivertracking.ui.theme.Purple80
import com.example.drivertracking.features.settings.viewmodel.SettingsViewModel

enum class DriverScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Settings(title = R.string.app_name),
    Camera(title = R.string.app_name),
    Logs(title = R.string.app_name)
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverAppBar(
    currentScreen: DriverScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "stringResource(R.string.back_button)"
                    )
                }
            }
        }
    )
}

@Composable
fun DriverApp(
    settingsViewModel: SettingsViewModel,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = DriverScreen.valueOf(
        backStackEntry?.destination?.route ?: DriverScreen.Start.name
    )
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(
        topBar = {
            DriverAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DriverScreen.Start.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = DriverScreen.Start.name) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize().padding(horizontal = screenWidth/3)
                ) {
                    Button(
                        onClick = { navController.navigate(DriverScreen.Settings.name) }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).padding(vertical = screenHeight/50)
                        ) {

                            Icon(
                                Icons.Rounded.Settings,
                                contentDescription = "Settings"
                            )
                            Spacer(modifier = Modifier.padding(all = Dp(5F)))
                            Text("Settings")
                        }
                    }
                    Button(
                        onClick = { navController.navigate(DriverScreen.Camera.name) },
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).padding(vertical = screenHeight/50),
                        ) {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = "camera"
                            )
                            Spacer(modifier = Modifier.padding(all = Dp(5F)))
                            Text("Camera")
                        }
                    }
                    Button(
                        onClick = { navController.navigate(DriverScreen.Logs.name) },
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).padding(vertical = screenHeight/50),
                        ) {
                            Icon(
                                Icons.Rounded.Menu,
                                contentDescription = "logs"
                            )
                            Spacer(modifier = Modifier.padding(all = Dp(5F)))
                            Text("Logs")
                        }

                    }
                }
            }

            composable(route = DriverScreen.Settings.name) {
                SettingsScreen(settingsViewModel = settingsViewModel)
            }
            composable(route = DriverScreen.Camera.name) {
                CameraPreview()
            }
            composable(route = DriverScreen.Logs.name) {
                LogsScreen()
            }
        }
    }
}
