package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Modifier.sophisticatedDarkBackground(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_glass")
    
    val orb1X by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1_x"
    )
    val orb1Y by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(19000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1_y"
    )
    
    val orb2X by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(17000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2_x"
    )
    val orb2Y by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(13000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2_y"
    )

    val orb3X by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(21000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb3_x"
    )
    val orb3Y by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb3_y"
    )

    return this.drawBehind {
        // Deep solid metal core
        drawRect(color = Color(0xFF090A10))
        
        // Fluid dark metallic background
        val bgBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF131522), Color(0xFF030305), Color(0xFF0F1017))
        )
        drawRect(brush = bgBrush)

        // Orb 1: Glowing Fluid Tangerine Orange - representing the dynamic Cusbuy orange
        drawCircle(
            color = Color(0xFFFF5722).copy(alpha = 0.16f),
            radius = size.width * 0.65f,
            center = androidx.compose.ui.geometry.Offset(size.width * orb1X, size.height * orb1Y)
        )

        // Orb 2: Glowing Liquid Cyan/Turquoise
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = 0.14f),
            radius = size.width * 0.55f,
            center = androidx.compose.ui.geometry.Offset(size.width * orb2X, size.height * orb2Y)
        )

        // Orb 3: Glowing Soft Amethyst Purple
        drawCircle(
            color = Color(0xFFD500F9).copy(alpha = 0.12f),
            radius = size.width * 0.60f,
            center = androidx.compose.ui.geometry.Offset(size.width * orb3X, size.height * orb3Y)
        )
    }
}

@Composable
fun Modifier.liquidGlassCard(
    cornerRadius: androidx.compose.ui.unit.Dp = 20.dp,
    borderWidth: androidx.compose.ui.unit.Dp = 1.dp
): Modifier {
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(Color.White.copy(alpha = 0.06f))
        .border(
            width = borderWidth,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.22f),
                    Color.White.copy(alpha = 0.03f),
                    Color.White.copy(alpha = 0.15f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}

@Composable
fun CusbuyLogoSymbol(
    modifier: Modifier = Modifier,
    outlineColor: Color = CusbuyWhite,
    innerPlayColor: Color = CusbuyOrange
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        // Stroke widths are perfectly proportional
        val strokeWidth = w * 0.075f
        
        // 1. Draw the Bag Handle (top loop)
        val handlePath = Path().apply {
            moveTo(w * 0.43f, h * 0.28f)
            cubicTo(
                w * 0.43f, h * 0.10f,
                w * 0.57f, h * 0.10f,
                w * 0.57f, h * 0.28f
            )
        }
        drawPath(
            path = handlePath,
            color = outlineColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 2. Draw the Bag Body (Back flap outline)
        val bagBodyPath = Path().apply {
            moveTo(w * 0.43f, h * 0.28f)
            lineTo(w * 0.57f, h * 0.28f)
            lineTo(w * 0.61f, h * 0.58f)
            lineTo(w * 0.41f, h * 0.58f)
        }
        drawPath(
            path = bagBodyPath,
            color = outlineColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 3. Draw the main tilted Play Button (the outer triangle)
        val playButtonPath = Path().apply {
            moveTo(w * 0.44f, h * 0.30f) // Left-top
            lineTo(w * 0.61f, h * 0.425f) // Right point
            lineTo(w * 0.40f, h * 0.56f) // Left-bottom
            close()
        }
        drawPath(
            path = playButtonPath,
            color = outlineColor,
            style = Stroke(
                width = strokeWidth * 1.05f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 4. Draw the INNER play button (vibrant orange/red triangle)
        val innerPlayPath = Path().apply {
            moveTo(w * 0.465f, h * 0.365f) // top-left nested
            lineTo(w * 0.54f, h * 0.425f) // nested right point
            lineTo(w * 0.45f, h * 0.495f) // nested bottom-left
            close()
        }
        drawPath(
            path = innerPlayPath,
            color = innerPlayColor,
            style = Stroke(
                width = strokeWidth * 0.85f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun CusbuyFullLogo(
    modifier: Modifier = Modifier,
    textColorCus: Color = CusbuyWhite,
    textColorBuy: Color = CusbuyOrange,
    symbolOutlineColor: Color = CusbuyWhite,
    symbolInnerColor: Color = CusbuyOrange,
    showTagline: Boolean = true,
    fontSize: androidx.compose.ui.unit.TextUnit = 32.sp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CusbuyLogoSymbol(
            modifier = Modifier.size(105.dp),
            outlineColor = symbolOutlineColor,
            innerPlayColor = symbolInnerColor
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Cus",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = fontSize),
                fontWeight = FontWeight.Black,
                color = textColorCus
            )
            Text(
                text = "buy",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = fontSize),
                fontWeight = FontWeight.Black,
                color = textColorBuy
            )
        }
        if (showTagline) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "VIDEO SHOPPING | LIVRAISON LOCALE",
                fontSize = (fontSize.value * 0.3125f).sp,
                color = textColorCus.copy(alpha = 0.54f),
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun CusbuyApp(viewModel: CusbuyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userMode by viewModel.userMode.collectAsState()
    val profile by viewModel.profileState.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val allActions by viewModel.allActions.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .sophisticatedDarkBackground()
            .testTag("app_container"),
        color = Color.Transparent
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                "ONBOARDING_SPLASH" -> OnboardingSplash(viewModel)
                "ONBOARDING_REGISTRATION" -> OnboardingRegistration(viewModel)
                "ONBOARDING_QUESTIONS" -> OnboardingQuestions(viewModel)
                "ONBOARDING_CHOICE" -> OnboardingChoice(viewModel)
                "MAIN_APP" -> MainAppScaffold(viewModel, userMode, profile, posts, stories, allActions)
                "PUBLISH_CAM" -> PublishWizard(viewModel)
                "LIVE_SCREEN" -> LiveStreamRoom(viewModel)
                else -> OnboardingSplash(viewModel)
            }
        }
    }
}

// ==========================================
// A — MODE SWITCHER HEADER & BOTTOM SHEET
// ==========================================
@Composable
fun ModeSwitcherHeader(
    userMode: UserMode,
    walletCoins: Int,
    onSwitchRequest: (UserMode) -> Unit,
    onNavigateToCoins: () -> Unit
) {
    val accentColor = if (userMode == UserMode.ACHETEUR) CusbuyOrange else CusbuyGreen
    var showSheet by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("header_mode_switcher"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode Switcher Pill Button
        Button(
            onClick = { showSheet = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF161616),
                contentColor = CusbuyWhite
            ),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("mode_toggle_pills")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (userMode == UserMode.ACHETEUR) "🛍️ " else "🏪 ",
                    fontSize = 13.sp
                )
                Text(
                    text = if (userMode == UserMode.ACHETEUR) "Acheteur" else "Vendeur",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Changer",
                    tint = CusbuyWhite.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // CB / CUSBUY Brand logo center
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CusbuyLogoSymbol(
                modifier = Modifier.size(28.dp),
                outlineColor = CusbuyWhite,
                innerPlayColor = CusbuyOrange
            )
            Spacer(modifier = Modifier.width(6.dp))
            Row {
                Text(
                    text = "Cus",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = CusbuyWhite,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "buy",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = CusbuyOrange,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        // Coins Top-up Shortcut
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onNavigateToCoins() }
                .background(CusbuyDarkSurface)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = "Coins",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$walletCoins",
                color = CusbuyWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showSheet) {
        Dialog(onDismissRequest = { showSheet = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, CusbuyWhite.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choisir l'Espace de travail",
                        style = MaterialTheme.typography.titleMedium,
                        color = CusbuyWhite,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Un seul compte lié pour acheter et vendre instantanément.",
                        fontSize = 12.sp,
                        color = CusbuyTextGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Option Acheteur
                    Card(
                        onClick = {
                            onSwitchRequest(UserMode.ACHETEUR)
                            showSheet = false
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (userMode == UserMode.ACHETEUR) CusbuyOrange.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (userMode == UserMode.ACHETEUR) CusbuyOrange else CusbuyWhite.copy(alpha = 0.08f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("select_buyer_mode_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🛍️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Passer en mode Acheteur", fontWeight = FontWeight.Bold, color = CusbuyWhite)
                                Text("Découvrir les feeds vidéos et commander en 1-tap", fontSize = 11.sp, color = CusbuyTextGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option Vendeur
                    Card(
                        onClick = {
                            onSwitchRequest(UserMode.VENDEUR)
                            showSheet = false
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (userMode == UserMode.VENDEUR) CusbuyGreen.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (userMode == UserMode.VENDEUR) CusbuyGreen else CusbuyWhite.copy(alpha = 0.08f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("select_seller_mode_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏪", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Passer en mode Vendeur", fontWeight = FontWeight.Bold, color = CusbuyWhite)
                                Text("Dashboard ventes, publier, gérer et lancer des Lives", fontSize = 11.sp, color = CusbuyTextGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = { showSheet = false }) {
                        Text("Annuler", color = CusbuyWhite)
                    }
                }
            }
        }
    }
}

// ==========================================
// B — SPLASH, ONBOARDING REGISTRATION & CHOICE
// ==========================================
@Composable
fun OnboardingSplash(viewModel: CusbuyViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .sophisticatedDarkBackground(),
        contentAlignment = Alignment.Center
    ) {
        // Ambient looping product dots simulated on canvas for TikTok style vibe
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = CusbuyOrange.copy(alpha = 0.08f),
                radius = size.width / 1.5f,
                center = androidx.compose.ui.geometry.Offset(size.width, size.height / 3)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Upper Section: Rotating/Animating logo
            CusbuyFullLogo(
                textColorCus = CusbuyWhite,
                textColorBuy = CusbuyOrange,
                symbolOutlineColor = CusbuyWhite,
                symbolInnerColor = CusbuyOrange,
                showTagline = true,
                fontSize = 42.sp
            )

                Spacer(modifier = Modifier.height(24.dp))
                
                // Video Loop Simulation visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(120.dp)
                        .liquidGlassCard(cornerRadius = 16.dp, borderWidth = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Videocam, "Video", tint = CusbuyWhite)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Démo Shopping Live", color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Achetez les meilleurs articles locaux en un seul clic !", color = CusbuyTextGray, fontSize = 11.sp)
                        }
                    }
                }

            // Lower Section: Primary Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.proceedToRegistration() },
                    colors = ButtonDefaults.buttonColors(containerColor = CusbuyOrange),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_create_account")
                ) {
                    Text("Créer un compte", color = CusbuyWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { viewModel.proceedToRegistration() },
                    border = BorderStroke(1.dp, CusbuyWhite.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CusbuyWhite),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Se connecter", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Continuer sans compte",
                    color = CusbuyTextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { viewModel.skipRegistrationAndContinue() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun OnboardingRegistration(viewModel: CusbuyViewModel) {
    var email by remember { mutableStateOf(viewModel.regEmail) }
    var nickname by remember { mutableStateOf(viewModel.regNickname) }
    var password by remember { mutableStateOf("••••••••") }
    var isSellerPreferred by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("ONBOARDING_SPLASH") }) {
                    Icon(Icons.Default.ArrowBack, "Retour", tint = CusbuyWhite)
                }
                Text("Créer votre Compte", style = MaterialTheme.typography.titleLarge, color = CusbuyWhite, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Inscrivez-vous gratuitement pour interagir avec les vendeurs de votre région.", color = CusbuyTextGray, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Surnom / Pseudo", color = CusbuyWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = nickname,
                onValueChange = { nickname = it; viewModel.regNickname = it },
                modifier = Modifier.fillMaxWidth().testTag("input_nickname"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CusbuyWhite,
                    unfocusedTextColor = CusbuyWhite,
                    focusedContainerColor = CusbuyDarkSurface,
                    unfocusedContainerColor = CusbuyDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Adresse email", color = CusbuyWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = email,
                onValueChange = { email = it; viewModel.regEmail = it },
                modifier = Modifier.fillMaxWidth().testTag("input_email"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CusbuyWhite,
                    unfocusedTextColor = CusbuyWhite,
                    focusedContainerColor = CusbuyDarkSurface,
                    unfocusedContainerColor = CusbuyDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Mot de passe", color = CusbuyWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CusbuyWhite,
                    unfocusedTextColor = CusbuyWhite,
                    focusedContainerColor = CusbuyDarkSurface,
                    unfocusedContainerColor = CusbuyDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Check to see if they prefer seller mode
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isSellerPreferred = !isSellerPreferred }
                    .background(CusbuyDarkSurface)
                    .padding(12.dp)
            ) {
                Checkbox(
                    checked = isSellerPreferred,
                    onCheckedChange = { isSellerPreferred = it },
                    colors = CheckboxDefaults.colors(checkedColor = CusbuyOrange)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Je souhaite vendre aussi !", color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Activer mon dashboard et mes options de publication", color = CusbuyTextGray, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isSellerPreferred) {
                        viewModel.navigateTo("ONBOARDING_QUESTIONS")
                    } else {
                        viewModel.submitRegistration(false)
                        viewModel.navigateTo("ONBOARDING_CHOICE")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CusbuyOrange),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_next_register")
            ) {
                Text(
                    text = if (isSellerPreferred) "Continuer vers les infos Vendeur →" else "S'inscrire",
                    color = CusbuyWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OnboardingQuestions(viewModel: CusbuyViewModel) {
    var businessName by remember { mutableStateOf(viewModel.regBusinessName.ifEmpty { "Ma Super Boutique" }) }
    var city by remember { mutableStateOf(viewModel.regCity.ifEmpty { "Douala" }) }
    var location by remember { mutableStateOf(viewModel.regLocation.ifEmpty { "Marché central, Stand 4" }) }
    var googleAccount by remember { mutableStateOf(viewModel.regGoogleAccount.ifEmpty { viewModel.regEmail }) }
    var category by remember { mutableStateOf(viewModel.regCategory.ifEmpty { "Cosmétiques & Mode" }) }
    var phone by remember { mutableStateOf(viewModel.regPhone.ifEmpty { "+237 60000000" }) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .statusBarsPadding()
            .testTag("onboarding_questions_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("ONBOARDING_REGISTRATION") }) {
                    Icon(Icons.Default.ArrowBack, "Retour", tint = CusbuyWhite)
                }
                Text("Profil Vendeur 🏪", style = MaterialTheme.typography.titleLarge, color = CusbuyGreen, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Pour commencer à vendre, veuillez fournir ces informations complémentaires exigées pour sécuriser la livraison locale.", color = CusbuyTextGray, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // City
            Text("Ville d'expédition", color = CusbuyWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = city,
                onValueChange = { city = it; viewModel.regCity = it },
                modifier = Modifier.fillMaxWidth().testTag("input_seller_city"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CusbuyWhite,
                    unfocusedTextColor = CusbuyWhite,
                    focusedContainerColor = CusbuyDarkSurface,
                    unfocusedContainerColor = CusbuyDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Business Name
            Text("Nom de la Boutique", color = CusbuyWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = businessName,
                onValueChange = { businessName = it; viewModel.regBusinessName = it },
                modifier = Modifier.fillMaxWidth().testTag("input_seller_business"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CusbuyWhite,
                    unfocusedTextColor = CusbuyWhite,
                    focusedContainerColor = CusbuyDarkSurface,
                    unfocusedContainerColor = CusbuyDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Location
            Text("Adresse précise de retrait physique", color = CusbuyWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = location,
                onValueChange = { location = it; viewModel.regLocation = it },
                modifier = Modifier.fillMaxWidth().testTag("input_seller_location"),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CusbuyWhite,
                    unfocusedTextColor = CusbuyWhite,
                    focusedContainerColor = CusbuyDarkSurface,
                    unfocusedContainerColor = CusbuyDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Google account linking
            Text("Compte Google associé", color = CusbuyWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = googleAccount,
                onValueChange = { googleAccount = it; viewModel.regGoogleAccount = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CusbuyWhite,
                    unfocusedTextColor = CusbuyWhite,
                    focusedContainerColor = CusbuyDarkSurface,
                    unfocusedContainerColor = CusbuyDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category choice
            Text("Catégorie d'articles", color = CusbuyWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = category,
                onValueChange = { category = it; viewModel.regCategory = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CusbuyWhite,
                    unfocusedTextColor = CusbuyWhite,
                    focusedContainerColor = CusbuyDarkSurface,
                    unfocusedContainerColor = CusbuyDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            Text("Numéro de téléphone d'assistance", color = CusbuyWhite, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it; viewModel.regPhone = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = CusbuyWhite,
                    unfocusedTextColor = CusbuyWhite,
                    focusedContainerColor = CusbuyDarkSurface,
                    unfocusedContainerColor = CusbuyDarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    viewModel.saveCompleteSellerOnboarding()
                    viewModel.navigateTo("ONBOARDING_CHOICE")
                },
                colors = ButtonDefaults.buttonColors(containerColor = CusbuyGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_save_seller_questions")
            ) {
                Text("Finaliser et Passer au Choix Mode →", color = CusbuyWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OnboardingChoice(viewModel: CusbuyViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .sophisticatedDarkBackground()
            .padding(24.dp)
            .statusBarsPadding()
            .testTag("onboarding_choice_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Par où commencer ?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = CusbuyWhite,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "L'intégralité des outils vous attend. Sélectionnez votre espace de départ.",
                    color = CusbuyTextGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Cards stacked or side by side
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card 1: Acheteur
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_buyer_onboarding")
                        .liquidGlassCard(cornerRadius = 20.dp, borderWidth = 1.dp)
                        .clickable { viewModel.completeOnboardingChoice(UserMode.ACHETEUR) }
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🛍️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Je veux acheter", color = CusbuyWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Découvre des milliers de produits locaux en vidéo live authentique et s'auto-approvisionne avec des d'achats sécurisés à proximité.",
                            color = CusbuyTextGray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Commencer à explorer →", color = CusbuyOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                // Card 2: Vendeur
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_seller_onboarding")
                        .liquidGlassCard(cornerRadius = 20.dp, borderWidth = 1.dp)
                        .clickable { viewModel.completeOnboardingChoice(UserMode.VENDEUR) }
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏪", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Je veux vendre", color = CusbuyWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Publie des vidéos courtes ou fiches produits, lance des direct Lives animés avec cadeaux, et touche des centaines de personnes.",
                            color = CusbuyTextGray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Commencer à vendre →", color = CusbuyGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Tu pourras switcher entre les deux modes à tout moment librement",
                    color = CusbuyTextGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==========================================
// C — MAIN VIEW: SCAFFOLD WITH TAB DIRECTIVES
// ==========================================
@Composable
fun MainAppScaffold(
    viewModel: CusbuyViewModel,
    userMode: UserMode,
    profile: ProfileEntity,
    posts: List<VideoPost>,
    stories: List<SellerStory>,
    allActions: List<ActionEntity>
) {
    // Current Active tab for both modes
    var activeBuyerTab by remember { mutableStateOf("FEED") } // "FEED", "SEARCH", "ACTIVITE", "PROFIL"
    var activeSellerTab by remember { mutableStateOf("DASHBOARD") } // "DASHBOARD", "VIDEOS", "ORDERS", "LIVE", "PROFILE"

    Scaffold(
        topBar = {
            ModeSwitcherHeader(
                userMode = userMode,
                walletCoins = profile.walletCoins,
                onSwitchRequest = { mode -> viewModel.switchUserMode(mode) },
                onNavigateToCoins = {
                    if (userMode == UserMode.ACHETEUR) {
                        activeBuyerTab = "PROFIL"
                    } else {
                        activeSellerTab = "PROFILE"
                    }
                }
            )
        },
        bottomBar = {
            if (userMode == UserMode.ACHETEUR) {
                NavigationBar(
                    containerColor = CusbuyDarkBg,
                    contentColor = CusbuyWhite,
                    modifier = Modifier.testTag("buyer_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = activeBuyerTab == "FEED",
                        onClick = { activeBuyerTab = "FEED" },
                        icon = { Icon(Icons.Default.Home, "Feed") },
                        label = { Text("Feed") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CusbuyOrange, indicatorColor = CusbuyOrange.copy(alpha = 0.15f))
                    )
                    NavigationBarItem(
                        selected = activeBuyerTab == "SEARCH",
                        onClick = { activeBuyerTab = "SEARCH" },
                        icon = { Icon(Icons.Default.Search, "Recherche") },
                        label = { Text("Rechercher") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CusbuyOrange, indicatorColor = CusbuyOrange.copy(alpha = 0.15f))
                    )
                    NavigationBarItem(
                        selected = activeBuyerTab == "ACTIVITE",
                        onClick = { activeBuyerTab = "ACTIVITE" },
                        icon = { Icon(Icons.Default.Analytics, "Achats") },
                        label = { Text("Achats") },
                        modifier = Modifier.testTag("buyer_tab_actions"),
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CusbuyOrange, indicatorColor = CusbuyOrange.copy(alpha = 0.15f))
                    )
                    NavigationBarItem(
                        selected = activeBuyerTab == "PROFIL",
                        onClick = { activeBuyerTab = "PROFIL" },
                        icon = { Icon(Icons.Default.Person, "Profil") },
                        label = { Text("Profil") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CusbuyOrange, indicatorColor = CusbuyOrange.copy(alpha = 0.15f))
                    )
                }
            } else {
                NavigationBar(
                    containerColor = CusbuyDarkBg,
                    contentColor = CusbuyWhite,
                    modifier = Modifier.testTag("seller_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = activeSellerTab == "DASHBOARD",
                        onClick = { activeSellerTab = "DASHBOARD" },
                        icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                        label = { Text("Metrics") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CusbuyGreen, indicatorColor = CusbuyGreen.copy(alpha = 0.15f))
                    )
                    NavigationBarItem(
                        selected = activeSellerTab == "VIDEOS",
                        onClick = { activeSellerTab = "VIDEOS" },
                        icon = { Icon(Icons.Default.VideoLibrary, "Vidéos") },
                        label = { Text("Mes Posts") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CusbuyGreen, indicatorColor = CusbuyGreen.copy(alpha = 0.15f))
                    )
                    NavigationBarItem(
                        selected = activeSellerTab == "ORDERS",
                        onClick = { activeSellerTab = "ORDERS" },
                        icon = { Icon(Icons.Default.Assessment, "Ventes") },
                        label = { Text("Ventes") },
                        modifier = Modifier.testTag("seller_tab_actions"),
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CusbuyGreen, indicatorColor = CusbuyGreen.copy(alpha = 0.15f))
                    )
                    NavigationBarItem(
                        selected = activeSellerTab == "LIVE",
                        onClick = { activeSellerTab = "LIVE" },
                        icon = { Icon(Icons.Default.CellTower, "Live Hub") },
                        label = { Text("Live") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CusbuyGreen, indicatorColor = CusbuyGreen.copy(alpha = 0.15f))
                    )
                    NavigationBarItem(
                        selected = activeSellerTab == "PROFILE",
                        onClick = { activeSellerTab = "PROFILE" },
                        icon = { Icon(Icons.Default.Storefront, "Boutique") },
                        label = { Text("Ma Boutique") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = CusbuyGreen, indicatorColor = CusbuyGreen.copy(alpha = 0.15f))
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (userMode == UserMode.ACHETEUR) {
                // BUYER SPACE
                when (activeBuyerTab) {
                    "FEED" -> BuyerFeedView(viewModel, posts, stories)
                    "SEARCH" -> BuyerSearchView(viewModel)
                    "ACTIVITE" -> ActionTrackerPage(viewModel, allActions, ActionType.BUY)
                    "PROFIL" -> ProfileView(viewModel, profile, userMode)
                    else -> BuyerFeedView(viewModel, posts, stories)
                }
            } else {
                // SELLER SPACE
                when (activeSellerTab) {
                    "DASHBOARD" -> SellerDashboard(viewModel, allActions)
                    "VIDEOS" -> SellerVideosTab(posts, profile)
                    "ORDERS" -> ActionTrackerPage(viewModel, allActions, ActionType.SELL)
                    "LIVE" -> SellerLiveSetup(viewModel)
                    "PROFILE" -> ProfileView(viewModel, profile, userMode)
                    else -> SellerDashboard(viewModel, allActions)
                }
            }
        }
    }
}

// ==========================================
// D — TIKTOK FEED EXPERIENCES & STORIES WIDGETS
// ==========================================
@Composable
fun BuyerFeedView(
    viewModel: CusbuyViewModel,
    posts: List<VideoPost>,
    stories: List<SellerStory>
) {
    val activeIdx by viewModel.activePostIndex.collectAsState()
    val isPlaying by viewModel.isVideoPlaying.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Stories space at the top
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(CusbuyDarkBg)
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(stories) { story ->
                StoryPill(story = story, onClick = {
                    viewModel.activeStoryIndex.value = stories.indexOf(story)
                })
            }
        }

        // Active Story Fullscreen Sticker Box
        val currentActiveStoryIdx by viewModel.activeStoryIndex.collectAsState()
        if (currentActiveStoryIdx != -1 && currentActiveStoryIdx < stories.size) {
            val storyItem = stories[currentActiveStoryIdx]
            StoryViewerPopup(story = storyItem, onEnded = {
                viewModel.activeStoryIndex.value = -1
            }, onVote = { isOpt1 ->
                viewModel.voteInStoryPoll(storyItem.id, isOpt1)
            })
        }

        // Main feed post
        if (posts.isNotEmpty()) {
            val currentPost = posts.getOrNull(activeIdx) ?: posts.first()
            var dragAmountY by remember { mutableStateOf(0f) }
            val currentPosts by rememberUpdatedState(posts)
            val currentActiveIdx by rememberUpdatedState(activeIdx)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("feed_tiktok_post")
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                val list = currentPosts
                                val active = currentActiveIdx
                                if (dragAmountY < -60f) { // Swiped UP -> Next Post
                                    if (active < list.size - 1) {
                                        viewModel.activePostIndex.value = active + 1
                                    } else {
                                        viewModel.activePostIndex.value = 0
                                    }
                                } else if (dragAmountY > 60f) { // Swiped DOWN -> Previous Post
                                    if (active > 0) {
                                        viewModel.activePostIndex.value = active - 1
                                    }
                                }
                                dragAmountY = 0f
                            },
                            onDragCancel = {
                                dragAmountY = 0f
                            },
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                dragAmountY += dragAmount
                            }
                        )
                    }
            ) {
                // Vertical Swipe simulated via quick next/prev arrow widgets on lateral borders
                TikTokPostContainer(
                    post = currentPost,
                    isPlaying = isPlaying,
                    onTogglePlay = { viewModel.isVideoPlaying.value = !viewModel.isVideoPlaying.value },
                    onDoubleTapLike = { viewModel.toggleLikePost(currentPost.id) },
                    onPostOrder = { viewModel.orderProduct(currentPost) },
                    viewModel = viewModel
                )

                // Swipe controllers - vertical navigation
                IconButton(
                    onClick = {
                        if (activeIdx > 0) {
                            viewModel.activePostIndex.value = activeIdx - 1
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .size(48.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, "Précédent", tint = CusbuyWhite.copy(alpha = 0.7f), modifier = Modifier.size(40.dp))
                }

                IconButton(
                    onClick = {
                        if (activeIdx < posts.size - 1) {
                            viewModel.activePostIndex.value = activeIdx + 1
                        } else {
                            // Loop back to start
                            viewModel.activePostIndex.value = 0
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .size(48.dp)
                        .testTag("next_feed_button")
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, "Suivant", tint = CusbuyWhite.copy(alpha = 0.7f), modifier = Modifier.size(40.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Inbox, "Vide", tint = CusbuyTextGray, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Aucun poste pour le moment.", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                Text("Repassez en mode vendeur pour publier !", color = CusbuyTextGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StoryPill(story: SellerStory, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(2.dp, CusbuyOrange, CircleShape)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(CusbuyDarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(story.sellerAvatar, fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = story.sellerName,
            fontSize = 11.sp,
            color = CusbuyWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(72.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StoryViewerPopup(
    story: SellerStory,
    onEnded: () -> Unit,
    onVote: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { onEnded() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, CusbuyOrange),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(story.sellerAvatar, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(story.sellerName, color = CusbuyWhite, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onEnded) {
                        Icon(Icons.Default.Close, "Fermer", tint = CusbuyWhite)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = when (story.imageOrBg) {
                                    "DarkSlate" -> listOf(Color(0xFF2E3A42), Color(0xFF1F282E))
                                    "RoseDust" -> listOf(Color(0xFF8A5A6C), Color(0xFF5A3E4B))
                                    else -> listOf(Color(0xFFFF5722), Color(0xFFFF9800))
                                }
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = story.title,
                            color = CusbuyWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive stickers
                        when (story.type) {
                            StoryWidgetType.POLL -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        story.pollQuestion ?: "",
                                        color = CusbuyWhite,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onVote(true) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CusbuyWhite.copy(alpha = 0.2f)),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(story.pollOption1 ?: "Option 1", fontSize = 11.sp, color = CusbuyWhite, maxLines = 1)
                                                Text("${story.pollVotes1} votes", fontSize = 10.sp, color = CusbuyTextGray)
                                            }
                                        }

                                        Button(
                                            onClick = { onVote(false) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CusbuyWhite.copy(alpha = 0.2f)),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(story.pollOption2 ?: "Option 2", fontSize = 11.sp, color = CusbuyWhite, maxLines = 1)
                                                Text("${story.pollVotes2} votes", fontSize = 10.sp, color = CusbuyTextGray)
                                            }
                                        }
                                    }
                                }
                            }
                            StoryWidgetType.QNA -> {
                                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(story.qnaQuestion ?: "", color = CusbuyWhite, fontSize = 13.sp, textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    var qnaText by remember { mutableStateOf("") }
                                    TextField(
                                        value = qnaText,
                                        onValueChange = { qnaText = it },
                                        placeholder = { Text("Votre question...", color = CusbuyTextGray, fontSize = 11.sp) },
                                        colors = TextFieldDefaults.colors(focusedTextColor = CusbuyWhite, unfocusedTextColor = CusbuyWhite, focusedContainerColor = CusbuyDarkBg.copy(alpha = 0.5f), unfocusedContainerColor = CusbuyDarkBg.copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { qnaText = ""; onEnded() },
                                        colors = ButtonDefaults.buttonColors(containerColor = CusbuyOrange),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Poser", fontSize = 11.sp)
                                    }
                                }
                            }
                            StoryWidgetType.COUNTDOWN -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("PROMO EXPLOSE DANS", fontSize = 11.sp, color = CusbuyWhite.copy(alpha = 0.8f))
                                    Text("02h : 14m : 05s", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Yellow)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { onEnded() },
                                        colors = ButtonDefaults.buttonColors(containerColor = CusbuyOrange)
                                    ) {
                                        Text("Voir l'article (${story.productLinkName})")
                                    }
                                }
                            }
                            else -> {
                                Text("Visitez notre magasin !", color = CusbuyWhite)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Stories à durée de vie 24h par Alpha Boutique", color = CusbuyTextGray, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun TikTokPostContainer(
    post: VideoPost,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onDoubleTapLike: () -> Unit,
    onPostOrder: () -> Unit,
    viewModel: CusbuyViewModel
) {
    var showComments by remember { mutableStateOf(false) }
    var showOrderDialog by remember { mutableStateOf(false) }
    var doubleTapHeartPop by remember { mutableStateOf(false) }

    // Ken burns zoom logic for images
    val infiniteTransition = rememberInfiniteTransition(label = "ken_burns")
    val kbScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "kb_zoom"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(post.id) {
                detectTapGestures(
                    onDoubleTap = {
                        onDoubleTapLike()
                        doubleTapHeartPop = true
                    },
                    onTap = { onTogglePlay() }
                )
            }
    ) {
        // CONTENT BACKGROUND (SIMULATION)
        when (post.type) {
            PostType.CAROUSEL -> {
                // Carousel swipeable preview
                var carouselIdx by remember { mutableStateOf(0) }
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "📸 CARROUSEL IMAGES\n\nSlide ${carouselIdx + 1}/3 de l'article\nSwipez avec les boutons ci-dessous",
                            color = CusbuyWhite,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Carousel Indicators bottom overlay
                    Row(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 110.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (carouselIdx == index) CusbuyOrange else CusbuyWhite.copy(alpha = 0.4f))
                            )
                        }
                    }

                    // Carousel controls
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(horizontal = 48.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { if (carouselIdx > 0) carouselIdx-- }) {
                            Icon(Icons.Default.KeyboardArrowLeft, "Précédent", tint = CusbuyWhite, modifier = Modifier.size(36.dp))
                        }
                        IconButton(onClick = { if (carouselIdx < 2) carouselIdx++ }) {
                            Icon(Icons.Default.KeyboardArrowRight, "Suivant", tint = CusbuyWhite, modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
            PostType.IMAGE -> {
                // Single image with Ken Burns slow zoom effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(kbScale)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF333333), Color(0xFF1E1E1E), Color(0xFF111111))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🖼️ IMAGE FIÈREMENT PERSISTANTE", color = CusbuyWhite.copy(alpha = 0.5f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Icon(Icons.Default.Brush, "Ken burns", tint = CusbuyOrange.copy(alpha = 0.8f), modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Simulée avec l'effet KEN BURNS\n(Zoom interactif progressif lent)", color = CusbuyWhite, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 13.sp)
                    }
                }
            }
            PostType.VIDEO -> {
                // Video simulator background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PlayCircleOutline else Icons.Default.PauseCircleOutline,
                            contentDescription = "Etat",
                            tint = CusbuyWhite.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isPlaying) "📹 CLIP EN LECTURE AUTOMATIQUE" else "⏸️ CLIP SUR PAUSE",
                            color = CusbuyWhite.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Simulated run indicator
                    LinearProgressIndicator(
                        progress = { post.videoSimulatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .height(4.dp),
                        color = CusbuyOrange,
                        trackColor = Color.Transparent,
                    )
                }
            }
        }

        // DOUBLE-TAP BOUNCING HEART
        if (doubleTapHeartPop) {
            LaunchedEffect(Unit) {
                delay(800)
                doubleTapHeartPop = false
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier
                        .size(120.dp)
                        .scale(1.2f)
                )
            }
        }

        // TIKTOK OVERLAY INFOS (Left bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.72f)
                .padding(16.dp)
                .padding(bottom = 8.dp) // Leave blank for nav bars
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = post.sellerName,
                    color = CusbuyWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CusbuyGreen)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text("Certifié Vendeur", fontSize = 8.sp, color = CusbuyDarkBg, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.description,
                color = CusbuyWhite,
                fontSize = 13.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                post.hashtags.forEach { tag ->
                    Text(text = "#$tag", color = CusbuyOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sound widget
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        viewModel.createPublishContent(
                            type = PostType.VIDEO,
                            title = "Réponse avec le son de ${post.sellerName}",
                            desc = "Fait avec le son de tendance: ${post.soundTitle}",
                            price = 45.0
                        )
                    }
                    .background(CusbuyWhite.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.MusicNote, "Son", tint = CusbuyWhite, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = post.soundTitle,
                    color = CusbuyWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // TIKTOK RIGTHSIDE OVERLAY ACTIONS (Right bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, CusbuyOrange, CircleShape)
                    .background(CusbuyWhite),
                contentAlignment = Alignment.Center
            ) {
                Text(post.sellerAvatarUrl, fontSize = 24.sp)
            }

            // Like Action Button
            IconButton(
                onClick = { onDoubleTapLike() },
                modifier = Modifier.testTag("btn_like_post")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color.Red else CusbuyWhite,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "${post.likesCount}",
                        fontSize = 11.sp,
                        color = CusbuyWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Comments Button
            IconButton(
                onClick = { showComments = true },
                modifier = Modifier.testTag("btn_comments")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Commentaires",
                        tint = CusbuyWhite,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "${post.comments.size}",
                        fontSize = 11.sp,
                        color = CusbuyWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Gifting Coin Action
            IconButton(onClick = { showOrderDialog = true }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Commander",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                    Text("Acheter", fontSize = 11.sp, color = CusbuyWhite, fontWeight = FontWeight.Bold)
                }
            }

            // Duet / Unboxing Reaction Button
            IconButton(
                onClick = {
                    viewModel.createPublishContent(
                        type = PostType.VIDEO,
                        title = "Réaction déballage - ${post.productInfo?.name ?: "Article"}",
                        desc = "Réaction en duo avec le produit original de @${post.sellerName}",
                        price = post.productInfo?.priceUSD ?: 15.0
                    )
                }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FiberSmartRecord, "Reaction Duo", tint = CusbuyOrange, modifier = Modifier.size(28.dp))
                    Text("Réagir Duo", fontSize = 9.sp, color = CusbuyWhite)
                }
            }

            // Social Share Button (Watermark deep link mockup)
            IconButton(onClick = {
                // Simulate deep link generation
            }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Share, "Partager", tint = Color.Cyan)
                    Text("Partager", fontSize = 10.sp, color = CusbuyWhite)
                }
            }
        }
    }

    // COMMENT SECTION TIKTOK-STYLE DIALOG
    if (showComments) {
        Dialog(onDismissRequest = { showComments = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                border = BorderStroke(1.dp, CusbuyWhite.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Commentaires (${post.comments.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = CusbuyWhite,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showComments = false }) {
                            Icon(Icons.Default.Close, "Fermer", tint = CusbuyWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(post.comments) { comment ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (comment.isPinned) CusbuyOrange.copy(alpha = 0.08f) else Color.Transparent
                                    )
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(comment.authorAvatar, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(comment.author, color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        if (comment.isPinned) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(CusbuyOrange)
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text("ÉPINGLÉ", fontSize = 8.sp, color = CusbuyWhite, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Text(comment.timestamp, color = CusbuyTextGray, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(comment.text, color = CusbuyWhite, fontSize = 12.sp)

                                // Linked Video Reply simulation if present
                                if (comment.replyVideoTitle != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CusbuyGreen.copy(alpha = 0.15f))
                                            .padding(6.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, null, tint = CusbuyGreen, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(comment.replyVideoTitle, fontSize = 10.sp, color = CusbuyGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = CusbuyWhite.copy(alpha = 0.12f))

                    // Comment compose box
                    var textArg by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = textArg,
                            onValueChange = { textArg = it },
                            placeholder = { Text("Tapez un commentaire... IA protège", color = CusbuyTextGray, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).testTag("comment_input_box"),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = CusbuyWhite,
                                unfocusedTextColor = CusbuyWhite,
                                focusedContainerColor = CusbuyDarkBg,
                                unfocusedContainerColor = CusbuyDarkBg
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                viewModel.addCommentToPost(post.id, textArg)
                                textArg = ""
                            },
                            modifier = Modifier.testTag("btn_send_comment")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, "Envoyer", tint = CusbuyOrange)
                        }
                    }
                }
            }
        }
    }

    // ORDER DIALOG (PAY COINS VS CASH)
    if (showOrderDialog) {
        Dialog(onDismissRequest = { showOrderDialog = false }) {
            val product = post.productInfo ?: ProductInfo("Article VIP", 29.0, "Douala", "Description")
            Card(
                colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, CusbuyOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛍️ Finaliser la Commande", style = MaterialTheme.typography.titleMedium, color = CusbuyWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(product.name, fontSize = 16.sp, color = CusbuyWhite, fontWeight = FontWeight.Black)
                    Text(product.description, fontSize = 12.sp, color = CusbuyTextGray, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Option A: Coins payment
                    Card(
                        onClick = {
                            viewModel.orderProduct(post)
                            showOrderDialog = false
                        },
                        colors = CardDefaults.cardColors(containerColor = CusbuyDarkBg),
                        border = BorderStroke(1.dp, Color(0xFFFFD700)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_order_by_coins")
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Stars, null, tint = Color(0xFFFFD700))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Acheter avec ${product.coinsRequired} Coins", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                            }
                            Text("Retrait direct sur votre portefeuille principal", fontSize = 10.sp, color = CusbuyTextGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option B: Cash payment
                    Card(
                        onClick = {
                            viewModel.orderProduct(post)
                            showOrderDialog = false
                        },
                        colors = CardDefaults.cardColors(containerColor = CusbuyDarkBg),
                        border = BorderStroke(1.dp, CusbuyWhite.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_order_by_cash")
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Acheter avec MoMo / Cash : $${product.priceUSD} USD", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                            Text("Livraison locale payée à l'arrivée chez vous", fontSize = 10.sp, color = CusbuyTextGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { showOrderDialog = false }) {
                        Text("Annuler", color = CusbuyWhite)
                    }
                }
            }
        }
    }
}

// ==========================================
// E — OTHER BUYER VIEWS & SEARCH VIEWS
// ==========================================
@Composable
fun BuyerSearchView(viewModel: CusbuyViewModel) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("buyer_search_screen")
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher hashtags (#LeProduitEn15s), marques...", color = CusbuyTextGray) },
            leadingIcon = { Icon(Icons.Default.Search, "Recherche", tint = CusbuyWhite) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = CusbuyWhite,
                unfocusedTextColor = CusbuyWhite,
                focusedContainerColor = CusbuyDarkSurface,
                unfocusedContainerColor = CusbuyDarkSurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Challenges Vendeurs en Tendance 🔥", color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CusbuyOrange)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("HOT", fontSize = 10.sp, color = CusbuyWhite, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("#LeProduitEn15s", color = CusbuyWhite, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Présentez vos colis en 15 secondes chrono pour obtenir un boost immédiat dans le feed e-commerce local !",
                    fontSize = 12.sp,
                    color = CusbuyTextGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.navigateTo("PUBLISH_CAM") },
                    colors = ButtonDefaults.buttonColors(containerColor = CusbuyOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Participer", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Sons Tendances Musiques", color = CusbuyWhite, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        val songs = listOf("Chill Beats for Shopping Vibe", "Upbeat Pop Cusbuy Glow", "Acoustic Africa Sunset")
        songs.forEach { song ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MusicNote, "Music", tint = CusbuyOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(song, color = CusbuyWhite, fontSize = 13.sp)
                }
                IconButton(onClick = { viewModel.navigateTo("PUBLISH_CAM") }) {
                    Icon(Icons.Default.ChevronRight, "Sélectionner", tint = CusbuyTextGray)
                }
            }
            Divider(color = CusbuyWhite.copy(alpha = 0.08f))
        }
    }
}

// ==========================================
// F — ACTION TRACKERS (BUYER VS SELLER HISTORY TIMELINES)
// ==========================================
@Composable
fun ActionTrackerPage(
    viewModel: CusbuyViewModel,
    actions: List<ActionEntity>,
    filterType: ActionType
) {
    val displayedActions = actions.filter {
        if (filterType == ActionType.BUY) it.type == "BUY" else it.type == "SELL"
    }

    val timelineColor = if (filterType == ActionType.BUY) CusbuyOrange else CusbuyGreen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag(if (filterType == ActionType.BUY) "buyer_action_tracker" else "seller_action_tracker")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (filterType == ActionType.BUY) "Historique des Achats 🛍️" else "Historique des Ventes 🏪",
                    style = MaterialTheme.typography.titleLarge,
                    color = CusbuyWhite,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (filterType == ActionType.BUY) "Suivez vos acquisitions et recharges" else "Suivez vos gains de boutique et livraisons",
                    color = CusbuyTextGray,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = { viewModel.clearHistories() }) {
                Icon(Icons.Default.DeleteSweep, "Vider", tint = Color.Red.copy(alpha = 0.8f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (displayedActions.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Outlined.List, "Vide", tint = CusbuyTextGray, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Aucune action enregistrée pour le moment.", color = CusbuyTextGray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displayedActions) { act ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Custom vertical timeline node
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(timelineColor)
                            )
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(60.dp)
                                    .background(CusbuyWhite.copy(alpha = 0.15f))
                            )
                        }

                        // Action content card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CusbuyWhite.copy(alpha = 0.08f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = act.itemTitle,
                                        color = CusbuyWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(timelineColor.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = act.status,
                                            fontSize = 9.sp,
                                            color = timelineColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = act.description,
                                    fontSize = 12.sp,
                                    color = CusbuyTextGray
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (act.amountCoins > 0) {
                                            Icon(Icons.Default.Stars, "Coins", tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("${act.amountCoins} Coins", color = CusbuyWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        if (act.amountCoins > 0 && act.amountUSD > 0.0) {
                                            Spacer(modifier = Modifier.width(12.dp))
                                        }
                                        if (act.amountUSD > 0) {
                                            Text(
                                                text = "$${act.amountUSD} USD",
                                                color = CusbuyWhite,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(
                                        text = act.timestamp,
                                        fontSize = 10.sp,
                                        color = CusbuyTextGray
                                    )
                                }

                                if (act.trackingNumber != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Nº Suivi: ${act.trackingNumber}",
                                        fontSize = 9.sp,
                                        color = Color.Cyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// G — SELLER WORKSPACE & DASHBOARDS
// ==========================================
@Composable
fun SellerDashboard(viewModel: CusbuyViewModel, actions: List<ActionEntity>) {
    val sales = actions.filter { it.type == "SELL" }
    val totalCashSalesUSD = sales.sumOf { it.amountUSD }
    val totalCoinsEarned = sales.sumOf { it.amountCoins }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("seller_dashboard_screen")
    ) {
        Text("Tableau de bord Vendeur 🏪", style = MaterialTheme.typography.titleLarge, color = CusbuyWhite, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Visualisez vos indicateurs de vente et gérez votre espace.", color = CusbuyTextGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // Large stats cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
                border = BorderStroke(1.dp, CusbuyGreen.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1.0f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ventes directes", fontSize = 11.sp, color = CusbuyTextGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$${"%.2f".format(totalCashSalesUSD)} USD", style = MaterialTheme.typography.titleMedium, color = CusbuyWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Paiements MoMo & cash", fontSize = 9.sp, color = CusbuyGreen)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1.0f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cusbuy Coins reçus", fontSize = 11.sp, color = CusbuyTextGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$totalCoinsEarned", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Convertibles (-30% commission)", fontSize = 9.sp, color = CusbuyTextGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Create product quick access shortcuts
        Text("Outils de Publication Rapide", color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.navigateTo("PUBLISH_CAM") },
                colors = ButtonDefaults.buttonColors(containerColor = CusbuyGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp).testTag("btn_publish_dashboard")
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Publier", fontSize = 12.sp, color = CusbuyDarkBg, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val simLive = LiveStreamSim(1, "Ma Boutique Live", "🏪", 150, "Cusbuy Direct Shopping", ProductInfo("Nouveau Colis", 29.0, "Douala", "Pack promo"))
                    viewModel.enterLive(simLive)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp).testTag("btn_go_live_dashboard")
            ) {
                Icon(Icons.Default.CellTower, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Go Live", fontSize = 12.sp, color = CusbuyWhite, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Ventes Récentes", color = CusbuyWhite, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Reuse activity but direct inline list
        val sellActions = sales.take(3)
        if (sellActions.isEmpty()) {
            Text("Aucune vente enregistrée pour l'instant. Lancez vos vidéos !", color = CusbuyTextGray, fontSize = 12.sp)
        } else {
            sellActions.forEach { act ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(act.itemTitle, color = CusbuyWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(act.description, color = CusbuyTextGray, fontSize = 11.sp)
                        }
                        Text("$${act.amountUSD}", color = CusbuyGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SellerVideosTab(posts: List<VideoPost>, profile: ProfileEntity) {
    val myPosts = posts.filter { it.sellerName.contains(profile.sellerBusinessName) || it.sellerName.contains(profile.nickname) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Vos Posts Publiés 📹", style = MaterialTheme.typography.titleMedium, color = CusbuyWhite, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (myPosts.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Videocam, null, tint = CusbuyTextGray, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aucun post partagé encore.", color = CusbuyTextGray, fontSize = 12.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(myPosts) { post ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .background(CusbuyGreen.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                when (post.type) {
                                    PostType.VIDEO -> Text("📹 VIDEO", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                                    PostType.IMAGE -> Text("🖼️ IMAGE", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                                    PostType.CAROUSEL -> Text("🎠 CARROUSEL", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                                }
                            }
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(post.productInfo?.name ?: "Article", maxLines = 1, fontWeight = FontWeight.Bold, color = CusbuyWhite, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("$${post.productInfo?.priceUSD} USD", color = CusbuyGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Favorite, null, tint = Color.Red, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("${post.likesCount}", color = CusbuyWhite, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// H — CREATOR PUBLICATION WIZARD SCREEN
// ==========================================
@Composable
fun PublishWizard(viewModel: CusbuyViewModel) {
    var selectedType by remember { mutableStateOf(PostType.VIDEO) }
    var title by remember { mutableStateOf("Chaussures Premium Adidas") }
    var description by remember { mutableStateOf("Derniers modèles arrivés d'Italie. Superbe qualité, semelle confortable.") }
    var price by remember { mutableStateOf("120") }
    var filterName by remember { mutableStateOf("None") }
    var voiceEffect by remember { mutableStateOf("Normal") }

    // Step state: 0 CONTENT TYPE SETUP, 1 PRICING & FINALIZE
    var currentStep by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("publish_wizard_screen")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo("MAIN_APP") }) {
                    Icon(Icons.Default.Close, "Annuler", tint = CusbuyWhite)
                }
                Text("Nouveau Post ${currentStep + 1}/2", style = MaterialTheme.typography.titleMedium, color = CusbuyGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentStep == 0) {
                // Content Type options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        PostType.VIDEO to "📹 Vidéo",
                        PostType.IMAGE to "🖼️ Image",
                        PostType.CAROUSEL to "🎠 Carrousel"
                    ).forEach { (type, label) ->
                        Button(
                            onClick = { selectedType = type },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == type) CusbuyGreen else CusbuyDarkSurface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("tab_publish_${type.name}")
                        ) {
                            Text(label, fontSize = 11.sp, color = if (selectedType == type) CusbuyDarkBg else CusbuyWhite, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Simulator preview of the source camera
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideoCameraFront, "Camera", tint = CusbuyWhite, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = when (selectedType) {
                                PostType.VIDEO -> "📹 ENREGISTREUR INSTANTANÉ (15s min - 3m max)\nGrille des tiers activée • Flash Auto"
                                PostType.IMAGE -> "🖼️ CRÉATION IMAGE UNIQUE\nSimulation de Ken Burns lente activée"
                                PostType.CAROUSEL -> "🎠 CARROUSEL MULTIPLE (2 à 10 images)"
                            },
                            color = CusbuyWhite,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Watermark badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(CusbuyGreen)
                            .padding(4.dp)
                    ) {
                        Text("WATERMARK CUSBUY", fontSize = 8.sp, color = CusbuyDarkBg, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Video/Image tools adjustments
                Text("Effets & Outils de filtres", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Filters row
                    listOf("Sweet", "Cool Glow", "Vintage", "Teint").forEach { filter ->
                        OutlinedButton(
                            onClick = { filterName = filter },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (filterName == filter) CusbuyGreen.copy(alpha = 0.2f) else Color.Transparent,
                                contentColor = CusbuyWhite
                            ),
                            border = BorderStroke(1.dp, if (filterName == filter) CusbuyGreen else CusbuyWhite.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(filter, fontSize = 9.sp, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Voix Off / Effet sonore vocal", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Normal", "Grave 🎙️", "Aigu 🐿️", "Écho 🔊").forEach { effect ->
                        OutlinedButton(
                            onClick = { voiceEffect = effect },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (voiceEffect == effect) CusbuyGreen.copy(alpha = 0.2f) else Color.Transparent,
                                contentColor = CusbuyWhite
                            ),
                            border = BorderStroke(1.dp, if (voiceEffect == effect) CusbuyGreen else CusbuyWhite.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(effect, fontSize = 9.sp, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { currentStep = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = CusbuyGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_publish_continue_step1")
                ) {
                    Text("Continuer vers Infos Produit", color = CusbuyDarkBg, fontWeight = FontWeight.Bold)
                }
            } else {
                // STEP 2: DETAILS
                Text("Titre de l'Article", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    colors = TextFieldDefaults.colors(focusedTextColor = CusbuyWhite, unfocusedTextColor = CusbuyWhite, focusedContainerColor = CusbuyDarkSurface, unfocusedContainerColor = CusbuyDarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_publish_title")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Description / Message commercial", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    colors = TextFieldDefaults.colors(focusedTextColor = CusbuyWhite, unfocusedTextColor = CusbuyWhite, focusedContainerColor = CusbuyDarkSurface, unfocusedContainerColor = CusbuyDarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Prix de mise en vente (USD)", color = CusbuyWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    colors = TextFieldDefaults.colors(focusedTextColor = CusbuyWhite, unfocusedTextColor = CusbuyWhite, focusedContainerColor = CusbuyDarkSurface, unfocusedContainerColor = CusbuyDarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_publish_price")
                )

                Spacer(modifier = Modifier.height(36.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { currentStep = 0 },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CusbuyWhite),
                        border = BorderStroke(1.dp, CusbuyWhite.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Modifier Contenu")
                    }

                    Button(
                        onClick = {
                            val priceVal = price.toDoubleOrNull() ?: 29.0
                            viewModel.createPublishContent(
                                type = selectedType,
                                title = title,
                                desc = description,
                                price = priceVal,
                                filterName = filterName,
                                voiceEffect = voiceEffect
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CusbuyGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp).testTag("btn_publish_finish")
                    ) {
                        Text("Partager dans le Feed", color = CusbuyDarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// I — LIVE STREAMING EXPERIENCE
// ==========================================
@Composable
fun LiveStreamRoom(viewModel: CusbuyViewModel) {
    val liveState by viewModel.activeLive.collectAsState()
    val flyers by viewModel.liveGiftFlyers.collectAsState()
    val leaderboard by viewModel.liveViewers.collectAsState()

    val currentProfile by viewModel.profileState.collectAsState()

    if (liveState == null) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Live introuvable.", color = CusbuyWhite)
            Button(onClick = { viewModel.navigateTo("MAIN_APP") }) { Text("Retour") }
        }
        return
    }

    val live = liveState!!

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .testTag("live_stream_room")
    ) {
        // Ambient simulated view camera feed on live streaming
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF331B05), Color.Black),
                            radius = size.maxDimension
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.Red, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("🔴 DIFFUSION EN DIRECT TIKTOK-STYLE", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(live.title, color = CusbuyWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Liqué par les spectateurs", color = CusbuyTextGray, fontSize = 11.sp)
            }
        }

        // Live Header overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CusbuyWhite)
                ) {
                    Text(live.sellerAvatar, modifier = Modifier.align(Alignment.Center), fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(live.sellerName, color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Red))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${live.viewerCount} spectateurs", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            IconButton(onClick = { viewModel.exitLive() }) {
                Icon(Icons.Default.Cancel, "Fermer", tint = CusbuyWhite, modifier = Modifier.size(32.dp))
            }
        }

        // Live Shopping Basket (Left bottom overlay)
        if (live.activeProduct != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface.copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, CusbuyOrange),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .padding(bottom = 120.dp)
                    .width(180.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🛍️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(live.activeProduct.name, color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("$${live.activeProduct.priceUSD}", color = CusbuyOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Tap pour acheter !", color = CusbuyTextGray, fontSize = 8.sp)
                    }
                }
            }
        }

        // Gifting reactions / chat (Bottom right side overlays)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 110.dp)
                .width(160.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Leaderboard micro view
            Card(
                colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text("Top Contributeurs 🏆", fontSize = 9.sp, color = Color.Yellow, fontWeight = FontWeight.Bold)
                    leaderboard.take(3).forEach { user ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(user.nickname, fontSize = 8.sp, color = CusbuyWhite, maxLines = 1)
                            Text("${user.coinsSent}c", fontSize = 8.sp, color = Color.Yellow)
                        }
                    }
                }
            }

            // Gifting Quick Row
            val gifts = listOf(
                LiveGift("Rose", "🌹", 5),
                LiveGift("Diamant", "💎", 50),
                LiveGift("Couronne", "👑", 200)
            )

            gifts.forEach { gift ->
                Button(
                    onClick = { viewModel.sendLiveGift(gift) },
                    colors = ButtonDefaults.buttonColors(containerColor = CusbuyOrange.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_gift_${gift.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${gift.icon} ${gift.name}", fontSize = 10.sp, color = CusbuyWhite)
                        Text("${gift.coinsCost}c", fontSize = 9.sp, color = Color.Yellow, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Animated live flyers feed of gifted notifications (Left Bottom floating up)
        LazyColumn(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .padding(bottom = 200.dp)
                .width(200.dp)
                .height(100.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(flyers) { flyer ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CusbuyGreen.copy(alpha = 0.85f))
                        .padding(6.dp)
                ) {
                    Text(flyer.first, color = CusbuyDarkBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live shopping status footer with chat and co-host invite mockups
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CusbuyWhite.copy(alpha = 0.5f))
                    .background(CusbuyDarkSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .weight(1f)
            ) {
                Text("Rejoindre le chat...", color = CusbuyTextGray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Co-host button
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CusbuyGreen)
            ) {
                Icon(Icons.Default.GroupAdd, "Co-Host", tint = CusbuyDarkBg)
            }
        }
    }
}

@Composable
fun SellerLiveSetup(viewModel: CusbuyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.CellTower, "Live setup", tint = CusbuyGreen, modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Espace Live Streaming Cusbuy",
            style = MaterialTheme.typography.titleMedium,
            color = CusbuyWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Présentez vos colis à vos spectateurs locaux pour encaisser des cadeaux Coins convertibles en argent réel !",
            color = CusbuyTextGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val simLive = LiveStreamSim(
                    id = 2,
                    sellerName = "Atelier de Couture",
                    sellerAvatar = "🧵",
                    viewerCount = 420,
                    title = "Nouveaux Colis Mode direct",
                    activeProduct = ProductInfo("Robe Africaine Unique", 55.0, "Douala", "Robe confectionnée main")
                )
                viewModel.enterLive(simLive)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CusbuyGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Lancer mon direct / Go Live !", color = CusbuyDarkBg, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// J — PROFILE VIEWS (BUYER & SELLER GENERAL STATS)
// ==========================================
@Composable
fun ProfileView(
    viewModel: CusbuyViewModel,
    profile: ProfileEntity,
    userMode: UserMode
) {
    val displayedName = if (userMode == UserMode.VENDEUR && profile.isSellerRegistered) profile.sellerBusinessName else profile.nickname
    val displayedCity = if (profile.sellerCity.isNotEmpty()) profile.sellerCity else "Cameroun"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("user_profile_screen")
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Photo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (userMode == UserMode.ACHETEUR) CusbuyOrange else CusbuyGreen)
                ) {
                    Text(
                        text = if (userMode == UserMode.ACHETEUR) "🛍️" else "🏪",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = displayedName, style = MaterialTheme.typography.titleLarge, color = CusbuyWhite, fontWeight = FontWeight.Black)
                Text(text = "Localisation: $displayedCity", color = CusbuyTextGray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Social followers stats Tiktok-style representation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("1,2K", color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Abonnements", fontSize = 11.sp, color = CusbuyTextGray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("14,5K", color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Abonnés", fontSize = 11.sp, color = CusbuyTextGray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("250K", color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("J'aime", fontSize = 11.sp, color = CusbuyTextGray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Coins management row inside profile
                Card(
                    colors = CardDefaults.cardColors(containerColor = CusbuyDarkBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Portefeuille Coins & Compte", color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Icon(Icons.Default.Stars, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Solde: ${profile.walletCoins} Cusbuy Coins", color = Color(0xFFFFD700), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text("Paiement MoMo / Direct: $${profile.walletCashUSD} USD", color = CusbuyWhite, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.buyCoinPack(100, 1.0) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (userMode == UserMode.ACHETEUR) CusbuyOrange else CusbuyGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Recharger +100c", fontSize = 10.sp, color = CusbuyDarkBg, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Bio Details & external links
        Text("Détails d'identification & Informations", color = CusbuyWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CusbuyDarkSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Adresse Email", color = CusbuyTextGray, fontSize = 12.sp)
                    Text(profile.email, color = CusbuyWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                if (profile.isSellerRegistered) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Boutique d'Expédition", color = CusbuyTextGray, fontSize = 12.sp)
                        Text(profile.sellerBusinessName, color = CusbuyWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Téléphone Aide locale", color = CusbuyTextGray, fontSize = 12.sp)
                        Text(profile.sellerPhone, color = CusbuyWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Lien Externe Bio", color = CusbuyTextGray, fontSize = 12.sp)
                    Text("https://cusbuy.com/${profile.nickname.lowercase()}", color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App QR code mock representation
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CusbuyWhite)
                    .padding(12.dp)
            ) {
                // simple simulated grid representing a qr code
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QrCode2, "QR Code", tint = CusbuyDarkBg, modifier = Modifier.size(96.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("QR Code Profil Cusbuy", color = CusbuyTextGray, fontSize = 11.sp)
        }
    }
}
