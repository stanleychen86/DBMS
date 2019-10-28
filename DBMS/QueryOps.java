import java.util.*;
import java.io.*;

public class QueryOps {
        public QueryOps() {
        }

public Attribute getAttribute(Table t, String attributeName){
	int attrListSize = t.listOfAttributes.size();
	int i = 0;
	while(i < attrListSize) {
		String attrName = t.listOfAttributes.get(i).name;
		if(!attrName.equals(attributeName)) {
			i++;
		} else {
			return t.listOfAttributes.get(i);
		}
	}
	return null;
	}

public Table renaming(Table tab, ArrayList<String> names_new) {
        //renaming
        //: 'rename' '('attribute_list')' atomic_expr;
        Table newTable = copy(tab);
        int i = 0;
        while(i < tab.attributeCount) {
                String newName = names_new.get(i);
                newTable.listOfAttributes.get(i).setName(newName);
                i++;
        }
        return newTable;
}

public Table unions(Table table1, Table table2) {
        // unions
        // : atomic_expr '+' atomic_expr;
        if (table1.attributeCount != table2.attributeCount) {
                System.out.println("Error: Unequal number of attributes!");
                return null;
        } else {
                int count = 0;
                boolean matchAttributes = true;
                while (count < table1.attributeCount && matchAttributes) {
                        String attr_1_name = table1.listOfAttributes.get(count).get_name();
                        String attr_2_name = table2.listOfAttributes.get(count).get_name();
                        matchAttributes = (attr_1_name == attr_2_name);
                        count++;
                }
                if (!matchAttributes) {
                        System.out.println("Error: Non-matching attributes!");
                        return null;
                }
                else {
                        Table result = new Table();
                        Table temp = new Table();
                        result = copy(table1);
                        temp = copy(table2);
                        for (int i = 0; i < temp.max_height(); i++) {
                                boolean notFound = true;
                                ArrayList<Literal> r1 = temp.get_row(i);
                                for (int j = 0; j < result.max_height(); j++) {
                                        ArrayList<Literal> r2 = result.get_row(i);
                                        notFound = !(compareLiteralLists(r1,r2));
                                }
                                if (notFound) {
                                        result.insertrow(r1);
                                }
                        }
                    
					return result;      

				}
        }
 }

public Table product(Table table1, Table table2) {
        // product
        // : atomic_expr '*' atomic_expr;
	String viewName = "Product Table";
	
	ArrayList<Attribute> table1Attr =  new ArrayList<Attribute>();
	table1Attr = table1.listOfAttributes;
	ArrayList<Attribute> table2Attr = new ArrayList<Attribute>();
	table2Attr = table2.listOfAttributes;
	
	ArrayList<Attribute> productAtt = new ArrayList<Attribute>();
	
	for (int i = 0; i < table1Attr.size(); i++) {
		Attribute a = table1Attr.get(i);
		productAtt.add(new Attribute(a.name, a.type));
	}
	
	for (int i = 0; i < table2Attr.size(); i++) {
		Attribute a = table2Attr.get(i);
		productAtt.add(new Attribute(a.name, a.type));
	}
	
	Table productResult = new Table(viewName, productAtt);
	
	for(int i = 0; i<table1.listOfAttributes.size();i++) {
		for(int j = 0; j<table1Attr.get(i).valList.size();j++) {
			for(int k = 0; k<table2Attr.get(0).valList.size();k++) {
				productResult.listOfAttributes.get(i).valList.add(table1Attr.get(i).valList.get(j));
			}
		}
	}
	
	
	for(int i = 0;i< table2.listOfAttributes.size();i++) {
		for(int k = 0; k<table1Attr.get(0).valList.size();k++) {
			for(int j = 0; j<table2Attr.get(i).valList.size();j++) {
				productResult.listOfAttributes.get(i + table1.listOfAttributes.size()).valList.add(table2Attr.get(i).valList.get(j));
			}
		}
	}
	DBMSEngine e = new DBMSEngine();
	e.create(productResult);
	e.write(viewName);

	return productResult;
  }

public boolean compareLiteralLists(ArrayList<Literal> list1, ArrayList<Literal> list2) {
	if(list1.size()!=list2.size()) {
		return false;
	}
	for(int i = 0; i< list1.size();i++) {
		if(list1.get(i).compareTo(list2.get(i))!=0) {
			return false;
		}	
	}
	return true;
} 

public Table projectionOp(Table t, ArrayList<String> names) {
			ArrayList<Attribute> projectionOutput = new ArrayList<Attribute>();
			String tableName = "Projection Table";
			Table t2 = copy(t);
			for (int i = 0; i < names.size(); i++) {
				String s = names.get(i);
				if(getAttribute(t,s) != null){
					projectionOutput.add(getAttribute(t,s));
				}
			}
			Table projectionResult = new Table(tableName, projectionOutput);
			return projectionResult;
    }


public Table atomicExpr() {
		return null;
	}
	
public Table selectionOp(Table tab, Condition cond) {
		String tableName = "Selection Table";
		ArrayList<Integer> condIndices = cond.get_indices(tab);
		Table select =  new Table(tableName, copy(tab).listOfAttributes, condIndices);
		return select;
	}

public boolean compareArrayList(ArrayList<Literal> a, ArrayList<Literal> b) {
	        if(a.size()!=b.size()) {
	        	return false;
	        }
	        for(int i =0; i< a.size(); i++) {
		        if(a.get(i).compareTo(b.get(i)) != 0) {
	        		return false;
		        }
	        }
	        return true;
        }

public Table removeDupes(Table tab) {
	        Table compareTable = copy(tab);
	        Table copyTable = copy(tab);
	        ArrayList<Integer> dupIndices = new ArrayList<Integer>();
	        for(int i = 0; i < compareTable.listOfAttributes.size(); i++) {
	        }
	
	        for(int i =0; i< tab.max_height(); i++) {
		        ArrayList<Literal> row_i = new ArrayList<Literal>();
		        row_i = tab.get_row(i);
		        boolean isUnique = true;
		        for(int j = 0; j< compareTable.max_height();j++) {
			        ArrayList<Literal> row_j = new ArrayList<Literal>();
			        row_j = compareTable.get_row(j);
			        if(compareLiteralLists(row_i, row_j)) {
				        isUnique = false;
			        }
		        }
		compareTable.insertrow(row_i);
		if(!isUnique) {
			dupIndices.add(i);
		}
		
		
	}
	copyTable.deleteIndices(dupIndices);
	return copyTable;
	
}

public Table difference(Table attr1, Table attr2) {
        // difference
        // : atomic_expr '-' atomic_expr;
	ArrayList<Integer> delIndices = new ArrayList<Integer>();
	
	Table diffTable = copy(attr1);
	diffTable.setName("Difference Table");
	int t1ValueSize = attr1.max_height();
	int t2ValueSize = attr2.max_height();
	for(int i = 0; i< t1ValueSize; i++) {
		ArrayList<Literal> t1_row_i = attr1.get_row(i);
		boolean notInT2 = true;
		for(int j = 0; j < t2ValueSize; j++) {
			ArrayList<Literal> t2_row_j = attr2.get_row(j);
			if(compareArrayList(t1_row_i, t2_row_j)) {
				notInT2 = false;
			}
		}
		if(!notInT2) {
			delIndices.add(i);
		}
		
	}

	
	diffTable.deleteIndices(delIndices);
		
	DBMSEngine dbe = new DBMSEngine();
	
	dbe.create(diffTable);
	dbe.write(diffTable.name);	
	return diffTable;
}

public Table copy(Table mesa){	
	ArrayList<Attribute> attribs = new ArrayList<Attribute>();
		for( int x=0 ; x < mesa.listOfAttributes.size(); x++){	
			ArrayList<Literal>litties = new ArrayList<Literal>();	
			ArrayList<Literal> valList = mesa.listOfAttributes.get(x).valList;
			for (int i = 0; i < valList.size(); i++) {
				Literal a = valList.get(i);
				if(!a.type.startsWith("VARCHAR")) {
					litties.add(new Literal(a.intVal));
				} else {
					litties.add(new Literal(a.show(), a.length));
				}
			}
			attribs.add(new Attribute(mesa.listOfAttributes.get(x).get_name(), litties, mesa.listOfAttributes.get(x).type));		
		}
	
	Table dup = new Table(mesa.get_name() + "(dup)", attribs);
	return dup;
        }
}
