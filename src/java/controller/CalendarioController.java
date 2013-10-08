package controller;

import facade.RecursoFacade;
import model.Recurso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;


import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import model.Equipamento;
import model.Pessoa;
import model.Reserva;
import model.Sala;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleModel;


import org.primefaces.model.ScheduleModel;
import outros.EquipamentoDataModel;
import outros.SalaDataModel;

/**
 *
 * @author
 * André
 */
@Named("calendarioController")
@SessionScoped
public class CalendarioController implements Serializable {

    private List<Equipamento> selectedEquipamentos;
    private Map<Equipamento, Equipamento> equips;
    private Recurso current, novaescolha;

    public Recurso getNovaescolha() {
        return novaescolha;
    }

    public void setNovaescolha(Recurso novaescolha) {
        this.novaescolha = novaescolha;
    }

    public Recurso getCurrent() {
        return current;
    }

    public void setCurrent(Recurso current) {
        this.current = current;
    }
    private DataModel items = null;
    @EJB
    private facade.RecursoFacade recursoFacade;
    @EJB
    private facade.ReservaFacade reservaFacade;
    @EJB
    private facade.PessoaFacade pessoaFacade;
    @EJB
    private facade.SalaFacade salaFacade;
    @EJB
    private facade.EquipamentoFacade equipamentoFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private ScheduleModel eventModel;
    private Reserva reserva = new Reserva();
    List<Pessoa> pessoas;
    private EquipamentoDataModel equipamentoDataModel;

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
    private SalaDataModel salaDataModel;

    public SalaDataModel getSalaDataModel() {
        if (salaDataModel == null) {
            List<Sala> salas = salaFacade.findAll();
            salaDataModel = new SalaDataModel(salas);
        }
        return salaDataModel;
    }

    public void setSalaDataModel(SalaDataModel salaDaraModel) {
        this.salaDataModel = salaDaraModel;
    }

    public List<Pessoa> completeReservante(String query) {
        List<Pessoa> suggestions = new ArrayList<Pessoa>();
        query = query.toLowerCase();
        for (Pessoa p : pessoas) {
            if (p.getNome().toLowerCase().startsWith(query)) {
                suggestions.add(p);
            }
        }
        return suggestions;
    }

    public List<Equipamento> getSelectedEquipamentos() {
        return selectedEquipamentos;
    }

    public void setSelectedEquipamentos(List<Equipamento> selectedEquipamentos) {
        this.selectedEquipamentos = selectedEquipamentos;
    }

    public Map<Equipamento, Equipamento> getEquipamentos() {

        //ArrayList<SelectItem> listaSelect = new ArrayList<SelectItem>();
        equips = new HashMap<Equipamento, Equipamento>();
        List<Equipamento> e = equipamentoFacade.findAll();

        for (int i = 0; i < e.size(); i++) {
            equips.put(e.get(i), e.get(i));
        }

        return equips;
    }


    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public ScheduleModel getEventModel() {
        if (eventModel == null) {
            if (current == null) {
                return null;
            }

            if (pessoas == null) {
                pessoas = pessoaFacade.findAll();
            }

            eventModel = new DefaultScheduleModel();
            for (Reserva res : current.getReservas()) {
                eventModel.addEvent(res);
            }
        }

        return eventModel;
    }

    public void escolheRecurso() {
        if (novaescolha == current) {
            return;
        }
        current = novaescolha;
        eventModel = null;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public void addReserva(ActionEvent actionEvent) {
        List<Reserva> reservasOcupadas = getReservasOcupadas(reserva);
        if (reservasOcupadas != null) {
            String nomeRecurso = "";
            for (int i = 0; i < reservasOcupadas.size(); i++) {
                Recurso recurso = reservasOcupadas.get(i).getRecurso();
                if (recurso instanceof Equipamento) {
                    Equipamento e = (Equipamento) recurso;
                    nomeRecurso += "Equipamento: " + e.getDescricao() + "\n";
                } else {
                    Sala s = (Sala) recurso;
                    nomeRecurso += "Sala: " + s.getNumero() + "\n";
                }

            }
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Recurso(s) ocupado(s)", nomeRecurso);
            addMessage(message);
        }

        if (selectedEquipamentos != null) {
            criaReservasAdicionais(selectedEquipamentos, reserva);
        }
        //reserva = reservaFacade.edit(reserva);
        reserva = reservaFacade.merge(reserva);
        current.addReserva(reserva);
        if (reserva.getId() == null) {
            eventModel.addEvent(reserva);
        } else {
            eventModel.updateEvent(reserva);
        }

    }

    public void criaReservasAdicionais(List<Equipamento> equipamentos, Reserva reserva) {
        Reserva[] reservas = new Reserva[equipamentos.size()];
        for (int i = 0; i < reservas.length; i++) {
            reservas[i] = new Reserva();
            reservas[i].setCentro(reserva.getCentro());
            reservas[i].setFim(reserva.getFim());
            reservas[i].setInicio(reserva.getInicio());
            reservas[i].setMotivo(reserva.getMotivo());
            reservas[i].setRealizacao(reserva.getRealizacao());
            reservas[i].setReservante(reserva.getReservante());
            reservas[i].setRecurso(equipamentos.get(i));
            reservas[i] = reservaFacade.merge(reservas[i]);
            equipamentos.get(i).addReserva(reservas[i]);
        }
        equipamentoDataModel = null;
    }

    public List<Reserva> getReservasOcupadas(Reserva reserva) {
        List<Reserva> reservas = reservaFacade.findAllBetween(reserva.getInicio(), reserva.getFim());
        return reservas;
    }

    public void removerReservasAdicionais(Reserva reserva) {
        List<Reserva> reservas = reservaFacade.findAll(reserva.getInicio(), reserva.getFim(), reserva.getRealizacao());
        for (int i = 0; i < reservas.size(); i++) {
            reservas.get(i).getRecurso().remReserva(reservas.get(i));
            reservaFacade.remove(reservas.get(i));
        }
        equipamentoDataModel = null;
        salaDataModel = null;
    }

    public void remReserva(ActionEvent actionEvent) {
        if (selectedEquipamentos != null) {
            removerReservasAdicionais(reserva);
        }
        eventModel.deleteEvent(reserva);
        // current.remReserva(reserva);
        //  reservaFacade.remove(reserva);
        reserva = null;
    }

    public void onReservaSelect(SelectEvent selectEvent) {
        reserva = (Reserva) selectEvent.getObject();

        //busca as reservas associadas com a reserva selecionada
        List<Reserva> reservas = reservaFacade.findAll(reserva.getInicio(), reserva.getFim(), reserva.getRealizacao());
        if (selectedEquipamentos != null) {
            selectedEquipamentos.clear();
        } else {
            selectedEquipamentos = new ArrayList<Equipamento>();
        }
        // atualiza a lista de equipamentos associados com aquela reserva no selectCheckBoxMenu
        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getRecurso() instanceof Equipamento) {
                selectedEquipamentos.add((Equipamento) reservas.get(i).getRecurso());
            }
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {
        reserva = new Reserva();
        reserva.setRecurso(current);
        Date inicio = (Date) selectEvent.getObject();
        Calendar fim = Calendar.getInstance();
        fim.setTime(inicio);
        fim.add(Calendar.HOUR, 2);
        reserva.setInicio(inicio);
        reserva.setFim(fim.getTime());
        reserva.setRealizacao(new Date());
    }

    public void onReservaMove(ScheduleEntryMoveEvent event) {
        //reserva = reservaFacade.edit((Reserva) event.getScheduleEvent());
        reserva = reservaFacade.merge((Reserva) event.getScheduleEvent());

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Reserva movida", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        //reserva = reservaFacade.edit((Reserva) event.getScheduleEvent());
        reserva = reservaFacade.merge((Reserva) event.getScheduleEvent());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Reserva redimensionada", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public CalendarioController() {
        eventModel = null;
        pessoas = null;

    }

    public void clearSelection() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("wdgListSala.clearSelection()");
        context.execute("wdgListEquipamento.clearSelection()");
    }

    public Recurso getSelected() {
        if (current == null) {
            current = new Recurso();
            selectedItemIndex = -1;
        }
        return current;
    }

//    public List<Equipamento> getSelectedEqui(){
//        if(currents == null){
//            currents = new ArrayList<Equipamento>();
//        }
//        return currents;
//    }
    private RecursoFacade getFacade() {
        return recursoFacade;
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
        current = (Recurso) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

        if (pessoas == null) {
            pessoas = pessoaFacade.findAll();
        }

        if (eventModel == null) {
            eventModel = new DefaultScheduleModel();
            for (Reserva res : current.getReservas()) {
                eventModel.addEvent(res);
            }
        }

        return "View";
    }

    public String prepareCreate() {
        current = new Recurso();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().save(current);
            JsfUtil.addSuccessMessage("RecursoCreated");
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit() {
        current = (Recurso) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("RecursoUpdated");
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy() {
        current = (Recurso) getItems().getRowData();
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
            JsfUtil.addSuccessMessage("RecursoDeleted");
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

        return JsfUtil.getSelectItems(recursoFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        List<Recurso> recursos = recursoFacade.findAll();
        return JsfUtil.getSelectItems(recursoFacade.findAll(), true);
    }

    public Recurso getRecurso(java.lang.Long id) {
        return recursoFacade.find(id);
    }

    @FacesConverter(forClass = Recurso.class)
    public static class RecursoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RecursoController controller = (RecursoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "calendarioController");
            return controller.getRecurso(getKey(value));
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
            if (object instanceof Recurso) {
                Recurso o = (Recurso) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Recurso.class.getName());
            }
        }
    }

    @FacesConverter(forClass = Equipamento.class, value = "equi")
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
