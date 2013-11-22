/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author charles
 */
@Entity
public class GrupoReservas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "grupoReservas")
    private List<Reserva> reservas;

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

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
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

  

    private void setDia(int dia, Reserva reserva) {

        Date dataInicio = reserva.getInicio();
        dataInicio = util.DateTools.setDia(dataInicio, dia);
        reserva.setInicio(dataInicio);
        Date dataFim = reserva.getFim();
        dataFim = util.DateTools.setDia(dataFim, dia);
        reserva.setFim(dataFim);

    }
    
    
    public void createReservaSemanal(Reserva reservaModelo, int numeroOcorrencias, List<Integer> diasDaSemana) {
        reservas = new ArrayList<Reserva>();
        List<Integer> diasPrimeiraSemana = util.DateTools.getDiasSelecionados(diasDaSemana, reservaModelo.getRealizacao());
        for (int i = 0; i < numeroOcorrencias; i++) {
            for (Integer diaPrimeiraSemana : diasPrimeiraSemana) {
                Reserva reservaSemanal = reservaModelo.getClone();
                setDia(diaPrimeiraSemana, reservaSemanal);
                Date dataInicial = reservaSemanal.getInicio();
                dataInicial = util.DateTools.addDia(dataInicial, 7 * numeroOcorrencias);
                reservaSemanal.setInicio(dataInicial);
                Date dataFinal = reservaSemanal.getFim();
                dataFinal = util.DateTools.addDia(dataFinal, 7 * numeroOcorrencias);
                reservaSemanal.setFim(dataFinal);
                reservas.add(reservaSemanal);
            }
        }

    }

}
