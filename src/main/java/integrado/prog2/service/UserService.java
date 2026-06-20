package integrado.prog2.service;

import integrado.prog2.dao.UserDao;
import integrado.prog2.entities.Usuario;
import integrado.prog2.exception.EntityNotFoundException;
import integrado.prog2.exception.ValidationException;

import java.util.List;
import java.util.regex.Pattern;

public class UserService {
    private static final Pattern BASIC_MAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<Usuario> findAll() {
        return userDao.findAll();
    }

    public Usuario findById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El usuario no existe o fue eliminado."));
    }

    public Usuario create(Usuario user) {
        validate(user);
        return userDao.save(user);
    }

    public Usuario update(Usuario user) {
        validate(user);
        validateId(user.getId(), "El ID del usuario es obligatorio para actualizar.");
        findById(user.getId());
        return userDao.update(user);
    }

    public void delete(Long id) {
        findById(id);
        userDao.softDelete(id);
    }

    private void validate(Usuario user) {
        if (user == null) {
            throw new ValidationException("El usuario es obligatorio.");
        }
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new ValidationException("El nombre del usuario es obligatorio.");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new ValidationException("El apellido del usuario es obligatorio.");
        }
        if (user.getMail() == null || user.getMail().isBlank()) {
            throw new ValidationException("El mail del usuario es obligatorio.");
        }
        if (!BASIC_MAIL_PATTERN.matcher(user.getMail()).matches()) {
            throw new ValidationException("El formato del mail es inválido.");
        }
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            throw new ValidationException("El celular del usuario es obligatorio.");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new ValidationException("La contraseña del usuario es obligatoria.");
        }
        if (user.getRole() == null) {
            throw new ValidationException("El rol del usuario es obligatorio.");
        }
    }

    private void validateId(Long id, String message) {
        if (id == null) {
            throw new ValidationException(message);
        }
    }
}
