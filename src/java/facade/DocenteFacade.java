package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Docente;
import org.hibernate.SessionFactory;

@Stateless
public class DocenteFacade extends AbstractFacade<Docente> {

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    public DocenteFacade() {
        super(Docente.class);
    }
}
