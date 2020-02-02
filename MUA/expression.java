package src.mua;

import java.util.*;

public class expression {
	private Vector<exprMember> members = new Vector<exprMember>();
	
	expression(String expr){
		exprMember newMem;
		expr = expr.trim();
		while(!expr.equals("")) {
			if(Character.isDigit(expr.charAt(0))){
				if(expr.length() == 1) {
					newMem = new exprMember(expr.substring(0, 1), "number");
					members.add(newMem);
					expr = expr.substring(1).trim();
				}else {
					for(int i=1; i<expr.length(); i++) {
						if(!Character.isDigit(expr.charAt(i)) && expr.charAt(i) != '.' || i == expr.length()-1) {
							if(i == expr.length()-1) {
								newMem = new exprMember(expr.substring(0, i+1), "number");
							}else {
								newMem = new exprMember(expr.substring(0, i), "number");
							}
							members.add(newMem);
							expr = expr.substring(i).trim();
							break;
						}
					}
				}
			}else if(expr.charAt(0) == '-' && (members.size() == 0 || members.get(members.size()-1).getType() == "operator")) {
				//judge whether the '-' represents negative or minus
				//if it is negative, do it first
				for(int i=1; i<expr.length(); i++) {
					if(!Character.isDigit(expr.charAt(i))) {
						newMem = new exprMember(expr.substring(0, i), "number");
						members.add(newMem);
						expr = expr.substring(i).trim();
						break;
					}
				}
			}else {
				newMem = new exprMember(expr.substring(0, 1), "operator");
				members.add(newMem);
				expr = expr.substring(1).trim();
			}
		}
	}

	//calculate the expression
	public String calculate() {
		exprMember newMem;
		double operand1, operand2;
		String result;
		//do * / %
		for(int i=0; i<members.size(); i++) {
			if(members.get(i).getName().equals("*")) {
				operand1 = members.get(i-1).getValue();
				operand2 = members.get(i+1).getValue();
				newMem = new exprMember((operand1*operand2)+"", "number");
				members.setElementAt(newMem, i-1);
				members.removeElementAt(i+1);
				members.removeElementAt(i);
				i--;
			}else if(members.get(i).getName().equals("/")) {
				operand1 = members.get(i-1).getValue();
				operand2 = members.get(i+1).getValue();
				newMem = new exprMember((operand1/operand2)+"", "number");
				members.setElementAt(newMem, i-1);
				members.removeElementAt(i+1);
				members.removeElementAt(i);
				i--;
			}else if(members.get(i).getName().equals("%")) {
				operand1 = members.get(i-1).getValue();
				operand2 = members.get(i+1).getValue();
				newMem = new exprMember((operand1%operand2)+"", "number");
				members.setElementAt(newMem, i-1);
				members.removeElementAt(i+1);
				members.removeElementAt(i);
				i--;
			}
		}
		//do + -
		for(int i=0; i<members.size(); i++) {
			if(members.get(i).getName().equals("+")) {
				operand1 = members.get(i-1).getValue();
				operand2 = members.get(i+1).getValue();
				newMem = new exprMember((operand1+operand2)+"", "number");
				members.setElementAt(newMem, i-1);
				members.removeElementAt(i+1);
				members.removeElementAt(i);
				i--;
			}else if(members.get(i).getName().equals("-")) {
				operand1 = members.get(i-1).getValue();
				operand2 = members.get(i+1).getValue();
				newMem = new exprMember((operand1-operand2)+"", "number");
				members.setElementAt(newMem, i-1);
				members.removeElementAt(i+1);
				members.removeElementAt(i);
				i--;
			}
		}
		result = members.get(0).getName();
		return result;
	}
	
	
}
