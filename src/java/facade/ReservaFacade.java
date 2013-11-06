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
        criteria.add(Restrictions.or(Restrictions.between("fim", inicio, fim), Restrictions.between("inicio", inicio, fim)));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        List results = criteria.list();
        session.close();
        return results;
    }

    /**
     * Busca as reservas de um determinado recurso dentro de uma certa data de início e data de fim
     *
     * @param inicio Data de início
     * @param fim Data de fim
     * @param reserva Recursos pesquisado
     * @return Lista contendo todas as reservas que obedecem ao critério especificado
     */
    public List<Reserva> findBetween(Date inicio, Date fim, Reserva reserva) {
        Session session = getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Reserva.class);

        criteria.add(Restrictions.or(Restrictions.between("fim", inicio, fim), Restrictions.between("inicio", inicio, fim)));
        criteria.add(Restrictions.eq("iid",reserva.getIid() ));
        List results = criteria.list();

        session.close();
        return results;
    }
}
