package com.example.kaiju

import com.example.kaiju.database.MovimientoDao
import com.example.kaiju.database.ProductoDao
import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.repository.InventarioRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class InventarioRepositorySpec : FunSpec({

    val guitarra = Producto(
        codigo = "GUI-001", nombre = "Guitarra", descripcion = "", categoria = "Instrumentos",
        tipo = "Cuerdas", precio = 150000, stockActual = 5, stockMinimo = 2
    )

    fun movimiento(tipo: TipoMovimiento, cantidad: Int) = Movimiento(
        codigoProducto = "GUI-001", tipo = tipo, cantidad = cantidad,
        motivo = "", fecha = "2026-11-26 10:00", usuario = "test"
    )

    // DAOs dobles: la prueba no usa Room ni emulador
    fun crearDaos(): Pair<ProductoDao, MovimientoDao> {
        val productoDao = mockk<ProductoDao>()
        val movimientoDao = mockk<MovimientoDao>()
        every { productoDao.obtenerTodos() } returns flowOf(emptyList())
        every { movimientoDao.obtenerTodos() } returns flowOf(emptyList())
        coEvery { productoDao.obtenerPorCodigo("GUI-001") } returns guitarra
        return productoDao to movimientoDao
    }

    test("una salida mayor al stock se rechaza y no guarda nada") {
        val (productoDao, movimientoDao) = crearDaos()
        val repo = InventarioRepository(productoDao, movimientoDao)

        val resultado = repo.registrarMovimiento(movimiento(TipoMovimiento.SALIDA_VENTA, 10))

        resultado.isFailure shouldBe true
        resultado.exceptionOrNull()?.message shouldContain "bajo cero"
        coVerify(exactly = 0) { movimientoDao.registrarMovimientoConStock(any(), any()) }
    }

    test("una entrada suma al stock y se guarda junto con el movimiento") {
        val (productoDao, movimientoDao) = crearDaos()
        coEvery { movimientoDao.registrarMovimientoConStock(any(), any()) } just Runs
        val repo = InventarioRepository(productoDao, movimientoDao)

        val resultado = repo.registrarMovimiento(movimiento(TipoMovimiento.ENTRADA, 10))

        resultado.isSuccess shouldBe true
        coVerify(exactly = 1) {
            movimientoDao.registrarMovimientoConStock(match { it.stockActual == 15 }, any())
        }
    }
})