package controller;

import facade.RecursoFacade;
import java.io.IOException;

import model.Recurso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import model.Equipamento;
import model.GrupoReserva;
import model.Pessoa;
import model.Reserva;
import model.Sala;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleModel;

import org.primefaces.model.ScheduleModel;
import util.DateTools;
import util.EquipamentoDataModel;
import util.RecursoDataModel;
import util.SalaDataModel;

//        org.primefaces.component.calendar.Calendar c = new org.primefaces.component.calendar.Calendar();
//        Collection<String> eventos = c.getEventNames();
//
//        org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu s = new org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu();
//        Collection<String> eventos2 = s.getEventNames();
//        
//        org.primefaces.component.datatable.DataTable d = new org.primefaces.component.datatable.DataTable();
@Named("calendarioController")
@SessionScoped
public class CalendarioController implements Serializable {

    boolean isRepetida;

    private List<Equipamento> selectedEquipamentos;

    private Recurso current, novaescolha, novaSala, novoEquipamento;
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
    @EJB
    private facade.GrupoReservaFacade grupoReservaFacade;

    private ScheduleModel eventModel;
    private Reserva reserva = new Reserva();
    private GrupoReserva grupoReserva = new GrupoReserva();
    List<Pessoa> pessoas;
    private EquipamentoDataModel equipamentoDataModel;
    private SalaDataModel salaDataModel;
    private RecursoDataModel recursoDataModel;
    private String opcaoRepeticaoEscolhida;
    private List<String> diasEscolhidos;
    private boolean reservaSemanal;
    private List<Reserva> reservasImpossiveis = new ArrayList<Reserva>();
    private Date dataFinalReservaSemanal;

    public CalendarioController() {
        org.primefaces.component.calendar.Calendar c = new org.primefaces.component.calendar.Calendar();
        Collection<String> eventos = c.getEventNames();
        eventModel = null;
        pessoas = null;
    }

    public RecursoDataModel getRecursoDataModel() {
        if (reserva != null && reserva.getRecursos() != null) {
            return recursoDataModel = new RecursoDataModel(reserva.getRecursos());
        } else {
            return recursoDataModel = new RecursoDataModel();
        }
    }

    public void setRecursoDataModel(RecursoDataModel recursoDataModel) {
        this.recursoDataModel = recursoDataModel;
    }

    public Date getDataFinalReservaSemanal() {
        return dataFinalReservaSemanal;
    }

    public void setDataFinalReservaSemanal(Date dataFinalReservaSemanal) {
        this.dataFinalReservaSemanal = dataFinalReservaSemanal;
    }

    public List<Reserva> getReservasImpossiveis() {
        return reservasImpossiveis;
    }

    public void setReservasImpossiveis(List<Reserva> reservasImpossiveis) {
        this.reservasImpossiveis = reservasImpossiveis;
    }

    public List<String> getDiasEscolhidos() {
        return diasEscolhidos;
    }

    public void setDiasEscolhidos(List<String> diasEscolhidos) {
        this.diasEscolhidos = diasEscolhidos;
    }

    public boolean isReservaSemanal() {
        return reservaSemanal;
    }

    public void setReservaSemanal(boolean reservaSemanal) {
        this.reservaSemanal = reservaSemanal;
    }

    public String getOpcaoRepeticaoEscolhida() {
        return opcaoRepeticaoEscolhida;
    }

    public void setOpcaoRepeticaoEscolhida(String opcaoRepeticaoEscolhida) {
        this.opcaoRepeticaoEscolhida = opcaoRepeticaoEscolhida;
    }

    public void recreateEventModel() {
        eventModel = null;
    }

    public void inicializarVariaveis() {
        salaDataModel = null;
        equipamentoDataModel = null;
        recreateEventModel();
        pessoas = null;
    }

    public GrupoReserva getGrupoReserva() {
        return grupoReserva;
    }

    public void setGrupoReserva(GrupoReserva grupoReserva) {
        this.grupoReserva = grupoReserva;
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

    public void removeRecursoReserva() {
        Recurso rec = (Recurso) recursoDataModel.getRowData();
        if (rec.getId() != current.getId()) {
            reserva.getRecursos().remove(rec);
        } else {
            RequestContext.getCurrentInstance().execute("recursoNaoApagavel.show()");
        }
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
        List<Pessoa> sugestoes = new ArrayList<Pessoa>();
        query = query.toLowerCase();
        for (Pessoa p : pessoas) {
            if (p.getNome().toLowerCase().startsWith(query)) {
                sugestoes.add(p);
            }
        }
        return sugestoes;
    }

    public List<Equipamento> getSelectedEquipamentos() {
        return selectedEquipamentos;
    }

    public void setSelectedEquipamentos(List<Equipamento> selectedEquipamentos) {
        this.selectedEquipamentos = selectedEquipamentos;
    }

    public List<Equipamento> getEquipamentos() {

        //verifica se existe alguma reserva antes de procurar os equipamentos livres
        if (reserva != null && reserva.getInicio() != null && reserva.getFim() != null) {
            return getEquipamentosLivres(reserva.getInicio(), reserva.getFim());
        } else {
            return new ArrayList<Equipamento>();
        }
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

    public void escolheSala() {
        limpaSelecaoTabelaEquipamento();
        if (novaSala == current) {
            return;
        }
        current = novaSala;
        eventModel = null;
    }

    public void escolheEquipamento() {
        limpaSelecaoTabelaSala();
        if (novoEquipamento == current) {
            return;
        }
        current = novoEquipamento;
        recreateEventModel();
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public void salvaReserva(ActionEvent actionEvent) {
        if (isReservaSemanal()) {
            grupoReserva = new GrupoReserva();
            addReservaSemanal();
        } else {
            addReserva();
        }
    }

    public void addReservaSemanal() {
        if (isEquipamentoSelecionado()) {
            reserva = adicionaEquipamentosNaReserva(reserva, selectedEquipamentos);
        }

        List<Integer> diasDaSemana = getDiasEscolhidos(diasEscolhidos);
        grupoReserva.buildReservaSemanal(reserva, dataFinalReservaSemanal, diasDaSemana);
        reservasImpossiveis = getReservasImpossiveis(grupoReserva);
        //se possui alguma reserva impossivel
        if (!reservasImpossiveis.isEmpty()) {
            //procura essa reserva na lista e remove
            for (Reserva reservaImpossivel : reservasImpossiveis) {
                for (Iterator<Reserva> res = grupoReserva.getReservas().iterator(); res.hasNext();) {
                    Reserva r = res.next();
                    if (r.getInicio().equals(reservaImpossivel.getInicio())) {
                        res.remove();
                    }
                }
            }
            RequestContext.getCurrentInstance().execute("reservasIndisponiveis.show()");
        }
        grupoReservaFacade.save(grupoReserva);
        for (Reserva reservaAgrupada : grupoReserva.getReservas()) {
            eventModel.addEvent(reservaAgrupada);
        }

        recreateEquipamentoDataModel();
        recreateSalaDataModel();
        recreateGrupoReserva();
        recreateDiasEscolhidos();
        recreateIsReservaSemanal();
        recreateDataFinal();
        recreateReserva();
        recreateSelectedEquipamento();
    }

    //Retorna as reservas que nao podem ser feitas pois possuem um ou mais recursos ocupados
    //naquele horario escolhido
    public List<Reserva> getReservasImpossiveis(GrupoReserva novoGrupoReserva) {
        List<Reserva> reservasImpos = new ArrayList<Reserva>();
        for (Reserva res : novoGrupoReserva.getReservas()) {
            List<Recurso> recursosOcupados = getRecursosOcupadosDaReserva(res);
            if (!recursosOcupados.isEmpty()) {
                reservasImpos.add(res);
            }
        }
        return reservasImpos;
    }

    public Reserva adicionaEquipamentosNaReserva(Reserva reserva, List<Equipamento> equipamentos) {
        for (Equipamento equipamento : equipamentos) {
            reserva.addRecurso(equipamento);
        }
        return reserva;
    }

    public void addReserva() {
        if (isEquipamentoSelecionado()) {
            reserva = adicionaEquipamentosNaReserva(reserva, selectedEquipamentos);
        }
        List<Recurso> recursosReservados = getRecursosOcupadosDaReserva(reserva);
        if (!recursosReservados.isEmpty()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Recurso(s) ocupado(s)", recursosReservados.toString());
            updateRecursoSelecionado();
            addMessage(message);
            showConfirmDialog();
            return;
        }

        reserva = reservaFacade.merge(reserva);
        eventModel.addEvent(reserva);

        recreateEquipamentoDataModel();
        recreateSalaDataModel();
        recreateSelectedEquipamento();
    }

    public List<Integer> getDiasEscolhidos(List<String> diasEscolhidos) {
        List<Integer> dias = new ArrayList<Integer>();
        for (String diaEscolhido : diasEscolhidos) {
            if (diaEscolhido.equals("Seg")) {
                dias.add(Calendar.MONDAY);
            }
            if (diaEscolhido.equals("Ter")) {
                dias.add(Calendar.TUESDAY);
            }
            if (diaEscolhido.equals("Qua")) {
                dias.add(Calendar.WEDNESDAY);
            }
            if (diaEscolhido.equals("Qui")) {
                dias.add(Calendar.THURSDAY);
            }
            if (diaEscolhido.equals("Sex")) {
                dias.add(Calendar.FRIDAY);
            }
            if (diaEscolhido.equals("Sab")) {
                dias.add(Calendar.SATURDAY);
            }
            if (diaEscolhido.equals("Dom")) {
                dias.add(Calendar.SUNDAY);
            }
        }
        return dias;
    }

    public void updateReserva(ActionEvent actionEvent) {

        List<Recurso> recursosReservados = getRecursosOcupadosDaReserva(reserva);
        List<Recurso> recursosPreviamenteSelecionados = reservaFacade.find(reserva.getIid()).getRecursos();
        recursosReservados.removeAll(recursosPreviamenteSelecionados);
        if (!recursosReservados.isEmpty()) {
            updateRecursoSelecionado();
            recreateEventModel();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Recurso(s) ocupado(s)", recursosReservados.toString());
            addMessage(message);
            showConfirmDialog();
            return;
        }

        if (isEquipamentoSelecionado()) {
            reserva = adicionaEquipamentosNaReserva(reserva, selectedEquipamentos);

        }

        if (reserva.getGrupoReserva() != null) {//se pertence a algum grupo
            grupoReserva.setRecursos(reserva.getRecursos());
            grupoReserva.setReservante(reserva.getReservante());
            grupoReserva.setMotivo(reserva.getMotivo());
            grupoReservaFacade.merge(grupoReserva);
            updateRecursoSelecionado();
            recreateEventModel();
            recreateSelectedEquipamento();
        } else {
            reserva = reservaFacade.merge(reserva);
            eventModel.updateEvent(reserva);
        }

        recreateEquipamentoDataModel();
        recreateSalaDataModel();
        recreateSelectedEquipamento();
    }

    public void updateRecursoSelecionado() {
        current = recursoFacade.find(current.getId());
    }

    public boolean isEquipamentoSelecionado() {
        return selectedEquipamentos != null && !selectedEquipamentos.isEmpty();
    }

    public boolean isNovaReserva(Reserva reserva) {
        if (reserva == null) {
            return false;
        }
        return reserva.getIid() == null;
    }

    public boolean isNovaReserva() {
        if (reserva == null) {
            return false;
        }
        return reserva.getIid() == null;
    }

    public void remReserva(ActionEvent actionEvent) {

        if (reserva.getGrupoReserva() != null) {
            RequestContext.getCurrentInstance().execute("removerReservaSemanal.show()");
        } else {
            removeReservaIndividual();
        }

    }

    public void removeReservaIndividual() {
        eventModel.deleteEvent(reserva);
        current.remReserva(reserva);
        reservaFacade.remove(reserva);
        recreateReserva();
        recreateEquipamentoDataModel();
        recreateSalaDataModel();
        updateRecursoSelecionado();
    }

    public void removeReservaSemanal() {
        GrupoReserva gp = reserva.getGrupoReserva();
        List<Reserva> reservas;
        reservas = reservaFacade.findAll(reserva.getGrupoReserva());
        gp.setReservas(reservas);

        for (Reserva reservaRemovida : gp.getReservas()) {
            eventModel.deleteEvent(reservaRemovida);
        }
        grupoReservaFacade.remove(gp);
        recreateReserva();
        recreateEquipamentoDataModel();
        recreateSalaDataModel();
        updateRecursoSelecionado();
    }

    public void recreateReserva() {
        reserva = null;
    }

    public void recreateSelectedRecurso() {
        novaSala = null;
        novoEquipamento = null;
    }

    public void recreateEquipamentoDataModel() {
        equipamentoDataModel = null;
    }

    public void recreateSalaDataModel() {
        salaDataModel = null;
    }

    public void onReservaSelect(SelectEvent selectEvent) {
        reserva = (Reserva) selectEvent.getObject();

        if (reserva.getGrupoReserva() != null) {
            grupoReserva = reserva.getGrupoReserva();
            List<Reserva> reservas = reservaFacade.findAll(grupoReserva);
            grupoReserva.setReservas(reservas);
        }

        showDialogDetalhesDaReserva();

    }

    public void atualizaSelectedEquipamentos(List<Recurso> recursos) {
        selectedEquipamentos = new ArrayList<Equipamento>();
        for (Recurso recurso : recursos) {
            if (recurso instanceof Equipamento) {
                selectedEquipamentos.add((Equipamento) recurso);
            }
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {

        if (!isValidDate((Date) selectEvent.getObject())) {
            showDialogDataInvalida();
            return;
        }

        if (selectedEquipamentos != null) {
            selectedEquipamentos.clear();
        }

        if (!selecionouRecurso()) {
            showDialogFaltaRecurso();
            return;
        }

        reserva = new Reserva();
        reserva.addRecurso(current);
        Date inicio = (Date) selectEvent.getObject();
        inicio = DateTools.setHora(inicio, 7);
        Calendar fim = Calendar.getInstance();
        fim.setTime(inicio);
        fim.add(Calendar.HOUR, 2);
        reserva.setInicio(inicio);
        reserva.setFim(fim.getTime());
        reserva.setRealizacao(new Date());
        showDialogDetalhesDaReserva();

    }

    public boolean selecionouRecurso() {
        return novaSala != null || novoEquipamento != null;
    }

    public void showDialogDetalhesDaReserva() {
        RequestContext.getCurrentInstance().execute("detalhesReserva.show()");
    }

    public void showDialogDataInvalida() {
        RequestContext.getCurrentInstance().execute("horarioInvalido.show()");
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

        return dataReserva.after(dataAtual) || diaDaReserva == diaAtual;
    }

    public void showConfirmDialog() {
        RequestContext.getCurrentInstance().execute("recursoOcupado.show()");
    }

    public void limpaSelecaoTabelaSala() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("wdgListSala.unselectAllRows()");
    }

    public void limpaSelecaoTabelaEquipamento() {
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
    }

    public void onReservaMove(ScheduleEntryMoveEvent event) {

        Reserva reservaRedimensionada = (Reserva) event.getScheduleEvent();

        if (!isValidDate(reservaRedimensionada.getInicio())) {
            updateRecursoSelecionado();
            recreateEventModel();
            showDialogDataInvalida();
            return;
        }
        reserva = reservaFacade.find(reservaRedimensionada.getIid());
        List<Recurso> recursosOcupados = getRecursosOcupados(reservaRedimensionada.getInicio(), reservaRedimensionada.getFim());
        recursosOcupados.retainAll(reservaRedimensionada.getRecursos());
        if (!recursosOcupados.isEmpty()) {
            updateRecursoSelecionado();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Recurso(s) ocupado(s)", recursosOcupados.toString());
            addMessage(message);
            recreateEventModel();
            showConfirmDialog();
            return;
        }
        reserva = reservaFacade.merge((Reserva) event.getScheduleEvent());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Reserva movida", "Recursos alterados: \n" + reserva.getRecursos());
        addMessage(message);
        recreateEquipamentoDataModel();
        recreateSalaDataModel();
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        Reserva reservaRedimensionada = (Reserva) event.getScheduleEvent();
        reserva = reservaFacade.find(reservaRedimensionada.getIid());
        List<Reserva> reservasOcupadas = reservaFacade.findAllBetween(reservaRedimensionada.getInicio(), reservaRedimensionada.getFim());
        reservasOcupadas.remove(reserva);
        List<Recurso> recursosOcupados = getRecursos(reservasOcupadas);

        if (reservaRedimensionada.getFim().after(reserva.getFim())) {
            if (!recursosOcupados.isEmpty()) {
                updateRecursoSelecionado();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Recurso(s) ocupado(s)", recursosOcupados.toString());
                addMessage(message);
                recreateEventModel();
                showConfirmDialog();
                return;
            }
        }

        reserva = reservaFacade.merge((Reserva) event.getScheduleEvent());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Reserva redimensionada", "Recursos alterados: \n" + reserva.getRecursos());
        addMessage(message);
        recreateEquipamentoDataModel();
        recreateSalaDataModel();
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void clearSelection() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("wdgListSala.clearSelection()");
        context.execute("wdgListEquipamento.clearSelection()");
    }

    private RecursoFacade getFacade() {
        return recursoFacade;
    }

    public Recurso getRecurso(java.lang.Long id) {
        return recursoFacade.find(id);
    }

    public List<Equipamento> getEquipamentosLivres(Date inicio, Date fim) {

        List<Equipamento> equipamentosLivre = new ArrayList<Equipamento>();
        List<Recurso> recursos = getRecursosLivres(inicio, fim);
        for (Recurso recurso : recursos) {
            if (recurso instanceof Equipamento) {
                equipamentosLivre.add((Equipamento) recurso);
            }
        }
        return equipamentosLivre;
    }

    public List<Recurso> getRecursosLivres(Date inicio, Date fim) {
        List<Recurso> recursosLivres = recursoFacade.findAll();
        List<Reserva> reservasOcupadas = reservaFacade.findAllBetween(inicio, fim);
        List<Recurso> recursosOcupados = new ArrayList<Recurso>();
        for (Reserva reservaOcupada : reservasOcupadas) {
            recursosOcupados.addAll(reservaOcupada.getRecursos());
        }
        recursosLivres.removeAll(recursosOcupados);
        return recursosLivres;
    }

    public List<Recurso> getRecursosOcupados(Date inicio, Date fim) {
        Set<Reserva> reservasOcupadas = new HashSet<Reserva>(reservaFacade.findAllBetween(inicio, fim));
        List<Recurso> recursosOcupados = new ArrayList<Recurso>();
        for (Reserva reservaOcupada : reservasOcupadas) {
            recursosOcupados.addAll(reservaOcupada.getRecursos());
        }
        return recursosOcupados;
    }

    /**
     * Retorna os recursos que foram selecionados pela nova reserva e ja estao reservados no horario da nova reserva no banco de dados.
     *
     * @param reserva Reserva que possui uma lista de recursos que serão analisados
     * @return Lista de recursos que ja estao reservados no horario da reserva
     */
    private List<Recurso> getRecursosOcupadosDaReserva(Reserva reserva) {
        List<Recurso> recursosSelecionados = reserva.getRecursos();
        List<Recurso> recursosOcupados = getRecursosOcupados(reserva.getInicio(), reserva.getFim());
        recursosOcupados.retainAll(recursosSelecionados);
        return recursosOcupados;
    }

    private List<Recurso> getRecursos(List<Reserva> reservasOcupadas) {
        List<Recurso> recursos = new ArrayList<Recurso>();
        for (Reserva reservaOcupada : reservasOcupadas) {
            recursos.addAll(reservaOcupada.getRecursos());
        }
        return recursos;
    }

    public List<Date> getDatasSelecionadas(List<Integer> diasDaSemana, Date dataSelecionada) {

        Calendar calendario = Calendar.getInstance();
        calendario.setTime(dataSelecionada);
        int diaSemanaSelecionado = calendario.get(Calendar.DAY_OF_WEEK);
        List<Date> datas = new ArrayList<Date>();
        for (int diaDaSemana : diasDaSemana) {
            calendario = Calendar.getInstance();
            calendario.setTime(dataSelecionada);
            calendario.add(Calendar.DAY_OF_MONTH, diaDaSemana - diaSemanaSelecionado);
            datas.add(calendario.getTime());
        }
        return datas;
    }

    private void recreateGrupoReserva() {
        grupoReserva = null;
    }

    private void recreateReservasImpossiveis() {
        reservasImpossiveis = null;
    }

    private void recreateDiasEscolhidos() {
        diasEscolhidos = null;
    }

    private void recreateIsReservaSemanal() {
        reservaSemanal = false;
    }

    private void recreateDataFinal() {
        dataFinalReservaSemanal = null;
    }

    private void recreateSelectedEquipamento() {
        selectedEquipamentos.clear();
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
