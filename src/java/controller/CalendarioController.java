package controller;

import facade.RecursoFacade;

import model.Recurso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
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
//import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleModel;

import org.primefaces.model.ScheduleModel;
import util.EquipamentoDataModel;
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
    int numeroOcocrrencias;
    List<Integer> diasDaSemana;
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
    private String opcaoRepeticaoEscolhida;
    private List<String> diasEscolhidos;
    private boolean reservaSemanal;
    private List<Reserva> reservasImpossiveis = new ArrayList<Reserva>();

    public CalendarioController() {
        eventModel = null;
        pessoas = null;
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

    public void addDiaDaSemana(int diaDaSemana) {
        diasDaSemana.add(diaDaSemana);
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
            return getEquipamentosNaoReservados(reserva);
        } else {
            return equipamentoFacade.findAll();
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

    public void verificaSelecaoRepeticao() {
        if (isReservaSemanal()) {
            // RequestContext.getCurrentInstance().execute("eventDialogRepeticao.show()");
        }
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
        recreateEventModel();
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public void salvaReserva(ActionEvent actionEvent) {
        if (isReservaSemanal()) {
            if (isEquipamentoSelecionado()) {
                reserva = adicionaEquipamentosNaReserva(reserva, selectedEquipamentos);
            }
            //String opcaoEscolhida = getOpcaoRepeticaoEscolhida();
            grupoReserva.setDiasDaSemana(getDiasEscolhidos(diasEscolhidos));
            grupoReserva.buildReservaSemanal(reserva);
            reservasImpossiveis = getReservasOcupadas(grupoReserva);
            if (!reservasImpossiveis.isEmpty()) {
                for (Reserva reservaImpossivel : reservasImpossiveis) {
                   // Reserva r = new Reserva();
                    grupoReserva.removeReserva(reservaImpossivel);
                }
                RequestContext.getCurrentInstance().execute("eventDialog5.show()");
            }
            grupoReservaFacade.save(grupoReserva);
            for (Reserva reservaAgrupada : grupoReserva.getReservas()) {
                eventModel.addEvent(reservaAgrupada);
            }

            recreateEquipamentoDataModel();
            recreateSalaDataModel();

        } else {
            addReserva(actionEvent);
        }
    }

    //Retorna as reservas que podem ser feitas possuem um ou mais recursos ocupados
    //naquele horario escolhido
    public List<Reserva> getReservasOcupadas(GrupoReserva novoGrupoReserva) {
        List<Reserva> reservasImpos = new ArrayList<Reserva>();
        for (Reserva res : novoGrupoReserva.getReservas()) {
            if (!getRecursosOcupadosPelaReserva(res).isEmpty()) {
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

    public void addReserva(ActionEvent actionEvent) {

        List<Recurso> recursosReservados = getRecursosOcupadosReserva(reserva, selectedEquipamentos);
        if (!recursosReservados.isEmpty()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Recurso(s) ocupado(s)", recursosReservados.toString());
            current = recursoFacade.find(current.getId());
            addMessage(message);
            showConfirmDialog();
            return;
        }
        if (isEquipamentoSelecionado()) {
            if (isEquipamentoSelecionado()) {
                reserva = adicionaEquipamentosNaReserva(reserva, selectedEquipamentos);
            }
        }
        reserva = reservaFacade.merge(reserva);
        eventModel.addEvent(reserva);

        recreateEquipamentoDataModel();
        recreateSalaDataModel();
    }

    public List<Integer> getDiasEscolhidos(String opcaoSelecionada) {
        List<Integer> dias = new ArrayList<Integer>();
        if (opcaoSelecionada.equals("Semanal")) {
            dias.add(Calendar.MONDAY);
            dias.add(Calendar.TUESDAY);
            dias.add(Calendar.WEDNESDAY);
            dias.add(Calendar.THURSDAY);
            dias.add(Calendar.FRIDAY);
            return dias;
        }
        if (opcaoSelecionada.equals("Segunda a Sexta")) {
            dias.add(Calendar.MONDAY);
            dias.add(Calendar.FRIDAY);
            return dias;
        }
        if (opcaoSelecionada.equals("Terça,Quinta")) {
            dias.add(Calendar.TUESDAY);
            dias.add(Calendar.THURSDAY);
            return dias;
        }

        if (opcaoSelecionada.equals("Segunda, Quarta, Sexta")) {
            dias.add(Calendar.MONDAY);
            dias.add(Calendar.WEDNESDAY);
            dias.add(Calendar.FRIDAY);
            return dias;
        }

        if (opcaoSelecionada.equals("Quarta, Sexta")) {
            dias.add(Calendar.WEDNESDAY);
            dias.add(Calendar.FRIDAY);
            return dias;
        }
        return dias;

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
        List<Recurso> recursosReservados = getRecursosOcupadosReservaId(reserva, selectedEquipamentos);
        if (!recursosReservados.isEmpty()) {
            current = recursoFacade.find(current.getId());
            recreateEventModel();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Recurso(s) ocupado(s)", recursosReservados.toString());
            current = recursoFacade.find(current.getId());
            addMessage(message);
            showConfirmDialog();
            return;
        }

        if (isEquipamentoSelecionado()) {
            reserva.getRecursos().clear();
            reserva.addRecurso(current);
            for (Equipamento equipamento : selectedEquipamentos) {
                if (equipamento.getId() != current.getId()) {
                    reserva.addRecurso(equipamento);

                }
            }
        }
        reserva = reservaFacade.merge(reserva);
        eventModel.updateEvent(reserva);

        recreateEquipamentoDataModel();
        recreateSalaDataModel();
    }

    public boolean isAlgumRecursoOcupado(Reserva reserva, List<Equipamento> selectedEquipamentos) {
        return !getRecursosOcupadosReserva(reserva, selectedEquipamentos).isEmpty();
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

    public void remReserva(ActionEvent actionEvent) {

        if (reserva.getGrupoReserva() != null) {
            GrupoReserva gp = grupoReservaFacade.find(reserva.getGrupoReserva().getId());
            List<Reserva> reservas;
            reservas = reservaFacade.findAll(reserva.getGrupoReserva());
            gp.setReservas(reservas);

            for (Reserva reservaRemovida : gp.getReservas()) {
                eventModel.deleteEvent(reservaRemovida);
            }
            grupoReservaFacade.remove(gp);
        } else {

            eventModel.deleteEvent(reserva);
            current.remReserva(reserva);
            reservaFacade.remove(reserva);
        }
        recreateReserva();
        recreateEquipamentoDataModel();
        recreateSalaDataModel();
        limparSelecaoTabelaEquipamento();
        limparSelecaoTabelaSala();
        recreateSelectedRecurso();
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
        atualizaSelectedEquipamentos(reserva.getRecursos());
        if (reserva.getIid() == null) {
            throw new RuntimeException("Reserva sem IID !!!!!!");// Teste para verificar problemas ocorrendo no merge
        }

        showDialog();

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
        //reserva.setRecurso(current);
        reserva.addRecurso(current);
        Date inicio = (Date) selectEvent.getObject();
        Calendar fim = Calendar.getInstance();
        fim.setTime(inicio);
        fim.add(Calendar.HOUR, 2);
        reserva.setInicio(inicio);
        reserva.setFim(fim.getTime());
        reserva.setRealizacao(new Date());
        showDialog();

    }

    public boolean selecionouRecurso() {
        return novaSala != null || novoEquipamento != null;
    }

    public void showDialog() {
        RequestContext.getCurrentInstance().execute("eventDialog.show()");
    }

    public void showDialogDataInvalida() {
        RequestContext.getCurrentInstance().execute("eventDialog3.show()");
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
    }

    public void onReservaMove(ScheduleEntryMoveEvent event) {

        Reserva reservaRedimensionada = (Reserva) event.getScheduleEvent();

        if (!isValidDate(reservaRedimensionada.getInicio())) {
            current = recursoFacade.find(current.getId());
            recreateEventModel();
            showDialogDataInvalida();
            return;
        }
        reserva = reservaFacade.find(reservaRedimensionada.getIid());
        atualizaSelectedEquipamentos(reserva.getRecursos());
        List<Recurso> recursosOcupados = getRecursosOcupadosReservaId(reservaRedimensionada, selectedEquipamentos);
        if (!recursosOcupados.isEmpty()) {
            current = recursoFacade.find(current.getId());
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
        atualizaSelectedEquipamentos(reserva.getRecursos());
        List<Recurso> recursosOcupados = getRecursosOcupadosReservaId(reservaRedimensionada, selectedEquipamentos);
        if (!recursosOcupados.isEmpty()) {
            current = recursoFacade.find(current.getId());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Recurso(s) ocupado(s)", recursosOcupados.toString());
            addMessage(message);
            recreateEventModel();
            showConfirmDialog();
            return;
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

    private List<Equipamento> getEquipamentosNaoReservados(Reserva reserva) {
        List<Recurso> recursosReservados = isNovaReserva(reserva)
                ? getRecursosOcupados(reserva.getInicio(), reserva.getFim()) : getRecursosOcupados(reserva.getInicio(), reserva.getFim(), reserva.getIid());
        List<Equipamento> equipamentosNaoReservados = equipamentoFacade.findAll();
        for (Recurso recursoReservado : recursosReservados) {
            if (recursoReservado instanceof Equipamento) {
                equipamentosNaoReservados.remove((Equipamento) recursoReservado);
            }
        }
        return equipamentosNaoReservados;
    }

    public List<Recurso> getRecursosOcupados(Date inicio, Date fim) {
        List<Reserva> reservasOcupadas = reservaFacade.findAllBetween(inicio, fim);
        //  List<Reserva> reservasOcupadas = reservaFacade.findBetweenTeste(inicio, fim, reserva.getIid());
        List<Recurso> recursosOcupados = new ArrayList<Recurso>();
        for (Reserva reservaOcupada : reservasOcupadas) {
            recursosOcupados.addAll(reservaOcupada.getRecursos());
        }
        return recursosOcupados;
    }

    public List<Recurso> getRecursosOcupados(Date inicio, Date fim, Long id) {
        List<Reserva> reservasOcupadas = reservaFacade.findBetween(inicio, fim, reserva.getIid());
        List<Recurso> recursosOcupados = new ArrayList<Recurso>();
        for (Reserva reservaOcupada : reservasOcupadas) {
            recursosOcupados.addAll(reservaOcupada.getRecursos());
        }
        return recursosOcupados;

    }

    /**
     * Retorna os recursos que foram selecionados pelas reserva dentro de um horario especifico e ja foram reservados no banco de dados.
     *
     * @param reserva Reserva que possui uma lista de recursos que serão analisados
     * @return Lista de recursos que ja estao reservados no horario da reserva
     */
    public List<Recurso> getRecursosOcupadosPelaReserva(Reserva reserva) {
        List<Recurso> recursosSelecionados = reserva.getRecursos();
        List<Recurso> recursosOcupados = getRecursosOcupados(reserva.getInicio(), reserva.getFim());
        recursosOcupados.retainAll(recursosSelecionados);
        return recursosOcupados;
    }

    public List<Recurso> getRecursosOcupadosReserva(Reserva reserva, List<Equipamento> equipamentosSelecionados) {
        List<Recurso> recursosSelecionados = new ArrayList<Recurso>();
        recursosSelecionados.addAll(equipamentosSelecionados);
        recursosSelecionados.add(current);

        List<Recurso> recursosOcupados = getRecursosOcupados(reserva.getInicio(), reserva.getFim());
        recursosOcupados.retainAll(recursosSelecionados);

        return recursosOcupados;
    }

    // Retorna or recursos selecionados pela reserva que ja estao reservados no banco de dados na nova data especificada
    // sem considerar os recursos salvo pela propria reserva.
    public List<Recurso> getRecursosOcupadosReservaId(Reserva reserva, List<Equipamento> equipamentosSelecionados) {
        List<Recurso> recursosSelecionados = new ArrayList<Recurso>();
        recursosSelecionados.addAll(equipamentosSelecionados);
        recursosSelecionados.add(current);

        List<Recurso> recursosOcupados = getRecursosOcupados(reserva.getInicio(), reserva.getFim(), reserva.getIid());
        recursosOcupados.retainAll(recursosSelecionados);

        return recursosOcupados;
    }

    public String getLabelBotaoAddReserva() {
        return isNovaReserva(reserva) ? "Salvar" : "Atualizar";
    }

    private List<Equipamento> getEquipamentosReservados() {
        List<Recurso> recursosReservados = getRecursosOcupados(reserva.getInicio(), reserva.getFim());
        List<Equipamento> equipamentosReservados = new ArrayList<Equipamento>();

        for (Recurso recurso : recursosReservados) {
            if (recurso instanceof Equipamento) {
                equipamentosReservados.add((Equipamento) recurso);
            }
        }
        return equipamentosReservados;
    }

//    public List<Reserva> getReservaSemanal(int numeroOcorrencias, List<Integer> diasDaSemana, Reserva reserva) {
//        List<Reserva> reservasSemanais = new ArrayList<Reserva>();
//        List<Date> datasSelecionadas = getDatasSelecionadas(diasDaSemana, reserva.getInicio());
//        for (int i = 0; i < numeroOcorrencias; i++) {
//            for (Date date : datasSelecionadas) {
//                Reserva reservaSemanal = new Reserva();
//                reservaSemanal = reserva.createClone(reservaSemanal);
//                reservaSemanal.setInicio(date);
//                int dia = util.DateTools.getDia(reservaSemanal.getInicio());
//                reservaSemanal.setFim(util.DateTools.setDia(reservaSemanal.getFim(), dia));
//
//                reservasSemanais.add(reservaSemanal);
//
//            }
//
//        }
//        return reservasSemanais;
//    }
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

//    public Reserva addSemana(Reserva reserva, int qtdeDias) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(reserva.getInicio());
//        c.add(Calendar.DAY_OF_MONTH, 7 * qtdeDias);
//        reserva.setInicio(c.getTime());
//        c.setTime(reserva.getFim());
//        c.add(Calendar.DAY_OF_MONTH, 7 * qtdeDias);
//        reserva.setFim(c.getTime());
//        return reserva;
//    }
    public List<String> getOpcoesRepeticao() {
        List<String> opcoes = new ArrayList<String>();
        opcoes.add("Semanal");
        opcoes.add("Segunda a Sexta");
        opcoes.add("Segunda,Quarta,Sexta");
        opcoes.add("Terça,Quinta");
        opcoes.add("Quarta, Sexta");
        return opcoes;
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
