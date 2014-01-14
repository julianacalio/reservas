/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.List;
import javax.faces.model.ListDataModel;
import model.Reserva;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author charles
 */
public class ReservaDataModel extends ListDataModel implements SelectableDataModel<Reserva> {

    public ReservaDataModel() {
    }

    public ReservaDataModel(List<Reserva> data) {
        super(data);
    }

    @Override
    public Object getRowKey(Reserva reserva) {
        return reserva.getIid();
    }

    @Override
    public Reserva getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  

        List<Reserva> reservas = (List<Reserva>) getWrappedData();

        for (Reserva reserva : reservas) {
            if (reserva.getIid().toString().equals(rowKey)) {
                return reserva;
            }
        }

        return null;
    }

}
