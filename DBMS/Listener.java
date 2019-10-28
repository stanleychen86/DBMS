import java.io.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.lang.*;
import java.util.*;

public class Listener extends GrammarBaseListener {

	private enum typeOfExpr{Atomic, Select, Project, Union, Rename, Product, Difference};
	private typeOfExpr expressionType;

	DBMSEngine engine = new DBMSEngine();
	QueryOps queryOps = new QueryOps();

	@Override
	public void exitQuery(GrammarParser.QueryContext ctx) {

		ctx.table = ctx.expr().table;
		ctx.table.setName(ctx.relation_name().relation_name);
		engine.tables.add(ctx.table);
	}

	@Override
	public void exitRelation_name(GrammarParser.Relation_nameContext ctx) {

		ctx.relation_name = ctx.identifier().name;
		
	}

	@Override
	public void enterIdentifier(GrammarParser.IdentifierContext ctx) {

		ctx.name = ctx.getText();

	}

	@Override
	public void exitExpr(GrammarParser.ExprContext ctx) {
		switch (expressionType){
			case Atomic :	ctx.table = ctx.atomic_expr().table;
							break;
			case Select :	ctx.table = ctx.selection().table;
							break;
			case Project :	ctx.table = ctx.projection().table;
							break;
			case Union :	ctx.table = ctx.union().table;
							break;
			case Rename :	ctx.table = ctx.renaming().table;
							break;
			case Product :	ctx.table = ctx.product().table;
							break;
			case Difference :	ctx.table = ctx.difference().table;
							break;
		}
	}

	@Override
	public void enterAtomic_expr(GrammarParser.Atomic_exprContext ctx) {
		
		expressionType = typeOfExpr.Atomic;
		
	}

	@Override
	public void exitAtomic_expr(GrammarParser.Atomic_exprContext ctx) {
		
		try{
			ctx.relation_name = ctx.relation_name().relation_name;
			ArrayList<Table> tables = engine.tables;
			for (int i = 0; i < tables.size(); i++) {
				Table t = tables.get(i);
				if (!t.name.equals(ctx.relation_name))
					continue;
				ctx.table = t;
			}
		}
		catch(NullPointerException np){
			if (expressionType == typeOfExpr.Atomic) {
				ctx.table = ctx.expr().atomic_expr().table;
			} else if (expressionType == typeOfExpr.Select) {
				ctx.table = ctx.expr().selection().table;
			} else if (expressionType == typeOfExpr.Project) {
				ctx.table = ctx.expr().projection().table;
			} else if (expressionType == typeOfExpr.Union) {
				ctx.table = ctx.expr().union().table;
			} else if (expressionType == typeOfExpr.Rename) {
				ctx.table = ctx.expr().renaming().table;
			} else if (expressionType == typeOfExpr.Product) {
				ctx.table = ctx.expr().product().table;
			} else if (expressionType == typeOfExpr.Difference) {
				ctx.table = ctx.expr().difference().table;
			}
		}
	}

	@Override
	public void exitSelection(GrammarParser.SelectionContext ctx) {	
		try {
			Table table = new Table();
			switch (ctx.atomic_expr().relation_name) {
			case "":
				ArrayList<Table> tables = engine.tables;
				for (int i = 0; i < tables.size(); i++) {
					Table t = tables.get(i);
					if (!t.name.equals(ctx.atomic_expr().relation_name))
						continue;
					table = t;
				}
				break;
			default:
				table = ctx.atomic_expr().table;
				break;
			}
			Table t2 = queryOps.selectionOp(table, ctx.condition().condition);
			t2.setName(table.name);
			ctx.table = t2;
			expressionType = typeOfExpr.Select;
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
	}

	@Override
	public void exitCondition(GrammarParser.ConditionContext ctx) {

		
		ArrayList<Conjunction> conjunctions = new ArrayList<Conjunction>();
		for(GrammarParser.ConjunctionContext conj : ctx.conjunction()){
			conjunctions.add(conj.conjunction);
		}

        ctx.condition = new Condition(conjunctions);
	}

	@Override
	public void exitConjunction(GrammarParser.ConjunctionContext ctx) {
		
		
		ArrayList<Comparison> comparisons = new ArrayList<Comparison>();
		for(GrammarParser.ComparisonContext comp : ctx.comparison()){
			comparisons.add(comp.comparison);
		}

        ctx.conjunction = new Conjunction(comparisons);
	}

	@Override
	public void exitComparison(GrammarParser.ComparisonContext ctx) {
			
		ctx.comparison = new Comparison(ctx.operand().get(0).operand, new Operation(ctx.getChild(1).getText()).o, ctx.operand().get(1).operand);
		
	}

	@Override
	public void exitOperand(GrammarParser.OperandContext ctx) {

		
		boolean isLit = (ctx.getText().charAt(0) == '"' || Character.isDigit(ctx.getText().charAt(0)));
		
		if(!isLit) {
			ctx.operand = new Operand(ctx.attribute_name().name);
		} else
			ctx.operand = new Operand(ctx.literal().literal);
	}

	@Override
	public void exitAttribute_name(GrammarParser.Attribute_nameContext ctx) {
		ctx.name = ctx.identifier().name;	
	}

	@Override
	public void exitLiteral(GrammarParser.LiteralContext ctx) {

		
		switch (ctx.getText().charAt(0)) {
		case '"':
			ctx.literal = new Literal(ctx.getText().substring(1,ctx.getText().length()-1) , ctx.getText().length()-2);
			break;
		default:
			ctx.literal = new Literal(Integer.parseInt(ctx.getText()));
			break;
		}
	}

	@Override
	public void exitProjection(GrammarParser.ProjectionContext ctx) {
			try {
				Table table = new Table();
				switch (ctx.atomic_expr().relation_name) {
				case "":
					ArrayList<Table> tables = engine.tables;
					for (int i = 0; i < tables.size(); i++) {
						Table t = tables.get(i);
						if (!t.name.equals(ctx.atomic_expr().relation_name))
							continue;
						table = t;
					}
					break;
				default:
					table = ctx.atomic_expr().table;
					break;
				}		
				Table t2 = queryOps.projectionOp(table, ctx.attribute_list().attributes);
				t2.setName(table.name);
				ctx.table = t2;

				expressionType = typeOfExpr.Project;
			} catch (IOException e) {
				 
				e.printStackTrace();
			}		
	}

	@Override
	public void exitAttribute_list(GrammarParser.Attribute_listContext ctx) {
		for (int i = 0; i < ctx.attribute_name().size(); ++i) {
			ctx.attributes.add(ctx.attribute_name().get(i).name);
		}	
	}


	@Override
	public void exitRenaming(GrammarParser.RenamingContext ctx) {
		try {
			ctx.table = queryOps.renaming(ctx.atomic_expr().table, ctx.attribute_list().attributes);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		expressionType = typeOfExpr.Rename;
	}

	@Override
	public void exitUnion(GrammarParser.UnionContext ctx) {
		try {
			ctx.table = queryOps.unions(ctx.atomic_expr().get(0).table, ctx.atomic_expr().get(1).table);
			expressionType = typeOfExpr.Union;
		} catch (UnsupportedEncodingException e) {
			 
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			 
			e.printStackTrace();
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
	}

	@Override
	public void exitDifference(GrammarParser.DifferenceContext ctx) {
		try {
			ctx.table = queryOps.difference(ctx.atomic_expr().get(0).table, ctx.atomic_expr().get(1).table);
			expressionType = typeOfExpr.Difference;
		} catch (UnsupportedEncodingException e) {
			 
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			 
			e.printStackTrace();
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
	}

	@Override
	public void exitProduct(GrammarParser.ProductContext ctx) {

		try {
			ctx.table = queryOps.product(ctx.atomic_expr().get(0).table, ctx.atomic_expr().get(1).table);
			expressionType = typeOfExpr.Product;
		} catch (UnsupportedEncodingException e) {
			 
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			 
			e.printStackTrace();
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
	}

	@Override
	public void exitOpen_cmd(GrammarParser.Open_cmdContext ctx) {

		try {
			engine.open(ctx.relation_name().relation_name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exitClose_cmd(GrammarParser.Close_cmdContext ctx) {

		try {
			engine.close(ctx.relation_name().relation_name);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exitWrite_cmd(GrammarParser.Write_cmdContext ctx) {

		ctx.relation_name = ctx.relation_name().relation_name;

		try {
			engine.write(ctx.relation_name);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			 
			e.printStackTrace();
		}

		
	}

	@Override
	public void enterExit_cmd(GrammarParser.Exit_cmdContext ctx) {

		engine.exit();

	}

	@Override
	public void exitShow_cmd(GrammarParser.Show_cmdContext ctx) {

		try {
			engine.show(ctx.atomic_expr().relation_name);
		} catch (FileNotFoundException e) {
			 
			e.printStackTrace();
		}
	}

	@Override
	public void exitCreate_cmd(GrammarParser.Create_cmdContext ctx) {

		ctx.relation_name = ctx.relation_name().relation_name;
		
		ArrayList<Attribute> ats = new ArrayList<Attribute>();
		ArrayList<Attribute> primary_key = new ArrayList<Attribute>();
		for(int i=0 ; i<ctx.typed_attribute_list().attribute_names.size(); ++i){
			ats.add(new Attribute(ctx.typed_attribute_list().attribute_names.get(i),ctx.typed_attribute_list().types.get(i)));
		}
		for(int i=0 ; i<ctx.attribute_list().attributes.size(); ++i){
			primary_key.add(new Attribute(ctx.attribute_list().attributes.get(i)));
		}
		
		ctx.table = engine.create(ctx.relation_name,ats,primary_key);

		
	}

	@Override
	public void exitInsert_cmd(GrammarParser.Insert_cmdContext ctx) {
		Table table = new Table();
		ArrayList<Table> tables = engine.tables;
		for (int i = 0; i < tables.size(); i++) {
			Table t = tables.get(i);
			if (!t.name.equals(ctx.relation_name().relation_name))
				continue;
			table = t;
		}
		int index=-1;
		for(int i = 0; i< engine.tables.size(); ++i){
			if (!engine.tables.get(i).name.equals(ctx.relation_name().relation_name))
				continue;
			index = i; 
		}
		
		try{
			ctx.literal();
			ArrayList<Literal> literals = new ArrayList<Literal>();
			for(GrammarParser.LiteralContext lc : ctx.literal()) {
				literals.add(lc.literal);
			}
			engine.insert(table, literals);
		}
		catch(IndexOutOfBoundsException e){
			try{
			if (expressionType == typeOfExpr.Atomic) {
				engine.insert(ctx.expr().atomic_expr().table, table);
			} else if (expressionType == typeOfExpr.Select) {
				engine.insert(ctx.expr().selection().table, table);
			} else if (expressionType == typeOfExpr.Project) {
				engine.insert(ctx.expr().projection().table, table);
			} else if (expressionType == typeOfExpr.Union) {
				engine.insert(ctx.expr().union().table, table);
			} else if (expressionType == typeOfExpr.Rename) {
				engine.insert(ctx.expr().renaming().table, table);
			} else if (expressionType == typeOfExpr.Product) {
				engine.insert(ctx.expr().product().table, table);
			} else if (expressionType == typeOfExpr.Difference) {
				engine.insert(ctx.expr().difference().table, table);
			}
			}
			catch(Exception e2){}
		} catch (UnsupportedEncodingException e) {
			 
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			 
			e.printStackTrace();
		}
	}


	@Override
	public void exitTyped_attribute_list(GrammarParser.Typed_attribute_listContext ctx) {

		for (int i = 0; i < ctx.attribute_name().size(); ++i) {
			ctx.attribute_names.add(ctx.attribute_name().get(i).name);
			ctx.types.add(ctx.type().get(i).type);
		}

		
	}

	@Override
	public void enterType(GrammarParser.TypeContext ctx) {

		ctx.type = ctx.getText();

	}

	@Override
	public void exitType(GrammarParser.TypeContext ctx) {

		int length = -1;
		if (ctx.integer() instanceof GrammarParser.IntegerContext) {
			length = ctx.integer().value;

		}

	}

	@Override
	public void enterInteger(GrammarParser.IntegerContext ctx) {

		ctx.value = Integer.parseInt(ctx.getText());

	}
}
