public class Operation {
	public enum operation {
		eq, neq, gthan, lthan, leq, geq
	}
	
	operation o;
	
	Operation(String s){
		
		if (s.equals("==")) {
			o = operation.eq;
		} else if (s.equals("!=")) {
			o = operation.neq;
		} else if (s.equals(">")) {
			o = operation.gthan;
		} else if (s.equals("<")) {
			o = operation.lthan;
		} else if (s.equals(">=")) {
			o = operation.geq;
		} else if (s.equals("<=")) {
			o = operation.leq;
		}

		
	}


}
