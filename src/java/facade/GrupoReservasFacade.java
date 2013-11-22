/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import controller.HibernateUtil;
import javax.ejb.Stateless;
import model.GrupoReservas;
import org.hibernate.SessionFactory;

/**
 *
 * @author charles
 */
@Stateless
public class GrupoReservasFacade extends AbstractFacade<GrupoReservas> {

    public GrupoReservasFacade() {
        super(GrupoReservas.class);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

}
