/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author charles
 */
public class DateTools {

    public static Date addHora(Date data, int quantidade) {
        Calendar c = prepareCalendar(data);
        c.add(Calendar.HOUR, quantidade);
        return c.getTime();
    }

    public static Date subHora(Date data, int quantidade) {
        Calendar c = prepareCalendar(data);
        c.add(Calendar.HOUR, -quantidade);
        return c.getTime();
    }

    public static Date addMinuto(Date data, int quantidade) {
        Calendar c = prepareCalendar(data);
        c.add(Calendar.MINUTE, quantidade);
        return c.getTime();
    }

    public static Date subMinuto(Date data, int quantidade) {
        Calendar c = prepareCalendar(data);
        c.add(Calendar.MINUTE, -quantidade);
        return c.getTime();
    }

    public static Date addDia(Date data, int quantidade) {
        Calendar c = prepareCalendar(data);
        c.add(Calendar.DAY_OF_MONTH, quantidade);
        return c.getTime();
    }

    public static Date subDia(Date data, int quantidade) {
        Calendar c = prepareCalendar(data);
        c.add(Calendar.DAY_OF_MONTH, -quantidade);
        return c.getTime();
    }

    public static Date setHora(Date data, int hora) {
        Calendar c = prepareCalendar(data);
        c.set(Calendar.HOUR_OF_DAY, hora);
        return c.getTime();
    }

    public static Date setMinutos(Date data, int minutos) {
        Calendar c = prepareCalendar(data);
        c.set(Calendar.MINUTE, minutos);
        return c.getTime();
    }

    public static int getMinutos(Date data) {
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.MINUTE);
    }

    public static int getHora(Date data) {
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static Date setDia(Date data, int dia) {
        Calendar c = prepareCalendar(data);
        c.set(Calendar.DAY_OF_MONTH, dia);
        return c.getTime();
    }

    public static int getDia(Date data) {
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar prepareCalendar(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        return cal;
    }

    
    //retorna os dias do mes que correspondem aos dias da semana selecionados em funcao da data selecionada
    public static List<Integer> getDiasSelecionados(List<Integer> diasDaSemana, Date dataSelecionada) {

        Calendar calendario = Calendar.getInstance();
        calendario.setTime(dataSelecionada);
        int diaSemanaSelecionado = calendario.get(Calendar.DAY_OF_WEEK);
        List<Integer> dias = new ArrayList<Integer>();
        for (int diaDaSemana : diasDaSemana) {
            calendario = Calendar.getInstance();
            calendario.setTime(dataSelecionada);
            calendario.add(Calendar.DAY_OF_MONTH, diaDaSemana - diaSemanaSelecionado);
            dias.add(calendario.get(Calendar.DAY_OF_MONTH));
        }
        return dias;
    }

}
