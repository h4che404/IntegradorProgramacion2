package integrado.prog2.service;

import integrado.prog2.dao.ProductDao;
import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntityNotFoundException;
import integrado.prog2.exception.ValidationException;

import java.util.List;

public class ProductService {
    private final ProductDao productDao;
    private final CategoryService categoryService;

    public ProductService(ProductDao productDao, CategoryService categoryService) {
        this.productDao = productDao;
        this.categoryService = categoryService;
    }

    public List<Producto> findAll() {
        return productDao.findAll();
    }

    public List<Producto> findByCategory(Long categoryId) {
        categoryService.findById(categoryId);
        return productDao.findByCategoryId(categoryId);
    }

    public Producto findById(Long id) {
        return productDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El producto no existe o fue eliminado."));
    }

    public Producto create(Producto product) {
        validate(product);
        Categoria category = categoryService.findById(product.getCategory().getId());
        product.setCategory(category);
        return productDao.save(product);
    }

    public Producto update(Producto product) {
        validate(product);
        findById(product.getId());
        Categoria category = categoryService.findById(product.getCategory().getId());
        product.setCategory(category);
        return productDao.update(product);
    }

    public void delete(Long id) {
        findById(id);
        productDao.softDelete(id);
    }

    private void validate(Producto product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new ValidationException("El nombre del producto es obligatorio.");
        }
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new ValidationException("El precio del producto debe ser mayor o igual a cero.");
        }
        if (product.getStock() < 0) {
            throw new ValidationException("El stock del producto debe ser mayor o igual a cero.");
        }
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new ValidationException("La categoría del producto es obligatoria.");
        }
    }
}
