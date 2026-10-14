package com.tienda.gestion.adapter.in.web;

import java.time.LocalDateTime;

public class ErrorResponse {

    private LocalDateTime fecha;
    private int estado;
    private String error;
    private String mensaje;

    public ErrorResponse(LocalDateTime fecha, int estado, String error, String mensaje) {
        this.fecha = fecha;
        this.estado = estado;
        this.error = error;
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}