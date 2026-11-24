package com.indianrailways.railone.ui.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Custom Ticket Shape with rounded outer corners and side semi-circle cutouts (notches).
 */
class TicketCardShape(
    private val cornerRadius: Dp = 16.dp,
    private val notchRadius: Dp = 12.dp,
    private val notchPositionFraction: Float = 0.72f // Height ratio where the notch appears
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }
        val notchRadiusPx = with(density) { notchRadius.toPx() }
        val notchCenterY = size.height * notchPositionFraction

        val path = Path().apply {
            reset()
            // Top-left corner
            moveTo(0f, cornerRadiusPx)
            arcTo(
                rect = Rect(0f, 0f, 2 * cornerRadiusPx, 2 * cornerRadiusPx),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Top edge
            lineTo(size.width - cornerRadiusPx, 0f)
            // Top-right corner
            arcTo(
                rect = Rect(size.width - 2 * cornerRadiusPx, 0f, size.width, 2 * cornerRadiusPx),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Right edge down to notch
            lineTo(size.width, notchCenterY - notchRadiusPx)
            // Right notch cutout (inward semi-circle)
            arcTo(
                rect = Rect(
                    size.width - notchRadiusPx,
                    notchCenterY - notchRadiusPx,
                    size.width + notchRadiusPx,
                    notchCenterY + notchRadiusPx
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            // Right edge down to bottom corner
            lineTo(size.width, size.height - cornerRadiusPx)
            // Bottom-right corner
            arcTo(
                rect = Rect(size.width - 2 * cornerRadiusPx, size.height - 2 * cornerRadiusPx, size.width, size.height),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Bottom edge
            lineTo(cornerRadiusPx, size.height)
            // Bottom-left corner
            arcTo(
                rect = Rect(0f, size.height - 2 * cornerRadiusPx, 2 * cornerRadiusPx, size.height),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            // Left edge up to notch
            lineTo(0f, notchCenterY + notchRadiusPx)
            // Left notch cutout (inward semi-circle)
            arcTo(
                rect = Rect(
                    -notchRadiusPx,
                    notchCenterY - notchRadiusPx,
                    notchRadiusPx,
                    notchCenterY + notchRadiusPx
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            // Left edge back to top corner
            close()
        }

        return Outline.Generic(path)
    }
}
