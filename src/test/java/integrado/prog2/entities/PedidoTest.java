package integrado.prog2.entities;

import integrado.prog2.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoTest {

    @Test
    void calcularTotalDebeAcumularSubtotalesYUnificarProductoRepetido() {
        Pedido pedido = new Pedido();
        Producto producto = crearProducto();

        pedido.addDetallePedido(2, producto.getPrice(), producto);
        pedido.addDetallePedido(1, producto.getPrice(), producto);

        assertEquals(1, pedido.getDetails().size());
        assertEquals(3, pedido.getDetails().getFirst().getQuantity());
        assertEquals(25500.0, pedido.calcularTotal());
    }

    @Test
    void addDetallePedidoDebeRechazarCantidadNoPositiva() {
        Pedido pedido = new Pedido();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> pedido.addDetallePedido(0, 8500.0, crearProducto())
        );

        assertEquals("La cantidad debe ser mayor a cero.", exception.getMessage());
    }

    private Producto crearProducto() {
        Categoria categoria = new Categoria("Pizzas", "Especialidades");
        categoria.setId(1L);

        Producto producto = new Producto("Muzzarella", 8500.0, "Pizza clásica", 10, "muzzarella.jpg", true, categoria);
        producto.setId(1L);
        return producto;
    }
}
