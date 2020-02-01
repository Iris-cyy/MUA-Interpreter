package src.mua;

public class exprMember {
	private String name;
	private String type; //number, operator
	private double value;
	
	exprMember(String memname, String memtype){
		name = memname;
		type = memtype;
		if(type.equals("number")) {
			value = Double.valueOf(name);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public double getValue() {
		return value;
	}
}
