
public class Operand {
	
	String nameOfAttr;
	Literal literal;
	
	String type;
	
	public Operand(){
		
	}
	
	public Operand(String attr){
		this.nameOfAttr = attr;
		this.type = "Attribute";
	}
	
	public Operand(Literal lit){
		this.literal = lit;
		this.type ="Literal";
	}
	
}
