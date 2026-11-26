package com.example.kaiju

import com.example.kaiju.model.Producto
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.repository.InventarioRepository
import com.example.kaiju.viewmodel.InventarioViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class InventarioViewModelSpec : FunSpec({

    val dispatcher = StandardTestDispatcher()
    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    // Repositorio doble: responde lo que el ViewModel pide al crearse (init, productos, movimientos)
    fun crearRepo(productos: List<Producto> = emptyList()): InventarioRepository {
        val repo = mockk<InventarioRepository>()
        every { repo.productos } returns flowOf(productos)
        every { repo.movimientos } returns flowOf(emptyList())
        coEvery { repo.precargarDatosSiEstaVacia() } just Runs
        return repo
    }

    test("un precio en cero se rechaza") {
        runTest(dispatcher) {
            val vm = InventarioViewModel(crearRepo())
            vm.actualizarCodigo("GUI-001")
            vm.actualizarNombre("Guitarra")
            vm.actualizarCategoria("Instrumentos")
            vm.actualizarPrecio("0")
            vm.actualizarStockMinimo("2")

            val valido = vm.validarFormulario()

            valido shouldBe false
            vm.estadoFormulario.value.errores.errorPrecio shouldBe "El precio debe ser mayor que cero"
        }
    }

    test("un ajuste sin motivo se rechaza") {
        runTest(dispatcher) {
            val vm = InventarioViewModel(crearRepo())
            vm.actualizarCodigoProductoMovimiento("GUI-001")
            vm.actualizarTipoMovimiento(TipoMovimiento.AJUSTE)
            vm.actualizarCantidadMovimiento("5")

            vm.validarFormularioMovimiento() shouldBe false
            vm.estadoFormularioMovimiento.value.errores.errorMotivo shouldBe
                    "El motivo es obligatorio para ajustes de stock"
        }
    }

})