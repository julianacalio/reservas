package controller;

import facade.EquipamentoFacade;
import model.Equipamento;
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
import util.EquipamentoDataModel;

@Named(value = "equipamentoController")
@SessionScoped
public class EquipamentoController implements Serializable {

    private Equipamento current;
    private DataModel items = null;
    @EJB
    private facade.EquipamentoFacade equipamentoFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private EquipamentoDataModel equipamentoDataModel;

    public EquipamentoController() {
    }

    public EquipamentoDataModel getEquipamentoDataModel() {
        if (equipamentoDataModel == null) {
            List<Equipamento> equipamentos = equipamentoFacade.findAll();
            equipamentoDataModel = new EquipamentoDataModel(equipamentos);
        }
        return equipamentoDataModel;
    }

    public void setEquipamentoDataModel(EquipamentoDataModel equipamentoDataModel) {
        this.equipamentoDataModel = equipamentoDataModel;
    }

    public Equipamento getSelected() {
        if (current == null) {
            current = new Equipamento();
            selectedItemIndex = -1;
        }
        return current;
    }

    private EquipamentoFacade getFacade() {
        return equipamentoFacade;
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
        recreateEquipamento();
        return "List";
    }

    public void recreateEquipamento() {
        current = null;
    }

    public String prepareView() {
        current = (Equipamento) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Equipamento();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {

        try {
            getFacade().save(current);
            JsfUtil.addSuccessMessage("Equipamento Criado: ", current.getDescricao());
//            current = null;
            return prepareList();
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar o equipamento com o mesmo patrimônio ", current.getDescricao());
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
        current = (Equipamento) equipamentoDataModel.getRowData();
        //current = (Equipamento) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {

        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Equipamento Atualizado: ", current.getDescricao());
            return "Edit";
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar um equipamento com o mesmo patrimônio", current.getPatrimonio());
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
        current = (Equipamento) equipamentoDataModel.getRowData();
        // current = (Equipamento) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
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
            JsfUtil.addSuccessMessage("Equipamento Apagado: ", current.getDescricao());
            current = null;
        } catch (EJBException ex) {
            if (ex.getCausedByException() instanceof ConstraintViolationException) {
                JsfUtil.addErrorMessage("Não pode deletar ", "Este equipamento já possui uma reserva cadastrada");
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
        equipamentoDataModel = null;
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
        return JsfUtil.getSelectItems(equipamentoFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(equipamentoFacade.findAll(), true);
    }

    public Equipamento getEquipamento(java.lang.Long id) {
        return equipamentoFacade.find(id);
    }

    @FacesConverter(forClass = Equipamento.class)
    public static class EquipamentoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EquipamentoController controller = (EquipamentoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "equipamentoController");
            return controller.getEquipamento(getKey(value));
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
            if (object instanceof Equipamento) {
                Equipamento o = (Equipamento) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Equipamento.class.getName());
            }
        }
    }
}
