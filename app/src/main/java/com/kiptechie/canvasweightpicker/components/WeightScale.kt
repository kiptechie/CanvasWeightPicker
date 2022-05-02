package com.kiptechie.canvasweightpicker.components

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withRotation
import com.kiptechie.canvasweightpicker.models.LineType
import com.kiptechie.canvasweightpicker.models.ScaleStyle
import kotlin.math.*

@Composable
fun WeightScale(
    modifier: Modifier = Modifier,
    style: ScaleStyle = ScaleStyle(),
    minWeight: Int = 20,
    maxWeight: Int = 250,
    initialWeight: Int = 80,
    onWeightChange: (Int) -> Unit
) {
    val radius = style.radius
    val scaleWidth = style.scaleWidth
    var center by remember {
        mutableStateOf(Offset.Zero)
    }
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    var angle by remember {
        mutableStateOf(0f)
    }
    var dragStartedAngle by remember {
        mutableStateOf(0f)
    }
    var oldAngle by remember {
        mutableStateOf(0f)
    }
    Canvas(
        modifier = modifier
            .pointerInput(key1 = true, block = {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartedAngle = -atan2(
                            y = circleCenter.x - offset.x,
                            x = circleCenter.y - offset.y
                        ) * (180f / PI.toFloat())
                    },
                    onDragEnd = {
                        oldAngle = angle
                    }
                ) { change, _ ->
                    val touchAngle = -atan2(
                        y = circleCenter.x - change.position.x,
                        x = circleCenter.y - change.position.y
                    ) * (180f / PI.toFloat())
                    val newAngle = oldAngle + (touchAngle - dragStartedAngle)
                    angle = newAngle.coerceIn(
                        minimumValue = initialWeight - maxWeight.toFloat(),
                        maximumValue = initialWeight - minWeight.toFloat()
                    )
                    onWeightChange((initialWeight - angle).roundToInt())
                }
            }),
        onDraw = {
            center = this.center
            circleCenter = Offset(center.x, scaleWidth.toPx() / 2f + radius.toPx())
            val outerRadius = radius.toPx() + scaleWidth.toPx() / 2f
            val innerRadius = radius.toPx() - scaleWidth.toPx() / 2f
            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    radius.toPx(),
                    Paint().apply {
                        strokeWidth = scaleWidth.toPx()
                        color = Color.WHITE
                        setStyle(Paint.Style.STROKE)
                        setShadowLayer(
                            60f,
                            0f,
                            0f,
                            Color.argb(50, 0, 0, 0)
                        )
                    }
                )
            }
            // draw lines
            for (i in minWeight..maxWeight) {
                val angleInRad = (i - initialWeight + angle - 90) * (PI / 180f).toFloat()
                Log.e("MANGLE", "Angle: $angleInRad")
                val lineType = when {
                    i % 10 == 0 -> LineType.TenStep
                    i % 5 == 0 -> LineType.FiveStep
                    else -> LineType.Normal
                }
                val lineLength = when (lineType) {
                    LineType.FiveStep -> style.fiveStepLineLength.toPx()
                    LineType.Normal -> style.normalLineLength.toPx()
                    LineType.TenStep -> style.tenStepLineLength.toPx()
                }
                val lineColor = when (lineType) {
                    LineType.FiveStep -> style.fiveStepLineColor
                    LineType.Normal -> style.normalLineColor
                    LineType.TenStep -> style.tenStepLineColor
                }
                val lineStart = Offset(
                    x = (outerRadius - lineLength) * cos(angleInRad) + circleCenter.x,
                    y = (outerRadius - lineLength) * sin(angleInRad) + circleCenter.y
                )
                val lineEnd = Offset(
                    x = outerRadius * cos(angleInRad) + circleCenter.x,
                    y = outerRadius * sin(angleInRad) + circleCenter.y
                )
                // draw text
                drawContext.canvas.nativeCanvas.apply {
                    if (lineType is LineType.TenStep) {
                        val textRadius =
                            outerRadius - lineLength - 5.dp.toPx() - style.textSize.toPx()
                        val x = textRadius * cos(angleInRad) + circleCenter.x
                        val y = textRadius * sin(angleInRad) + circleCenter.y
                        withRotation(
                            degrees = angleInRad * (180f / PI.toFloat()) + 90f,
                            pivotX = x,
                            pivotY = y
                        ) {
                            drawText(
                                abs(i).toString(),
                                x,
                                y,
                                Paint().apply {
                                    textSize = style.textSize.toPx()
                                    textAlign = Paint.Align.CENTER
                                }
                            )
                        }
                    }
                }
                drawLine(
                    color = lineColor,
                    start = lineStart,
                    end = lineEnd,
                    strokeWidth = 1.dp.toPx()
                )
                val middleTop = Offset(
                    x = circleCenter.x,
                    y = circleCenter.y - innerRadius - style.scaleIndicatorLength.toPx()
                )
                val bottomLeft = Offset(
                    x = circleCenter.x - 4f,
                    y = circleCenter.y - innerRadius
                )
                val bottomRight = Offset(
                    x = circleCenter.x + 4f,
                    y = circleCenter.y - innerRadius
                )
                val indicator = Path().apply {
                    moveTo(middleTop.x, middleTop.y)
                    lineTo(bottomLeft.x, bottomLeft.y)
                    lineTo(bottomRight.x, bottomRight.y)
                    lineTo(middleTop.x, middleTop.y)
                }
                drawPath(
                    path = indicator,
                    color = style.scaleIndicatorColor
                )
            }
        }
    )
}