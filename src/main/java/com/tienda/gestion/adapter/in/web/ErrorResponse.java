package com.tienda.gestion.adapter.in.web;

import java.time.LocalDateTime;

/**
 * Representa la estructura de una respuesta de error de la API.
 * Contiene la fecha, el código de estado, el tipo de error
 * y un mensaje descriptivo.
 */
public class ErrorResponse {

    // Fecha y hora en que ocurrió el error.
    private LocalDateTime fecha;

    // Código de estado HTTP asociado al error.
    private int estado;

    // Nombre o tipo del error producido.
    private String error;

    // Descripción del error para facilitar su comprensión.
    private String mensaje;

    /**
     * Construye una respuesta de error con todos sus datos.
     *
     * @param fecha fecha y hora del error
     * @param estado código de estado HTTP
     * @param error nombre o tipo del error
     * @param mensaje descripción del error
     */
    public ErrorResponse(LocalDateTime fecha, int estado, String error, String mensaje) {
        this.fecha = fecha;
        this.estado = estado;
        this.error = error;
        this.mensaje = mensaje;
    }

    /**
     * Obtiene la fecha y hora del error.
     *
     * @return fecha y hora del error
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha y hora del error.
     *
     * @param fecha nueva fecha y hora
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el código de estado HTTP.
     *
     * @return código de estado
     */
    public int getEstado() {
        return estado;
    }

    /**
     * Establece el código de estado HTTP.
     *
     * @param estado nuevo código de estado
     */
    public void setEstado(int estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el nombre o tipo del error.
     *
     * @return nombre o tipo del error
     */
    public String getError() {
        return error;
    }

    /**
     * Establece el nombre o tipo del error.
     *
     * @param error nuevo nombre o tipo del error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Obtiene el mensaje descriptivo del error.
     *
     * @return mensaje del error
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el mensaje descriptivo del error.
     *
     * @param mensaje nuevo mensaje del error
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}