package controller;

import facade.DocenteFacade;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import model.Docente;
import org.hibernate.exception.ConstraintViolationException;
import util.DocenteDataModel;

/**
 *
 * @author charles
 */
@Named(value = "docenteController")
@SessionScoped
public class DocenteController implements Serializable {

    Docente current;
    private DataModel items = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    @EJB
    private facade.DocenteFacade docenteFacade;
    private DocenteDataModel docenteDataModel;

    public DocenteController() {
        current = new Docente();
    }

    public DocenteDataModel getDocenteDataModel() {
        if (docenteDataModel == null) {
            List<Docente> docentes = docenteFacade.findAll();
            docenteDataModel = new DocenteDataModel (docentes);
        }
        return docenteDataModel;
    }

    public void setDocenteDataModel(DocenteDataModel docenteDataModel) {
        this.docenteDataModel = docenteDataModel;
    }

    public String prepareView() {
        current = (Docente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

        return "View";
    }

    public String prepareEdit() {
        current = (Docente) docenteDataModel.getRowData();
        
        return "Edit";
    }

    public String destroy() {
        current = (Docente) docenteDataModel.getRowData();
        
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    private void recreateModel() {
        items = null;
        docenteDataModel = null;
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage("Docente deletado: ", current.getNome());
            current = null;
        } catch (EJBException ex) {
            if (ex.getCausedByException() instanceof ConstraintViolationException) {
                JsfUtil.addErrorMessage("Não pode deletar ", "Este docente já possui uma reserva cadastrada");
            } else {
                JsfUtil.addErrorMessage("Não pode deletar ", ex.getMessage());
            }

        } catch (Exception e) {
            JsfUtil.addErrorMessage("PersistenceErrorOccured", e.getMessage());
        }
    }

    public Docente getSelected() {
        if (current == null) {
            current = new Docente();
            selectedItemIndex = -1;
        }
        return current;
    }

    private DocenteFacade getFacade() {
        return docenteFacade;
    }

    public String prepareCreate() {
        current = new Docente();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {

        try {

            getFacade().save(current);

            //ejbFacade.merge(docente);
            JsfUtil.addSuccessMessage("Docente Criado: ", current.getNome());
            current = null; // inicializa a variavel para limpar os dados dos componentes
            //return prepareCreate();
            return prepareList();
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar um docente com o mesmo número de matrícula", current.getMatricula());
            } else {
                JsfUtil.addErrorMessage("Erro de Persistência", ex.getMessage());
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Erro de Persistência", e.getMessage());
            return null;
        }
    }

    public String prepareList() {
        recreateModel();
        recreateDocente();
        return "List";
    }

    public void recreateDocente() {
        current = null;
    }

    public String update() {

        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Docente Atualizado", current.getNome());
            //return "View";
            return "Edit";
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar um docente com o mesmo número de matrícula", current.getMatricula());
            } else {
                JsfUtil.addErrorMessage("Erro de Persistência", ex.getMessage());
            }
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Erro de Persistência", e.getMessage());
            return null;
        }
    }

    

    public Docente getDocente() {
        return this.current;
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
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
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
