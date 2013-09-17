/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.Equipamento;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class EquipamentoFacade extends AbstractFacade<Equipamento>{
    
    
    public EquipamentoFacade(){
        super(Equipamento.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
      return HibernateUtil.getSessionFactory();
    }
}
