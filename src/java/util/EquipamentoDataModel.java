/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;



  
 

  
import java.util.List;
import javax.faces.model.ListDataModel;
import model.Equipamento;

import org.primefaces.model.SelectableDataModel;  
  
public class EquipamentoDataModel extends ListDataModel implements SelectableDataModel<Equipamento> {    
  
    public EquipamentoDataModel() {  
    }  
  
    public EquipamentoDataModel(List<Equipamento> data) {  
        super(data);  
    }  
      
    @Override  
    public Equipamento getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Equipamento> equipamentos = (List<Equipamento>) getWrappedData();  
          
        for(Equipamento equipamento : equipamentos) {  
            if(equipamento.getDescricao().equals(rowKey))  
                return equipamento;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Equipamento equipamento) {  
        return equipamento.getDescricao();  
    }  

    
}  