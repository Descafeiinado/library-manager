package br.edu.ifba.inf008.plugins.reports.infrastructure.repositories;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.repositories.Repository;
import br.edu.ifba.inf008.plugins.reports.domain.entities.Report;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReportsRepository implements Repository<Report, String> {

    /**
     * Singleton instance of ReportRepository.
     */
    private static final ReportsRepository INSTANCE = new ReportsRepository();

    protected ReportsRepository() {
    }

    public static ReportsRepository getInstance() {
        return INSTANCE;
    }

    /*
     * In-memory storage for reports.
     */

    private static final Map<String, Report> delegate = new HashMap<>();

    @Override
    public Optional<Report> findById(String s) {
        return Optional.ofNullable(delegate.get(s));
    }

    @Override
    public List<Report> findAll() {
        return List.copyOf(delegate.values());
    }

    @Override
    public Report save(Report entity) {
        delete(entity);

        return delegate.put(entity.getId(), entity);
    }

    @Override
    public void delete(Report entity) {
        if (entity != null) {
            delegate.remove(entity.getId());
        }
    }

    /**
     * Methods that would never be used in this repository.
     */

    @Override
    public Optional<Report> findOne(String fieldName, Object value) {
        return Optional.empty();
    }

    @Override
    public List<Report> findAll(String fieldName, Object value) {
        return List.of();
    }

    @Override
    public PageableResponse<Report> findAll(PageRequest pageRequest) {
        return null;
    }

    @Override
    public PageableResponse<Report> findAll(PageRequest pageRequest, String fieldName,
            Object value) {
        return null;
    }

}
