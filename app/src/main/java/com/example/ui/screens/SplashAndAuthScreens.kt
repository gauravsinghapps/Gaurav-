package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.attemptAutoSignIn
import com.example.auth.onGoogleSignInClicked
import com.example.ui.components.ZivoraLogoBadge
import com.example.ui.theme.ZivoraDeepInk
import com.example.ui.theme.ZivoraMutedInk
import com.example.ui.theme.ZivoraOrange
import com.example.ui.theme.ZivoraOrangeDark
import com.example.ui.theme.ZivoraOrangeLight
import com.example.ui.theme.ZivoraPeachContainer
import com.example.ui.theme.ZivoraPeachSoft
import com.example.ui.theme.ZivoraPureWhite
import kotlinx.coroutines.delay

@Composable
fun ZivoraSplashScreen(
    onSplashComplete: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    var animateIn by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.72f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splash_logo_scale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "splash_alpha"
    )

    LaunchedEffect(Unit) {
        animateIn = true
        var autoAttemptFinished = false
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { autoAttemptFinished = true },
            onUnauthenticated = { autoAttemptFinished = true },
            scope = scope
        )
        delay(1200)
        // Give auto-sign-in a tiny grace window if still resolving
        var waitTicks = 0
        while (!autoAttemptFinished && waitTicks < 10) {
            delay(100)
            waitTicks++
        }
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ZivoraOrange, ZivoraOrangeDark)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .scale(logoScale)
                .alpha(contentAlpha)
        ) {
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = ZivoraPureWhite,
                shadowElevation = 16.dp,
                modifier = Modifier.size(104.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    ZivoraLogoBadge(size = 86.dp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 40.sp,
                    letterSpacing = 3.sp
                ),
                fontWeight = FontWeight.Bold,
                color = ZivoraPureWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.titleMedium,
                color = ZivoraPureWhite.copy(alpha = 0.92f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(36.dp))
            CircularProgressIndicator(
                color = ZivoraPureWhite,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Rides • Grocery • Food",
            style = MaterialTheme.typography.labelMedium,
            color = ZivoraPureWhite.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        )
    }
}

@Composable
fun ZivoraAuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Orange Brand Hero Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ZivoraOrange, ZivoraOrangeLight)
                        ),
                        shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = ZivoraPureWhite,
                        shadowElevation = 10.dp,
                        modifier = Modifier.size(84.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            ZivoraLogoBadge(size = 68.dp)
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displaySmall.copy(letterSpacing = 2.5.sp),
                        fontWeight = FontWeight.Bold,
                        color = ZivoraPureWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.app_tagline),
                        style = MaterialTheme.typography.titleMedium,
                        color = ZivoraPureWhite.copy(alpha = 0.94f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .widthIn(max = 540.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Welcome to ZIVORA",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ZivoraDeepInk
                )
                Text(
                    text = "Sign in once with your Google account to book rides, deliver cargo, order farm-fresh groceries, and enjoy top local meals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ZivoraMutedInk
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AuthFeatureRow(
                        icon = Icons.Default.DirectionsCar,
                        title = "Ride & Cargo Fleet",
                        subtitle = "Car, Taxi, Auto, E-Rickshaw, Pickup & Loader (Bike disabled)"
                    )
                    AuthFeatureRow(
                        icon = Icons.Default.LocalGroceryStore,
                        title = "Express Grocery Delivery",
                        subtitle = "Fresh produce, dairy, bakery & daily staples in 15 mins"
                    )
                    AuthFeatureRow(
                        icon = Icons.Default.Restaurant,
                        title = "Curated Food Ordering",
                        subtitle = "Hot meals from top-rated kitchens delivered to your door"
                    )
                }

                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        errorMessage = null
                        isLoading = true
                        onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = {
                                isLoading = false
                                onAuthSuccess()
                            },
                            onAuthError = { msg ->
                                isLoading = false
                                errorMessage = msg
                            },
                            scope = scope,
                            onAuthCancelled = {
                                isLoading = false
                            }
                        )
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZivoraOrange,
                        contentColor = ZivoraPureWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("google_sign_in_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = ZivoraPureWhite,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Signing in...", style = MaterialTheme.typography.titleMedium)
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = ZivoraPureWhite,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "G",
                                    color = ZivoraOrange,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.sign_in_with_google),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = ZivoraOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Secured with Firebase Auth & Cloud Sync",
                        style = MaterialTheme.typography.labelMedium,
                        color = ZivoraMutedInk
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ZivoraPeachSoft,
                    border = BorderStroke(1.dp, ZivoraPeachContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ZivoraOrangeDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Note: Two-wheeler Bike booking is disabled across ZIVORA for safety compliance.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZivoraMutedInk
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ZivoraPeachContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = ZivoraOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
