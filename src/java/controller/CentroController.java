package controller;

import model.Centro;
import facade.CentroFacade;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.hibernate.exception.ConstraintViolationException;
import util.CentroDataModel;

@Named("centroController")
@SessionScoped
public class CentroController implements Serializable {

    private Centro current;
    private DataModel items = null;

    @EJB
    private facade.CentroFacade centroFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private CentroDataModel centroDataModel;

    public CentroController() {
    }

    public CentroDataModel getCentroDataModel() {
        if (centroDataModel == null) {
            List<Centro> centros = getFacade().findAll();
            centroDataModel = new CentroDataModel(centros);
        }
        return centroDataModel;
    }

    public void setCentroDataModel(CentroDataModel centroDataModel) {
        this.centroDataModel = centroDataModel;
    }

    public Centro getSelected() {
        if (current == null) {
            current = new Centro();
            selectedItemIndex = -1;
        }
        return current;
    }

    private CentroFacade getFacade() {
        return centroFacade;
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
        current = (Centro) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Centro();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {

        try {
            getFacade().save(current);
            JsfUtil.addSuccessMessage("Centro Criado: ", current.getNome());
            current = null;
            return prepareList();
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar uma centro com o mesmo nome", current.getNome());
            } else {
                JsfUtil.addErrorMessage("Erro de Persistência", ex.getMessage());
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Erro de Persistência", e.getMessage());
            return null;
        }

    }

    public String prepareEdit() {
        current = (Centro) centroDataModel.getRowData();
//        current = (Centro) getItems().getRowData();
//        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String teste() {
        return "Edit";
    }

    public String update() {

        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Centro Atualizado: ", current.getNome());
            return "Edit";
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar um centro com o mesmo nome: ", current.getNome());
            } else {
                JsfUtil.addErrorMessage("Erro de Persistência", ex.getMessage());
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Erro de Persistência", e.getMessage());
            return null;
        }

    }

    public String destroy() {
        current = (Centro) centroDataModel.getRowData();
//        current = (Centro) getItems().getRowData();
//        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
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
            JsfUtil.addSuccessMessage("Centro Apagado: ", current.getNome());
            current = null;
        } catch (EJBException ex) {
            if (ex.getCausedByException() instanceof ConstraintViolationException) {
                JsfUtil.addErrorMessage("Não pode deletar ", "Esta centro já possui uma reserva cadastrada");
            } else {
                JsfUtil.addErrorMessage("Não pode deletar ", ex.getMessage());
            }

        } catch (Exception e) {
            JsfUtil.addErrorMessage("PersistenceErrorOccured", e.getMessage());
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
        centroDataModel = null;
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
        return JsfUtil.getSelectItems(centroFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(centroFacade.findAll(), true);
    }

    public Centro getCentro(java.lang.Long id) {
        return centroFacade.find(id);
    }

    @FacesConverter(forClass = Centro.class)
    public static class CentroControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CentroController controller = (CentroController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "centroController");
            return controller.getCentro(getKey(value));
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
            if (object instanceof Centro) {
                Centro o = (Centro) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Centro.class.getName());
            }
        }
    }

    public static void main(String Args[]) {

        Centro c = new Centro();
        c.setNome("CMCC");

    }
}
