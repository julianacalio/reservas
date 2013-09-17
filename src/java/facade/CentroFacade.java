/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Centro;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class CentroFacade extends AbstractFacade<Centro>{
    
    public CentroFacade(){
        super(Centro.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
       return HibernateUtil.getSessionFactory();
    }
    
}
