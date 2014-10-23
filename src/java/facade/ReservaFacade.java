/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
//import javax.el.Expression;
import model.GrupoReserva;
import model.Reserva;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
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
     * Retorna todas as reservas que pertencem aquele grupo de reservas
     *
     * @param grupoReserva Objeto grupo de reservas com um id diferente de null
     * @return reservas que pertencem aquele grupo
     */
    public List<Reserva> findAll(GrupoReserva grupoReserva) {
        Session session = getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Reserva.class);

        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        criteria.add(Restrictions.eq("grupoReserva", grupoReserva));
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

    public List<Reserva> findAllOrder() {
        Session session = getSessionFactory().openSession();
        Criteria crit = session.createCriteria(Reserva.class);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        crit.createAlias("this.emprestimo", "e");
        crit.addOrder(Order.desc("e.retirada"));
        //crit.setMaxResults(50);
        List results = crit.list();
        session.close();
        return results;
    }
    
    public List<Reserva> findAllRecurso(String tipo) {
        Session session = getSessionFactory().openSession();
        Criteria crit = session.createCriteria(Reserva.class);
//        crit.createAlias("recursos", "recurso");
//        crit.add(Expression.gt("recurso.RECURSO_DETAILS_TYPE", tipo));
//        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
//        crit.createAlias("this.emprestimo", "e");
        
//        crit.setFetchMode("Recurso", FetchMode.JOIN).add(Restrictions.eq("RECURSO_DETAILS_TYPE", tipo));
        
        crit.createCriteria("recursos");
        crit.add(Restrictions.eq("RECURSO_DETAILS_TYPE", tipo));
      
        
        
//        crit.add(Restrictions.eq(tipo, crit))
//        crit.addOrder(Order.desc("e.retirada"));
        //crit.setMaxResults(50);
        
        List results = crit.list();
        session.close();
        return results;      
    }
    

    /**
     * Busca as reservas que comecem ou terminem dentro de uma data de início e uma data de fim sem considerar as reservas feitas pelo ID passado.
     *
     *
     * @param inicio Data de início
     * @param fim Data de fim
     * @param desconsiderar_ID_Reserva da reserva que nao será considerada na busca
     * @return Lista contendo todas as reservas que obedecem ao critério especificado
     */
//    public List<Reserva> findBetween(Date inicio, Date fim, Long desconsiderar_ID_Reserva) {
//        Session session = getSessionFactory().openSession();
//        Criteria criteria1 = session.createCriteria(Reserva.class);
//
//        criteria1.add(Restrictions.or(Restrictions.between("inicio", inicio, fim), Restrictions.between("fim", inicio, fim)));
//        criteria1.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
//        criteria1.add(Restrictions.ne("iid", desconsiderar_ID_Reserva));
//        List results1 = criteria1.list();
//
//        Criteria criteria2 = session.createCriteria(Reserva.class);
//        criteria2.add(Restrictions.and(Restrictions.le("inicio", inicio), Restrictions.ge("fim", fim)));
//        criteria2.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
//        criteria2.add(Restrictions.ne("iid", desconsiderar_ID_Reserva));
//        List results2 = criteria2.list();
//
//        results1.addAll(results2);
//        session.close();
//        return results1;
//    }
}
