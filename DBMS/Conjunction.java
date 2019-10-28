import java.io.*;
import java.util.*;

public class Conjunction {
	
	ArrayList<Integer> indices;
	ArrayList<Comparison> comparisons;

	public Conjunction(ArrayList<Comparison> comparisons) {
		this.comparisons = comparisons;	
	}

	public ArrayList<Integer> intersection(ArrayList<Integer> list1, ArrayList<Integer> list2){
		ArrayList<Integer> intersection = new ArrayList<Integer>();
		
		for(int i = 0; i < list1.size(); i++) {
			if (!list2.contains(list1.get(i)))
				continue;
			intersection.add(list1.get(i));		
		}	
		return intersection;
	}
	
	public ArrayList<Integer> get_indices(Table table){
		
		ArrayList<Integer> intersect_all = new ArrayList<Integer>();
		intersect_all = comparisons.get(0).get_indices(table);
		int i = 0;
		int comparisonListSize = comparisons.size();
		while(i < comparisonListSize) {
			Comparison temp = comparisons.get(i);
			intersect_all = intersection(intersect_all, temp.get_indices(table));
			i++;
		}
		return intersect_all;
	}
}
