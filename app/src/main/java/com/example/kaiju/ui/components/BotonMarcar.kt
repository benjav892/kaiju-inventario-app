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
import androidx.compose.material.icons.filled.Favorite


@Composable
fun BotonMarcar (){
    var marcado by remember {mutableStateOf(false)}

    Button(onClick =  {marcado =!marcado }){
        //icono favorito para uso decorativo,
        //sin descripcion ya que ya tiene un texto de marcado o de marcar
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null
        )
        Text(if (marcado)"✓ marcado" else "marcar")
    }
}