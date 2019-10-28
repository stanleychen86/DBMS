import java.io.*;
import java.util.*;
import java.lang.Math;


public class Table {
	
	public String fname;
	public String name;
	public int attributeCount;
	public ArrayList<Attribute> listOfAttributes;
	
	ArrayList<Integer> primaryKey = new ArrayList<Integer>();
	
	public Table(){

	}
	
	public Table(String relName) {
		name = relName;
		fname = relName + ".db"; 
		File fi = new File(fname); // make file
		if (fi.exists())
			System.out.println("File created");
		this.listOfAttributes = new ArrayList<Attribute>();
		this.attributeCount = 0;
		this.primaryKey = new ArrayList<Integer>();
	}


	public Table(String relName, ArrayList<Attribute> a_list) {
		try {
		name = relName;
		fname = relName + ".db";
		listOfAttributes = a_list;
		attributeCount = a_list.size();
			
		PrintWriter writer = new PrintWriter(fname, "UTF-8");
	
		writer.println(relName);
		if(a_list != null) {
			for(Attribute e : a_list) {
				writer.println(e.name);
				writer.println(e.type);
							
				for(Literal f : e.valList) {
					writer.print(f.show() + ";");
				}			
				writer.println("ENDLIST;");
			}
		}
		
		writer.println("ENDTABLE");
		writer.close();
		}
		catch (Exception e) {
			System.out.println("An error occurred.");	
		}
		
	}
	
	public Table(String relName, ArrayList<Attribute> a, ArrayList<Integer> indices) {
		name = relName;
		attributeCount = a.size();
		fname = relName + ".db";
		
		listOfAttributes = a;
		ArrayList<Integer> delIndices = new ArrayList<Integer>();
		
			for(int i =0; i<this.max_height();i++) {
				if(!(indices.contains(i))) {
					delIndices.add(i);
				}
			}
		deleteIndices(delIndices);	
	}
	
	public Table(String relName, ArrayList<Attribute> a, ArrayList<Integer> indices, ArrayList<String> primary) {
		try {
		name = relName;
		attributeCount = a.size();
		fname = relName + ".db";
		listOfAttributes = a;
		attributeCount = a.size();
		
		for(String key : primary){
			primaryKey.add(listOfAttributes.indexOf(key));
		}
		
		PrintWriter writer = new PrintWriter(fname, "UTF-8");
		
		writer.println(relName);
		if(a!=null) {
		for(Attribute e : a) {
			writer.println(e.name);
			writer.println(e.type);	
			writer.println("ENDLIST;");
		}
	}	
		writer.println("ENDTABLE");	
		writer.close();
		}
		catch (Exception e) {
			System.out.println("An error occurred.");	
		}
	}
		
	public void setName(String newName) {
		name = newName;
		fname = newName + ".db";
	}
	
	public String get_name() {
		return this.name;
	}
	
	public int max_height() {
		int max_height = 0;
		for (int i = 0; i < listOfAttributes.size(); i++) {
			if (listOfAttributes.get(i).valList.size() > max_height) {
				max_height = listOfAttributes.get(i).valList.size();
			}
		}
	return max_height;
	}
	
	public void showTable() throws FileNotFoundException {
		for(int i = 0; i < listOfAttributes.size(); i++) {
			System.out.printf("%-50s", listOfAttributes.get(i).name);
		}	
		System.out.println();
		int attrValListSize = listOfAttributes.get(0).valList.size();
		int i = 0;
		while (i < attrValListSize) {
			for(Attribute a : listOfAttributes) {
				System.out.printf("%-50s", a.valList.get(i).show());
			}
			System.out.println();
			i++;
		}
	}
	
	public int countAttributes() throws FileNotFoundException {
	int count = 0;
		Scanner scan = new Scanner (new File (fname));
		while(scan.hasNextLine()) {
			if((!(scan.nextLine()).equals("ENDTABLE"))) {
				count++;
			}	 
		 }
		 int numAttr = (count-1)/3;

	return numAttr;
	}
	
	
	public final void insertrow(ArrayList<Literal> rowValues)
	{  						
		try {
			int attrCount = listOfAttributes.size();
			for(int i = 0; i< attrCount; ++i){
				listOfAttributes.get(i).append(rowValues.get(i));
			}
		}
		catch (Exception e) {
			System.out.println("An error occurred.");	
		}
	}
	
	public ArrayList<Literal> get_row(int index) {
		if (index >= 0 && index <= max_height()) {
			ArrayList<Literal> new_row = new ArrayList<Literal>();
			int attributeListSize = listOfAttributes.size();
			int i = 0;
			while(i < attributeListSize) {
				new_row.add(listOfAttributes.get(i).valList.get(index));
				i++;
			}
			return new_row;
		} else {
			System.out.println("Out of range error!");
			return new ArrayList<Literal>();
		}
	}
	
		public void createListofAttributes() throws FileNotFoundException{
		Scanner scan = new Scanner (new File (fname));
		int i = 0;
		
		while(scan.hasNextLine() && i < attributeCount) {
			String attributeName = scan.nextLine();
			String type = scan.nextLine();
			String contents = scan.nextLine();
			int numElements = 0;
			for(int ichar  = 0; ichar<contents.length(); ichar++) {
				if(contents.charAt(ichar) == ';') {
					numElements += 1;
				}
				
			}
			numElements -= 1;
		
			if(!(type.substring(0,7).equals("VARCHAR"))) {
				String[] contentsArray = new String[numElements];
				contentsArray = contents.split(";");
				int[] intArray = new int[numElements];
				for(int index = 0; index<numElements; index++) {
					intArray[index] = Integer.parseInt(contentsArray[index]);
				}				
				ArrayList<Literal> valList = new ArrayList<Literal>();				
				for(int index = 0; index<numElements;index++) {
					Literal addToValues = new Literal(intArray[index]);
					valList.add(addToValues);
				}
				Attribute b =new Attribute(attributeName, valList, type);
				listOfAttributes.add(b);
			}
			else {
				String vcLen = type.substring(8, type.length()-1);
				int length = Integer.parseInt(vcLen);
				String[] contentsArray = new String[numElements];
				contentsArray = contents.split(";");
				ArrayList<Literal> valList = new ArrayList<Literal>();
				for(int index = 0; index<numElements; index++) {
					Literal addToValues = new Literal(contentsArray[index], length);
					valList.add(addToValues);
				}				
				Attribute a =new Attribute(attributeName, valList, type);
				listOfAttributes.add(a);
			}	
			i++;	
		}		
	}
	
	public final void deleteIndices(ArrayList<Integer> indices) {
		for(int i = indices.size()-1; i >= 0 ; --i){
			for(int j = 0; j < attributeCount; ++j) {
				listOfAttributes.get(j).deleteAtIndex(indices.get(i));
			}	
		}	
	}
}
