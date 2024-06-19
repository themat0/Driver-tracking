package com.example.drivertracking

import androidx.annotation.StringRes
import com.example.drivertracking.ui.CameraPreview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.drivertracking.ui.theme.Purple80

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
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = DriverScreen.valueOf(
        backStackEntry?.destination?.route ?: DriverScreen.Start.name
    )

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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = { navController.navigate(DriverScreen.Settings.name) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Purple80)
                    ) {
                        Text("Settings")
                    }
                    Button(
                        onClick = { navController.navigate(DriverScreen.Camera.name) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Purple80)
                    ) {
                        Text("Camera")
                    }
                    Button(
                        onClick = { navController.navigate(DriverScreen.Logs.name) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Purple80)
                    ) {
                        Text("Logs")
                    }
                }
            }
            composable(route = DriverScreen.Settings.name) {
                val context = LocalContext.current
                Text(text = "Settings")
            }
            composable(route = DriverScreen.Camera.name) {
                CameraPreview()
            }
            composable(route = DriverScreen.Logs.name) {
                Text(text = "Logs")
                val context = LocalContext.current
            }
        }
    }
}
