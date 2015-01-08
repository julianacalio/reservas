package util;

import facade.ReservaFacade;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import model.Reserva;


public class ReservaLazyModel extends LazyDataModel<Reserva> {

    private static final long serialVersionUID = 1L;
    
    private List<Reserva> reservas;
    
    private ReservaFacade rf = new ReservaFacade();
    
    @Override
    public List<Reserva> load(int startingAt, int maxPerPage, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        
        try {
            // with datatable pagination limits
            reservas = rf.load(startingAt, maxPerPage);

        } catch (Exception e) {
            e.printStackTrace();
        }
 
        // Total de reservas
        if(getRowCount() <= 0){
            setRowCount(rf.countDistinct());
        }
 
        // set the page dize
        setPageSize(maxPerPage);
 
        return reservas;
    }
    
    @Override
    public Object getRowKey(Reserva reserva) {
        return reserva.getIid();
    }

    @Override
    public Reserva getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  

        List<Reserva> reservas2 = (List<Reserva>) getWrappedData();

        for (Reserva reserva : reservas2) {
            if (reserva.getIid().toString().equals(rowKey)) {
                return reserva;
            }
        }

        return null;
    }
    
}