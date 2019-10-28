public class Literal implements Comparable<Literal> {
	
	//INTEGERs
	int intVal;
	
	//VARCHARs
	String vcVal;
	int length;
	String type;
	
	public Literal() {
	}
	
	public Literal(int num){ 
		this.intVal = num;
		this.type = "INTEGER";
	}
	
	public Literal(String str, int len){
		this.vcVal = str;
		this.length = len;
		this.type = "VARCHAR";
	}

	public boolean isINTEGER(){
		if (type == "INTEGER") {
			return true;
		}
		return false;
	}
	
	public boolean isVARCHAR(){
		if (type == "VARCHAR") {
			return true;
		}
		return false;
	}
	
		
	public String showType(){
		if(isVARCHAR())
			return "VARCHAR(" + length + ")";
		else
			return "INTEGER";
	}

	public String show(){
		if(isINTEGER())
			return intVal + "";
		else
			return vcVal;
	}
	
		public int compareTo(Literal l){
		if(!l.isINTEGER())
			return this.show().compareTo(l.show());
		else {
			if(intVal >= l.intVal) {
				if(intVal == l.intVal) {
					return 0;
				}
				else {
				return 1;
				}
			} else {
				return -1;
			}
		}
	}
	
}
