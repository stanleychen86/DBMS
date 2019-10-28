import java.io.*;
import java.util.*;
import java.lang.*;

/* Create an ArrayList<Integer> to store indices of entries
that match the desired Condition. Then, iterate through
Conjunctions (ors, ands, etc) and then add any new entries that
match the desired Condition. */

public class Condition {
	
	ArrayList<Integer> indices = new ArrayList<Integer>();
	ArrayList<Conjunction> conjunctions;

	public Condition(ArrayList<Conjunction> conjunctions) {
		this.conjunctions = conjunctions;
		
	}
	
	public ArrayList<Integer> get_indices(Table t){
		for (int j = 0; j < conjunctions.size(); j++) {
			Conjunction c = conjunctions.get(j);
			ArrayList<Integer> get_indices = c.get_indices(t);
			for (int k = 0; k < get_indices.size(); k++) {
				Integer i = get_indices.get(k);
				if(!(indices.contains(i))) {
                    indices.add(i);
                }
			}
		}
		return indices;
	}
}
