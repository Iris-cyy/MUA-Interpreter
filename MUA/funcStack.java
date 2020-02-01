package src.mua;

import java.io.IOException;
import java.util.*;

//the stack to store functions
public class funcStack {
	private static Vector<function> functions = new Vector<function>();
	
	//determine whether a word is a function name or not
	public boolean isFunc(String name) {
		for(int i=0; i<functions.size(); i++) {
			if(functions.get(i).getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	//add a function to function stack
	public void add(String name, String list) {
		String vars = ""; //variables
		String opers = ""; //operations
		list = list.substring(1, list.length()-1).trim(); //remove "[]"
		int comp = 0; //whether a list is complete
		int hasvar = 0; //whether variables are stored
		for(int i=0; i<list.length(); i++) {
			//read in a complete list
			if(list.charAt(i) == '[') {
				comp--;
			}else if(list.charAt(i) == ']') {
				comp++;
			}
			if(comp == 0 && hasvar == 1) {
				//enter operation list
				opers = list.substring(1, i).trim();
				list = list.substring(i+1).trim();
				break;
			}
			if(comp == 0 && hasvar == 0) {
				//enter variable list
				vars = list.substring(1, i).trim();
				list = list.substring(i+1).trim();
				hasvar = 1;
				i = -1;
			}
		}
		//add the new function to stack
		function new_func = new function(name, vars, opers);
		functions.addElement(new_func);
	}

	//run the specific function
	public String runFunc(String funcName, commandStack funcVarStack) throws IOException {
		String ret = "";
		//find the newly created function to run
		for(int i=functions.size()-1; i>=0; i--) {
			if(functions.get(i).getName().equals(funcName)) {
				//create a same function to run (in case of recursion)
				function new_func = new function(functions.get(i));
				ret = new_func.run(funcVarStack);
				if(ret == "STOP!!!!") {
					//the function is stopped 
					return ret;
				}
				break;
			}
		}
		return ret;
	}
	
	//return the number of variables of a certain function
	public int funcVarNum(String funcName) {
		for(int i=functions.size()-1; i>=0; i--) {
			if(functions.get(i).getName().equals(funcName)) {
				return functions.get(i).getVarNum();
			}
		}
		return 0;
	}

	public int size() {
		return functions.size();
	}
	
	public function get(int i) {
		return functions.get(i);
	}

	public void removeElementAt(int i) {
		functions.removeElementAt(0);
	}
}
