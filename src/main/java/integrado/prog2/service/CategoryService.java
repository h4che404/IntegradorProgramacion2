package integrado.prog2.service;

import integrado.prog2.dao.CategoryDao;
import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.EntityNotFoundException;
import integrado.prog2.exception.ValidationException;

import java.util.List;

public class CategoryService {
    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public List<Categoria> findAll() {
        return categoryDao.findAll();
    }

    public Categoria findById(Long id) {
        return categoryDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La categoría no existe o fue eliminada."));
    }

    public Categoria create(Categoria category) {
        validate(category);
        return categoryDao.save(category);
    }

    public Categoria update(Categoria category) {
        validate(category);
        findById(category.getId());
        return categoryDao.update(category);
    }

    public void delete(Long id) {
        findById(id);
        if (categoryDao.countActiveProducts(id) > 0) {
            throw new ValidationException("La categoría tiene productos activos y no puede eliminarse.");
        }
        categoryDao.softDelete(id);
    }

    private void validate(Categoria category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new ValidationException("El nombre de la categoría es obligatorio.");
        }
    }
}
