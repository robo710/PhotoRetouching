package com.sonchan.photoretouching.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .background(color = Color(0xFFFFFFFF))
            .fillMaxSize()
    ){

    }
}

@Preview
@Composable
fun MainScreenPreview(){
    MainScreen()
}