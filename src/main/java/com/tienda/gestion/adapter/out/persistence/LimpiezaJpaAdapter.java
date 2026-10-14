package com.tienda.gestion.adapter.out.persistence;
import com.tienda.gestion.application.port.out.LimpiezaRepository;
import com.tienda.gestion.domain.model.LimpiezaResultado;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
@Repository
public class LimpiezaJpaAdapter implements LimpiezaRepository {
    private final EntityManager em;
    public LimpiezaJpaAdapter(EntityManager em) { this.em = em; }
    @Override
    public LimpiezaResultado eliminarDatosComerciales() {
        em.flush();
        // Borrar según las relaciones; nunca se modifica UsuarioEntity.
        int detalles = em.createQuery("delete from DetalleVentaEntity").executeUpdate();
        int ventas = em.createQuery("delete from VentaEntity").executeUpdate();
        int productos = em.createQuery("delete from ProductoEntity").executeUpdate();
        em.clear();
        return new LimpiezaResultado(detalles, ventas, productos, true);
    }
}
