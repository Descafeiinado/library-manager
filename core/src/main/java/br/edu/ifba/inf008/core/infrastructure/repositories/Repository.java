package br.edu.ifba.inf008.core.infrastructure.repositories;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    Optional<T> findOne(String fieldName, Object value);
    List<T> findAll();
    PageableResponse<T> findAll(PageRequest pageRequest);
    PageableResponse<T> findAll(PageRequest pageRequest, String fieldName, Object value);
    T save(T entity);
    void delete(T entity);
}
