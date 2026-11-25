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
 * Ticket card shape with rounded outer corners and inward left/right semi-circle notches.
 */
class TicketCardShape(
    private val cornerRadius: Dp = 16.dp,
    private val notchRadius: Dp = 11.dp,
    private val notchPositionFraction: Float = 0.72f
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
            moveTo(0f, cornerRadiusPx)
            arcTo(
                rect = Rect(0f, 0f, 2 * cornerRadiusPx, 2 * cornerRadiusPx),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(size.width - cornerRadiusPx, 0f)
            arcTo(
                rect = Rect(size.width - 2 * cornerRadiusPx, 0f, size.width, 2 * cornerRadiusPx),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(size.width, notchCenterY - notchRadiusPx)
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
            lineTo(size.width, size.height - cornerRadiusPx)
            arcTo(
                rect = Rect(size.width - 2 * cornerRadiusPx, size.height - 2 * cornerRadiusPx, size.width, size.height),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(cornerRadiusPx, size.height)
            arcTo(
                rect = Rect(0f, size.height - 2 * cornerRadiusPx, 2 * cornerRadiusPx, size.height),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(0f, notchCenterY + notchRadiusPx)
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
            close()
        }

        return Outline.Generic(path)
    }
}

/**
 * Horizontal Upcoming Journey ticket card shape with top and bottom semi-circle notches
 * matching the official RailOne Home Screen Upcoming Journey widget.
 */
class UpcomingTicketShape(
    private val cornerRadius: Dp = 18.dp,
    private val notchRadius: Dp = 9.dp,
    private val notchXFraction: Float = 0.72f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = with(density) { cornerRadius.toPx() }
        val nr = with(density) { notchRadius.toPx() }
        val nx = size.width * notchXFraction

        val path = Path().apply {
            reset()
            // Top-left corner
            moveTo(0f, r)
            arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
            // Top edge to top notch
            lineTo(nx - nr, 0f)
            // Top inward semi-circle notch
            arcTo(Rect(nx - nr, -nr, nx + nr, nr), 180f, -180f, false)
            // Top edge to top-right corner
            lineTo(size.width - r, 0f)
            arcTo(Rect(size.width - 2 * r, 0f, size.width, 2 * r), 270f, 90f, false)
            // Right edge to bottom-right corner
            lineTo(size.width, size.height - r)
            arcTo(Rect(size.width - 2 * r, size.height - 2 * r, size.width, size.height), 0f, 90f, false)
            // Bottom edge to bottom notch
            lineTo(nx + nr, size.height)
            // Bottom inward semi-circle notch
            arcTo(Rect(nx - nr, size.height - nr, nx + nr, size.height + nr), 0f, -180f, false)
            // Bottom edge to bottom-left corner
            lineTo(r, size.height)
            arcTo(Rect(0f, size.height - 2 * r, 2 * r, size.height), 90f, 90f, false)
            close()
        }

        return Outline.Generic(path)
    }
}
