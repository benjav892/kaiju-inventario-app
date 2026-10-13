package com.example.kaiju.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kaiju.model.Producto


@Composable
fun TarjetaProducto(producto: Producto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Nombre: ${producto.nombre}", color = MaterialTheme.colorScheme.primary)
            Text(text = "Categoria: ${producto.categoria}")
            Text(text = "precio $${producto.precio}")
            Text(text = "Stock: ${producto.stockActual}")
            BotonMarcar()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTarjetaProducto() {
    TarjetaProducto(producto = Producto(
        codigo = "kai-400",
        nombre = "Producto1",
        descripcion = "El primer producto",
        categoria = "Categoría1",
        tipo = "Tipo1",
        precio = 15990,
        stockActual = 2,
        stockMinimo = 5,
        detalle = null
    ))
}