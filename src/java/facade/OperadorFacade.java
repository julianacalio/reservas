/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Operador;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class OperadorFacade extends AbstractFacade<Operador> {

    public OperadorFacade() {
        super(Operador.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }
}
