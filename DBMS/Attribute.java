import java.util.*;
import java.io.*;
import java.lang.*;


public class Attribute {
	
	String name;
	
	String type;

	ArrayList<Literal> valList = new ArrayList<Literal>(); 
	
	public Attribute(String name) {
		this.name = name;
	}
	
	public Attribute(String name, String type) {
		this.name = name;
		this.type = type;
		
		
	}

	public Attribute(String name, ArrayList<Literal> vals) { 
		this.name = name;
		
		this.valList = vals;	

	}
	
	public Attribute(String name, ArrayList<Literal> vals, String type) { 
		this.name = name;
		this.type = type;
		valList = vals;	
	}
	
	
	public void setName(String newName) {
		this.name = newName;
	}
	public String get_name() {
		return name;
	}
	
	public void showAttribute() {	
			System.out.println("Name of Attribute: " + name + " Type of Attribute: " + type);
	}
	
	public String toString() {
		return name + ": " + type;
	}
	
	public void deleteAtIndex(int i){
		valList.remove(i);
	}
	
	public void append(Literal newVal)
	{
		if(!type.equals("INTEGER") || !newVal.showType().equals("INTEGER")) {
			if(type.startsWith("VARCHAR") && newVal.showType().startsWith("VARCHAR")){
				valList.add(newVal);
			}
			else
				System.out.println("An error occurred.");
		} else {
			
			valList.add(newVal);
		}

	}

}
