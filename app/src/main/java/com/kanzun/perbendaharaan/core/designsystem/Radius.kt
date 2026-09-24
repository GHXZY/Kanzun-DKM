package com.kanzun.perbendaharaan.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object Radius {
    // Stripe Core Radius Scale
    val None = 0.dp
    val XS = 2.dp
    val SM = 4.dp // rounded.sm (Buttons, Inputs, Badges, Micro-elements)
    val MD = 6.dp // rounded.md (Cards, Panels)
    val LG = 8.dp // rounded.lg (Modals, Dialogs, Hero Cards)
    val XL = 12.dp // rounded.xl (Bottom Sheet)
    val Full = 9999.dp // rounded.full (Pills, Circular Avatars)

    // Semantic Component Mapping
    val SmallComponent = SM // 4.dp
    val Input = SM // 4.dp (Stripe tight corner rule)
    val Button = SM // 4.dp (Stripe tight corner rule)
    val Card = MD // 6.dp (Stripe cards)
    val LargeCard = LG // 8.dp
    val HeroCard = LG // 8.dp
    val FloatingNavbar = LG // 8.dp
    val BottomSheet = XL // 12.dp
    val Modal = LG // 8.dp
    val Pill = Full // 9999.dp
}

object KanzunShapes {
    val SmallComponent = RoundedCornerShape(Radius.SmallComponent)
    val Input = RoundedCornerShape(Radius.Input)
    val Button = RoundedCornerShape(Radius.Button)
    val Card = RoundedCornerShape(Radius.Card)
    val LargeCard = RoundedCornerShape(Radius.LargeCard)
    val HeroCard = RoundedCornerShape(Radius.HeroCard)
    val FloatingNavbar = RoundedCornerShape(Radius.FloatingNavbar)
    val BottomSheet = RoundedCornerShape(
        topStart = Radius.BottomSheet,
        topEnd = Radius.BottomSheet
    )
    val Modal = RoundedCornerShape(Radius.Modal)
    val Pill = RoundedCornerShape(Radius.Pill)
}
