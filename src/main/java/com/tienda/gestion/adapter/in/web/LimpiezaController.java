package com.tienda.gestion.adapter.in.web;
import com.tienda.gestion.application.port.in.LimpiezaUseCase;
import com.tienda.gestion.domain.model.LimpiezaResultado;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/admin/datos-comerciales")
public class LimpiezaController {
    private final LimpiezaUseCase limpieza;
    public LimpiezaController(LimpiezaUseCase limpieza) { this.limpieza = limpieza; }
    @DeleteMapping
    public LimpiezaResultado limpiar(@RequestHeader("X-Confirmar-Eliminacion") String confirmacion) {
        return limpieza.limpiar(confirmacion);
    }
}
