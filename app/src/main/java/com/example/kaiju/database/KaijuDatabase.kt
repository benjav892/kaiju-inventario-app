package com.example.kaiju.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto

@Database(entities = [Producto::class, Movimiento::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class KaijuDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile
        private var instancia: KaijuDatabase? = null

        fun obtenerInstancia(context: Context): KaijuDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    KaijuDatabase::class.java,
                    "kaiju_database"
                ).build().also { instancia = it }
            }
    }
}