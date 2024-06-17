package com.leeweeder.weighttracker.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.weighttracker.ui.LocalNavController
import com.leeweeder.weighttracker.ui.components.NumberKeyBoard
import com.leeweeder.weighttracker.ui.components.rememberNumberKeyBoardState
import com.leeweeder.weighttracker.util.MAX_WEIGHT
import com.leeweeder.weighttracker.util.Screen

@Composable
fun OnBoardingScreen(viewmodel: OnBoardingViewModel = hiltViewModel()) {
    OnBoardingScreen(setWeight = viewmodel::setGoalWeight)
}

@Composable
fun OnBoardingScreen(
    setWeight: (Double) -> Unit
) {
    val navController = LocalNavController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val numberKeyBoardState =
            rememberNumberKeyBoardState(maxValue = MAX_WEIGHT)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Set your goal!", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(32.dp))
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = numberKeyBoardState.value,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = (MaterialTheme.typography.displayLarge.fontSize.value + 20).sp),
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        var isMenuExpanded by remember { mutableStateOf(false) }
                        var selectedUnit by remember { mutableStateOf("kg") }
                        TextButton(
                            onClick = { isMenuExpanded = true },
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(ButtonDefaults.TextButtonWithIconContentPadding)
                        ) {
                            Text(text = selectedUnit)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                        }
                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text(text = "kg") },
                                onClick = {
                                    selectedUnit = "kg"
                                    isMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                Button(
                    onClick = {
                        setWeight(numberKeyBoardState.value.toDouble())
                        navController.navigate(
                            Screen.AddEditLogScreen.createRoute(
                                fromSetGoalWeightScreen = true
                            )
                        ) {
                            popUpTo(Screen.OnBoardingScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Next")
                }
                Spacer(modifier = Modifier.height(8.dp))
                val outsidePadding = 16.dp
                NumberKeyBoard(
                    state = numberKeyBoardState, modifier = Modifier
                        .padding(horizontal = outsidePadding)
                        .padding(bottom = outsidePadding)
                )
            }
        }
    }
}