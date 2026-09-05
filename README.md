# Evidencia de Aprendizaje 2 - Taller Practico

Aplicacion Android de dos actividades que permiten enviar datos entre dos vistas conectando con la logica de la aplicacion. En este caso se implemento un flujo de carrito de compra: desde el catalogo se seleccionan productos, se confirma o cancela el pedido en una segunda pantalla y el resultado regresa a la actividad principal.

## Datos de identificacion

- **Nombre:** Santiago Jaramillo
- **Institucion:** IU Digital de Antioquia
- **Materia:** Programacion de Dispositivos Moviles

## Funcionamiento

La aplicacion consta de dos actividades:

### MainActivity (Catalogo)

- Muestra tres productos: Smartphone, Laptop y Audifonos, cada uno con su precio.
- Cada producto tiene botones para aumentar (+) y disminuir (-) la cantidad seleccionada.
- El total se calcula y se muestra en tiempo real conforme se modifica la cantidad de cada producto.
- El boton **Pagar** envia las cantidades y el total a la segunda actividad.
- El boton **Reiniciar** (icono rojo en la parte superior derecha) limpia las cantidades, el total y el mensaje de resultado para volver al estado inicial.
- Al regresar de la segunda actividad, muestra el resultado final:

  - **Pedido confirmado:** cuadro verde con icono y el mensaje "Pedido confirmado por [monto]".
  - **Pedido cancelado:** cuadro rojo con icono y el mensaje "Pedido cancelado" (sin monto).
  - **Sin productos:** si se presiona Pagar sin haber seleccionado nada, muestra el aviso "Agrega al menos un producto antes de pagar".

### SecondActivity (Confirmacion de pedido)

- Recibe las cantidades y el total enviados desde MainActivity.
- Muestra el detalle del pedido: solo los productos con cantidad mayor a cero, junto con su subtotal.
- Muestra el total a pagar.
- El boton **Confirmar pedido** regresa a MainActivity indicando que el pedido fue confirmado.
- El boton **Cancelar pedido** regresa a MainActivity indicando que el pedido fue cancelado.

### Comunicacion entre actividades

- **Envios:** de MainActivity a SecondActivity mediante un `Intent` con extras (`EXTRA_QUANTITY_1`, `EXTRA_QUANTITY_2`, `EXTRA_QUANTITY_3`, `EXTRA_TOTAL`).
- **Respuestas:** SecondActivity devuelve un resultado con `setResult()` mas un Intent con `EXTRA_RESULT_LABEL` y `EXTRA_RESULT_TOTAL`. MainActivity lo recibe con `registerForActivityResult(ActivityResultContracts.StartActivityForResult())`.
- **Persistencia:** las cantidades y el mensaje de resultado se guardan con `onSaveInstanceState` para sobrevivir a la rotacion del dispositivo.

### Tecnologias y recursos

- **Lenguaje:** Kotlin
- **Interfaz:** ViewBinding
- **Material Design:** MaterialToolbar, MaterialButton, MaterialCardView, MaterialTextView
- Los precios y totales se formatean con `NumberFormat` en es-MX.

## Requisitos

- Android Studio (con JDK incluido)
- SDK de Android con minSdk 24
- Un emulador o dispositivo fisico con depuracion USB activada

## Como ejecutar

1. Clona o abre el proyecto en Android Studio.
2. Espera a que Gradle sincronice el proyecto.
3. Conecta un dispositivo o inicia un emulador.
4. Presiona el boton Run (Play) en Android Studio.
5. Selecciona el dispositivo de destino y confirma.

Tambien puede generarse el APK desde la terminal:

```
gradlew.bat assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

Para construir por linea de comandos es necesario definir JAVA_HOME apuntando al JBR de Android Studio:

```
set JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
gradlew.bat assembleDebug
```

## Estructura del proyecto

```
app/src/main/java/com/example/evidencia_aprendizaje_2/
  MainActivity.kt      Actividad principal (catalogo y resultado)
  SecondActivity.kt    Actividad secundaria (confirmacion del pedido)

app/src/main/res/layout/
  activity_main.xml    Layout del catalogo
  activity_second.xml  Layout de confirmacion del pedido

app/src/main/res/drawable/
  ic_shopping_cart.xml        Icono del boton Pagar
  ic_check_circle.xml         Icono del boton Confirmar pedido
  ic_check_circle_green.xml   Icono de resultado confirmado
  ic_cancel_circle_red.xml    Icono de resultado cancelado
  ic_refresh_red.xml          Icono del boton Reiniciar
  bg_result_confirmado.xml    Fondo del mensaje de pedido confirmado
  bg_result_cancelado.xml     Fondo del mensaje de pedido cancelado

app/src/main/res/menu/
  menu_main.xml               Menu con la accion de reiniciar
```
