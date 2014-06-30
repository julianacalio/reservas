/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.List;
import javax.faces.model.ListDataModel;
import model.Usuario;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author charles
 */
public class UsuarioDataModel extends ListDataModel implements SelectableDataModel<Usuario> {

    public UsuarioDataModel() {
    }

    public UsuarioDataModel(List<Usuario> data) {
        super(data);
    }

    @Override
    public Usuario getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  

        List<Usuario> docentes = (List<Usuario>) getWrappedData();

        for (Usuario docente : docentes) {
            if (docente.getLogin().equals(rowKey)) {
                return docente;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(Usuario docente) {
        return docente.getLogin();
    }

}
