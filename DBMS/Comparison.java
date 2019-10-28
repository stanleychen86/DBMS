import java.io.*;
import java.lang.Math;
import java.util.*;

public class Comparison {
	ArrayList<Integer> indices;
	
	Operand operandA, operandB;
	Operation.operation o;
	
	public Comparison(Operand a, Operation.operation o, Operand b){
		operandA = a;
		operandB = b;
		this.o = o;
	}
	
	public boolean checkForAttributes(Operand a, Operand b){
		return (a.type.equals("Attribute") && b.type.equals("Attribute"));
	}
	
	public ArrayList<Integer> get_indices(Table table){
		Attribute att = getAttribute(table, operandA.nameOfAttr);
		att.showAttribute();
		int a_len = att.valList.size();
		int b_len;
		Attribute attrB = new Attribute("");
		switch (operandB.type) {
		case "Attribute":
			attrB = getAttribute(table, operandB.nameOfAttr);
			b_len = attrB.valList.size();
			break;
		default:
			b_len=1;
			break;
		}
		ArrayList<Integer> indices = new ArrayList<Integer> ();
		int maxLen = Math.max(a_len, b_len);
		int i = 0;
		while(i < maxLen) {
			if (o == o.eq) {
				if(operandB.type.equals("Attribute")){
					if(att.valList.get(i).compareTo(attrB.valList.get(i)) == 0){
						 indices.add(i);
						}
					}
					else {
						if(att.valList.get(i).compareTo(operandB.literal) == 0){
						 indices.add(i);
						}
					}
			} else if (o == o.neq) {
				if(operandB.type.equals("Attribute")){
					if(att.valList.get(i).compareTo(attrB.valList.get(i)) != 0){
						indices.add(i);
						}
					}
					else{	
						if(att.valList.get(i).compareTo(operandB.literal) != 0){
							indices.add(i);
						}
					}
			} else if (o == o.gthan) {
				if(operandB.type.equals("Attribute")){
					if(att.valList.get(i).compareTo(attrB.valList.get(i)) > 0){
					 indices.add(i);
					}
				}
				else{	
					if(att.valList.get(i).compareTo(operandB.literal) > 0){
					 indices.add(i);
					}
				}
			} else if (o == o.lthan) {
				if(operandB.type.equals("Attribute")){
					if(att.valList.get(i).compareTo(attrB.valList.get(i)) < 0){
					 indices.add(i);
					}
				}
				else{	
					if(att.valList.get(i).compareTo(operandB.literal) < 0){
					 indices.add(i);
					}
				}
			} else if (o == o.geq) {
				if(operandB.type.equals("Attribute")){
					if(att.valList.get(i).compareTo(attrB.valList.get(i)) >= 0){
					indices.add(i);
					}
				}
			else{	
				if(att.valList.get(i).compareTo(operandB.literal) >= 0){
					indices.add(i);
					}
				}
			} else if (o == o.leq) {
				if(operandB.type.equals("Attribute")){
					if(att.valList.get(i).compareTo(attrB.valList.get(i)) <= 0){
					indices.add(i);
					}
				}
				else{	
					if(att.valList.get(i).compareTo(operandB.literal) <= 0){
					indices.add(i);
					}
				}
			}
			i++;
			}
		return indices;
	}
	
	public Attribute getAttribute(Table tbl, String nameOfAttr){
		int attrListSize = tbl.listOfAttributes.size();
		int i = 0;
		while(i < attrListSize){
			if(tbl.listOfAttributes.get(i).name.equals(nameOfAttr)){
				return tbl.listOfAttributes.get(i);
			}
			i++;
		}
		return null;
	}
	
}
