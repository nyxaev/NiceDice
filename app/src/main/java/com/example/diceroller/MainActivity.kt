package com.example.diceroller

import android.graphics.Color as CorAndroid
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diceroller.ui.theme.CorBorda
import com.example.diceroller.ui.theme.CorBotao
import com.example.diceroller.ui.theme.CorFaceClara
import com.example.diceroller.ui.theme.CorFaceSombra
import com.example.diceroller.ui.theme.CorPonto
import com.example.diceroller.ui.theme.CorPontoBrilho
import com.example.diceroller.ui.theme.CorPontoUm
import com.example.diceroller.ui.theme.CorPontoUmBrilho
import com.example.diceroller.ui.theme.CorTexto
import com.example.diceroller.ui.theme.DiceRollerTheme
import com.example.diceroller.ui.theme.FundoBase
import com.example.diceroller.ui.theme.FundoTopo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(CorAndroid.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(CorAndroid.TRANSPARENT)
        )
        setContent {
            DiceRollerTheme {
                TelaDado(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun TelaDado(modifier: Modifier = Modifier) {
    var valor by rememberSaveable { mutableStateOf(1) }
    var rolando by remember { mutableStateOf(false) }

    val giroX = remember { Animatable(0f) }
    val giroY = remember { Animatable(0f) }
    val giroZ = remember { Animatable(0f) }
    val altura = remember { Animatable(0f) }
    val escala = remember { Animatable(1f) }

    val escopo = rememberCoroutineScope()
    val haptico = LocalHapticFeedback.current
    val interacao = remember { MutableInteractionSource() }

    val opacidadeTexto by animateFloatAsState(
        targetValue = if (rolando) 0f else 1f,
        animationSpec = tween(250),
        label = "opacidadeTexto"
    )
    val descricao = stringResource(R.string.dado_descricao, valor)

    fun rolar() {
        if (rolando) return
        rolando = true
        escopo.launch {
            val resultado = (1..6).random()
            val sentido = if ((0..1).random() == 0) 1f else -1f
            coroutineScope {
                launch {
                    giroY.animateTo(
                        giroY.value + 720f * sentido,
                        tween(950, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    giroX.animateTo(
                        giroX.value - 360f * sentido,
                        tween(950, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    giroZ.animateTo(14f * sentido, tween(320))
                    giroZ.animateTo(
                        0f,
                        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                    )
                }
                launch {
                    altura.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
                    altura.animateTo(
                        0f,
                        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                    )
                }
                launch {
                    escala.animateTo(1.12f, tween(380, easing = FastOutSlowInEasing))
                    escala.animateTo(
                        1f,
                        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
                    )
                }
                launch {
                    var espera = 45L
                    var anterior = valor
                    while (espera < 170L) {
                        var proximo = (1..6).random()
                        while (proximo == anterior) proximo = (1..6).random()
                        valor = proximo
                        anterior = proximo
                        delay(espera)
                        espera += 14L
                    }
                    valor = resultado
                }
            }
            giroX.snapTo(0f)
            giroY.snapTo(0f)
            haptico.performHapticFeedback(HapticFeedbackType.LongPress)
            rolando = false
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .background(Brush.verticalGradient(listOf(FundoTopo, FundoBase)))
            .safeDrawingPadding()
    ) {
        val lado = minOf(maxWidth * 0.58f, maxHeight * 0.4f).coerceIn(96.dp, 260.dp)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(lado * 1.4f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(width = lado, height = lado * 0.16f)
                        .offset(y = lado * 0.62f)
                ) {
                    val progresso = altura.value
                    val largura = size.width * (0.85f - 0.25f * progresso)
                    drawOval(
                        color = Color.Black.copy(alpha = 0.4f - 0.25f * progresso),
                        topLeft = Offset((size.width - largura) / 2f, 0f),
                        size = Size(largura, size.height)
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(lado)
                        .graphicsLayer {
                            rotationX = giroX.value
                            rotationY = giroY.value
                            rotationZ = giroZ.value
                            scaleX = escala.value
                            scaleY = escala.value
                            translationY = -altura.value * lado.toPx() * 0.2f
                            cameraDistance = 14f * density
                        }
                        .semantics { contentDescription = descricao }
                        .clickable(
                            interactionSource = interacao,
                            indication = null
                        ) { rolar() }
                ) {
                    desenharDado(valor)
                }
            }

            Text(
                text = stringResource(R.string.voce_tirou, valor),
                color = CorTexto,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.graphicsLayer { alpha = opacidadeTexto }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { rolar() },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CorBotao,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(52.dp)
            ) {
                Text(
                    text = stringResource(R.string.rolar),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun DrawScope.desenharDado(valor: Int) {
    val lado = size.minDimension
    val raio = CornerRadius(lado * 0.2f, lado * 0.2f)

    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(CorFaceClara, CorFaceSombra),
            start = Offset.Zero,
            end = Offset(lado, lado)
        ),
        cornerRadius = raio
    )
    drawRoundRect(
        color = CorBorda,
        cornerRadius = raio,
        style = Stroke(width = lado * 0.015f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.6f),
        topLeft = Offset(lado * 0.04f, lado * 0.04f),
        size = Size(lado * 0.92f, lado * 0.92f),
        cornerRadius = CornerRadius(lado * 0.17f, lado * 0.17f),
        style = Stroke(width = lado * 0.012f)
    )

    val raioPonto = if (valor == 1) lado * 0.12f else lado * 0.085f
    val corPonto = if (valor == 1) CorPontoUm else CorPonto
    val brilhoPonto = if (valor == 1) CorPontoUmBrilho else CorPontoBrilho

    posicoesDosPontos(valor).forEach { (fx, fy) ->
        val centro = Offset(lado * fx, lado * fy)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(brilhoPonto, corPonto),
                center = centro - Offset(raioPonto * 0.3f, raioPonto * 0.3f),
                radius = raioPonto * 1.5f
            ),
            radius = raioPonto,
            center = centro
        )
    }
}

private fun posicoesDosPontos(valor: Int): List<Pair<Float, Float>> {
    val a = 0.27f
    val m = 0.5f
    val b = 0.73f
    return when (valor) {
        1 -> listOf(m to m)
        2 -> listOf(a to a, b to b)
        3 -> listOf(a to a, m to m, b to b)
        4 -> listOf(a to a, b to a, a to b, b to b)
        5 -> listOf(a to a, b to a, m to m, a to b, b to b)
        else -> listOf(a to a, b to a, a to m, b to m, a to b, b to b)
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 568)
@Composable
fun DiceRollerApp() {
    DiceRollerTheme {
        TelaDado(modifier = Modifier.fillMaxSize())
    }
}
