package com.tienda.gestion.adapter.in.web;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tienda.gestion.application.exception.ProductoNoEncontradoException;
import com.tienda.gestion.application.exception.VentaNoEncontradaException;
import com.tienda.gestion.application.exception.ConflictoInventarioException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;

/**
 * Maneja las excepciones de la aplicación y las transforma en respuestas HTTP.
 * Permite devolver errores con una estructura uniforme mediante ErrorResponse.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja los casos en que no se encuentra una venta.
     *
     * @param ex excepción que indica que la venta no existe
     * @return respuesta HTTP 404 con el mensaje del error
     */
    @ExceptionHandler(VentaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> ventaNoEncontrada(VentaNoEncontradaException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Maneja los conflictos relacionados con el inventario,
     * como cuando no hay suficiente stock.
     *
     * @param ex excepción que describe el conflicto de inventario
     * @return respuesta HTTP 409
     */
    @ExceptionHandler(ConflictoInventarioException.class)
    public ResponseEntity<ErrorResponse> inventario(ConflictoInventarioException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Maneja las violaciones de restricciones de integridad de datos.
     *
     * @param ex excepción producida por una operación sobre los datos
     * @return respuesta HTTP 409 con un mensaje descriptivo
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex) {
        return error(HttpStatus.CONFLICT, "La operación viola una restricción de datos. No se puede eliminar un producto asociado a ventas.");
    }

    /**
     * Maneja los conflictos producidos por bloqueos de concurrencia.
     *
     * @param ex excepción relacionada con el bloqueo de datos
     * @return respuesta HTTP 409
     */
    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> concurrencia(PessimisticLockingFailureException ex) {
        return error(HttpStatus.CONFLICT, "El inventario está siendo actualizado; vuelve a intentar la operación");
    }

    /**
     * Maneja los errores de validación de los datos recibidos.
     *
     * @param ex excepción que contiene los errores de validación
     * @return respuesta HTTP 400 con los campos que presentan errores
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
        // Reúne los errores por campo, los ordena y los concatena en un mensaje.
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).sorted().collect(java.util.stream.Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, mensaje);
    }

    /**
     * Maneja las solicitudes cuyo JSON no puede leerse correctamente.
     *
     * @param ex excepción generada al interpretar el cuerpo de la solicitud
     * @return respuesta HTTP 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex) {
        return error(HttpStatus.BAD_REQUEST, "JSON inválido o tipos de datos incorrectos");
    }

    /**
     * Maneja los errores de autenticación.
     *
     * @param ex excepción relacionada con las credenciales
     * @return respuesta HTTP 401
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> credenciales(AuthenticationException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    /**
     * Maneja los intentos de acceso a recursos sin los permisos necesarios.
     *
     * @param ex excepción que indica que el acceso fue denegado
     * @return respuesta HTTP 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> acceso(AccessDeniedException ex) {
        return error(HttpStatus.FORBIDDEN, "No tienes permiso para esta operación");
    }

    /**
     * Construye una respuesta de error con una estructura uniforme.
     *
     * @param status estado HTTP que se devolverá
     * @param mensaje descripción del error
     * @return respuesta HTTP con un objeto ErrorResponse
     */
    private ResponseEntity<ErrorResponse> error(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensaje));
    }

    /**
     * Maneja los casos en que no se encuentra un producto.
     *
     * @param ex excepción que indica que el producto no existe
     * @return respuesta HTTP 404 con los detalles del error
     */
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarProductoNoEncontrado(
            ProductoNoEncontradoException ex) {

        // Construye la respuesta con la fecha, el estado HTTP y el mensaje.
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );

        // Devuelve la respuesta con estado HTTP 404.
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    /**
     * Maneja los argumentos que no son válidos para una operación.
     *
     * @param ex excepción que contiene el motivo de la entrada inválida
     * @return respuesta HTTP 400 con el mensaje correspondiente
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarDatosInvalidos(
            IllegalArgumentException ex) {

        // Construye la respuesta de error para la solicitud inválida.
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );

        // Devuelve la respuesta con estado HTTP 400.
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}