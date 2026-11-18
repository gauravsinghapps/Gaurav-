package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.auth.authStateFlow
import com.example.auth.signOutUser
import com.example.data.UiState
import com.example.data.ZivoraCatalog
import com.example.data.ZivoraRepository
import com.example.ui.components.AddressPickerDialog
import com.example.ui.components.ZivoraLogoBadge
import com.example.ui.screens.ActivityAndProfileScreen
import com.example.ui.screens.FoodScreen
import com.example.ui.screens.GroceryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RideBookingScreen
import com.example.ui.screens.ZivoraAuthScreen
import com.example.ui.screens.ZivoraSplashScreen
import com.example.ui.theme.ZivoraOrange
import com.example.ui.theme.ZivoraOrangeDark
import com.example.ui.theme.ZivoraPeachContainer
import com.example.ui.theme.ZivoraPureWhite
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

enum class ZivoraTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("zivora_home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    RIDES("zivora_rides", "Rides", Icons.Filled.DirectionsCar, Icons.Outlined.DirectionsCar),
    GROCERY("zivora_grocery", "Grocery", Icons.Filled.LocalGroceryStore, Icons.Outlined.LocalGroceryStore),
    FOOD("zivora_food", "Food", Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    ACTIVITY("zivora_activity", "Activity", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong)
}

@Composable
fun ZivoraApp(auth: FirebaseAuth = Firebase.auth) {
    var splashCompleted by remember { mutableStateOf(false) }
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)

    if (!splashCompleted) {
        ZivoraSplashScreen(onSplashComplete = { splashCompleted = true })
    } else {
        val user = currentUser
        if (user == null) {
            ZivoraAuthScreen(onAuthSuccess = {})
        } else {
            key(user.uid) {
                ZivoraAuthenticatedShell(
                    currentUserId = user.uid,
                    userDisplayName = user.displayName ?: "ZIVORA Member",
                    userEmail = user.email ?: ""
                )
            }
        }
    }
}

@Composable
private fun ZivoraAuthenticatedShell(
    currentUserId: String,
    userDisplayName: String,
    userEmail: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddressDialog by remember { mutableStateOf(false) }

    val viewModel: ZivoraViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                ZivoraViewModel(
                    repository = ZivoraRepository(db),
                    currentUserId = currentUserId,
                    userDisplayName = userDisplayName,
                    userEmail = userEmail
                )
            }
        }
    )

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val addressesState by viewModel.addressesState.collectAsStateWithLifecycle()
    val ridesState by viewModel.rideBookingsState.collectAsStateWithLifecycle()
    val ordersState by viewModel.ordersState.collectAsStateWithLifecycle()
    val rideDraft by viewModel.rideDraft.collectAsStateWithLifecycle()
    val groceryCart by viewModel.groceryCart.collectAsStateWithLifecycle()
    val foodCart by viewModel.foodCart.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    LaunchedEffect(feedbackMessage) {
        val msg = feedbackMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearFeedback()
    }

    val activeAddress = (profileState as? UiState.Success)?.data?.defaultAddress
        ?.takeIf { it.isNotBlank() } ?: ZivoraCatalog.popularLocations.first()
    val resolvedName = (profileState as? UiState.Success)?.data?.displayName
        ?.takeIf { it.isNotBlank() } ?: userDisplayName
    val savedAddresses = (addressesState as? UiState.Success)?.data ?: emptyList()
    val activeRides = (ridesState as? UiState.Success)?.data ?: emptyList()
    val activeOrders = (ordersState as? UiState.Success)?.data ?: emptyList()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigateToTab: (ZivoraTab) -> Unit = { tab ->
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 680.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = ZivoraPureWhite,
                        tonalElevation = 6.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        ZivoraTab.entries.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            val badgeCount = when (tab) {
                                ZivoraTab.GROCERY -> groceryCart.totalItemCount
                                ZivoraTab.FOOD -> foodCart.totalItemCount
                                else -> 0
                            }
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateToTab(tab) },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (badgeCount > 0) {
                                                Badge(containerColor = ZivoraOrange) {
                                                    Text(badgeCount.toString(), color = ZivoraPureWhite)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ZivoraOrangeDark,
                                    selectedTextColor = ZivoraOrangeDark,
                                    indicatorColor = ZivoraPeachContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = ZivoraPureWhite,
                        header = {
                            ZivoraLogoBadge(
                                size = 40.dp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        },
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        ZivoraTab.entries.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ZivoraOrangeDark,
                                    selectedTextColor = ZivoraOrangeDark,
                                    indicatorColor = ZivoraPeachContainer
                                ),
                                modifier = Modifier.testTag("rail_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = ZivoraTab.HOME.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(ZivoraTab.HOME.route) {
                            HomeScreen(
                                displayName = resolvedName,
                                activeAddress = activeAddress,
                                activeRides = activeRides,
                                activeOrders = activeOrders,
                                onChangeAddressClick = { showAddressDialog = true },
                                onSelectVehicleAndBook = { vehicle ->
                                    viewModel.selectVehicle(vehicle)
                                    navigateToTab(ZivoraTab.RIDES)
                                },
                                onOpenGrocery = { navigateToTab(ZivoraTab.GROCERY) },
                                onOpenFood = { navigateToTab(ZivoraTab.FOOD) },
                                onOpenActivity = { navigateToTab(ZivoraTab.ACTIVITY) }
                            )
                        }
                        composable(ZivoraTab.RIDES.route) {
                            RideBookingScreen(
                                draft = rideDraft,
                                activeRides = activeRides,
                                onSelectVehicle = viewModel::selectVehicle,
                                onPickupChange = viewModel::updateRidePickup,
                                onDropChange = viewModel::updateRideDrop,
                                onSwapLocations = viewModel::swapRideLocations,
                                onPaymentChange = viewModel::updateRidePayment,
                                onTogglePromo = viewModel::toggleRidePromo,
                                onConfirmBooking = {
                                    viewModel.confirmRideBooking()
                                },
                                onAdvanceRideStatus = viewModel::advanceRideStatus
                            )
                        }
                        composable(ZivoraTab.GROCERY.route) {
                            GroceryScreen(
                                cartState = groceryCart,
                                activeAddress = activeAddress,
                                onCategorySelect = viewModel::setGroceryCategory,
                                onSearchChange = viewModel::setGrocerySearch,
                                onAdjustQuantity = viewModel::adjustGroceryQuantity,
                                onSlotSelect = viewModel::setGrocerySlot,
                                onPaymentSelect = viewModel::setGroceryPayment,
                                onPlaceOrder = {
                                    viewModel.placeGroceryOrder(activeAddress) {
                                        navigateToTab(ZivoraTab.ACTIVITY)
                                    }
                                }
                            )
                        }
                        composable(ZivoraTab.FOOD.route) {
                            FoodScreen(
                                cartState = foodCart,
                                activeAddress = activeAddress,
                                onCuisineSelect = viewModel::setFoodCuisine,
                                onSearchChange = viewModel::setFoodSearch,
                                onAdjustFoodQuantity = viewModel::adjustFoodQuantity,
                                onPaymentSelect = viewModel::setFoodPayment,
                                onPlaceFoodOrder = {
                                    viewModel.placeFoodOrder(activeAddress) {
                                        navigateToTab(ZivoraTab.ACTIVITY)
                                    }
                                }
                            )
                        }
                        composable(ZivoraTab.ACTIVITY.route) {
                            ActivityAndProfileScreen(
                                displayName = resolvedName,
                                email = userEmail,
                                activeAddress = activeAddress,
                                ridesState = ridesState,
                                ordersState = ordersState,
                                addressesState = addressesState,
                                onAdvanceRideStatus = viewModel::advanceRideStatus,
                                onAdvanceOrderStatus = viewModel::advanceOrderStatus,
                                onOpenAddressDialog = { showAddressDialog = true },
                                onSelectActiveAddress = viewModel::setActiveAddress,
                                onDeleteAddress = viewModel::deleteAddress,
                                onSignOut = {
                                    signOutUser(
                                        credentialManager = credentialManager,
                                        scope = scope
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddressDialog) {
        AddressPickerDialog(
            currentAddress = activeAddress,
            savedAddresses = savedAddresses,
            onSelectAddress = viewModel::setActiveAddress,
            onAddAddress = { label, fullAddr, landmark ->
                viewModel.addAddress(label, fullAddr, landmark)
            },
            onDismiss = { showAddressDialog = false }
        )
    }
}
