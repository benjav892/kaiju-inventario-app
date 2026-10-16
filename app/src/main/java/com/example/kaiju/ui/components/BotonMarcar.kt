package com.example.kaiju.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CheckCircle


@Composable
fun BotonMarcar (){
    var marcado by remember {mutableStateOf(false)}

    Button(onClick =  {marcado =!marcado }){
        //icono para uso decorativo,
        //sin descripcion ya que ya tiene un texto de marcado o de marcar
        Icon(
            imageVector = if (marcado) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
            contentDescription = null
        )
        Text(if (marcado)" marcado" else " marcar")
    }
}