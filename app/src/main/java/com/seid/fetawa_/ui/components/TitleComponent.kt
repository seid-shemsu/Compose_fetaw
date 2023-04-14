package com.seid.fetawa_.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleComponent(title: String) {
    Text(
        title,
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Black,
        modifier = Modifier.padding(
            horizontal = 20.dp,
            vertical = 15.dp
        )
    )
}