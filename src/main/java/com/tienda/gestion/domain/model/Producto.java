package com.tienda.gestion.domain.model;

import java.time.LocalDate;

/**
 * Representa un producto dentro del dominio de la tienda.
 *
 * Contiene la información básica para identificar un producto,
 * controlar su precio, registrar su stock y gestionar su vencimiento.
 */
public class Producto {

    /** Identificador único del producto. */
    private Long id;

    /** Nombre descriptivo del producto. */
    private String nombre;

    /** Precio unitario del producto. */
    private double precio;

    /** Cantidad de unidades disponibles en inventario. */
    private int stock;

    /** Fecha de vencimiento del producto, cuando corresponda. */
    private LocalDate fechaVencimiento;

    /** Cantidad mínima de unidades permitidas antes de alertar por stock bajo. */
    private int stockMinimo;

    /**
     * Construye un producto sin inicializar sus atributos.
     */
    public Producto() {
    }

    /**
     * Obtiene el identificador del producto.
     *
     * @return identificador del producto
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del producto.
     *
     * @param id identificador del producto
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del producto.
     *
     * @param nombre nombre descriptivo del producto
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el precio unitario.
     *
     * @return precio del producto
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio unitario.
     *
     * @param precio precio del producto
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene las unidades disponibles.
     *
     * @return cantidad actual en stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * Establece la cantidad disponible en inventario.
     *
     * @param stock cantidad de unidades disponibles
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Obtiene la fecha de vencimiento.
     *
     * @return fecha de vencimiento, o null si no está definida
     */
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * Establece la fecha de vencimiento.
     *
     * @param fechaVencimiento fecha de vencimiento del producto
     */
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Obtiene el umbral mínimo de stock.
     *
     * @return cantidad mínima configurada
     */
    public int getStockMinimo() {
        return stockMinimo;
    }

    /**
     * Establece el umbral mínimo de stock.
     *
     * @param stockMinimo cantidad mínima para el control de inventario
     */
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}