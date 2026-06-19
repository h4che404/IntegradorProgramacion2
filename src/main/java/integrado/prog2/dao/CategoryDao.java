package integrado.prog2.dao;

import integrado.prog2.config.ConnectionFactory;
import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.DataAccessException;
import integrado.prog2.exception.DuplicateEntityException;
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

public class CategoryDao implements CrudDao<Categoria> {
    private static final String UNIQUE_VIOLATION_SQL_STATE = "23000";

    private final ConnectionFactory connectionFactory;

    public CategoryDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Categoria> findAll() {
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categoria WHERE eliminado = false ORDER BY id";
        List<Categoria> categories = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(mapCategory(resultSet));
            }
            return categories;
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudieron listar las categorías.", exception);
        }
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at FROM categoria WHERE id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapCategory(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo buscar la categoría.", exception);
        }
    }

    @Override
    public Categoria save(Categoria entity) {
        String sql = "INSERT INTO categoria (nombre, descripcion, eliminado, created_at) VALUES (?, ?, ?, ?)";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setBoolean(3, entity.isEliminado());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getCreatedAt()));
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
            return entity;
        } catch (SQLException exception) {
            throw translateException("No se pudo crear la categoría.", "Ya existe una categoría con ese nombre.", exception);
        }
    }

    @Override
    public Categoria update(Categoria entity) {
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setLong(3, entity.getId());

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new EntityNotFoundException("La categoría no existe o fue eliminada.");
            }
            return entity;
        } catch (SQLException exception) {
            throw translateException("No se pudo actualizar la categoría.", "Ya existe una categoría con ese nombre.", exception);
        }
    }

    @Override
    public void softDelete(Long id) {
        String sql = "UPDATE categoria SET eliminado = true WHERE id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new EntityNotFoundException("La categoría no existe o fue eliminada.");
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo eliminar la categoría.", exception);
        }
    }

    public long countActiveProducts(Long categoryId) {
        String sql = "SELECT COUNT(*) FROM producto WHERE categoria_id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                return 0L;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo validar si la categoría tiene productos activos.", exception);
        }
    }

    private Categoria mapCategory(ResultSet resultSet) throws SQLException {
        return new Categoria(
                resultSet.getLong("id"),
                resultSet.getBoolean("eliminado"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getString("nombre"),
                resultSet.getString("descripcion")
        );
    }

    private RuntimeException translateException(String genericMessage, String duplicateMessage, SQLException exception) {
        if (UNIQUE_VIOLATION_SQL_STATE.equals(exception.getSQLState()) || exception.getErrorCode() == 1062) {
            return new DuplicateEntityException(duplicateMessage, exception);
        }
        return new DataAccessException(genericMessage, exception);
    }
}
