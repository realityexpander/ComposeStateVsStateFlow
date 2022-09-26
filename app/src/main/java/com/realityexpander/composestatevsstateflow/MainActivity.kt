package com.realityexpander.composestatevsstateflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.realityexpander.composestatevsstateflow.ui.theme.ComposeStateVsStateFlowTheme

// To test process death:
// 1. Run the app
// 2. Click the home button
// 3. in terminal run:
//    adb shell am kill com.realityexpander.composestatevsstateflow
// 4. From the task switcher, click the app icon to restart the app.

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeStateVsStateFlowTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel = viewModel<MainViewModel>()


                    // Note: simple accessor to the state.
                    val composeColor = viewModel.composeColor

                    val composeColor2 = viewModel.composeColor2


                    // Note: more verbose to collect state, but allows for operators.
                    val stateFlowColor by viewModel.stateFlowColor.collectAsState()

                    // Derived state from the stateFlowColor.
                    val stateFlowColorHex by viewModel.stateFlowColorHex.collectAsState()

                    // Uses savedStateHandle in ViewModel to persist state across process death.
                    val stateFlowColor2 by viewModel.stateFlowColor2.collectAsState()


                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        // stateFlowColor version uses a stateFlowColorHex derived state.
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0f)
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color(stateFlowColor))
                                .clickable {
                                    viewModel.generateStateFlowColor()
                                }
                        ){
                            Text(
                                text = stateFlowColorHex,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        // SavedStateHandle version of stateFlowColor (survives process death).
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0f)
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color(stateFlowColor2))
                                .clickable {
                                    viewModel.generateStateFlowColor()
                                }
                        ){
                            Text(
                                text = "SavedStateHandle: " + stateFlowColor2.toString(16),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        // Uses Compose state.
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0f)
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.Red)  // 1 - set border to colorize
                                .padding(start = 10.dp) // 2 - set padding to colorize the border
                                .background(Color(composeColor)) // 3 - set the fill color for the box (Must not be a transparent color)
                                .clickable {
                                    viewModel.generateComposeColor()
                                }

                        ) {
                            Text(
                                text = "0x" + composeColor.toString(16),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        // Uses Compose state from savedStateHandle.
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0f)
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.Blue)  // 1 - set border to colorize
                                .padding(start = 10.dp)  // 2 - set padding to colorize the border
                                .background(Color(composeColor2)) // 3 - set the fill color for the box (Must not be a transparent color)
                                .clickable {
                                    viewModel.generateComposeColor()
                                }

                        ) {
                            Text(
                                text = "SavedStateHandle: 0x" + composeColor2.toString(16),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
//                    Box(modifier=Modifier
//                        .fillMaxSize()
//                        .background(Color(stateFlowColor))
//                        .clickable { viewModel.generateNewColor() }
//                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeStateVsStateFlowTheme {
        Greeting("Android")
    }
}