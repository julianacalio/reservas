/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Pessoa;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class PessoaFacade extends AbstractFacade<Pessoa> {

    public PessoaFacade() {
        super(Pessoa.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }
}
