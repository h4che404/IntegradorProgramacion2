package integrado.prog2.service;

import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceTest {

    @Test
    void createDebeValidarQueExistaAlMenosUnDetalle() {
        OrderService orderService = new OrderService(null, null, null);
        Pedido pedido = new Pedido();
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        pedido.setUser(usuario);
        pedido.setPaymentMethod(FormaPago.EFECTIVO);

        ValidationException exception = assertThrows(ValidationException.class, () -> orderService.create(pedido));

        assertEquals("El pedido debe tener al menos un detalle.", exception.getMessage());
    }
}
