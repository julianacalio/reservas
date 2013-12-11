/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.List;
import javax.faces.model.ListDataModel;
import model.Equipamento;
import model.Recurso;
import model.Sala;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author charles
 */
public class RecursoDataModel extends ListDataModel implements SelectableDataModel<Recurso> {

    public RecursoDataModel() {
    }

    public RecursoDataModel(List<Recurso> data) {
        super(data);
    }

    @Override
    public Object getRowKey(Recurso recurso) {
        
        return (recurso instanceof Equipamento) ? ((Equipamento) recurso).getDescricao() : ((Sala) recurso).getNumero();
        
    }

    @Override
    public Recurso getRowData(String rowKey) {
        List<Recurso> recursos = (List<Recurso>) getWrappedData();

        for (Recurso recurso : recursos) {
            if (recurso instanceof Sala) {
                Sala s = (Sala) recurso;
                if (s.getNumero().equals(rowKey)) {
                    return (Sala) recurso;
                }
            } else {
                Equipamento e = (Equipamento) recurso;
                if (e.getDescricao().equals(rowKey)) {
                    return ((Equipamento) recurso);
                }
            }
        }
        return null;
    }

}
