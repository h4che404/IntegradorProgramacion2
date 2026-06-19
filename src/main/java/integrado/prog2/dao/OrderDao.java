package integrado.prog2.dao;

import integrado.prog2.config.ConnectionFactory;
import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.DataAccessException;
import integrado.prog2.exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDao implements CrudDao<Pedido> {
    private final ConnectionFactory connectionFactory;

    public OrderDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Pedido> findAll() {
        return findAllByUserId(null);
    }

    public List<Pedido> findAllByUserId(Long userId) {
        StringBuilder sql = new StringBuilder(baseOrderSelect())
                .append(" WHERE p.eliminado = false");

        if (userId != null) {
            sql.append(" AND p.usuario_id = ?");
        }

        sql.append(" ORDER BY p.id DESC");
        List<Pedido> orders = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            if (userId != null) {
                statement.setLong(1, userId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapOrder(resultSet, connection));
                }
            }
            return orders;
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudieron listar los pedidos.", exception);
        }
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        String sql = baseOrderSelect() + " WHERE p.id = ? AND p.eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapOrder(resultSet, connection));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo buscar el pedido.", exception);
        }
    }

    @Override
    public Pedido save(Pedido entity) {
        String orderSql = "INSERT INTO pedido (fecha, estado, total, forma_pago, usuario_id, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String detailSql = "INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, subtotal, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement orderStatement = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement detailStatement = connection.prepareStatement(detailSql)) {

                orderStatement.setDate(1, java.sql.Date.valueOf(entity.getDate()));
                orderStatement.setString(2, entity.getStatus().name());
                orderStatement.setDouble(3, entity.getTotal());
                orderStatement.setString(4, entity.getPaymentMethod().name());
                orderStatement.setLong(5, entity.getUser().getId());
                orderStatement.setBoolean(6, entity.isEliminado());
                orderStatement.setTimestamp(7, Timestamp.valueOf(entity.getCreatedAt()));
                orderStatement.executeUpdate();

                try (ResultSet generatedKeys = orderStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getLong(1));
                    }
                }

                for (DetallePedido detail : entity.getDetails()) {
                    detailStatement.setLong(1, entity.getId());
                    detailStatement.setLong(2, detail.getProduct().getId());
                    detailStatement.setInt(3, detail.getQuantity());
                    detailStatement.setDouble(4, detail.getSubtotal());
                    detailStatement.setBoolean(5, detail.isEliminado());
                    detailStatement.setTimestamp(6, Timestamp.valueOf(detail.getCreatedAt()));
                    detailStatement.addBatch();
                }

                detailStatement.executeBatch();
                connection.commit();
                return entity;
            } catch (SQLException exception) {
                connection.rollback();
                throw new DataAccessException("No se pudo crear el pedido.", exception);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo crear el pedido.", exception);
        }
    }

    @Override
    public Pedido update(Pedido entity) {
        String sql = "UPDATE pedido SET estado = ?, forma_pago = ?, total = ? WHERE id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getStatus().name());
            statement.setString(2, entity.getPaymentMethod().name());
            statement.setDouble(3, entity.getTotal());
            statement.setLong(4, entity.getId());

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new EntityNotFoundException("El pedido no existe o fue eliminado.");
            }
            return entity;
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo actualizar el pedido.", exception);
        }
    }

    @Override
    public void softDelete(Long id) {
        String orderSql = "UPDATE pedido SET eliminado = true WHERE id = ? AND eliminado = false";
        String detailsSql = "UPDATE detalle_pedido SET eliminado = true WHERE pedido_id = ?";

        try (Connection connection = connectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement orderStatement = connection.prepareStatement(orderSql);
                 PreparedStatement detailsStatement = connection.prepareStatement(detailsSql)) {
                orderStatement.setLong(1, id);
                int updatedRows = orderStatement.executeUpdate();
                if (updatedRows == 0) {
                    throw new EntityNotFoundException("El pedido no existe o fue eliminado.");
                }

                detailsStatement.setLong(1, id);
                detailsStatement.executeUpdate();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw new DataAccessException("No se pudo eliminar el pedido.", exception);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo eliminar el pedido.", exception);
        }
    }

    private String baseOrderSelect() {
        return "SELECT p.id AS pedido_id, p.fecha, p.estado, p.total, p.forma_pago, p.eliminado, p.created_at, "
                + "u.id AS usuario_id, u.nombre AS usuario_nombre, u.apellido AS usuario_apellido, u.mail AS usuario_mail, "
                + "u.celular AS usuario_celular, u.password AS usuario_password, u.rol AS usuario_rol, "
                + "u.eliminado AS usuario_eliminado, u.created_at AS usuario_created_at "
                + "FROM pedido p INNER JOIN usuario u ON p.usuario_id = u.id";
    }

    private Pedido mapOrder(ResultSet resultSet, Connection connection) throws SQLException {
        Usuario user = new Usuario(
                resultSet.getLong("usuario_id"),
                resultSet.getBoolean("usuario_eliminado"),
                resultSet.getTimestamp("usuario_created_at").toLocalDateTime(),
                resultSet.getString("usuario_nombre"),
                resultSet.getString("usuario_apellido"),
                resultSet.getString("usuario_mail"),
                resultSet.getString("usuario_celular"),
                resultSet.getString("usuario_password"),
                Rol.valueOf(resultSet.getString("usuario_rol"))
        );

        Pedido order = new Pedido(
                resultSet.getLong("pedido_id"),
                resultSet.getBoolean("eliminado"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getDate("fecha").toLocalDate(),
                Estado.fromDatabase(resultSet.getString("estado")),
                resultSet.getDouble("total"),
                FormaPago.fromDatabase(resultSet.getString("forma_pago")),
                user,
                loadDetails(connection, resultSet.getLong("pedido_id"))
        );
        order.calcularTotal();
        return order;
    }

    private List<DetallePedido> loadDetails(Connection connection, Long orderId) throws SQLException {
        String sql = "SELECT dp.id AS detalle_id, dp.cantidad, dp.subtotal, dp.eliminado, dp.created_at, "
                + "pr.id AS producto_id, pr.nombre AS producto_nombre, pr.precio AS producto_precio, pr.descripcion AS producto_descripcion, "
                + "pr.stock AS producto_stock, pr.imagen AS producto_imagen, pr.disponible AS producto_disponible, "
                + "pr.eliminado AS producto_eliminado, pr.created_at AS producto_created_at, "
                + "c.id AS categoria_id, c.nombre AS categoria_nombre, c.descripcion AS categoria_descripcion, "
                + "c.eliminado AS categoria_eliminado, c.created_at AS categoria_created_at "
                + "FROM detalle_pedido dp "
                + "INNER JOIN producto pr ON dp.producto_id = pr.id "
                + "INNER JOIN categoria c ON pr.categoria_id = c.id "
                + "WHERE dp.pedido_id = ? AND dp.eliminado = false ORDER BY dp.id";

        List<DetallePedido> details = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Categoria category = new Categoria(
                            resultSet.getLong("categoria_id"),
                            resultSet.getBoolean("categoria_eliminado"),
                            resultSet.getTimestamp("categoria_created_at").toLocalDateTime(),
                            resultSet.getString("categoria_nombre"),
                            resultSet.getString("categoria_descripcion")
                    );

                    Producto product = new Producto(
                            resultSet.getLong("producto_id"),
                            resultSet.getBoolean("producto_eliminado"),
                            resultSet.getTimestamp("producto_created_at").toLocalDateTime(),
                            resultSet.getString("producto_nombre"),
                            resultSet.getDouble("producto_precio"),
                            resultSet.getString("producto_descripcion"),
                            resultSet.getInt("producto_stock"),
                            resultSet.getString("producto_imagen"),
                            resultSet.getBoolean("producto_disponible"),
                            category
                    );

                    details.add(new DetallePedido(
                            resultSet.getLong("detalle_id"),
                            resultSet.getBoolean("eliminado"),
                            resultSet.getTimestamp("created_at").toLocalDateTime(),
                            resultSet.getInt("cantidad"),
                            resultSet.getDouble("subtotal"),
                            product
                    ));
                }
            }
        }
        return details;
    }
}
