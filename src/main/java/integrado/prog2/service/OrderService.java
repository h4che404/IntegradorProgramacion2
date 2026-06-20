package integrado.prog2.service;

import integrado.prog2.dao.OrderDao;
import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.exception.EntityNotFoundException;
import integrado.prog2.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final OrderDao orderDao;
    private final UserService userService;
    private final ProductService productService;

    public OrderService(OrderDao orderDao, UserService userService, ProductService productService) {
        this.orderDao = orderDao;
        this.userService = userService;
        this.productService = productService;
    }

    public List<Pedido> findAll() {
        return orderDao.findAll();
    }

    public List<Pedido> findAllByUserId(Long userId) {
        if (userId != null) {
            userService.findById(userId);
        }
        return orderDao.findAllByUserId(userId);
    }

    public Pedido findById(Long id) {
        return orderDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El pedido no existe o fue eliminado."));
    }

    public Pedido create(Pedido order) {
        validate(order);

        Usuario activeUser = userService.findById(order.getUser().getId());
        order.setUser(activeUser);
        order.setDate(order.getDate() == null ? LocalDate.now() : order.getDate());
        order.setStatus(order.getStatus() == null ? Estado.PENDIENTE : order.getStatus());

        List<DetallePedido> normalizedDetails = new ArrayList<>();
        Pedido normalizedOrder = new Pedido();
        normalizedOrder.setDate(order.getDate());
        normalizedOrder.setStatus(order.getStatus());
        normalizedOrder.setPaymentMethod(order.getPaymentMethod());
        normalizedOrder.setUser(activeUser);

        for (DetallePedido detail : order.getDetails()) {
            Producto product = productService.findById(detail.getProduct().getId());
            normalizedOrder.addDetallePedido(detail.getQuantity(), product.getPrice(), product);
        }

        for (DetallePedido detail : normalizedOrder.getDetails()) {
            normalizedDetails.add(detail);
        }

        order.setDetails(normalizedDetails);
        order.calcularTotal();
        return orderDao.save(order);
    }

    public Pedido update(Pedido order) {
        if (order == null) {
            throw new ValidationException("El pedido es obligatorio.");
        }
        if (order.getId() == null) {
            throw new ValidationException("El ID del pedido es obligatorio para actualizar.");
        }
        if (order.getStatus() == null) {
            throw new ValidationException("El estado del pedido es obligatorio.");
        }
        if (order.getPaymentMethod() == null) {
            throw new ValidationException("La forma de pago es obligatoria.");
        }

        Pedido existingOrder = findById(order.getId());
        existingOrder.setStatus(order.getStatus());
        existingOrder.setPaymentMethod(order.getPaymentMethod());
        return orderDao.update(existingOrder);
    }

    public void delete(Long id) {
        findById(id);
        orderDao.softDelete(id);
    }

    private void validate(Pedido order) {
        if (order == null) {
            throw new ValidationException("El pedido es obligatorio.");
        }
        if (order.getUser() == null || order.getUser().getId() == null) {
            throw new ValidationException("El pedido debe tener un usuario activo.");
        }
        if (order.getPaymentMethod() == null) {
            throw new ValidationException("La forma de pago es obligatoria.");
        }
        if (order.getDetails() == null || order.getDetails().isEmpty()) {
            throw new ValidationException("El pedido debe tener al menos un detalle.");
        }

        for (DetallePedido detail : order.getDetails()) {
            if (detail == null) {
                throw new ValidationException("Cada detalle del pedido debe existir.");
            }
            if (detail.getProduct() == null || detail.getProduct().getId() == null) {
                throw new ValidationException("Cada detalle del pedido debe tener un producto válido.");
            }
            if (detail.getQuantity() <= 0) {
                throw new ValidationException("La cantidad del detalle debe ser mayor a cero.");
            }
        }
    }
}
