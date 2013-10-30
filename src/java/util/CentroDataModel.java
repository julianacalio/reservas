/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.List;
import javax.faces.model.ListDataModel;
import model.Centro;

import org.primefaces.model.SelectableDataModel;

public class CentroDataModel extends ListDataModel implements SelectableDataModel<Centro> {

    public CentroDataModel() {
    }

    public CentroDataModel(List<Centro> data) {
        super(data);
    }

    @Override
    public Centro getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  

        List<Centro> centros = (List<Centro>) getWrappedData();

        for (Centro centro : centros) {
            if (centro.getNome().equals(rowKey)) {
                return centro;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(Centro centro) {
        return centro.getNome();
    }

}
