package com.tienda.gestion.application.exception;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Long id) {
        super("Producto no encontrado con ID: " + id);
    }
}