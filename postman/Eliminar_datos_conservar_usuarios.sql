-- Tienda MAYEN: eliminar datos comerciales y conservar autorización.
-- Ejecutar en Query Tool de pgAdmin conectado a tienda_mayenm.
-- Elimina todas las filas de estas tres tablas; no elimina usuarios ni roles.
BEGIN;
DO $$
BEGIN
    IF current_database() <> 'tienda_mayenm' THEN
        RAISE EXCEPTION 'Base incorrecta: conectar a tienda_mayenm';
    END IF;
END
$$;
LOCK TABLE public.detalles_venta, public.ventas, public.productos IN ACCESS EXCLUSIVE MODE;
DELETE FROM public.detalles_venta;
DELETE FROM public.ventas;
DELETE FROM public.productos;
COMMIT;

SELECT 'productos' AS tabla, COUNT(*) AS registros FROM public.productos
UNION ALL SELECT 'ventas', COUNT(*) FROM public.ventas
UNION ALL SELECT 'detalles_venta', COUNT(*) FROM public.detalles_venta
UNION ALL SELECT 'usuarios conservados', COUNT(*) FROM public.usuarios;
