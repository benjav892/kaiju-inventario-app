package com.example.kaiju.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Query("SELECT * FROM movimientos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos WHERE codigoProducto = :codigo ORDER BY id DESC")
    fun obtenerPorProducto(codigo: String): Flow<List<Movimiento>>

    @Insert
    suspend fun insertar(movimiento: Movimiento)

    @Insert
    suspend fun insertarTodos(movimientos: List<Movimiento>)

    @Update
    suspend fun actualizarProducto(producto: Producto)

    // ejecuta la actualización de stock y el registro del movimiento en una sola transacción
    @Transaction
    suspend fun registrarMovimientoConStock(
        productoActualizado: Producto,
        movimiento: Movimiento
    ) {
        actualizarProducto(productoActualizado)
        insertar(movimiento)
    }
}