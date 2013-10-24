package controller;

import facade.SalaFacade;
import model.Sala;


import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import outros.SalaDataModel;

@Named(value = "salaController")
@SessionScoped
public class SalaController implements Serializable {

    private Sala current;
    private DataModel items = null; 
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    @EJB
    private facade.SalaFacade ejbFacade;
    private SalaDataModel salaDataModel;
    
    
    public SalaController() {
        current = new Sala();
    }
    
    
    public SalaDataModel getSalaDataModel() {
        if (salaDataModel == null) {
            List<Sala> salas = getFacade().findAll();
            salaDataModel = new SalaDataModel(salas);
        }
        return salaDataModel;
    }

    public void setSalaDataModel(SalaDataModel salaDaraModel) {
        this.salaDataModel = salaDaraModel;
    }

    public Sala getSelected() {
        if (current == null) {
            current = new Sala();
            selectedItemIndex = -1;
        }
        return current;
    }

    private SalaFacade getFacade() {
        return ejbFacade;
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
        return "List";
    }

    public String prepareView() {
        current = (Sala) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Sala();
        selectedItemIndex = -1;
        return "Create";
       // return create();
    }

    public String create() {
        try {
            getFacade().save(current);
            JsfUtil.addSuccessMessage("Sala Criada");
            current = null;
            //return prepareCreate();
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit() {
        current = (Sala) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Sala Atualizada");
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy() {
        current = (Sala) getItems().getRowData();
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
            current = null;
            JsfUtil.addSuccessMessage("Sala Apagada");
        } catch (Exception e) {
            JsfUtil.addErrorMessage("PersistenceErrorOccured");
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
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Sala getSala(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Sala.class)
    public static class SalaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SalaController controller = (SalaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "salaController");
            return controller.getSala(getKey(value));
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
            if (object instanceof Sala) {
                Sala o = (Sala) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Sala.class.getName());
            }
        }
    }
}
