package com.example.kaiju

import com.example.kaiju.model.Producto
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.repository.InventarioRepository
import com.example.kaiju.viewmodel.InventarioViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

    test("un código repetido se rechaza") {
        runTest(dispatcher) {
            val existente = Producto(
                codigo = "GUI-001", nombre = "Guitarra", descripcion = "", categoria = "Instrumentos",
                tipo = "Cuerdas", precio = 150000, stockActual = 5, stockMinimo = 2
            )
            val vm = InventarioViewModel(crearRepo(listOf(existente)))
            // productos usa WhileSubscribed: hay que suscribirse para que cargue la lista
            backgroundScope.launch { vm.productos.collect { } }
            advanceUntilIdle()

            vm.actualizarCodigo("GUI-001")
            vm.actualizarNombre("Otra guitarra")
            vm.actualizarCategoria("Instrumentos")
            vm.actualizarPrecio("100000")
            vm.actualizarStockMinimo("1")

            vm.validarFormulario() shouldBe false
            vm.estadoFormulario.value.errores.errorCodigo shouldBe "Ese código ya existe"
        }
    }

    test("un producto válido se guarda en el repositorio y el formulario se limpia") {
        runTest(dispatcher) {
            val repo = crearRepo()
            coEvery { repo.insertarProducto(any()) } just Runs
            val vm = InventarioViewModel(repo)
            vm.actualizarCodigo("GUI-001")
            vm.actualizarNombre("Guitarra")
            vm.actualizarCategoria("Instrumentos")
            vm.actualizarPrecio("150000")
            vm.actualizarStockMinimo("2")

            val guardado = vm.guardarProducto()
            advanceUntilIdle()

            guardado shouldBe true
            coVerify(exactly = 1) {
                repo.insertarProducto(match { it.codigo == "GUI-001" && it.precio == 150000 })
            }
            vm.estadoFormulario.value.codigo shouldBe ""
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

    test("un ajuste con motivo es válido") {
        runTest(dispatcher) {
            val vm = InventarioViewModel(crearRepo())
            vm.actualizarCodigoProductoMovimiento("GUI-001")
            vm.actualizarTipoMovimiento(TipoMovimiento.AJUSTE)
            vm.actualizarCantidadMovimiento("5")
            vm.actualizarMotivoMovimiento("Conteo físico")

            vm.validarFormularioMovimiento() shouldBe true
        }
    }
})