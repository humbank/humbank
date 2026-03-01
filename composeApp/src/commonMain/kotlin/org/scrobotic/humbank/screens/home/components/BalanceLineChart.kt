package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.ui.humbankPalette
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class BalancePoint @OptIn(ExperimentalTime::class) constructor(
    val timestamp: Instant,
    val balance: Double,
    val description: String
)

@OptIn(ExperimentalTime::class)
@Composable
fun BalanceLineChart(
    transactions: List<Transaction>,
    accountId: String,
    currentBalance: Double
) {
    val palette = humbankPalette()
    val balancePoints = remember(transactions, accountId, currentBalance) {
        calculateBalanceProgression(transactions, accountId, currentBalance)
    }

    val lineColor = palette.primaryButton
    val gridColor = palette.cardStroke.copy(alpha = 0.4f)
    val labelColor = palette.muted
    val cardSurface = palette.cardSurface

    if (balancePoints.size < 2) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(20.dp),
            color = palette.cardSurface,
            border = BorderStroke(
                1.dp,
                Brush.verticalGradient(
                    colors = listOf(palette.cardStroke.copy(alpha = 0.6f), palette.cardStroke.copy(alpha = 0.1f))
                )
            ),
            tonalElevation = 0.dp,
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Not enough data to display chart",
                    color = palette.muted,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    val textMeasurer = rememberTextMeasurer()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = palette.cardSurface,
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                colors = listOf(palette.cardStroke.copy(alpha = 0.6f), palette.cardStroke.copy(alpha = 0.1f))
            )
        ),
        tonalElevation = 0.dp,
        shadowElevation = 4.dp
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = 20.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)
        ) {
            val leftPadding = 48.dp.toPx()
            val bottomPadding = 32.dp.toPx()
            val chartWidth = size.width - leftPadding - 8.dp.toPx()
            val chartHeight = size.height - bottomPadding

            val minBalance = balancePoints.minOf { it.balance }
            val maxBalance = balancePoints.maxOf { it.balance }
            val range = maxBalance - minBalance
            val pad = if (range > 0) range * 0.18 else 100.0
            val displayMin = minBalance - pad
            val displayMax = maxBalance + pad
            val displayRange = displayMax - displayMin

            if (displayRange == 0.0) {
                val y = chartHeight / 2
                drawLine(
                    color = lineColor,
                    start = Offset(leftPadding, y),
                    end = Offset(leftPadding + chartWidth, y),
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                return@Canvas
            }

            val labelStyle = TextStyle(color = labelColor, fontSize = 9.sp, fontWeight = FontWeight.Medium)

            // Horizontal grid lines + Y labels
            val ySteps = 4
            for (i in 0..ySteps) {
                val value = displayMin + (i * displayRange / ySteps)
                val y = chartHeight - (i * chartHeight / ySteps)

                // Dashed-style grid: draw short segments
                var x = leftPadding
                while (x < leftPadding + chartWidth) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, y),
                        end = Offset(minOf(x + 6.dp.toPx(), leftPadding + chartWidth), y),
                        strokeWidth = 0.8.dp.toPx()
                    )
                    x += 12.dp.toPx()
                }

                drawText(
                    textMeasurer = textMeasurer,
                    text = value.toInt().toString(),
                    style = labelStyle,
                    topLeft = Offset(0f, y - 7.dp.toPx())
                )
            }

            // Vertical grid lines + X labels
            val xSteps = min(balancePoints.size - 1, 5)
            for (i in 0..xSteps) {
                val index = (i * (balancePoints.size - 1) / xSteps).coerceIn(0, balancePoints.lastIndex)
                val x = leftPadding + (i * chartWidth / xSteps)

                var y = 0f
                while (y < chartHeight) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, y),
                        end = Offset(x, minOf(y + 6.dp.toPx(), chartHeight)),
                        strokeWidth = 0.8.dp.toPx()
                    )
                    y += 12.dp.toPx()
                }

                val dateText = formatDateLabel(balancePoints[index].timestamp)
                drawText(
                    textMeasurer = textMeasurer,
                    text = dateText,
                    style = labelStyle,
                    topLeft = Offset(x - 12.dp.toPx(), chartHeight + 8.dp.toPx())
                )
            }

            // Calculate points
            val points = balancePoints.mapIndexed { index, point ->
                val x = leftPadding + (index * chartWidth / (balancePoints.size - 1))
                val normalizedValue = (point.balance - displayMin) / displayRange
                val y = chartHeight - (normalizedValue * chartHeight).toFloat()
                Offset(x, y)
            }

            // Smooth fill path using cubic bezier approximation
            val fillPath = Path().apply {
                if (points.size >= 2) {
                    moveTo(points.first().x, chartHeight)
                    lineTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val cx1 = prev.x + (curr.x - prev.x) * 0.5f
                        val cx2 = curr.x - (curr.x - prev.x) * 0.5f
                        cubicTo(cx1, prev.y, cx2, curr.y, curr.x, curr.y)
                    }
                    lineTo(points.last().x, chartHeight)
                    close()
                }
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.25f),
                        lineColor.copy(alpha = 0.04f)
                    ),
                    startY = 0f,
                    endY = chartHeight
                )
            )

            // Smooth line path
            val linePath = Path().apply {
                if (points.size >= 2) {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val cx1 = prev.x + (curr.x - prev.x) * 0.5f
                        val cx2 = curr.x - (curr.x - prev.x) * 0.5f
                        cubicTo(cx1, prev.y, cx2, curr.y, curr.x, curr.y)
                    }
                }
            }

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw data points
            points.forEach { point ->
                drawCircle(color = lineColor, radius = 4.dp.toPx(), center = point)
                drawCircle(color = cardSurface, radius = 2.dp.toPx(), center = point)
            }

            // Highlight last point (current balance)
            points.lastOrNull()?.let { point ->
                drawCircle(color = lineColor.copy(alpha = 0.2f), radius = 8.dp.toPx(), center = point)
                drawCircle(color = lineColor, radius = 4.dp.toPx(), center = point)
                drawCircle(color = cardSurface, radius = 2.dp.toPx(), center = point)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun calculateBalanceProgression(
    transactions: List<Transaction>,
    accountId: String,
    currentBalance: Double
): List<BalancePoint> {
    if (transactions.isEmpty()) return emptyList()

    val sortedTransactions = transactions.sortedBy { it.transaction_date }
    val points = mutableListOf<BalancePoint>()
    var runningBalance = currentBalance

    sortedTransactions.reversed().forEach { tx ->
        points.add(
            0,
            BalancePoint(
                timestamp = tx.transaction_date,
                balance = runningBalance,
                description = tx.description
            )
        )
        runningBalance -= if (tx.receiver == accountId) tx.amount else -tx.amount
    }

    if (sortedTransactions.isNotEmpty()) {
        points.add(
            0,
            BalancePoint(
                timestamp = sortedTransactions.first().transaction_date,
                balance = runningBalance,
                description = "Starting balance"
            )
        )
    }

    return points
}

@OptIn(ExperimentalTime::class)
private fun formatDateLabel(timestamp: Instant): String {
    val dateTime = timestamp.toString()
    // Extract HH:mm from ISO format e.g. "2024-02-15T10:30:00Z" -> "10:30"
    return if (dateTime.length >= 16) dateTime.substring(11, 16) else dateTime.take(5)
}
