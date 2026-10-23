package com.example.kaiju.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kaiju.database.KaijuDatabase
import com.example.kaiju.repository.InventarioRepository

class InventarioViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val database = KaijuDatabase.obtenerInstancia(context)
    private val repository = InventarioRepository(
        database.productoDao(),
        database.movimientoDao()
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventarioViewModel::class.java)) {
            return InventarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}