package com.leeweeder.weighttracker.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leeweeder.weighttracker.ui.components.NumberKeyBoard
import com.leeweeder.weighttracker.ui.components.rememberNumberKeyBoardState

@Composable
fun OnBoardingScreen() {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Text(text = "Set your goal!")

        val numberKeyBoardState = rememberNumberKeyBoardState(defaultValue = "0")
        Card {
            Text(text = numberKeyBoardState.value)
        }
        NumberKeyBoard(state = numberKeyBoardState, modifier = Modifier.padding(horizontal = 8.dp)) {

        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun OnBoardingScreenPreview() {
    OnBoardingScreen()
}