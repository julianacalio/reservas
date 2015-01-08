/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facade;

import controller.HibernateUtil;
import java.util.List;
import javax.ejb.Stateless;
import model.Usuario;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario>{

    public UsuarioFacade() {
        super(Usuario.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
  
        return HibernateUtil.getSessionFactory();
        
    }
    
    
    public Usuario findByLogin(String login) {


            try {
                Session session = getSessionFactory().openSession();
                Query query = session.createQuery("from Usuario u where u.login = :login ");
                query.setParameter("login", login);
                List resultado = query.list();

                if (resultado.size() == 1) {
                    Usuario userFound = (Usuario) resultado.get(0);
                    return userFound;
                } else {
                    return null;
                }
            } catch (HibernateException e) {
                return null;
            }

    }
    
    
}
