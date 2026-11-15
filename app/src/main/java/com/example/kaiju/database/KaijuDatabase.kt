package com.example.kaiju.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto

// cambiar la versión a 2 para avisar a Room que la estructura cambió (implementacion recursos nativos funcionales)
@Database(entities = [Producto::class, Movimiento::class], version = 2, exportSchema = false)
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
                )
                    // Room recrea la base de datos sin caerse:
                    .fallbackToDestructiveMigration()
                    .build().also { instancia = it }
            }
    }
}