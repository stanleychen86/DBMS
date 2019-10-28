import java.io.File;
import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.Scanner;
import static java.lang.System.out;


public class DBtest {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		DBMSEngine engine = new DBMSEngine();
		QueryOps q = new QueryOps();
		
		System.out.println("Make a list of literals called nums.");
		ArrayList<Literal> nums = new ArrayList<Literal>();
		nums.add(new Literal(0));
		nums.add(new Literal(10));
		System.out.println("Make a list of literals called varchars.");
		ArrayList<Literal> varchars = new ArrayList<Literal>();
		varchars.add(new Literal("Hi",2));
		varchars.add(new Literal("World",5));
		
		Attribute a1 = new Attribute("numbers", nums, "INTEGER");
		Attribute a2 = new Attribute("words", varchars, "VARCHAR");
		
		for (Literal l : nums) {
			System.out.print(l.showType() + " ");
			System.out.println(l.show());
		}
		
		for (Literal l : varchars) {
			System.out.print(l.showType() + " ");
			System.out.println(l.show());
		}
		
		a1.append(new Literal("THISSHOULDFAIL", 14)); // should see error message
		a2.append(new Literal("THISSHOULDWORK", 14)); // should work
	
		/*The following test cases come from the provided animals.db example*/
		
		// Make our lists of literals for names, kinds, and ages
		ArrayList<Literal> names = new ArrayList<Literal>();
		ArrayList<Literal> kinds = new ArrayList<Literal>();
		ArrayList<Literal> ages = new ArrayList<Literal>();
		
		// Create the literals and add them to their respective lists
		Literal l1 = new Literal("Joe",3);
		Literal l2 = new Literal("Spot",4);
		Literal l3 = new Literal("Snoopy",6);
		Literal l4 = new Literal("Tweety",6);
		Literal l5 = new Literal("Joe",3);
		names.add(l1);
		names.add(l2);
		names.add(l3);
		names.add(l4);
		names.add(l5);
		
		kinds.add(new Literal("cat",3));
		kinds.add(new Literal("dog",3));
		kinds.add(new Literal("dog",3));
		kinds.add(new Literal("bird",4));
		kinds.add(new Literal("bird",4));
		
		ages.add(new Literal(4));
		ages.add(new Literal(10));
		ages.add(new Literal(3));
		ages.add(new Literal(1));
		ages.add(new Literal(2));
		
		// Create attributes
		Attribute name = new Attribute("name",names, "VARCHAR");
		Attribute kind = new Attribute("kind",kinds, "VARCHAR");
		Attribute age = new Attribute("age",ages, "INTEGER");
		
		// Put the attributes in a list of attributes
		ArrayList<Attribute> aList = new ArrayList<Attribute>();
		aList.add(name);
		aList.add(kind);
		aList.add(age);
		
		ArrayList<Attribute> animalPrimaries = new ArrayList<Attribute>();
		animalPrimaries.add(name);
		animalPrimaries.add(kind);
		
		// Demonstrating create, open, and show operations
		Table animalTable = engine.create("animals", aList, animalPrimaries); // make animals.db
		engine.open("animals");
		engine.show("animals");
		
		ArrayList<Literal> insertRowTest = new ArrayList<Literal>();
		insertRowTest.add(new Literal("Juneau",6));
		insertRowTest.add(new Literal("dog",3));
		insertRowTest.add(new Literal(7));
		
		engine.insert(animalTable, insertRowTest); // should add Juneau
		System.out.println("\n\n\n");
		engine.show("animals");
	
		// testing projection
		ArrayList<String> str = new ArrayList<String>();
		str.add("name");
		str.add("age");
		System.out.println("\nProjection test on names and ages");
		Table t = q.projectionOp(animalTable, str);		//test projection onto new table
		t.showTable(); // should only print names and ages
		engine.tables.add(t);
		
		Table t2 = q.copy(animalTable);
		engine.tables.add(t2);
		engine.show(t2.name);
	
		Table t3 = q.unions(animalTable, t2);
		
		engine.tables.add(t3);
		engine.show(t3.name); // should show both tables combined
		
		Table dupAnimals = engine.create("animals", aList, animalPrimaries);
		
		engine.insert(animalTable, dupAnimals); // making a copy of the table and doing insert
		
		engine.show("animals");
		
		Operation equals = new Operation("==");
		Operation greater = new Operation(">");
		Literal kindLit = new Literal("kind",3);
		Literal ageLit = new Literal(10);
		Comparison kindEq = new Comparison(new Operand("kind"), equals.o, new Operand(new Literal("dog",3)));
		Comparison ageOverTen = new Comparison(new Operand("age"), greater.o, new Operand(ageLit));
		ArrayList<Comparison> comps1 = new ArrayList<Comparison>();
		comps1.add(kindEq);
		Conjunction conj = new Conjunction(comps1);
		ArrayList<Conjunction> conjs1 = new ArrayList<Conjunction>();
		conjs1.add(conj);
		Condition kindEqDog = new Condition(conjs1); // kind == "dog"
		
		ArrayList<Comparison> comps2 = new ArrayList<Comparison>();
		comps2.add(ageOverTen);
		Conjunction conj2 = new Conjunction(comps2);
		ArrayList<Conjunction> conjs2 = new ArrayList<Conjunction>();
		conjs2.add(conj2);
		Condition overTenCond = new Condition(conjs2);
		
		Table dogTable = q.selectionOp(animalTable, kindEqDog);
		
		dogTable.showTable();
		engine.tables.add(dogTable);
		
		// Should be empty as no dogs are older than 10
		Table dogsOverTen = q.selectionOp(animalTable, overTenCond); 
		engine.tables.add(dogsOverTen);
		//dogsOverTen.showTable();
		// Testing write
		engine.write(t.name);
		engine.write(dogTable.name);
		engine.write(dogsOverTen.name);
		
		//Testing rename, change age to years
		ArrayList<String> newNames = new ArrayList<String>();
		newNames.add("nombre");
		newNames.add("species");
		newNames.add("years");
		Table renamedTable = q.renaming(animalTable, newNames);
		System.out.println("\n\nName is now Nombre! Kind is now Species! Age is now Years!");
		renamedTable.showTable();
		//Testing delete-delete all dogs
		engine.delete(animalTable, kindEqDog);
		System.out.println("\n\nWho let the dogs out of the table?");
		animalTable.showTable();
		
		//Testing difference
		ArrayList<Attribute> difference1 = new ArrayList<Attribute>();
		ArrayList<Attribute> difference2 = new ArrayList<Attribute>();
		
		ArrayList<Literal> diffLits = new ArrayList<Literal>();
		for (int i = 0; i < ages.size(); i++) {
			diffLits.add(new Literal(ages.get(i).intVal + 3));
		}
		difference1.add(new Attribute("diffages", ages, "INTEGER"));
		difference2.add(new Attribute("diffages", diffLits, "INTEGER"));
		
		Table diffTest1 = new Table("dt1", difference1);
		Table diffTest2 = new Table("dt2", difference2);
		
		System.out.println("\n\nDiff Table 1");
		diffTest1.showTable();
		System.out.println("\n\nDiff Table 2");
		diffTest2.showTable();
		System.out.println("\n\nDifference test");
		q.difference(diffTest1, diffTest2).showTable();
		
		System.out.println("\n\nDiff Table 1");
		diffTest1.showTable();
		System.out.println("\n\nDiff Table 2");
		diffTest2.showTable();
		System.out.println("\n\nProduct of Diff Table 1 and Diff Table 2");
		q.product(diffTest1, diffTest2).showTable();
		// Testing update
		ArrayList<Attribute> changeThese = new ArrayList<Attribute>();
		changeThese.add(age);
		ArrayList<Literal> newAges = new ArrayList<Literal>();
		newAges.add(new Literal(12));
		Comparison comp3 = new Comparison(new Operand("age"), equals.o, new Operand(new Literal(10)));
		ArrayList<Comparison>updateComparisons = new ArrayList<Comparison>();
		updateComparisons.add(comp3);
		Conjunction conj3 = new Conjunction(updateComparisons);
		ArrayList<Conjunction> updateConjunctions = new ArrayList<Conjunction>();
		updateConjunctions.add(conj3);
		Condition cond3 = new Condition(updateConjunctions);
		
		dogTable.showTable();
		engine.update(dogTable, changeThese, newAges, cond3); // this actually doesn't work
		System.out.println("\n\nAll dogs aged 10 are now 12");
		engine.show(dogTable.name);
		
		
		
		// Testing exit-should delete Projection Table and other tables made using queries
		//engine.exit();
	}
	
}
