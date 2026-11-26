package com.example.kaiju

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kaiju.ui.screen.PantallaLogin
import com.example.kaiju.ui.theme.KaijuTheme
import com.example.kaiju.viewmodel.SesionViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginPantallaTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginVacioMuestraErroresEnLosDosCampos() {
        composeRule.setContent {
            KaijuTheme { PantallaLogin(sesionViewModel = SesionViewModel(), onLoginExitoso = {}) }
        }

        composeRule.onNodeWithText("Ingresar").performClick()

        composeRule.onNodeWithText("Ingresa tu correo").assertIsDisplayed()
        composeRule.onNodeWithText("Ingresa tu contraseña").assertIsDisplayed()
    }

    @Test
    fun correoSinArrobaMuestraFormatoInvalido() {
        composeRule.setContent {
            KaijuTheme { PantallaLogin(sesionViewModel = SesionViewModel(), onLoginExitoso = {}) }
        }

        composeRule.onNodeWithText("Correo electrónico").performTextInput("sin-arroba")
        composeRule.onNodeWithText("Contraseña").performTextInput("1234")
        composeRule.onNodeWithText("Ingresar").performClick()

        composeRule.onNodeWithText("Formato de correo inválido").assertIsDisplayed()
    }
}