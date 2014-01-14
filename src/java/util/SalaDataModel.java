/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

  
import java.util.List;
import javax.faces.model.ListDataModel;
import model.Sala;

import org.primefaces.model.SelectableDataModel;  
  
public class SalaDataModel extends ListDataModel implements SelectableDataModel<Sala> {    
  
    public SalaDataModel() {  
    }  
  
    public SalaDataModel(List<Sala> data) {  
        super(data);  
    }  
      
    @Override  
    public Sala getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Sala> salas = (List<Sala>) getWrappedData();  
          
        for(Sala sala : salas) {  
            if(sala.getNumero().equals(rowKey))  
                return sala;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Sala sala) {  
        return sala.getNumero();  
    }  

    
}  