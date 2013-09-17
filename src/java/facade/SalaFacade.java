/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Sala;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class SalaFacade extends AbstractFacade<Sala>{
    
   
    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }
    
    
    public SalaFacade(){
        super(Sala.class);
    }
}
