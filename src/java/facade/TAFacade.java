/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.TA;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class TAFacade extends AbstractFacade<TA>{
    
    

    public TAFacade() {
        super(TA.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
       return HibernateUtil.getSessionFactory();
    }
}
