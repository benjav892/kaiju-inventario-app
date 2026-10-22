package com.example.kaiju.repository

import com.example.kaiju.model.Movimiento
import com.example.kaiju.model.Producto
import com.example.kaiju.model.Rol
import com.example.kaiju.model.TipoMovimiento
import com.example.kaiju.model.Usuario

object DatosDePrueba {

    // 3 usuarios de prueba: uno por cada perfil del enum Rol
    val usuariosIniciales: List<Usuario> = listOf(
        Usuario(
            id = "USR-01",
            nombre = "Carla Méndez (Admin)",
            rol = Rol.ADMINISTRADOR
        ),
        Usuario(
            id = "USR-02",
            nombre = "Diego Rojas (Ventas)",
            rol = Rol.VENDEDOR
        ),
        Usuario(
            id = "USR-03",
            nombre = "Matías Soto (Bodega)",
            rol = Rol.ENCARGADO_INVENTARIO
        )
    )

    // catalogo con 12 productos ficticios
    // incluye precios como Int en pesos chilenos y productos con stockActual <= stockMinimo
    val productosIniciales: List<Producto> = listOf(
        Producto(
            codigo = "KJ-POL-001",
            nombre = "Polera Oversize Kaiju Classic",
            descripcion = "Polera 100% algodón pesado con estampado frontal Kaiju.",
            categoria = "Poleras",
            tipo = "Prenda",
            precio = 16990,
            stockActual = 18,
            stockMinimo = 5,
            detalle = "Talla L, Color Negro"
        ),
        Producto(
            codigo = "KJ-POL-002",
            nombre = "Polera Gráfica Tokyo Neon",
            descripcion = "Polera corte urbano con serigrafía de alta densidad en espalda.",
            categoria = "Poleras",
            tipo = "Prenda",
            precio = 18990,
            stockActual = 3, // EN ALERTA (3 <= 5)
            stockMinimo = 5,
            detalle = "Talla M, Color Blanco"
        ),
        Producto(
            codigo = "KJ-PLN-001",
            nombre = "Polerón Canguro Mecha",
            descripcion = "Polerón con capucha y bolsillo frontal, interior de franela.",
            categoria = "Polerones",
            tipo = "Prenda",
            precio = 34990,
            stockActual = 12,
            stockMinimo = 4,
            detalle = "Talla XL, Color Gris Carbón"
        ),
        Producto(
            codigo = "KJ-PLN-002",
            nombre = "Polerón Crewneck Minimal",
            descripcion = "Polerón cuello redondo con bordado pequeño en pecho.",
            categoria = "Polerones",
            tipo = "Prenda",
            precio = 29990,
            stockActual = 2, // EN ALERTA (2 <= 4)
            stockMinimo = 4,
            detalle = "Talla M, Color Verde Musgo"
        ),
        Producto(
            codigo = "KJ-PAN-001",
            nombre = "Pantalón Cargo Streetwear",
            descripcion = "Pantalón tela ripstop resistente con 6 bolsillos laterales.",
            categoria = "Pantalones",
            tipo = "Prenda",
            precio = 32990,
            stockActual = 10,
            stockMinimo = 3,
            detalle = "Talla 42, Color Negro"
        ),
        Producto(
            codigo = "KJ-PAN-002",
            nombre = "Jogger Urbano Kaiju",
            descripcion = "Pantalón buzo calce relajado con puño elástico en tobillo.",
            categoria = "Pantalones",
            tipo = "Prenda",
            precio = 26990,
            stockActual = 3, // EN ALERTA (3 <= 3)
            stockMinimo = 3,
            detalle = "Talla L, Color Beige"
        ),
        Producto(
            codigo = "KJ-CHA-001",
            nombre = "Chaqueta Cortaviento Kaiju",
            descripcion = "Cortaviento impermeable ligero con cierre termosellado.",
            categoria = "Chaquetas",
            tipo = "Prenda",
            precio = 45990,
            stockActual = 7,
            stockMinimo = 2,
            detalle = "Talla L, Color Negro/Morado"
        ),
        Producto(
            codigo = "KJ-CHA-002",
            nombre = "Chaqueta Bomber bordada",
            descripcion = "Chaqueta estilo aviador acolchada con parches bordados.",
            categoria = "Chaquetas",
            tipo = "Prenda",
            precio = 52990,
            stockActual = 1, // EN ALERTA (1 <= 2)
            stockMinimo = 2,
            detalle = "Talla M, Color Azul Marino"
        ),
        Producto(
            codigo = "KJ-ACC-001",
            nombre = "Gorro Beanie Bordado",
            descripcion = "Gorro tejido punto doble ideal para invierno.",
            categoria = "Accesorios",
            tipo = "Accesorio",
            precio = 9990,
            stockActual = 25,
            stockMinimo = 6,
            detalle = "Talla Única, Color Negro"
        ),
        Producto(
            codigo = "KJ-ACC-002",
            nombre = "Tote Bag Lona Kaiju",
            descripcion = "Bolso reutilizable de lona gruesa 380g con diseño exclusivo.",
            categoria = "Accesorios",
            tipo = "Accesorio",
            precio = 8990,
            stockActual = 15,
            stockMinimo = 5,
            detalle = "Modelo 2026, Color Crudo"
        ),
        Producto(
            codigo = "KJ-INS-001",
            nombre = "Pack Bolsas Compostables Tienda",
            descripcion = "Insumo de empaque para entrega de compras en local.",
            categoria = "Insumos",
            tipo = "Insumo",
            precio = 4500,
            stockActual = 4, // EN ALERTA (4 <= 5) -> Insumo para probar SALIDA_CONSUMO
            stockMinimo = 5,
            detalle = "Lote B-2026, Medida 40x50cm"
        ),
        Producto(
            codigo = "KJ-INS-002",
            nombre = "Rollo Etiquetas Térmicas Precios",
            descripcion = "Rollo de 500 etiquetas adhesivas para rotuladora de bodega.",
            categoria = "Insumos",
            tipo = "Insumo",
            precio = 3990,
            stockActual = 9,
            stockMinimo = 3,
            detalle = "Lote E-104, Vencimiento adhesivo 12/2027"
        )
    )

    // historial de movimientos ficticios para probar trazabilidad
    val movimientosIniciales: List<Movimiento> = listOf(
        Movimiento(
            id = 1,
            codigoProducto = "KJ-POL-001",
            tipo = TipoMovimiento.ENTRADA,
            cantidad = 25,
            motivo = "Recepción inicial colección temporada",
            fecha = "2026-10-15 09:30",
            usuario = "Matías Soto (Bodega)"
        ),
        Movimiento(
            id = 2,
            codigoProducto = "KJ-POL-001",
            tipo = TipoMovimiento.SALIDA_VENTA,
            cantidad = 7,
            motivo = "Venta en tienda presencial",
            fecha = "2026-10-16 14:20",
            usuario = "Diego Rojas (Ventas)"
        ),
        Movimiento(
            id = 3,
            codigoProducto = "KJ-POL-002",
            tipo = TipoMovimiento.SALIDA_VENTA,
            cantidad = 12,
            motivo = "Venta promocional fin de semana",
            fecha = "2026-10-17 18:10",
            usuario = "Diego Rojas (Ventas)"
        ),
        Movimiento(
            id = 4,
            codigoProducto = "KJ-PLN-001",
            tipo = TipoMovimiento.SALIDA_VENTA,
            cantidad = 5,
            motivo = "Venta en tienda presencial",
            fecha = "2026-10-18 11:45",
            usuario = "Diego Rojas (Ventas)"
        ),
        Movimiento(
            id = 5,
            codigoProducto = "KJ-INS-001",
            tipo = TipoMovimiento.SALIDA_CONSUMO,
            cantidad = 6,
            motivo = "Uso interno en empaque de ventas semanales",
            fecha = "2026-10-19 16:00",
            usuario = "Matías Soto (Bodega)"
        ),
        Movimiento(
            id = 6,
            codigoProducto = "KJ-CHA-002",
            tipo = TipoMovimiento.AJUSTE,
            cantidad = 1,
            motivo = "Diferencia detectada en conteo físico de bodega",
            fecha = "2026-10-20 10:15",
            usuario = "Carla Méndez (Admin)"
        )
    )
}