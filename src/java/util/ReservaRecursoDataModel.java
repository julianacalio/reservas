/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.List;
import javax.faces.model.ListDataModel;
import model.ReservaRecurso;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author charles
 */
public class ReservaRecursoDataModel extends ListDataModel implements SelectableDataModel<ReservaRecurso> {

    public ReservaRecursoDataModel(List<ReservaRecurso> data) {  
        super(data);  
    }  
    
    @Override
    public Object getRowKey(ReservaRecurso reservaRecurso) {
          return reservaRecurso.getNumero();  
    }

    @Override
    public ReservaRecurso getRowData(String rowKey) {
         //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<ReservaRecurso> reservaRecursos = (List<ReservaRecurso>) getWrappedData();  
          
        for(ReservaRecurso reservaRecurso : reservaRecursos) {  
            if(reservaRecurso.getNumero().equals(rowKey))  
                return reservaRecurso;  
        }  
          
        return null;  
    }
    
}
