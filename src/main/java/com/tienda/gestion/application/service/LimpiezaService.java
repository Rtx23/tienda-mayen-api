package com.tienda.gestion.application.service;

import com.tienda.gestion.application.port.in.LimpiezaUseCase;
import com.tienda.gestion.application.port.out.LimpiezaRepository;
import com.tienda.gestion.domain.model.LimpiezaResultado;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de coordinar la limpieza de los datos comerciales.
 *
 * Implementa los casos de uso definidos en LimpiezaUseCase y delega
 * la eliminación de datos al repositorio correspondiente.
 */
@Service
public class LimpiezaService implements LimpiezaUseCase {

    /** Repositorio responsable de eliminar los datos comerciales. */
    private final LimpiezaRepository repository;

    /**
     * Construye el servicio con el repositorio de limpieza.
     *
     * @param repository repositorio utilizado para realizar la limpieza
     */
    public LimpiezaService(LimpiezaRepository repository) {
        this.repository = repository;
    }

    /**
     * Elimina los datos comerciales después de comprobar la confirmación.
     *
     * Esta operación requiere el rol ADMIN y se ejecuta dentro
     * de una transacción.
     *
     * @param confirmacion texto utilizado para confirmar la eliminación
     * @return resultado de la operación de limpieza
     * @throws IllegalArgumentException si la confirmación no coincide
     *                                  con el texto requerido
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public LimpiezaResultado limpiar(String confirmacion) {

        // Comprueba que la solicitud incluya la frase exacta de confirmación.
        if (!"ELIMINAR_DATOS_COMERCIALES".equals(confirmacion)) {
            throw new IllegalArgumentException(
                    "Confirmar eliminación de productos, ventas y detalles"
            );
        }

        // Delega en el repositorio la eliminación de los datos comerciales.
        return repository.eliminarDatosComerciales();
    }
}