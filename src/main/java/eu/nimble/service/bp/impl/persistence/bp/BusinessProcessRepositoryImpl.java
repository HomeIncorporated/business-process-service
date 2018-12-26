package eu.nimble.service.bp.impl.persistence.bp;

import com.netflix.discovery.converters.Auto;
import eu.nimble.utility.persistence.GenericJPARepository;
import eu.nimble.utility.persistence.GenericJPARepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * In this class, we do not use the methods of @{@link GenericJPARepositoryImpl} directly since the transaction management
 * is available only if the methods are defined in this class.
 *
 * Created by suat on 23-Nov-18.
 */
@Component
@Transactional(transactionManager = "bpdbTransactionManager")
public class BusinessProcessRepositoryImpl implements GenericJPARepository {

    private GenericJPARepositoryImpl hibernateUtility;

    @Autowired
    private EntityManagerFactory emf;

    @PersistenceContext(unitName = "bp-data-model")
    private EntityManager em;

    @PostConstruct
    private void initializeHibernateUtility() {
        this.hibernateUtility = new GenericJPARepositoryImpl(emf, em);
    }
    @Override
    public <T> T getSingleEntityByHjid(Class<T> klass, long hjid) {
        return hibernateUtility.getSingleEntityByHjid(klass, hjid);
    }

    @Override
    public <T> T getSingleEntityByHjidWithCleanEm(Class<T> klass, long hjid) {
        return hibernateUtility.getSingleEntityByHjidWithCleanEm(klass, hjid);
    }

    @Override
    public <T> T getSingleEntity(String query, String[] parameterNames, Object[] parameterValues) {
        return hibernateUtility.getSingleEntity(query, parameterNames, parameterValues);
    }

    @Override
    public <T> List<T> getEntities(String query) {
        return hibernateUtility.getEntities(query);
    }

    @Override
    public <T> List<T> getEntities(String query, String[] parameterNames, Object[] parameterValues) {
        return hibernateUtility.getEntities(query, parameterNames, parameterValues);
    }

    @Override
    public <T> List<T> getEntities(String query, String[] parameterNames, Object[] parameterValues, Integer limit, Integer offset) {
        return hibernateUtility.getEntities(query, parameterNames, parameterValues, limit, offset);
    }

    @Override
    public <T> T updateEntity(T entity) {
        return hibernateUtility.updateEntity(entity);
    }

    @Override
    public <T> void deleteEntity(T entity) {
        hibernateUtility.deleteEntity(entity);
    }

    @Override
    public <T> void deleteEntityByHjid(Class<T> klass, long hjid) {
        hibernateUtility.deleteEntityByHjid(klass, hjid);
    }

    @Override
    public <T> void persistEntity(T entity) {
        hibernateUtility.persistEntity(entity);
    }
}
