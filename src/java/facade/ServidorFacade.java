/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Servidor;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class ServidorFacade extends AbstractFacade<Servidor>{

    public ServidorFacade(){
        super(Servidor.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }
}
