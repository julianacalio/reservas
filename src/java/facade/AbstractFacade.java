package facade;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    //private Session session;
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract SessionFactory getSessionFactory();

    public void save(T entity) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();
        session.close();
    }

    public void edit(T entity) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.update(entity);
        try {
            transaction.commit();
        } finally {
            session.close();
        }
    }

    public T merge(T entity) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        //session.merge(entity);
        T entidade = (T) session.merge(entity);
        transaction.commit();
        session.close();
        //return entidade;
        return entity;

    }

    public void remove(T entity) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(entity);
        try {
            transaction.commit();
        } finally {
            session.close();
        }
    }

    public T find(Long id) {
        Session session = getSessionFactory().openSession();
        T entity = (T) session.get(entityClass, id);
        session.close();
        return entity;
    }

    public List<T> findAll() {
        Session session = getSessionFactory().openSession();
        Criteria crit = session.createCriteria(entityClass);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        crit.setMaxResults(50);
        List results = crit.list();
        session.close();
        return results;
    }

    public List<T> findRange(int[] range) {
        Session session = getSessionFactory().openSession();
        Criteria crit = session.createCriteria(entityClass);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        crit.setMaxResults(range[1] - range[0]);
        crit.setFirstResult(range[0]);
        List results = crit.list();
        session.close();
        return results;
    }

    public int count() {
        Session session = getSessionFactory().openSession();
        Criteria crit = session.createCriteria(entityClass);
        int count = ((Number) crit.setProjection(Projections.rowCount()).uniqueResult()).intValue();
        session.close();
        return count;
    }

  
}
