package com.kanzun.perbendaharaan.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object Radius {
    val SmallComponent = 10.dp
    val Input = 12.dp
    val Button = 14.dp
    val Card = 20.dp
    val LargeCard = 24.dp
    val FloatingNavbar = 28.dp
    val BottomSheet = 28.dp
    val HeroCard = 28.dp
    val Pill = 999.dp
}

object KanzunShapes {
    val SmallComponent = RoundedCornerShape(Radius.SmallComponent)
    val Input = RoundedCornerShape(Radius.Input)
    val Button = RoundedCornerShape(Radius.Button)
    val Card = RoundedCornerShape(Radius.Card)
    val LargeCard = RoundedCornerShape(Radius.LargeCard)
    val FloatingNavbar = RoundedCornerShape(Radius.FloatingNavbar)
    val BottomSheet = RoundedCornerShape(
        topStart = Radius.BottomSheet,
        topEnd = Radius.BottomSheet
    )
    val HeroCard = RoundedCornerShape(Radius.HeroCard)
    val Pill = RoundedCornerShape(Radius.Pill)
}
