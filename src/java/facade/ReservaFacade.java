/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import model.Recurso;
import model.Reserva;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author charles
 */
@Stateless
public class ReservaFacade extends AbstractFacade<Reserva> {

    public ReservaFacade() {
        super(Reserva.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    /**
     * Busca todas as reservas que possuem a mesma data de início, fim e realização.
     *
     * @param inicio Data inicio da reserva
     * @param fim Data do fim da reserva
     * @param realizacao Data de realização da reserva
     * @return Lista contendo todas as reservas que obedecem ao critério especificado
     */
    public List<Reserva> findAll(Date inicio, Date fim, Date realizacao) {
        Session session = getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Reserva.class);
        criteria.add(Restrictions.eq("inicio", inicio));
        criteria.add(Restrictions.eq("fim", fim));
        criteria.add(Restrictions.eq("realizacao", realizacao));

        List results = criteria.list();
        session.close();
        return results;
    }

    /**
     * Busca todas as reservas que estiverem entre a data de início e fim especificada
     *
     * @param inicio Data de Início
     * @param fim Data de Fim
     * @return Lista contendo todas as reservas que obedecem ao critério especificado
     */
    public List<Reserva> findAllBetween(Date inicio, Date fim) {
        Session session = getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Reserva.class);
        criteria.add(Restrictions.or(Restrictions.between("inicio", inicio, fim), Restrictions.between("fim", inicio, fim)));
        List res1 = criteria.list();
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        List results1 = criteria.list();
        
        Criteria criteria2 = session.createCriteria(Reserva.class);
        criteria2.add(Restrictions.and(Restrictions.le("inicio", inicio), Restrictions.ge("fim", fim)));
        criteria2.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        List results2 = criteria2.list();

        results1.addAll(results2);
        
        session.close();
        return results1;
    }

    /**
     * Busca as reservas de um determinado recurso dentro de uma certa data de início e data de fim
     *
     * @param inicio Data de início
     * @param fim Data de fim
     * @param id Recursos pesquisado
     * @return Lista contendo todas as reservas que obedecem ao critério especificado
     */
    public List<Reserva> findBetween(Date inicio, Date fim, Long id) {
        Session session = getSessionFactory().openSession();
        Criteria criteria1 = session.createCriteria(Reserva.class);

        criteria1.add(Restrictions.or(Restrictions.between("inicio", inicio, fim), Restrictions.between("fim", inicio, fim)));
        criteria1.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        criteria1.add(Restrictions.ne("iid", id));
        List results1 = criteria1.list();

        Criteria criteria2 = session.createCriteria(Reserva.class);
        criteria2.add(Restrictions.and(Restrictions.le("inicio", inicio), Restrictions.ge("fim", fim)));
        criteria2.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        criteria2.add(Restrictions.ne("iid", id));
        List results2 = criteria2.list();

        results1.addAll(results2);
        session.close();
        return results1;
    }

    public List<Reserva> findBetweenTeste(Date inicio, Date fim, Long id) {
        Session session = getSessionFactory().openSession();

        Criteria criteria2 = session.createCriteria(Reserva.class);
        criteria2.add(Restrictions.or(Restrictions.between("inicio", inicio, fim), Restrictions.between("fim", inicio, fim)));
        criteria2.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        criteria2.add(Restrictions.ne("iid", id));
        List results2 = criteria2.list();

        Criteria criteria1 = session.createCriteria(Reserva.class);
        criteria1.add(Restrictions.and(Restrictions.le("inicio", inicio), Restrictions.ge("fim", fim)));
        criteria1.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        List r = criteria1.list();
        criteria1.add(Restrictions.ne("iid", id));
        List results = criteria1.list();

        session.close();
        return results;
    }
}
