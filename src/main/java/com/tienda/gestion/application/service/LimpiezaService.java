package com.tienda.gestion.application.service;
import com.tienda.gestion.application.port.in.LimpiezaUseCase;
import com.tienda.gestion.application.port.out.LimpiezaRepository;
import com.tienda.gestion.domain.model.LimpiezaResultado;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class LimpiezaService implements LimpiezaUseCase {
    private final LimpiezaRepository repository;
    public LimpiezaService(LimpiezaRepository repository) { this.repository = repository; }
    @Override @Transactional @PreAuthorize("hasRole('ADMIN')")
    public LimpiezaResultado limpiar(String confirmacion) {
        if (!"ELIMINAR_DATOS_COMERCIALES".equals(confirmacion)) {
            throw new IllegalArgumentException("Confirmar eliminación de productos, ventas y detalles");
        }
        return repository.eliminarDatosComerciales();
    }
}
