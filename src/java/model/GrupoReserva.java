/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
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


    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }


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

    
    private void setDiaMesAno(Date data, Reserva reserva) {
        Date dataInicio = reserva.getInicio();
        int mes = util.DateTools.getMes(data);
        int dia = util.DateTools.getDia(data);
        int ano = util.DateTools.getAno(data);
        dataInicio = util.DateTools.setDia(dataInicio, dia);
        dataInicio = util.DateTools.setMes(dataInicio, mes);
        dataInicio = util.DateTools.setAno(dataInicio, ano);
        reserva.setInicio(dataInicio);
        Date dataFim = reserva.getFim();
        dataFim = util.DateTools.setDia(dataFim, dia);
        dataFim = util.DateTools.setMes(dataFim, mes);
        dataFim = util.DateTools.setAno(dataFim, ano);
        reserva.setFim(dataFim);
    }

    
    public void buildReservaSemanal(Reserva reservaModelo, Date dataFinalEscolhida, List<Integer> diasDaSemana) {

        dataFinalEscolhida = DateTools.setHora(dataFinalEscolhida, DateTools.getHoras(reservaModelo.getFim()));
        dataFinalEscolhida = DateTools.setMinutos(dataFinalEscolhida, DateTools.getMinutos(reservaModelo.getFim()));
        dataFinalEscolhida = DateTools.setSegundos(dataFinalEscolhida, 1);

        reservas = new ArrayList<Reserva>();
        List<Date> diasPrimeiraSemana = util.DateTools.getDiasPrimeiraSemana(diasDaSemana, reservaModelo.getInicio());
        Date dataFinal = Calendar.getInstance().getTime();
        int i = 0;
        while (dataFinal.before(dataFinalEscolhida)) {
            for (Date date : diasPrimeiraSemana) {
                Reserva reservaSemanal = reservaModelo.createClone();
                setDiaMesAno(date, reservaSemanal);
                Date dataInicial = reservaSemanal.getInicio();
                dataInicial = util.DateTools.addDia(dataInicial, 7 * i);
                reservaSemanal.setInicio(dataInicial);
                dataFinal = reservaSemanal.getFim();
                dataFinal = util.DateTools.addDia(dataFinal, 7 * i);
                reservaSemanal.setFim(dataFinal);
                reservaSemanal.setGrupoReserva(this);
                if ((dataInicial.after(reservaModelo.getInicio()) || dataInicial.equals(reservaModelo.getInicio())) && dataFinal.before(dataFinalEscolhida)) {
                    reservas.add(reservaSemanal);
                }
            }
            i++;
        }

    }

}
