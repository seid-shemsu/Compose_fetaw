package com.seid.fetawa_.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seid.fetawa_.utils.Constants.blue

@Composable
fun AnimatedShimmer(
    height: Dp,
    width: Dp,
    shape: Shape = RoundedCornerShape(2.dp),
) {
    val shimmerColors = listOf(
        blue.copy(alpha = 0.2f),
        blue.copy(alpha = 0.05f),
        blue.copy(alpha = 0.2f),
    )

    val transition = rememberInfiniteTransition()
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Reverse,
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    )

    val brush = Brush.linearGradient(
        shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )

    ShimmerItem(brush = brush, height = height, width = width, shape = shape)
}

@Composable
fun ShimmerItem(
    brush: Brush,
    height: Dp,
    width: Dp,
    shape: Shape = RoundedCornerShape(2.dp),
) {
    Card(
        shape = shape,
        modifier = Modifier.padding(10.dp),
    ) {
        Spacer(
            modifier = Modifier
                .height(height)
                .width(width)
                .background(brush)
        )
    }

}