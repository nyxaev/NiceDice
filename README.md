<div align="center">


# NiceDice

**Um dado fluido em Jetpack Compose, feito para rodar bem até em celular pequeno.**

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![minSdk](https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white)
![Licença](https://img.shields.io/badge/licen%C3%A7a-Apache%202.0-blue)

</div>

---

## O que é

O **NiceDice** é um app Android de rolar dado. Você toca no dado (ou no botão **Rolar**) e ele gira em 3D, sobe, quica ao cair e para em uma face de 1 a 6.

Não usa nenhuma imagem: as seis faces são **desenhadas direto em código** com `Canvas`, então o dado fica nítido em qualquer tamanho de tela.

<div align="center"
</div>

## Como a animação funciona

Quando você toca para rolar, várias animações rodam **ao mesmo tempo**:

| Efeito | O que acontece | Como é feito |
| --- | --- | --- |
| Giro 3D | O dado gira 720° no eixo Y e 360° no eixo X, com perspectiva | `rotationX` e `rotationY` no `graphicsLayer`, com `cameraDistance` |
| Inclinação | Balança de leve para um lado e volta | `rotationZ` com `tween` e depois `spring` |
| Pulo | O dado sobe e cai | `translationY` animado de 0 a 1 e de volta |
| Escala | Cresce um pouco no ar e volta | `scaleX` e `scaleY` de 1 a 1,12 e de volta |
| Sombra | Encolhe e clareia quando o dado sobe | Elipse desenhada com base na altura do dado |
| Faces | Trocam rápido e vão desacelerando até o resultado | Laço com `delay` crescente (de 45 ms a 170 ms) |
| Vibração | Toque curto ao assentar | `HapticFeedbackType.LongPress` |

O resultado (1 a 6) é sorteado **no início**; o giro e a troca de faces só servem para chegar nele de forma natural. O quique final usa `spring` com `DampingRatioMediumBouncy`.

### Fluxo de uma rolagem

1. O toque no dado ou no botão **Rolar** chama a função de rolagem.
2. Se o dado já estiver rolando, o toque é ignorado.
3. O resultado, de 1 a 6, é sorteado.
4. Giro 3D, pulo, escala e sombra animam ao mesmo tempo, enquanto as faces trocam cada vez mais devagar.
5. O dado assenta com um quique e o aparelho vibra.
6. O texto **Você tirou N** aparece.

## Conceitos de Compose usados

- **Estado**: `rememberSaveable` guarda a face atual e sobrevive à rotação da tela.
- **`Animatable`**: controla cada valor animado (giro, altura, escala) dentro de uma corrotina.
- **`coroutineScope` + `launch`**: roda todas as animações em paralelo e espera todas terminarem.
- **`graphicsLayer`**: aplica rotação, escala e deslocamento sem recompor a tela.
- **`Canvas` / `DrawScope`**: desenha o corpo do dado, o brilho e os pontos.
- **`BoxWithConstraints`**: calcula o tamanho do dado a partir do espaço disponível.
- **Acessibilidade**: o dado tem `contentDescription` com a face atual.

## Pensado para tela pequena

O tamanho do dado não é fixo. Ele é o **menor** entre 58% da largura e 40% da altura disponíveis, limitado entre 96 dp e 260 dp. Assim:

- em celular pequeno em pé, o dado cabe sem cortar;
- em modo paisagem, o conjunto dado + texto + botão continua visível sem rolagem;
- o app usa tela cheia (*edge-to-edge*) e respeita as barras do sistema.

## Estrutura do projeto

```text
app/src/main/
├── java/com/example/diceroller/
│   ├── MainActivity.kt        # tela, animação e desenho do dado
│   └── ui/theme/
│       ├── Color.kt           # paleta do app
│       ├── Theme.kt           # tema escuro
│       └── Type.kt            # tipografia
└── res/values/
    ├── strings.xml            # textos em português
    └── themes.xml             # tema da Activity
```

## Como rodar

1. Clone o repositório:
   ```bash
   git clone https://github.com/nyxaev/NiceDice.git
   ```
2. Abra a pasta no **Android Studio** (**File > Open**).
3. Aguarde o Gradle sincronizar.
4. Escolha um emulador ou celular e clique em **Run**.

## Tecnologias

| Item | Versão |
| --- | --- |
| Kotlin | 2.1.0 |
| Android Gradle Plugin | 8.8.0 |
| Compose BOM | 2024.12.01 |
| Material 3 | via BOM |
| Activity Compose | 1.9.3 |
| minSdk / targetSdk | 24 / 35 |

## Personalizando

Tudo fica em `MainActivity.kt` e `ui/theme/Color.kt`:

- **Cores**: troque os valores em `Color.kt` (fundo, face, pontos e botão).
- **Duração do giro**: mude os `tween(950, ...)` dos giros em X e Y.
- **Velocidade das faces**: ajuste `espera` (começa em `45L`) e o passo `14L`.
- **Quique**: troque o `DampingRatioMediumBouncy` por `DampingRatioLowBouncy` para quicar mais.
- **Tamanho do dado**: altere os fatores `0.58f` (largura) e `0.4f` (altura).

## Créditos e licença

O projeto parte do codelab [Create an interactive Dice Roller app](https://developer.android.com/codelabs/basic-android-kotlin-compose-build-a-dice-roller-app), do Android Developers, e foi reescrito com visual, animações e textos próprios.

Distribuído sob a licença **Apache 2.0**. Veja o arquivo [`LICENSE`](LICENSE).
