/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Aluno;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class AlunoFacade extends AbstractFacade<Aluno> {

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }
    
    public AlunoFacade() {
        super(Aluno.class);
    }
}
