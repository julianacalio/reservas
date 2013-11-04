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
import util.EquipamentoDataModel;
import util.SalaDataModel;

@Named("calendarioController")
@SessionScoped
public class CalendarioController implements Serializable {

    private List<Equipamento> selectedEquipamentos;
    private Map<Equipamento, Equipamento> equips;
    private Recurso current, novaescolha, novaSala, novoEquipamento;
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
    private SalaDataModel salaDataModel;

    public CalendarioController() {

        eventModel = null;
        pessoas = null;

    }

    public void inicializarVariaveis() {
        salaDataModel = null;
        equipamentoDataModel = null;
        eventModel = null;
        pessoas = null;
    }

    public Recurso getNovaescolha() {
        return novaescolha;
    }

    public void setNovaescolha(Recurso novaescolha) {
        this.novaescolha = novaescolha;
    }

    public Recurso getNovaSala() {
        return novaSala;
    }

    public void setNovaSala(Recurso novaSala) {
        this.novaSala = novaSala;
    }

    public Recurso getNovoEquipamento() {
        return novoEquipamento;
    }

    public void setNovoEquipamento(Recurso novoEquipamento) {
        this.novoEquipamento = novoEquipamento;
    }

    public Recurso getCurrent() {
        return current;
    }

    public void setCurrent(Recurso current) {
        this.current = current;
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

        equips = new HashMap<Equipamento, Equipamento>();
        List<Equipamento> e;
        //verifica se existe alguma reserva antes de procurar os equipamentos livres
        if (reserva != null && reserva.getInicio() != null && reserva.getFim() != null) {
            e = getEquipamentosLivres(reserva);
        } else {
            e = equipamentoFacade.findAll();
        }

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
                res.setRecursosAssociados(getRecursosAssociados(res));
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

    public void escolheSala() {
        limparSelecaoTabelaEquipamento();
        if (novaSala == current) {
            return;
        }
        current = novaSala;
        eventModel = null;
    }

    public void escolheEquipamento() {
        limparSelecaoTabelaSala();
        if (novoEquipamento == current) {
            return;
        }
        current = novoEquipamento;
        eventModel = null;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public void addReserva(ActionEvent actionEvent) {
        if (isNovaReserva(reserva)) {
            List<Reserva> reservasOcupadas = getReservasOcupadas(reserva, selectedEquipamentos);
            if (reservasOcupadas != null && !reservasOcupadas.isEmpty()) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Recurso(s) ocupado(s)", getNomeReservasOcupadas(reservasOcupadas));
                addMessage(message);
                showConfirmDialog();
                return;
            }
            reserva = reservaFacade.merge(reserva);
            current.addReserva(reserva);
            reserva.setRecursosAssociados(getRecursosAssociados(reserva));
            eventModel.addEvent(reserva);

        } else {
            reserva = reservaFacade.merge(reserva);
            eventModel.updateEvent(reserva);
        }

        //reserva = reservaFacade.edit(reserva);
       // reserva = reservaFacade.merge(reserva);
        if (isEquipamentoSelecionado()) {
            criaReservasAdicionais(selectedEquipamentos, reserva);
        }

        //current.addReserva(reserva);
//        if (isNovaReserva(reserva)) {
//            current.addReserva(reserva);
//            reserva.setRecursosAssociados(getRecursosAssociados(reserva));
//            eventModel.addEvent(reserva);
//        } else {
//            eventModel.updateEvent(reserva);
//        }
    }

    public boolean isReservaOcupada(Reserva reserva) {
        List<Reserva> reservasOcupadas = getReservasOcupadas(reserva, selectedEquipamentos);
        return reservasOcupadas != null && !reservasOcupadas.isEmpty();
    }

    public boolean isEquipamentoSelecionado() {
        return selectedEquipamentos != null && !selectedEquipamentos.isEmpty();
    }

    public String getNomeReservasOcupadas(List<Reserva> reservasOcupadas) {

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
        return nomeRecurso;
    }

    public void criaReservasAdicionais(List<Equipamento> equipamentosAssociados, Reserva reserva) {
        Reserva[] reservas = new Reserva[equipamentosAssociados.size()];
        for (int i = 0; i < reservas.length; i++) {
            reservas[i] = new Reserva();
            reservas[i].setCentro(reserva.getCentro());
            reservas[i].setFim(reserva.getFim());
            reservas[i].setInicio(reserva.getInicio());
            reservas[i].setMotivo(reserva.getMotivo());
            reservas[i].setRealizacao(reserva.getRealizacao());
            reservas[i].setReservante(reserva.getReservante());
            reservas[i].setRecurso(equipamentosAssociados.get(i));
            reservas[i] = reservaFacade.merge(reservas[i]);
            equipamentosAssociados.get(i).addReserva(reservas[i]);
            // Atualiza a lista de equipamentos associados com este reserva para ser mostrado no calendario
            reserva.getRecursosAssociados().add(equipamentosAssociados.get(i).getDescricao());
        }
        equipamentoDataModel = null;
    }

    public boolean isNovaReserva(Reserva reserva) {
        return reserva.getIid() == null;
    }

    public List<String> getRecursosAssociados(Reserva reserva) {

        // List<Reserva> reservas = reservaFacade.findAllBetween(reserva.getInicio(), reserva.getFim());
        List<Reserva> reservas = reservaFacade.findAll(reserva.getInicio(), reserva.getFim(), reserva.getRealizacao());
        List<String> nomeRecursos = new ArrayList<String>();
        for (Reserva res : reservas) {
            if (res.getRecurso() instanceof Equipamento) {
                Equipamento e = (Equipamento) res.getRecurso();
                nomeRecursos.add(e.getDescricao());
            } else {
                Sala s = (Sala) res.getRecurso();
                nomeRecursos.add("Sala: " + s.getNumero());
            }

        }
        return nomeRecursos;
    }

    public List<Reserva> getReservasOcupadas(Reserva reserva, List<Equipamento> equipamentos) {

        // Verifica Disponibilidade do recurso na reserva principal
        List<Reserva> reservas = reservaFacade.findBetween(reserva.getInicio(), reserva.getFim(), reserva.getRecurso());

        if (equipamentos == null) {
            equipamentos = new ArrayList<Equipamento>();
        }
        // Verifica disponibilidade dos recursos adicionais
        for (int i = 0; i < equipamentos.size(); i++) {
            List<Reserva> reservasEquipamentos = reservaFacade.findBetween(reserva.getInicio(), reserva.getFim(), equipamentos.get(i));
            reservas.addAll(reservasEquipamentos);
        }
        return reservas;
    }

    public void removerReservasConjuntas(Reserva reserva) {
        List<Reserva> reservasComMesmaData = reservaFacade.findAll(reserva.getInicio(), reserva.getFim(), reserva.getRealizacao());
        for (int i = 0; i < reservasComMesmaData.size(); i++) {
            if (reservasComMesmaData.get(i).getIid() != reserva.getIid()) {
                reservasComMesmaData.get(i).getRecurso().remReserva(reservasComMesmaData.get(i));
                reservaFacade.remove(reservasComMesmaData.get(i));
            }
        }
        equipamentoDataModel = null;
        salaDataModel = null;
    }

    public void remReserva(ActionEvent actionEvent) {
        if (reserva.getRecurso() instanceof Sala && selectedEquipamentos != null && !selectedEquipamentos.isEmpty()) {
            removerReservasConjuntas(reserva);
        }
//        else {
//            current.remReserva(reserva);
//            reservaFacade.remove(reserva);
//        }
        eventModel.deleteEvent(reserva);
        current.remReserva(reserva);
        reservaFacade.remove(reserva);
        reserva = null;
    }

    public void onReservaSelect(SelectEvent selectEvent) {
        reserva = (Reserva) selectEvent.getObject();

        if (reserva.getIid() == null) {
            throw new RuntimeException("Reserva sem IID !!!!!!");

        }

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
        if (selectedEquipamentos != null) {
            selectedEquipamentos.clear();
        }

        if (!selecionouRecurso()) {
            showDialogFaltaRecurso();
            return;
        }
//        org.primefaces.component.calendar.Calendar c = new org.primefaces.component.calendar.Calendar();
//        Collection<String> eventos = c.getEventNames();
//
//        org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu s = new org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu();
//        Collection<String> eventos2 = s.getEventNames();
//        
//        org.primefaces.component.datatable.DataTable d = new org.primefaces.component.datatable.DataTable();

        reserva = new Reserva();
        reserva.setRecurso(current);
        Date inicio = (Date) selectEvent.getObject();
        Calendar fim = Calendar.getInstance();
        fim.setTime(inicio);
        fim.add(Calendar.HOUR, 2);
        reserva.setInicio(inicio);
        reserva.setFim(fim.getTime());
        reserva.setRealizacao(new Date());

        if (isValidDate(reserva.getInicio())) {
            showDialog();
        }
    }

    public boolean selecionouRecurso() {

        return novaSala != null || novoEquipamento != null;
    }

    public void showDialog() {
        RequestContext.getCurrentInstance().execute("eventDialog.show()");
    }

    public void showDialogFaltaRecurso() {
        RequestContext.getCurrentInstance().execute("eventDialogSelectRecurso.show()");
    }

    public boolean isValidDate(Date data) {

        Calendar dataReserva = Calendar.getInstance();
        dataReserva.setTime(data);
        int diaDaReserva = dataReserva.get(Calendar.DAY_OF_YEAR);

        Calendar dataAtual = Calendar.getInstance();
        int diaAtual = dataAtual.get(Calendar.DAY_OF_YEAR);
        return diaDaReserva >= diaAtual;
    }

    public void showConfirmDialog() {
        RequestContext.getCurrentInstance().execute("eventDialog2.show()");
    }

    public void limparSelecaoTabelaSala() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("wdgListSala.unselectAllRows()");
    }

    public void limparSelecaoTabelaEquipamento() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("wdgListEquipamento.unselectAllRows()");
    }

    public void onValueChangeHoraInicio() {
        Calendar fim = Calendar.getInstance();
        fim.setTime(reserva.getInicio());
        fim.add(Calendar.HOUR, 2);
        reserva.setFim(fim.getTime());
    }

    public void onValueChangeHoraFim() {
        Date inicio = reserva.getInicio();
        Date fim = reserva.getFim();
        if (fim.before(inicio)) {
            reserva.setFim(reserva.getInicio());
        }
    }

    public boolean possuiEquipamentosReservados() {
        List<Reserva> reservasOcupadas = getReservasOcupadas(reserva, selectedEquipamentos);
        if (reservasOcupadas != null && !reservasOcupadas.isEmpty()) {
            return true;
        } else {
        }
        return false;
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
            JsfUtil.addSuccessMessage("RecursoCreated", null);
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
            JsfUtil.addSuccessMessage("RecursoUpdated", null);
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
            JsfUtil.addSuccessMessage("Recurso Apagado", null);
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

        return JsfUtil.getSelectItems(recursoFacade.findAll(), true);
    }

    public Recurso getRecurso(java.lang.Long id) {
        return recursoFacade.find(id);
    }

    private List<Equipamento> getEquipamentosLivres(Reserva reserva) {

        List<Equipamento> equipamentosLivres = equipamentoFacade.findAll();
        if (reserva.getRecurso() instanceof Equipamento) {
            equipamentosLivres.remove((Equipamento) reserva.getRecurso());
        }
        List<Reserva> reservas = getReservasOcupadas(reserva, equipamentosLivres);

        for (Reserva res : reservas) {
            if (res.getRecurso() instanceof Equipamento) {
                equipamentosLivres.remove((Equipamento) res.getRecurso());
            }
        }

        return equipamentosLivres;

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
