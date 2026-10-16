package com.example.diceroller.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EsquemaEscuro = darkColorScheme(
    primary = CorBotao,
    background = FundoBase,
    surface = FundoBase,
    onBackground = CorTexto,
    onSurface = CorTexto
)

@Composable
fun DiceRollerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaEscuro,
        typography = Typography,
        content = content
    )
}
