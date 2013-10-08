/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package outros;

import java.util.List;
import javax.faces.model.ListDataModel;
import model.Equipamento;
import model.Recurso;
import model.Sala;

import org.primefaces.model.SelectableDataModel;

public class RecursoDataModel extends ListDataModel implements SelectableDataModel<Recurso> {

    public RecursoDataModel() {
    }

    public RecursoDataModel(List<Recurso> data) {
        super(data);
    }

    @Override
    public Recurso getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  

        List<Recurso> recursos = (List<Recurso>) getWrappedData();

        for (Recurso recurso : recursos) {
            if (recurso instanceof Sala) {
                Sala sala = (Sala) recurso;
                if (sala.getNumero().equals(rowKey)) {
                    return sala;
                }
            }
            if (recurso instanceof Equipamento) {
                Equipamento equipamento = (Equipamento) recurso;
                if (equipamento.getDescricao().equals(rowKey)) {
                    return equipamento;
                }
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Recurso recurso) {
        if (recurso instanceof Sala) {
            Sala sala = (Sala) recurso;
            return sala.getNumero();
        }
        if (recurso instanceof Equipamento) {
            Equipamento equipamento = (Equipamento) recurso;
            return equipamento.getDescricao();
        }
        return null;
    }
}
