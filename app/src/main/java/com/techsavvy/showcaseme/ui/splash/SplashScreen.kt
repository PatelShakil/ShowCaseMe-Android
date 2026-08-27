package com.techsavvy.showcaseme.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.techsavvy.showcaseme.R
import com.techsavvy.showcaseme.common.Brand
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.ui.nav.Screens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, viewModel: SplashViewModel) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    
    // Animation for the "Scanning" line
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    val scanPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    // Floating background particles animation
    val particlesRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    var animationFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Sequential animation
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        
        alpha.animateTo(1f, tween(1000))
        rotation.animateTo(360f, tween(1200, easing = FastOutSlowInEasing))
        
        delay(2000) // Branding exposure
        animationFinished = true
    }

    val loginState = viewModel.loginState.value

    LaunchedEffect(loginState, animationFinished) {
        if (animationFinished && loginState != null && loginState !is Resource.Loading) {
            val destination = when (loginState) {
                is Resource.Success -> if (loginState.result.status) Screens.Home.route else Screens.Login.route
                is Resource.Failure -> Screens.Login.route
                else -> Screens.Login.route
            }
            
            navController.navigate(destination) {
                popUpTo(Screens.Splash.route) {
                    inclusive = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Animated Background Decoration (Floating Squares/Dots)
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { rotationZ = particlesRotation }) {
            val color = Color(0xFF2D8B6F).copy(alpha = 0.05f)
            drawRect(
                color = color,
                topLeft = Offset(size.width * 0.2f, size.height * 0.2f),
                size = androidx.compose.ui.geometry.Size(100f, 100f)
            )
            drawCircle(
                color = color,
                center = Offset(size.width * 0.8f, size.height * 0.7f),
                radius = 50f
            )
            drawRect(
                color = color,
                topLeft = Offset(size.width * 0.7f, size.height * 0.15f),
                size = androidx.compose.ui.geometry.Size(60f, 60f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.brand_logo),
                    contentDescription = "ShowCaseMe Logo",
                    modifier = Modifier
                        .size(180.dp)
                        .scale(scale.value)
                        .alpha(alpha.value)
                        .graphicsLayer { rotationY = rotation.value }
                )
                
                // Scanning Line Effect
                Canvas(modifier = Modifier.size(180.dp).alpha(alpha.value * 0.5f)) {
                    val y = size.height * scanPosition
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color(0xFF2D8B6F), Color.Transparent)
                        ),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 4.dp.toPx()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = Brand.NAME,
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                ),
                modifier = Modifier
                    .alpha(alpha.value)
                    .graphicsLayer { 
                        translationY = (1f - alpha.value) * 50f
                    }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Your Identity, Your Showcase",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.alpha(alpha.value)
            )
            
            Spacer(modifier = Modifier.height(80.dp))
            
            // Modern Animated Loading
            if (loginState is Resource.Loading || !animationFinished) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    val loadingProgress by infiniteTransition.animateFloat(
                        initialValue = -0.5f,
                        targetValue = 1.5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "loadingProgress"
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.3f)
                            .align(Alignment.CenterStart)
                            .graphicsLayer { 
                                translationX = 200.dp.toPx() * loadingProgress
                            }
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.primary,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}
