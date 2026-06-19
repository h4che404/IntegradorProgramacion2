package integrado.prog2.dao;

import integrado.prog2.config.ConnectionFactory;
import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.DataAccessException;
import integrado.prog2.exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDao implements CrudDao<Producto> {
    private final ConnectionFactory connectionFactory;

    public ProductDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Producto> findAll() {
        String sql = baseSelect() + " WHERE p.eliminado = false ORDER BY p.id";
        List<Producto> products = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }
            return products;
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudieron listar los productos.", exception);
        }
    }

    public List<Producto> findByCategoryId(Long categoryId) {
        String sql = baseSelect() + " WHERE p.eliminado = false AND p.categoria_id = ? ORDER BY p.id";
        List<Producto> products = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
                return products;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudieron listar los productos por categoría.", exception);
        }
    }

    @Override
    public Optional<Producto> findById(Long id) {
        String sql = baseSelect() + " WHERE p.id = ? AND p.eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapProduct(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo buscar el producto.", exception);
        }
    }

    @Override
    public Producto save(Producto entity) {
        String sql = "INSERT INTO producto (nombre, precio, descripcion, stock, imagen, disponible, categoria_id, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillProductStatement(entity, statement);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
            return entity;
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo crear el producto.", exception);
        }
    }

    @Override
    public Producto update(Producto entity) {
        String sql = "UPDATE producto SET nombre = ?, precio = ?, descripcion = ?, stock = ?, imagen = ?, disponible = ?, categoria_id = ? WHERE id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setDouble(2, entity.getPrice());
            statement.setString(3, entity.getDescription());
            statement.setInt(4, entity.getStock());
            statement.setString(5, entity.getImage());
            statement.setBoolean(6, entity.isAvailable());
            statement.setLong(7, entity.getCategory().getId());
            statement.setLong(8, entity.getId());

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new EntityNotFoundException("El producto no existe o fue eliminado.");
            }
            return entity;
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo actualizar el producto.", exception);
        }
    }

    @Override
    public void softDelete(Long id) {
        String sql = "UPDATE producto SET eliminado = true WHERE id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new EntityNotFoundException("El producto no existe o fue eliminado.");
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo eliminar el producto.", exception);
        }
    }

    private String baseSelect() {
        return "SELECT p.id, p.nombre, p.precio, p.descripcion, p.stock, p.imagen, p.disponible, p.eliminado, p.created_at, "
                + "c.id AS categoria_id, c.nombre AS categoria_nombre, c.descripcion AS categoria_descripcion, "
                + "c.eliminado AS categoria_eliminado, c.created_at AS categoria_created_at "
                + "FROM producto p INNER JOIN categoria c ON p.categoria_id = c.id";
    }

    private void fillProductStatement(Producto entity, PreparedStatement statement) throws SQLException {
        statement.setString(1, entity.getName());
        statement.setDouble(2, entity.getPrice());
        statement.setString(3, entity.getDescription());
        statement.setInt(4, entity.getStock());
        statement.setString(5, entity.getImage());
        statement.setBoolean(6, entity.isAvailable());
        statement.setLong(7, entity.getCategory().getId());
        statement.setBoolean(8, entity.isEliminado());
        statement.setTimestamp(9, Timestamp.valueOf(entity.getCreatedAt()));
    }

    private Producto mapProduct(ResultSet resultSet) throws SQLException {
        Categoria category = new Categoria(
                resultSet.getLong("categoria_id"),
                resultSet.getBoolean("categoria_eliminado"),
                resultSet.getTimestamp("categoria_created_at").toLocalDateTime(),
                resultSet.getString("categoria_nombre"),
                resultSet.getString("categoria_descripcion")
        );

        return new Producto(
                resultSet.getLong("id"),
                resultSet.getBoolean("eliminado"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getString("nombre"),
                resultSet.getDouble("precio"),
                resultSet.getString("descripcion"),
                resultSet.getInt("stock"),
                resultSet.getString("imagen"),
                resultSet.getBoolean("disponible"),
                category
        );
    }
}
