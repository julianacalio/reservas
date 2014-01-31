package controller;

import facade.TAFacade;
import model.TA;

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
import util.TADataModel;

@Named("tAController")
@SessionScoped
public class TAController implements Serializable {

    private TA current;
    private DataModel items = null;
    @EJB
    private facade.TAFacade taFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private TADataModel taDataModel;

    public TAController() {
    }

    public TADataModel getTADataModel() {
        if (taDataModel == null) {
            List<TA> tas = getFacade().findAll();
            taDataModel = new TADataModel(tas);
        }
        return taDataModel;
    }

    public void setTADataModel(TADataModel taDataModel) {
        this.taDataModel = taDataModel;
    }

    public TA getSelected() {
        if (current == null) {
            current = new TA();
            selectedItemIndex = -1;
        }
        return current;
    }

    private TAFacade getFacade() {
        return taFacade;
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
        current = (TA) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new TA();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {

            getFacade().save(current);

            //ejbFacade.merge(docente);
            JsfUtil.addSuccessMessage("Técnico Administrativo Criado: ", current.getNome());
            current = null; // inicializa a variavel para limpar os dados dos componentes
            //return prepareCreate();
            return prepareList();
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar um técnico administrativo com o mesmo número de matrícula", current.getMatricula());
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
        current = (TA) taDataModel.getRowData();
//        current = (TA) getItems().getRowData();
//        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Técnico Administrativo Atualizado", current.getNome());
            //return "View";
            return "Edit";
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar um técnico adiminstrativo com o mesmo número de matrícula", current.getMatricula());
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
        current = (TA) taDataModel.getRowData();
       // selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        //recreatePagination();
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
            JsfUtil.addSuccessMessage("Técnico Administrativo deletado: ", current.getNome());
            current = null;
        } catch (EJBException ex) {
            if (ex.getCausedByException() instanceof ConstraintViolationException) {
                JsfUtil.addErrorMessage("Não pode deletar ", "Este Técnico Administrativos já possui uma reserva cadastrada");
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
        taDataModel = null;
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
        return JsfUtil.getSelectItems(taFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(taFacade.findAll(), true);
    }

    public TA getTA(java.lang.Long id) {
        return taFacade.find(id);
    }

    @FacesConverter(forClass = TA.class)
    public static class TAControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TAController controller = (TAController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tAController");
            return controller.getTA(getKey(value));
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
            if (object instanceof TA) {
                TA o = (TA) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + TA.class.getName());
            }
        }
    }
}
