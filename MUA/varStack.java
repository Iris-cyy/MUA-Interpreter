package src.mua;

import java.util.*;

//the stack to store vars
public class varStack {	
	private Vector<variable> variables = new Vector<variable>();

	//return the number of vars in a stack
	public int size() {
		return variables.size();
	}
	
	//search in the stack to judge whether a word is the name of a variable
	public String isname(String name) {
		int found = 0;
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)){
				found = 1;
				break;
			}
		}
		if(found == 0) {
			return "false";
		}else {
			return "true";
		}
	}
	
	//get the type of a certain variable
	public String getType(String name) {
		String type = "";
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)) {
				type = variables.get(i).getType();
			}
		}
		return type;
	}
	
	//add a new variable to stack
	public void add(variable var) {
		variable new_var = new variable(var);
		variables.addElement(new_var);
	}
	
	public void add(String name) {
		if(isname(name) == "false") {
			variable new_var = new variable(name);
			variables.addElement(new_var);
		}
	}
	
	public void add(String name, double value) {
		int found = 0;
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)){
				variables.get(i).changeType("number");
				variables.get(i).changeNumValue(value);
				found = 1;
				break;
			}
		}
		if(found == 0) {
			variable new_var = new variable(name, value);
			variables.addElement(new_var);
		}
	}
	
	public void add(String name, boolean value) {
		int found = 0;
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)){
				variables.get(i).changeType("boolean");
				variables.get(i).changeBoolValue(value);
				found = 1;
				break;
			}
		}
		if(found == 0) {
			variable new_var = new variable(name, value);
			variables.addElement(new_var);
		}
	}
	
	public void add(String name, String value) {
		int found = 0;
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)){
				if(value.trim().charAt(0) == '[') {
					variables.get(i).changeType("list");
					variables.get(i).changeListStr(value);
				}else {
					variables.get(i).changeType("word");
					variables.get(i).changeWordName(value);
				}
				
				found = 1;
				break;
			}
		}
		if(found == 0) {
			variable new_var = new variable(name, value);
			variables.addElement(new_var);
		}
	}
	
	//remove a certain variable from the stack  according to its name
	public void remove(String name) {
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)) {
				variables.removeElementAt(i);
			}
		}
	}
	
	//get the value of a variable according to its name
	public String getValue(String name) {
		String result = "";
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)) {
				if(variables.get(i).getType().equals("number")) {
					result = getNumValue(name) + "";
				}else if(variables.get(i).getType().equals("bool")){
					if(getBoolValue(name)) {
						result = "true";
					}else {
						result = "false";
					}
				}else if(variables.get(i).getType().equals("word")) {
					result = getWordName(name);
				}else if(variables.get(i).getType().contentEquals("list")) {
					result = getListStr(name);
				}
			}
		}
		return result;
	}
	
	//eg. make "a 3, type is number
	//if type is number, get value
	public double getNumValue(String name) {
		double result = 0;
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)) {
				result = variables.get(i).getNumValue();
			}
		}
		return result;
	}
	
	//eg. make "a true, type is bool
	//if type is bool, get value
	public boolean getBoolValue(String name) {
		boolean result = false;
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)) {
				result = variables.get(i).getBoolValue();
			}
		}
		return result;
	}
	
	//eg. make "b "a, type of b is word
	public String getWordName(String name) {
		String result = "";
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)) {
				result = variables.get(i).getWordName();
			}
		}
		return result;
	}
	
	public String getListStr(String name) {
		String result = "";
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)) {
				result = variables.get(i).getListStr();
			}
		}
		return result;
	}
	
	//get a variable in the stack according to its index
	public variable get(int i) {
		return variables.get(i);
	}

	//get a variable in the stack according to its name
	public variable find(String name) {
		variable ret = null;
		for(int i=0; i<variables.size(); i++) {
			if(variables.get(i).getName().equals(name)) {
				ret = variables.get(i);
			}
		}
		return ret;
	}
	
	public void removeElementAt(int i) {
		variables.removeElementAt(i);
	}
}

