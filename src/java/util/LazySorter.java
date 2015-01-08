/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.Comparator;
import model.Reserva;

import org.primefaces.model.SortOrder;

/**
 *
 * @author charles
 */
public class LazySorter implements Comparator<Reserva> {
 
    private String sortField;
     
    private SortOrder sortOrder;
     
    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
 
    public int compare(Reserva reserva1, Reserva reserva2) {
        try {
            Object value1 = Reserva.class.getField(this.sortField).get(reserva1);
            Object value2 = Reserva.class.getField(this.sortField).get(reserva2);
 
            int value = ((Comparable)value1).compareTo(value2);
             
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }

    
}