# Kaiju — Inventario

Aplicación Android para la gestión y trazabilidad de inventario de Kaiju (tienda de ropa), con control de stock, registro de movimientos, alertas de stock bajo y ranking de ventas, según el perfil de cada usuario.

Proyecto del ramo **Desarrollo de Aplicaciones Móviles (Duoc UC)**, Parcial 2.

- Estado del documento: en progreso

## Integrantes

| Nombre completo | Correo |
| --- | --- |
| Benjamin Vasquez | benjam.vasquezc@duocuc.cl |
| Camilo Vera | c.vera2@duocuc.cl/ veravalenzuelacamilo@gmail.com |
| Sebastian Toro | se.torof@duocuc.cl |


## Funcionalidades

- **Inicio de sesión por perfil**: se elige uno de tres usuarios de prueba (sin contraseña).
- **Catálogo**: listado de productos con búsqueda por nombre o código y filtro por categoría (chips).
- **Detalle de producto**: datos, stock, indicador de nivel y movimientos asociados.
- **Alta de productos**: formulario con validaciones (código único, nombre mínimo de 3 caracteres, precio > 0, stock mínimo ≥ 0).
- **Registro de movimientos**: entradas, salidas por venta, salidas por consumo y ajustes, con actualización automática del stock.
- **Historial de movimientos**: bitácora completa de movimientos (solo Administrador).
- **Alertas**: productos con `stockActual <= stockMinimo` (Administrador y Encargado de Inventario).
- **Ranking de ventas**: más / menos vendidos, resumen consolidado y tendencias por categoría.
- **Persistencia local** con Room: los datos se conservan entre sesiones y se precargan con datos de prueba la primera vez.

## Perfiles y permisos

| Usuario de prueba | Rol | Permisos |
|---|---|---|
| Carla Méndez (Admin) | `ADMINISTRADOR` | Crear/editar productos, ver historial y alertas. Movimientos: `ENTRADA`, `AJUSTE` |
| Diego Rojas (Ventas) | `VENDEDOR` | Catálogo, ranking. Movimientos: solo `SALIDA_VENTA` |
| Matías Soto (Bodega) | `ENCARGADO_INVENTARIO` | Ver alertas. Movimientos: `ENTRADA`, `SALIDA_VENTA`, `SALIDA_CONSUMO`, `AJUSTE` |

La barra de navegación inferior se adapta al rol: el Administrador ve **Historial** y **Agregar**; los demás ven **Movimiento**. **Alertas** solo aparece para Administrador y Encargado de Inventario.

## Reglas de negocio

- **RN-01**: una salida (venta o consumo) no puede dejar el stock bajo cero. Se valida en el formulario y de nuevo en el repositorio.
- **RN-04**: el motivo es obligatorio en los ajustes de stock.
- Un `AJUSTE` fija el stock al valor indicado; una `ENTRADA` lo suma y una `SALIDA_*` lo resta.
- Un producto entra en alerta cuando su stock actual es menor o igual a su stock mínimo.

## Recursos nativos

- **Cámara**: botón de escaneo con flujo completo de permisos en tiempo de ejecución (solicitud, explicación propia con diálogo, reconsulta al volver desde Ajustes mediante `LifecycleResumeEffect`). Incluye un simulador de lectura de etiquetas para probar en emulador.
- **Compartir**: reportes de stock y ventas mediante `Intent.ACTION_SEND`, con confirmación háptica.
- **Galería**: selector de foto de referencia con `PickVisualMedia` (no requiere permisos de almacenamiento).

## Tecnologías

- Kotlin y Jetpack Compose (Material 3)
- Navigation Compose
- Room 2.8.4 con KSP
- Arquitectura MVVM con `StateFlow`
- Android Gradle Plugin 9.4.1, Kotlin 2.2.10, Gradle 9.6.0
- `minSdk` 24, `targetSdk` / `compileSdk` 37

## Estructura del proyecto

```
app/src/main/java/com/example/kaiju/
├── MainActivity.kt
├── database/
│   ├── KaijuDatabase.kt               # Base de datos Room (singleton)
│   ├── ProductoDao.kt
│   ├── MovimientoDao.kt
│   └── Converters.kt                  # TypeConverter de TipoMovimiento
├── model/
│   ├── Producto.kt                    # Entidad Room
│   ├── Movimiento.kt                  # Entidad Room
│   ├── TipoMovimiento.kt              # ENTRADA, SALIDA_VENTA, SALIDA_CONSUMO, AJUSTE
│   ├── Usuario.kt
│   ├── Rol.kt                         # ADMINISTRADOR, VENDEDOR, ENCARGADO_INVENTARIO
│   ├── FormularioProductoEstado.kt
│   ├── FormularioMovimientoEstado.kt
│   ├── FormularioLoginEstado.kt
│   └── ErroresLogin.kt
├── repository/
│   ├── InventarioRepository.kt        # Acceso a datos y lógica de stock
│   └── DatosDePrueba.kt               # Usuarios y 12 productos iniciales
├── viewmodel/
│   ├── InventarioViewModel.kt         # Productos, movimientos y formularios
│   ├── InventarioViewModelFactory.kt
│   └── SesionViewModel.kt             # Sesión y permisos por rol
├── navigation/
│   ├── Rutas.kt
│   └── AppNavHost.kt                  # NavHost y barra inferior según rol
└── ui/
    ├── components/                    # TarjetaProducto, TarjetaMovimientos, IndicadorStock,
    │                                  # BotonConPermisoCamara, RecursosNativosKaiju, etc.
    ├── screen/
    │   ├── PantallaLogin.kt
    │   ├── PantallaCatalogo.kt
    │   ├── PantallaDetalleProducto.kt
    │   ├── PantallaFormularioProducto.kt
    │   ├── PantallaMovimiento.kt
    │   ├── PantallaHistorialMovimientos.kt
    │   ├── PantallaAlertas.kt
    │   └── PantallaRanking.kt
    └── theme/                         # Color, Type, Theme
```

## Requisitos y cómo ejecutar

- Android Studio (versión compatible con AGP 9.4.1) con JDK 11 o superior.
- Emulador o teléfono con Android 7.0 (API 24) o superior.

1. Clonar o descargar el repositorio.
2. Abrir la carpeta en Android Studio y esperar la sincronización de Gradle.
3. Presionar Run ▶.
4. En la pantalla de login, elegir un usuario de prueba para ingresar.

La primera ejecución crea la base de datos `kaiju_database` y la precarga con los datos de prueba. Para reiniciarla, desinstalar la app o borrar sus datos desde Ajustes.

## Limitaciones conocidas

- El login es de demostración: no hay contraseñas ni backend.
- El escáner de la cámara es un simulador; aún no lee códigos reales.
- La base de datos está en la versión 1 sin migraciones (`exportSchema = false`).

## Decisiones propias del equipo

- Se decidió implementar un login de demostración sin backend: el usuario se elige entre tres perfiles de prueba y no hay contraseñas.
- Se decidió solicitar el permiso de cámara para el escaneo de códigos de productos.
- Se decidió solicitar acceso a la galería del dispositivo para elegir la foto de referencia de un producto.

## Enlace

- Tablero de Trello: https://trello.com/invite/b/6ac68b3b7e9030283f8d51be/ATTI1a76cf2173b3135cc8caf071e90dd3912AD45153/kaiju-app
