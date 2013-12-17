/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import util.DateTools;

/**
 *
 * @author charles
 */
@Entity
public class GrupoReserva implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "grupoReserva", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<Reserva>();

//    @ElementCollection
//    private List<Integer> diasDaSemana = new ArrayList<Integer>();

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

//    public void addDiaDaSemana(int DIA_DA_SEMANA) {
//        diasDaSemana.add(DIA_DA_SEMANA);
//    }
//
//    public List<Integer> getDiasDaSemana() {
//        return diasDaSemana;
//    }

//    public void setDiasDaSemana(List<Integer> diasDaSemana) {
//        this.diasDaSemana = diasDaSemana;
//    }

    public void addReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    public void removeReserva(Reserva reserva) {
        reservas.remove(reserva);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReservante(Pessoa reservante) {
        for (Reserva reserva : reservas) {
            reserva.setReservante(reservante);
        }
    }

    public void setInicio(Date inicio) {
        for (Reserva reserva : reservas) {
            reserva.setInicio(inicio);
        }
    }

    public void setRealizacao(Date realizacao) {
        for (Reserva reserva : reservas) {
            reserva.setRealizacao(realizacao);
        }
    }

    public void setRecursos(List<Recurso> recursos) {
        for (Reserva reserva : reservas) {
            reserva.setRecursos(recursos);
        }
    }

    public void setMotivo(String motivo) {
        for (Reserva reserva : reservas) {
            reserva.setMotivo(motivo);
        }
    }

    private void setDia(int dia, Reserva reserva) {

        Date dataInicio = reserva.getInicio();
        dataInicio = util.DateTools.setDia(dataInicio, dia);
        reserva.setInicio(dataInicio);
        Date dataFim = reserva.getFim();
        dataFim = util.DateTools.setDia(dataFim, dia);
        reserva.setFim(dataFim);

    }

//    public void buildReservaSemanal(Reserva reservaModelo, int numeroOcorrencias) {
//        reservas = new ArrayList<Reserva>();
//        List<Integer> diasSemanaSelecionados = util.DateTools.getDiasSelecionados(this.diasDaSemana, reservaModelo.getInicio());
//        for (int i = 0; i < numeroOcorrencias; i++) {
//            for (Integer diaPrimeiraSemana : diasSemanaSelecionados) {
//                Reserva reservaSemanal = reservaModelo.createClone();
//                setDia(diaPrimeiraSemana, reservaSemanal);
//                Date dataInicial = reservaSemanal.getInicio();
//                dataInicial = util.DateTools.addDia(dataInicial, 7 * i);
//                reservaSemanal.setInicio(dataInicial);
//                Date dataFinal = reservaSemanal.getFim();
//                dataFinal = util.DateTools.addDia(dataFinal, 7 * i);
//                reservaSemanal.setFim(dataFinal);
//                reservaSemanal.setGrupoReserva(this);
//                if (dataInicial.after(reservaModelo.getInicio()) || dataInicial.equals(reservaModelo.getInicio())) {
//                    reservas.add(reservaSemanal);
//                }
//            }
//        }
//
//    }

    //arrumar este metodo
    public void buildReservaSemanal(Reserva reservaModelo, Date dataFinalEscolhida, List<Integer> diasDaSemana) {

        dataFinalEscolhida = DateTools.setHora(dataFinalEscolhida, DateTools.getHoras(reservaModelo.getFim()));
        dataFinalEscolhida = DateTools.setMinutos(dataFinalEscolhida, DateTools.getMinutos(reservaModelo.getFim()));
        dataFinalEscolhida = DateTools.setSegundos(dataFinalEscolhida, 1);
        

        reservas = new ArrayList<Reserva>();
        List<Integer> diasSemanaSelecionados = util.DateTools.getDiasSelecionados(diasDaSemana, reservaModelo.getInicio());

        Date dataFinal = Calendar.getInstance().getTime();
        int i = 0;
        while (dataFinal.before(dataFinalEscolhida)) {
            for (Integer diaPrimeiraSemana : diasSemanaSelecionados) {
                Reserva reservaSemanal = reservaModelo.createClone();
                setDia(diaPrimeiraSemana, reservaSemanal);
                Date dataInicial = reservaSemanal.getInicio();
                dataInicial = util.DateTools.addDia(dataInicial, 7 * i);
                reservaSemanal.setInicio(dataInicial);
                dataFinal = reservaSemanal.getFim();
                dataFinal = util.DateTools.addDia(dataFinal, 7 * i);
                reservaSemanal.setFim(dataFinal);
                reservaSemanal.setGrupoReserva(this);
                if ((dataInicial.after(reservaModelo.getInicio()) || dataInicial.equals(reservaModelo.getInicio()))&& dataFinal.before(dataFinalEscolhida)) {
                    reservas.add(reservaSemanal);
                }
            }
            i++;
        }

    }

}
