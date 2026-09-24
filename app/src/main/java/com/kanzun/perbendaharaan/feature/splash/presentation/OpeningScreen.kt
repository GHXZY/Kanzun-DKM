package com.kanzun.perbendaharaan.feature.splash.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kanzun.perbendaharaan.R
import com.kanzun.perbendaharaan.core.database.entity.MosqueEntity
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun OpeningScreen(
    mosqueProfile: MosqueEntity?,
    isIdentityCustomized: Boolean,
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        delay(2200L)
        onTimeout()
    }

    val hasCustomIdentity = isIdentityCustomized ||
        (!mosqueProfile?.logoPath.isNullOrBlank() && File(mosqueProfile?.logoPath ?: "").exists()) ||
        (mosqueProfile != null && mosqueProfile.name.isNotBlank() && mosqueProfile.name != "Masjid Agung Al-Mubarak")

    val headlineText = if (hasCustomIdentity) {
        mosqueProfile?.name?.ifBlank { "KANZUN DKM" } ?: "KANZUN DKM"
    } else {
        "KANZUN DKM"
    }

    val subtitleText = if (hasCustomIdentity) {
        mosqueProfile?.address?.ifBlank { "Bendahara Masjid" } ?: "Bendahara Masjid"
    } else {
        "Bendahara Masjid"
    }

    val customLogoPath = if (hasCustomIdentity) mosqueProfile?.logoPath else null
    val hasCustomLogoFile = !customLogoPath.isNullOrBlank() && File(customLogoPath).exists()

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF38A3A5),
            Color(0xFF2278B8),
            Color(0xFF14569C),
            Color(0xFF0A306E),
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTimeout,
            ),
    ) {
        // Centered Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
        ) {
            Surface(
                modifier = Modifier.size(136.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                shadowElevation = 8.dp,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (hasCustomLogoFile) {
                        AsyncImage(
                            model = File(customLogoPath!!),
                            contentDescription = "Logo Masjid",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        AsyncImage(
                            model = "file:///android_asset/kanzun_logo.svg",
                            contentDescription = "Logo Kanzun DKM",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            error = painterResource(R.drawable.ic_kanzun_logo),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = headlineText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    color = Color.White,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitleText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.9f),
                ),
                textAlign = TextAlign.Center,
            )
        }

        // Static Bottom Footer
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                        append("KANZUN DKM")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = Color.White.copy(alpha = 0.9f))) {
                        append(" | Aplikasi Bendahara Masjid")
                    }
                },
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
