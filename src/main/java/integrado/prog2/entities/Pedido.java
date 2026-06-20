package integrado.prog2.entities;

import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.interfaces.Calculable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido extends BaseEntity implements Calculable {
    private LocalDate date;
    private Estado status;
    private Double total;
    private FormaPago paymentMethod;
    private Usuario user;
    private List<DetallePedido> details;

    public Pedido() {
        super();
        this.date = LocalDate.now();
        this.status = Estado.PENDIENTE;
        this.total = 0.0;
        this.details = new ArrayList<>();
    }

    public Pedido(Long id, boolean eliminado, LocalDateTime createdAt, LocalDate date, Estado status, Double total,
                  FormaPago paymentMethod, Usuario user, List<DetallePedido> details) {
        super(id, eliminado, createdAt);
        this.date = date;
        this.status = status;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.user = user;
        this.details = details != null ? details : new ArrayList<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Estado getStatus() {
        return status;
    }

    public void setStatus(Estado status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total != null ? total : 0.0;
    }

    public FormaPago getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(FormaPago paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public List<DetallePedido> getDetails() {
        return details;
    }

    public void setDetails(List<DetallePedido> details) {
        this.details = details != null ? new ArrayList<>(details) : new ArrayList<>();
    }

    public void addDetallePedido(int quantity, Double unitPrice, Producto product) {
        if (quantity <= 0) {
            throw new ValidationException("La cantidad debe ser mayor a cero.");
        }
        if (unitPrice == null || unitPrice < 0) {
            throw new ValidationException("El precio unitario debe ser mayor o igual a cero.");
        }
        if (product == null) {
            throw new ValidationException("El producto es obligatorio.");
        }

        DetallePedido existingDetail = findDetallePedidoByProducto(product);
        double subtotal = quantity * unitPrice;

        if (existingDetail != null) {
            existingDetail.setQuantity(existingDetail.getQuantity() + quantity);
            existingDetail.setSubtotal(existingDetail.getSubtotal() + subtotal);
        } else {
            details.add(new DetallePedido(quantity, subtotal, product));
        }

        calcularTotal();
    }

    public DetallePedido findDetallePedidoByProducto(Producto product) {
        return details.stream()
                .filter(detail -> detail.getProduct() != null && detail.getProduct().equals(product))
                .findFirst()
                .orElse(null);
    }

    public boolean deleteDetallePedidoByProducto(Producto product) {
        boolean removed = details.removeIf(detail -> detail.getProduct() != null && detail.getProduct().equals(product));
        if (removed) {
            calcularTotal();
        }
        return removed;
    }

    @Override
    public Double calcularTotal() {
        total = details.stream().mapToDouble(DetallePedido::getSubtotal).sum();
        return total;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | %s | Total: %.2f | Date: %s",
                getId(),
                user != null ? user.getFirstName() + " " + user.getLastName() : "N/A",
                status != null ? status.getLabel() : "N/A",
                paymentMethod != null ? paymentMethod.getLabel() : "N/A",
                total,
                date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pedido order)) {
            return false;
        }
        return Objects.equals(getId(), order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
