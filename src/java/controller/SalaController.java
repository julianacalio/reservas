package controller;

import facade.SalaFacade;
import model.Sala;
import java.io.Serializable;
import java.util.ArrayList;
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
import util.SalaDataModel;

@Named(value = "salaController")
@SessionScoped
public class SalaController implements Serializable {

    // Precisa ser definido depois uma maneira melhor de fazer
    List<String> cores;

    private Sala current;
    private DataModel items = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    @EJB
    private facade.SalaFacade salaFacade;
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

    public List<String> getCores() {
        cores = new ArrayList<String>();
        cores.add("");
        cores.add("aquamarine");
        cores.add("blanchedalmond");
        cores.add("brown");
        cores.add("burlywood");
        cores.add("cadetblue");
        cores.add("chocolate");
        cores.add("coral");
        cores.add("cornflowerblue");
        cores.add("darkgoldenrod");
        cores.add("darkgreen");
        cores.add("darkmagenta");
        cores.add("darkslateblue");
        cores.add("dodgerblue");
        cores.add("green");
        cores.add("hotpink");
        cores.add("indianred");
        cores.add("lightpink");
        cores.add("lightsalmon");
        cores.add("lightgreen");
        cores.add("lightskyblue");
        cores.add("moccasin");
        cores.add("orange");
        cores.add("olive");
        cores.add("olivedrab");
        cores.add("palegoldenrod");
        cores.add("palegreen");
        cores.add("peachpuff");
        cores.add("peru");
        cores.add("rosybrown");
        cores.add("greenyellow");
        return cores;
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
        return salaFacade;
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
        recreateSala();
        return "List";
    }

    public void recreateSala() {
        current = null;
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
            JsfUtil.addSuccessMessage("Sala Criada: ", current.toString());
            current = null;
            return prepareList();
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar uma sala com o mesmo número", current.getNumero());
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
        current = (Sala) salaDataModel.getRowData();
//        current = (Sala) getItems().getRowData();
//        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("Sala Atualizada: ", current.getNumero());
            return "Edit";
        } catch (EJBException ex) {
            if ((ex.getCausedByException() instanceof ConstraintViolationException)) {
                JsfUtil.addErrorMessage("Não pode salvar uma sala com o mesmo número", current.getNumero());
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
        current = (Sala) salaDataModel.getRowData();
//        current = (Sala) getItems().getRowData();
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
            JsfUtil.addSuccessMessage("Sala Apagada: ", current.getNumero());
            current = null;
        } catch (EJBException ex) {
            if (ex.getCausedByException() instanceof ConstraintViolationException) {
                JsfUtil.addErrorMessage("Não pode deletar ", "Esta sala já possui uma reserva cadastrada");
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
        salaDataModel = null;
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
        return JsfUtil.getSelectItems(salaFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(salaFacade.findAll(), true);
    }

    public Sala getSala(java.lang.Long id) {
        return salaFacade.find(id);
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
