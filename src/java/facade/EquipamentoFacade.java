/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import model.Equipamento;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author charles
 */
@Stateless
public class EquipamentoFacade extends AbstractFacade<Equipamento> {

    public EquipamentoFacade() {
        super(Equipamento.class);
    }

    public List<Equipamento> findAllDifferent(Map<String, Long> equipamentos) {
        Session session = getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Equipamento.class);
        criteria.add(Restrictions.allEq(equipamentos));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);//faz um select distinct
        List results = criteria.list();
        session.close();
        return results;
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }
}
