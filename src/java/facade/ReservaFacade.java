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
public class ReservaFacade extends AbstractFacade<Reserva>{
    
    
    public ReservaFacade(){
        super(Reserva.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }
    
    
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

    public List<Reserva> findAllBetween(Date inicio, Date fim) {
        Session session = getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Reserva.class);
        criteria.add(Restrictions.or(Restrictions.between("fim", inicio, fim), Restrictions.between("inicio", inicio, fim)));
        List results = criteria.list();
        session.close();
        return results;
    }

    public List<Reserva> findBetween(Date inicio, Date fim, Recurso recurso) {
        Session session = getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Reserva.class);

        criteria.add(Restrictions.or(Restrictions.between("fim", inicio, fim), Restrictions.between("inicio", inicio, fim)));
        criteria.add(Restrictions.eq("recurso", recurso));
        List results = criteria.list();

        session.close();
        return results;
    }
}
