package com.example.fwdthirdproject


sealed class ButtonState {
    object Loading : ButtonState()
    object Completed : ButtonState()
}