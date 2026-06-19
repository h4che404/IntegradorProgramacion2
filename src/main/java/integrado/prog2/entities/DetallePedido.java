package integrado.prog2.entities;

import integrado.prog2.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

public class DetallePedido extends BaseEntity {
    private int quantity;
    private Double subtotal;
    private Producto product;

    public DetallePedido() {
        super();
    }

    public DetallePedido(int quantity, Double subtotal, Producto product) {
        super();
        setQuantity(quantity);
        this.subtotal = subtotal;
        this.product = product;
    }

    public DetallePedido(Long id, boolean eliminado, LocalDateTime createdAt, int quantity, Double subtotal, Producto product) {
        super(id, eliminado, createdAt);
        setQuantity(quantity);
        this.subtotal = subtotal;
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("La cantidad debe ser mayor a cero.");
        }
        this.quantity = quantity;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Producto getProduct() {
        return product;
    }

    public void setProduct(Producto product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return String.format("- %s | Cantidad: %d | Subtotal: %.2f",
                product != null ? product.getName() : "N/A",
                quantity,
                subtotal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetallePedido detail)) {
            return false;
        }
        return Objects.equals(getId(), detail.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
