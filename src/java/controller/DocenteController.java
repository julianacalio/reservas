package controller;

import facade.DocenteFacade;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import model.Docente;

/**
 *
 * @author
 * charles
 */
@Named(value = "docenteController")
@SessionScoped
public class DocenteController implements Serializable {

    Docente docente;
    private DataModel items = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    @EJB
    private facade.DocenteFacade ejbFacade;

    public DocenteController() {
        docente = new Docente();
    }

    public String prepareView() {
        docente = (Docente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

        return "View";
    }

    public String prepareEdit() {
        docente = (Docente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String destroy() {
        docente = (Docente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    private void recreateModel() {
        items = null;
    }

    private void performDestroy() {
        try {
            getFacade().remove(docente);
            docente = null;
            JsfUtil.addSuccessMessage("Docente deletado");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Ocorreu um erro de persistência");
        }
    }

    public Docente getSelected() {
        if (docente == null) {
            docente = new Docente();
            selectedItemIndex = -1;
        }
        return docente;
    }
    
    private DocenteFacade getFacade() {
        return ejbFacade;
    }

    public String prepareCreate() {
        docente = new Docente();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {

        try {
           // Docente docente2 = new Docente();
           // docente2.setNome("TesteDocente");
           // ejbFacade.save(docente2);
           getFacade().save(docente);
           docente = null; // iniciala a variavel para limpar os dados dos componentes
            //ejbFacade.merge(docente);
            JsfUtil.addSuccessMessage("Docente Criado");
            //return prepareCreate();
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Ocorreu um erro de persistência");
            return null;
        }
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String update() {

        try {
            getFacade().edit(docente);
            JsfUtil.addSuccessMessage("Docente Atualizado");
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Ocorreu um erro de persistência");
            return null;
        }
    }

    public static void main(String args[]) {
        System.out.println("Docente controlller");
    }

    public Docente getDocente() {
        return this.docente;
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
            docente = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
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

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();

        }
        return items;
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

    public SelectItem[] getItemsAvailableSelectOne() {

        return JsfUtil.getSelectItems(getFacade().findAll(), true);
    }

    public Docente getDocente(java.lang.Long id) {

        return getFacade().find(id);
    }

    @FacesConverter(forClass = Docente.class)
    public static class DocenteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DocenteController controller = (DocenteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "docenteController");
            return controller.getDocente(getKey(value));
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
            if (object instanceof Docente) {
                Docente o = (Docente) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Docente.class.getName());
            }
        }
    }
}
