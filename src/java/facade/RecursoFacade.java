/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Recurso;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class RecursoFacade extends AbstractFacade<Recurso>{
    
    public RecursoFacade(){
        super(Recurso.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }
    
}
