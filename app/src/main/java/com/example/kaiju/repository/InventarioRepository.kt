package com.example.kaiju.repository

import com.example.kaiju.database.MovimientoDao
import com.example.kaiju.database.ProductoDao
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow

class InventarioRepository(
    private val productoDao: ProductoDao,
    private val movimientoDao: MovimientoDao
) {
    val productos: Flow<List<Producto>> = productoDao.obtenerTodos()
    val movimientos: Flow<List<Movimiento>> = movimientoDao.obtenerTodos()

    fun obtenerMovimientosPorProducto(codigo: String): Flow<List<Movimiento>> {
        return movimientoDao.obtenerPorProducto(codigo)
    }

    suspend fun obtenerProductoPorCodigo(codigo: String): Producto? {
        return productoDao.obtenerPorCodigo(codigo)
    }

    suspend fun insertarProducto(producto: Producto) {
        productoDao.insertar(producto)
    }

    suspend fun actualizarProducto(producto: Producto) {
        productoDao.actualizar(producto)
    }

    suspend fun precargarDatosSiEstaVacia() {
        if (productoDao.contarProductos() == 0) {
            productoDao.insertarTodos(DatosDePrueba.productosIniciales)
            movimientoDao.insertarTodos(DatosDePrueba.movimientosIniciales)
        }
    }

    suspend fun registrarMovimiento(movimiento: Movimiento): Result<Unit> {
        return try {
            val producto = productoDao.obtenerPorCodigo(movimiento.codigoProducto)
                ?: return Result.failure(Exception("Producto no encontrado"))

            val nuevoStock = when (movimiento.tipo) {
                TipoMovimiento.ENTRADA -> producto.stockActual + movimiento.cantidad
                TipoMovimiento.SALIDA_VENTA, TipoMovimiento.SALIDA_CONSUMO -> {
                    val remanente = producto.stockActual - movimiento.cantidad
                    if (remanente < 0) {
                        return Result.failure(
                            Exception("Una salida no puede dejar el stock bajo cero (Disponible: ${producto.stockActual}).")
                        )
                    }
                    remanente
                }
                TipoMovimiento.AJUSTE -> {
                    if (movimiento.cantidad < 0) {
                        return Result.failure(
                            Exception("El ajuste no puede dejar el stock bajo cero.")
                        )
                    }
                    movimiento.cantidad
                }
            }

            val productoActualizado = producto.copy(stockActual = nuevoStock)

            // guarda el nuevo stock y el movimiento en una sola transacción de Room
            movimientoDao.registrarMovimientoConStock(
                productoActualizado = productoActualizado,
                movimiento = movimiento
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al procesar la transacción: ${e.message}"))
        }
    }
}