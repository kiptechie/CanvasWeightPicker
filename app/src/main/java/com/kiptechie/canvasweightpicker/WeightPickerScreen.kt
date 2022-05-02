package com.kiptechie.canvasweightpicker

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiptechie.canvasweightpicker.components.WeightScale
import com.kiptechie.canvasweightpicker.models.ScaleStyle

@ExperimentalAnimationApi
@Composable
fun WeightPickerScreen() {
    var weight by remember {
        mutableStateOf(80)
    }
    val haptic = LocalHapticFeedback.current
    Box(modifier = Modifier.fillMaxSize()) {
        WeightScale(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter),
            style = ScaleStyle(
                scaleWidth = 140.dp
            ),
            onWeightChange = {
                weight = it
                when {
                    it % 10 == 0 -> {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
            }
        )
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .align(Alignment.Center),
            text = "$weight Kg",
            fontSize = 32.sp
        )
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            text = stringResource(id = R.string.app_name),
            fontSize = 32.sp
        )
    }
}