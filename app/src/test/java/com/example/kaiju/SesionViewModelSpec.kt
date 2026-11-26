package com.example.kaiju

import com.example.kaiju.model.AuthResponse
import com.example.kaiju.model.Rol
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.model.UsuarioResponse
import com.example.kaiju.remote.KaijuApiClient
import com.example.kaiju.remote.KaijuApiService
import com.example.kaiju.viewmodel.SesionViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response


@OptIn(ExperimentalCoroutinesApi::class)
class SesionViewModelSpec : FunSpec({

    val dispatcher = StandardTestDispatcher()
    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest {
        Dispatchers.resetMain()
        KaijuApiClient.tokenJwt = null
    }
    test("un 401 del backend muestra correo o contraseña incorrectos") {
        runTest(dispatcher) {
            val api = mockk<KaijuApiService>()
            val error401 = HttpException(Response.error<AuthResponse>(401, "".toResponseBody(null)))
            coEvery { api.login(any()) } throws error401
            val vm = SesionViewModel(api)
            vm.actualizarEmail("ana@duoc.cl")
            vm.actualizarPassword("mala")

            vm.iniciarSesion { }
            advanceUntilIdle()

            vm.estadoFormulario.value.errores.errorGeneral shouldBe "Correo o contraseña incorrectos"
            vm.usuarioActivo.value shouldBe null
        }
    }

    test("un login correcto deja al usuario activo con el rol del backend") {
        runTest(dispatcher) {
            val api = mockk<KaijuApiService>()
            coEvery { api.login(any()) } returns AuthResponse(
                accessToken = "token-falso", tokenType = "Bearer", expiresIn = 3600,
                usuario = UsuarioResponse(1, "Ana", "Pérez", "ana@duoc.cl", null, "Vendedor")
            )
            val vm = SesionViewModel(api)
            vm.actualizarEmail("ana@duoc.cl")
            vm.actualizarPassword("1234")

            var exito = false
            vm.iniciarSesion { exito = true }
            advanceUntilIdle()

            exito shouldBe true
            vm.usuarioActivo.value?.nombre shouldBe "Ana Pérez"
            vm.usuarioActivo.value?.rol shouldBe Rol.VENDEDOR
            vm.obtenerTiposMovimientoPermitidos() shouldBe listOf(TipoMovimiento.SALIDA_VENTA)
            vm.puedeGestionarUsuarios() shouldBe false
        }
    }
})