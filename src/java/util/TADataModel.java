/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.List;
import javax.faces.model.ListDataModel;
import model.TA;

import org.primefaces.model.SelectableDataModel;

public class TADataModel extends ListDataModel implements SelectableDataModel<TA> {

    public TADataModel() {
    }

    public TADataModel(List<TA> data) {
        super(data);
    }

    @Override
    public TA getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  

        List<TA> tecnicosAdministrativos = (List<TA>) getWrappedData();

        for (TA tecnicoAdminstrativo : tecnicosAdministrativos) {
            if (tecnicoAdminstrativo.getNome().equals(rowKey)) {
                return tecnicoAdminstrativo;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(TA tecnicoAdministrativo) {
        return tecnicoAdministrativo.getNome();
    }

}
