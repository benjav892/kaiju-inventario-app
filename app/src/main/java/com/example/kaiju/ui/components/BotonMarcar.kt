package com.example.kaiju.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun BotonMarcar (){
    var marcado by remember {mutableStateOf(false)}

    Button(onClick =  {marcado =!marcado }){
        Text(if (marcado)"✓ marcado" else "marcar")
    }
}