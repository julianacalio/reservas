package controller;

import email.Email;
import facade.ReservaFacade;
import model.Reserva;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import model.Pessoa;
import model.TA;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import util.ReservaDataModel;

@Named("reservaController")
@SessionScoped
public class ReservaController implements Serializable {
    
    private Reserva current;
    private ReservaDataModel reservaDataModel;
    private DataModel items = null;
    @EJB
    private facade.ReservaFacade reservaFacade;
    @EJB
    private facade.TAFacade taFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    public ReservaController() {
    }
    
    public Reserva getCurrent() {
        return current;
    }
    
    public void setCurrent(Reserva current) {
        this.current = current;
    }
    
    public ReservaDataModel getReservaDataModel() {
        /////
        //  if (reservaDataModel == null) {
        List<Reserva> reservas = reservaFacade.findAllOrder();
        for (Iterator<Reserva> res = reservas.iterator(); res.hasNext();) {
            Reserva r = res.next();
            if (r.getEmprestimo() == null) {
                res.remove();
            }
        }
        reservaDataModel = new ReservaDataModel(reservas);
        // }
        return reservaDataModel;
    }
    
    public void onEdit(RowEditEvent event) {        
        current = (Reserva) event.getObject();
        updateEmprestimo();
    }
    
    public void updateEmprestimo() {
        current = (Reserva) reservaDataModel.getRowData();
        reservaFacade.merge(current);
    }
    
    public List<Pessoa> completeReservante(String query) {
        List<TA> tas = taFacade.findAll();
        List<Pessoa> sugestoes = new ArrayList<Pessoa>();
        query = query.toLowerCase();
        for (TA ta : tas) {
            if (ta.getNome().toLowerCase().startsWith(query)) {
                sugestoes.add(ta);
            }
        }
        return sugestoes;
    }
    
    public void setReservaDataModel(ReservaDataModel reservaDataModel) {
        this.reservaDataModel = reservaDataModel;
    }
    
    public Reserva getSelected() {
        if (current == null) {
            current = new Reserva();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    private ReservaFacade getFacade() {
        return reservaFacade;
    }
    
    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }
                
                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }
    
    public String prepareList() {
        recreateModel();
        return "Emprestimo";
    }
    
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        updateEmprestimo();
        if (newValue != null && !newValue.equals(oldValue)) {            
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);            
            FacesContext.getCurrentInstance().addMessage(null, msg);            
        }        
    }
    
    public String prepareView() {
        current = (Reserva) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }
    
    public String prepareCreate() {
        current = new Reserva();
        selectedItemIndex = -1;
        return "Create";
    }
    
    public String create() {
        try {
            getFacade().save(current);
            JsfUtil.addSuccessMessage("ReservaCreated", null);
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "PersistenceErrorOccured");
            return null;
        }
    }
    
    public String prepareEdit() {
        current = (Reserva) reservaDataModel.getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }
    
    public String update() {
        try {
            getFacade().edit(current);
            (new Thread(new Email(current,3))).start();
            JsfUtil.addSuccessMessage("Emprestimo Atualizado", null);
            
            return "Emprestimo";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "PersistenceErrorOccured");
            return null;
        }
    }
    
    public String destroy() {
        current = (Reserva) reservaDataModel.getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }
    
    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }
    
    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage("ReservaDeleted", null);
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "PersistenceErrorOccured");
        }
    }
    
    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }
    
    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }
    
    private void recreateModel() {
        items = null;
    }
    
    private void recreatePagination() {
        pagination = null;
    }
    
    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }
    
    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }
    
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(reservaFacade.findAll(), false);
    }
    
    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(reservaFacade.findAll(), true);
    }
    
    public Reserva getReserva(java.lang.Long id) {
        return reservaFacade.find(id);
    }
    
    @FacesConverter(forClass = Reserva.class)
    public static class ReservaControllerConverter implements Converter {
        
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ReservaController controller = (ReservaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "reservaController");
            return controller.getReserva(getKey(value));
        }
        
        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }
        
        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }
        
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Reserva) {
                Reserva o = (Reserva) object;
                return getStringKey(o.getIid());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Reserva.class.getName());
            }
        }
    }
    
}
