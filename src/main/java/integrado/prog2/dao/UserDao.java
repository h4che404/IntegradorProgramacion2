package integrado.prog2.dao;

import integrado.prog2.config.ConnectionFactory;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
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

public class UserDao implements CrudDao<Usuario> {
    private static final String UNIQUE_VIOLATION_SQL_STATE = "23000";

    private final ConnectionFactory connectionFactory;

    public UserDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Usuario> findAll() {
        String sql = "SELECT id, nombre, apellido, mail, celular, password, rol, eliminado, created_at FROM usuario WHERE eliminado = false ORDER BY id";
        List<Usuario> users = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
            return users;
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudieron listar los usuarios.", exception);
        }
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT id, nombre, apellido, mail, celular, password, rol, eliminado, created_at FROM usuario WHERE id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo buscar el usuario.", exception);
        }
    }

    @Override
    public Usuario save(Usuario entity) {
        String sql = "INSERT INTO usuario (nombre, apellido, mail, celular, password, rol, eliminado, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillUserStatement(entity, statement, false);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
            return entity;
        } catch (SQLException exception) {
            throw translateException("No se pudo crear el usuario.", "Ya existe un usuario con ese mail.", exception);
        }
    }

    @Override
    public Usuario update(Usuario entity) {
        String sql = "UPDATE usuario SET nombre = ?, apellido = ?, mail = ?, celular = ?, password = ?, rol = ? WHERE id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillUserStatement(entity, statement, true);

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new EntityNotFoundException("El usuario no existe o fue eliminado.");
            }
            return entity;
        } catch (SQLException exception) {
            throw translateException("No se pudo actualizar el usuario.", "Ya existe un usuario con ese mail.", exception);
        }
    }

    @Override
    public void softDelete(Long id) {
        String sql = "UPDATE usuario SET eliminado = true WHERE id = ? AND eliminado = false";

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new EntityNotFoundException("El usuario no existe o fue eliminado.");
            }
        } catch (SQLException exception) {
            throw new DataAccessException("No se pudo eliminar el usuario.", exception);
        }
    }

    private void fillUserStatement(Usuario entity, PreparedStatement statement, boolean includeId) throws SQLException {
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getMail());
        statement.setString(4, entity.getPhone());
        statement.setString(5, entity.getPassword());
        statement.setString(6, entity.getRole().name());

        if (includeId) {
            statement.setLong(7, entity.getId());
        } else {
            statement.setBoolean(7, entity.isEliminado());
            statement.setTimestamp(8, Timestamp.valueOf(entity.getCreatedAt()));
        }
    }

    private Usuario mapUser(ResultSet resultSet) throws SQLException {
        return new Usuario(
                resultSet.getLong("id"),
                resultSet.getBoolean("eliminado"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getString("nombre"),
                resultSet.getString("apellido"),
                resultSet.getString("mail"),
                resultSet.getString("celular"),
                resultSet.getString("password"),
                Rol.valueOf(resultSet.getString("rol"))
        );
    }

    private RuntimeException translateException(String genericMessage, String duplicateMessage, SQLException exception) {
        if (UNIQUE_VIOLATION_SQL_STATE.equals(exception.getSQLState()) || exception.getErrorCode() == 1062) {
            return new DuplicateEntityException(duplicateMessage, exception);
        }
        return new DataAccessException(genericMessage, exception);
    }
}
