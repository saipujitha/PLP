package cop5556fa19;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class test {

	public static void main(String[] args) {
		int[] arr1 = {28,6,22,8,44,17};
		int[] arr2 = {22,28,8,6};
		int output[] = relativeSortArray(arr1, arr2);
		 for(int i=0; i<arr1.length; i++ ){
		System.out.println(output[i]);}
	}
	
	
	public static int[] relativeSortArray(int[] arr1, int[] arr2) {
		int[] result1 = new int[arr1.length];
		int[] order = new int[arr1.length];
         HashMap<Integer,Integer> sorting = new HashMap<>();
         HashMap<Integer,Integer> input = new HashMap<>();
         for(int i=0; i<arr2.length; i++ ){
        	 input.put(arr2[i], i);
         }
         
         for(int j=0; j<arr1.length; j++ ){
        	 sorting.put(arr1[j],0);}
         
         
         for(int j=0; j<arr1.length; j++ ){
        		 sorting.put(arr1[j],(sorting.get(arr1[j])+1));
        	 }
         int temp = 0;
         for(int j=0; j<arr2.length; j++ ){
        	 int iter = sorting.get(arr2[j]);
        	 sorting.put(arr2[j], 0);
        	 while(iter >0) {
        		 result1[temp] = arr2[j];
        		 temp++;
        		 iter--;
        	 }
         }
         int ord=0;
         for(int j=0; j<arr1.length; j++ ){
        	 if(sorting.get(arr1[j])!=0) {
        		order[ord] = arr1[j];
        		ord++;
        	 }}
         
             int n = order.length;  
             for (int i = 0; i < n-1; i++) 
             { 
                 // Find the minimum element in unsorted array 
                 int min_idx = i; 
                 for (int j = i+1; j < n; j++) 
                     if (order[j] < order[min_idx]) 
                         min_idx = j; 
       
                 // Swap the found minimum element with the first 
                 // element 
                 int temp1 = order[min_idx]; 
                 order[min_idx] = order[i]; 
                 order[i] = temp1; 
             } 
         
         for(int j=0; j<order.length; j++ ){
        	 if(order[j] !=0) {
        		 int iter = sorting.get(order[j]);
            	 sorting.put(order[j], 0);
            	 while(iter >0) {
            		 result1[temp] = order[j];
            		 temp++;
            		 iter--;
            	 }
         }  }
         
         
		return result1;
        
    }	    	
}
