package integrado.prog2.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T> {
    List<T> findAll();

    Optional<T> findById(Long id);

    T save(T entity);

    T update(T entity);

    void softDelete(Long id);
}
