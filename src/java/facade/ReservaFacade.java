/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Reserva;
import org.hibernate.SessionFactory;

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
}
