package com.tienda.gestion.adapter.in.web;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tienda.gestion.application.exception.ProductoNoEncontradoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja las excepciones cuando no se encuentra un producto.
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarProductoNoEncontrado(
            ProductoNoEncontradoException ex) {

        // Construye una respuesta con código HTTP 404.
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    // Maneja errores producidos por datos inválidos.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarDatosInvalidos(
            IllegalArgumentException ex) {

        // Construye una respuesta con código HTTP 400.
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}