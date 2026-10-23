package com.example.kaiju.database

import androidx.room.TypeConverter
import com.example.kaiju.model.TipoMovimiento

class Converters {
    @TypeConverter
    fun fromTipoMovimiento(value: TipoMovimiento?): String? = value?.name

    @TypeConverter
    fun toTipoMovimiento(value: String?): TipoMovimiento? =
        value?.let { enumValueOf<TipoMovimiento>(it) }
}