///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package util;
//
//import java.util.Comparator;
//import org.primefaces.model.SortOrder;
///**
// *
// * @author charles
// */
//public abstract class AbstractLazySorter<T> implements Comparator<T> {
// 
//    private final Class<T> entityClass;
//    
//    private String sortField;
//     
//    private SortOrder sortOrder;
//    
//    public AbstractLazySorter(Class<T> entityClass) {
//        this.entityClass = entityClass;
//    }
//     
//    public AbstractLazySorter(String sortField, SortOrder sortOrder) {
//        this.sortField = sortField;
//        this.sortOrder = sortOrder;
//    }
// 
//    public int compare(T entity1, T entity2) {
//        try {
//            Object value1 = T.class.getField(this.sortField).get(entity1);
//            Object value2 = T.class.getField(this.sortField).get(entity2);
// 
//            int value = ((Comparable)value1).compareTo(value2);
//             
//            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
//        }
//        catch(Exception e) {
//            throw new RuntimeException();
//        }
//    }
//
//    
//}