package com.realityexpander.composestatevsstateflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class MainViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    // STATEFLOW STATE
    // 1- Note: Two variables here. One is a MutableStateFlow, the other is a StateFlow.
    // 2- Note: Can use flow operators with stateFlow.
    // Use MutableStateFlow() (kotlin flow) instead of mutableStateOf() (compose).
    private val _stateFlowColor = MutableStateFlow(0xFFFFFFFF)
    val stateFlowColor = _stateFlowColor.asStateFlow()


    // STATEFLOW STATE USING SavedStateHandle
    // **RECOMMENDED METHOD**
    // Using savedStateHandle to save state (restored after process death)
    // Automatically restored and emitted on configuration change.
    // WHY? Only one variable is needed and it survives process death, and keeps the VM free of compose.
    val stateFlowColor2 = savedStateHandle.getStateFlow("stateFlowColor2", 0xFFFFFFFF)

    // You can also setup complex derived states with stateFlow (cant with compose)
    val stateFlowColorHex = stateFlowColor2.map {
        String.format("#%08X", 0xFFFFFFFF and it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "#FFFFFFFF")



    // COMPOSE STATE
    // 1- Note: just one variable needed here.
    // 2- Note: *CANNOT* use operators with mutableState.
    // NOT RECOMMENDED TO USE COMPOSE STATE IN VIEWMODEL (it makes reliance on compose in the VM)
    var composeColor by mutableStateOf(0xFFFFFFFF)
        private set


    // COMPOSE STATE USING SavedStateHandle
    var composeColor2 by mutableStateOf(
        savedStateHandle["composeColor2"] ?: 0xFFFFFFFF
    )
        private set


    fun generateStateFlowColor() {
        val color = Random.nextLong(0xFFFFFFFF)

        // STATEFLOW STATE
        _stateFlowColor.value = color


        // STATEFLOW STATE USING SavedStateHandle  **RECOMMENDED METHOD **
        // Note: ONLY need to update the savedStateHandle, which automatically updates the stateFlowColor2
        savedStateHandle["stateFlowColor2"] = color // saves in the savedStateHandle
    }

    fun generateComposeColor() {
        val color = Random.nextLong(0xFFFFFFFF) or 0xFF000000 // Make solid color (alpha = 255)

        // COMPOSE STATE
        // Simple, but not recommended to use compose state in viewmodel.
        composeColor = color


        // COMPOSE STATE USING SavedStateHandle
        // Using the stateHandle to save state (restored after process death)
        // NOTE: *MUST* update the compose mutableState variable AND the savedStateHandle variable.
        composeColor2 = color
        savedStateHandle["composeColor2"] = color // saves in the savedStateHandle
    }

}