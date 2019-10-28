import java.io.*;
import java.util.*;
import javax.swing.*;

public class DBMSEngine{


ArrayList<Table> tables = new ArrayList<Table>();
	


public final Table open(String relation_name) throws FileNotFoundException{
	
	Table t = new Table(relation_name);
	tables.add(t);
	return t;
	
}





public final void close(String tablename) throws FileNotFoundException{
	
	
	write(tablename);
	
	
}


public final void write(String tablename){
	
	boolean isFound = false;
	File file = new File(".");
	File folder = new File(file.getAbsolutePath());
	Table writeTable = new Table();
	
	try{
	
	for(Table t: tables) {
		if(t.name.equals(tablename)) {
			
			writeTable = t;
			isFound = true;
		}
		
	}
	
	if(isFound) {
		
		for(File f : folder.listFiles()) {	
			if(f.getName().equals(tablename+".db")) {		
				f.delete();	
			}
		}

		PrintWriter writer = new PrintWriter(tablename + ".db", "UTF-8");
		writer.println(writeTable.name);

		ArrayList<Attribute> listOfAttributes = writeTable.listOfAttributes;
		for (int i = 0; i < listOfAttributes.size(); i++) {
			Attribute a = listOfAttributes.get(i);
			writer.println(a.name);
			writer.println(a.type);
			for(Literal l : a.valList) {	
				writer.print(l.show() + ";");	
			}
			writer.print("endoflist;\n");
		}
	
		writer.println("endoftable");
		writer.close();
		return;
} else {
		System.out.println("table not found");
	}

	}
	
	catch(FileNotFoundException fnf){
		
		System.out.println("couldnt find file"); // stack overflow and 'access denied' fnf exception
		
	}
	
	catch(UnsupportedEncodingException uex){
		
	System.out.println("Encoding not supported");
	
	}


	
}

public final void exit(){
	
	File f = new File(".");
	File fldr = new File(f.getAbsolutePath());
	
	for(File fi: fldr.listFiles()) {
		
		if(fi.getName().contains(" Table")) {
			fi.delete();
		}
	}
	
	System.exit(0);
	
	
	
}

public final void show(String tablename) throws FileNotFoundException{
	
	for(Table t: tables) {
		
		if(t.name.equals(tablename)) {
			
			t.showTable();
			break;
		}
		
		
	}
	
	
	
}


public final Table create(String title, ArrayList<Attribute> attribs, ArrayList<Attribute> primaryKey ) throws FileNotFoundException{
	
		Table t = new Table(title, attribs);
		tables.add(t);	
		return t;

}

public final Table create(Table t){
	
	tables.add(t);
	return t;
	
}


public final void update(Table t, ArrayList<Attribute> attribs, ArrayList<Literal> literals, Condition cond) throws FileNotFoundException{
	
	
	for(int i=0; i<attribs.size(); ++i) {
		
		for(int j=0; j<attribs.get(i).valList.size(); ++j) {
			System.out.println("updating");
			if(cond.indices.contains(j) || attribs.get(i).valList.contains(attribs.get(i).valList.get(j))){
				attribs.get(i).valList.set(j,literals.get(i));
			}
		}
		
		
	}
	
	
}

public final void insert(Table t, ArrayList<Literal> literals) throws FileNotFoundException{
	
	
	t.insertrow(literals);
	write(t.name);
	return;
}

public final void insert(Table fromTable, Table toTable) throws FileNotFoundException {

	int index = tables.indexOf(toTable);
	ArrayList<Literal> litties = new ArrayList<Literal>();
	int maxSize = fromTable.listOfAttributes.get(0).valList.size();
	for(int i=0 ; i < maxSize; ++i) {
		for(int j=0 ; j < fromTable.attributeCount; ++j) {
			switch (fromTable.listOfAttributes.get(j).valList.get(i).type) {
			case "INTEGER":
				litties.add(new Literal(fromTable.listOfAttributes.get(j).valList.get(i).intVal));
				break;
			default:
				litties.add(new Literal(fromTable.listOfAttributes.get(j).valList.get(i).vcVal, fromTable.listOfAttributes.get(j).valList.get(i).length));
				break;
			}
		}
		insert(toTable, litties);
		litties.clear();
	}
	
	tables.set(index, toTable);
	System.out.println("before write");
	write(toTable.name);
	System.out.println("after write");
}



public final void delete(Table t, Condition condition) throws FileNotFoundException {
	t.deleteIndices(condition.indices);
	}

}
