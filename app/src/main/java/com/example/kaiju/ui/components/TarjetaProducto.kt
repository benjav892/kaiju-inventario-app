package com.example.kaiju.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun TarjetaProducto(id: String, nombre: String, descripcion: String, stock: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "ID: $id", fontWeight= FontWeight.ExtraLight)
            Text(text = "Nombre: $nombre", color = MaterialTheme.colorScheme.primary)
            Text(text = "Descripcion: $descripcion")
            Text(text = "Stock: $stock")
            BotonMarcar()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTarjetaProducto() {
    TarjetaProducto("kai-400", "producto1", "el primer producto", 1)
}