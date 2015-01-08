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

    public static Date setSegundos(Date data, int segundos) {
        Calendar c = prepareCalendar(data);
        c.set(Calendar.SECOND, segundos);
        return c.getTime();
    }

    public static int getMinutos(Date data) {
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.MINUTE);
    }

    public static int getSegundos(Date data) {
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.SECOND);
    }

    public static int getHoras(Date data) {
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static Date setDia(Date data, int dia) {
        Calendar c = prepareCalendar(data);
        c.set(Calendar.DAY_OF_MONTH, dia);
        return c.getTime();
    }

    public static Date setMes(Date data, int mes) {
        Calendar c = prepareCalendar(data);
        c.set(Calendar.MONTH, mes);
        return c.getTime();
    }

    public static Date setAno(Date data, int ano) {
        Calendar c = prepareCalendar(data);
        c.set(Calendar.YEAR, ano);
        return c.getTime();
    }

    public static int getDia(Date data) {
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.DAY_OF_MONTH);
    }
    
    public static int getDiaSemana(Date data){
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getMes(Date data) {
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.MONTH);
    }

    public static int getAno(Date data) {
        Calendar c = prepareCalendar(data);
        return c.get(Calendar.YEAR);
    }

    public static Calendar prepareCalendar(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        return cal;
    }

    public static String getData(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        String month = String.valueOf(c.get(Calendar.MONTH) + 1); //janeiro comeca a contar como mes 0.
        month = fillWithZero(month);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        day = fillWithZero(day);
        String year = String.valueOf(c.get(Calendar.YEAR));
        year = fillWithZero(year);
        return day + "/" + month + "/" + year;
        
        
    }

    public static String fillWithZero(String numero) {
        return numero.length() < 2 ? "0" + numero : numero;
    }

    public static String getHora(Date data) {
        Calendar c = Calendar.getInstance();
        c.setTime(data);
        String hora = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        hora = fillWithZero(hora);
        String minuto = String.valueOf(c.get(Calendar.MINUTE));
        minuto = fillWithZero(minuto);
        return hora + ":" + minuto;
    }

 

    //retorna os dias do mes que correspondem aos dias da semana selecionados em funcao da data selecionada
    public static List<Date> getDiasPrimeiraSemana(List<Integer> diasDaSemana, Date dataSelecionada) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(dataSelecionada);
        int diaSemanaSelecionado = calendario.get(Calendar.DAY_OF_WEEK);
        List<Date> diasPrimeiraSemana = new ArrayList<Date>();
        for (int diaDaSemana : diasDaSemana) {
            calendario = Calendar.getInstance();
            calendario.setTime(dataSelecionada);
            calendario.add(Calendar.DAY_OF_MONTH, (diaDaSemana - diaSemanaSelecionado));
            diasPrimeiraSemana.add(calendario.getTime());
        }
        return diasPrimeiraSemana;
    }

}
