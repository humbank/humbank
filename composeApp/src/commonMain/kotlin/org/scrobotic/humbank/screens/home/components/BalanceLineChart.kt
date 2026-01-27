package org.scrobotic.humbank.screens.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.scrobotic.humbank.data.Transaction
import org.scrobotic.humbank.data.formatCurrency
import org.scrobotic.humbank.ui.BlueStart

@Composable
fun BalanceLineChart(txs: List<Transaction>, accountId: String, currentBal: Double) {
    val textMeasurer = rememberTextMeasurer()

    val labelStyle = androidx.compose.ui.text.TextStyle(
        color = Color.Gray,
        fontSize = 10.sp
    )

    val gridColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 50.dp, bottom = 35.dp, end = 10.dp, top = 10.dp)
        ) {
            if (txs.size < 2) {
                // Fallback if no data
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 2.dp.toPx()
                )
                return@Canvas
            }

            val sortedTxs = txs.sortedBy { it.created }

            var runningTotal = currentBal.toFloat()

            val balancePoints = sortedTxs
                .asReversed()
                .map { tx ->
                    val isIncoming = tx.receiver == accountId
                    val point = runningTotal
                    runningTotal -= if (isIncoming) tx.amount.toFloat() else -tx.amount.toFloat()
                    point
                }
                .asReversed()


            val sortedTxsWithBalance = sortedTxs.sortedBy { it.created } // ascending by date

            val max = sortedTxsWithBalance.maxOfOrNull { it.currentBalance.toFloat() } ?: 1f
            val min = sortedTxsWithBalance.minOfOrNull { it.currentBalance.toFloat() } ?: 0f
            val range = (max - min).coerceAtLeast(1f)

            val displayMax = max + (range * 0.15f)
            val displayMin = min - (range * 0.15f)
            val displayRange = displayMax - displayMin



            // horztnknak fgrid line
            val ySteps = 4
            for (i in 0..ySteps) {
                val value = displayMin + (i * (displayRange / ySteps))
                val y = size.height - (i * (size.height / ySteps))

                // Grid line
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )

                // Y-axis label
                drawText(
                    textMeasurer = textMeasurer,
                    text = value.toDouble().formatCurrency(),
                    style = labelStyle,
                    topLeft = Offset(-48.dp.toPx(), y - 8.dp.toPx())
                )
            }

            // vertical grid
            val xSteps = (balancePoints.size - 1).coerceAtMost(5).coerceAtLeast(1)
            for (i in 0..xSteps) {
                val index = (i * (sortedTxs.size - 1) / xSteps).coerceIn(0, sortedTxs.lastIndex)
                val x = i * (size.width / xSteps)

                // Vertical grid line
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx()
                )


                val dateText = sortedTxs[index].created.toString().substring(5, 10)
                println(dateText)

                // X-axis date label DOESNT WORK FOR SOME REASAOSJNANOPASNA
                drawText(
                    textMeasurer = textMeasurer,
                    text = dateText,
                    style = labelStyle,
                    topLeft = Offset(x - 15.dp.toPx(), size.height + 5.dp.toPx())
                )
            }


            val path = Path().apply {
                sortedTxsWithBalance.forEachIndexed { i, tx ->
                    val x = i * (size.width / (sortedTxsWithBalance.size - 1).coerceAtLeast(1))
                    val y = size.height - ((tx.currentBalance.toFloat() - displayMin) / displayRange * size.height)

                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
            }

            val fillPath = Path().apply {
                sortedTxsWithBalance.forEachIndexed { i, tx ->
                    val x = i * (size.width / (sortedTxsWithBalance.size - 1).coerceAtLeast(1))
                    val y = size.height - ((tx.currentBalance.toFloat() - displayMin) / displayRange * size.height)

                    if (i == 0) {
                        moveTo(x, size.height)
                        lineTo(x, y)
                    } else {
                        lineTo(x, y)
                    }

                    if (i == sortedTxsWithBalance.lastIndex) {
                        lineTo(x, size.height)
                        close()
                    }
                }
            }


            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BlueStart.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = size.height
                )
            )



            drawPath(
                path = path,
                color = BlueStart,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            balancePoints.forEachIndexed { i, balance ->
                val x = i * (size.width / (balancePoints.size - 1).coerceAtLeast(1))
                val y = size.height - ((balance - displayMin) / displayRange * size.height)

                drawCircle(
                    color = BlueStart,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color(0xFF121212),
                    radius = 2.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}