package src.mua;

import java.io.IOException;
import java.util.*;

public class function {
	private String funcName;
	private String funcVarList; //store the variables together as a String, separate by " ", eg. a b c
	private String funcOperList; //store the operations together as a String, separate by "\n", eg. make "a 4 /n add :a 3
	private varStack funcVariable = new varStack(); //local variables
	private Vector<commandStack> funcOperation = new Vector<commandStack>(); //operations (each line of operation is a stack)
	
	function(){}
	
	function(String name, String var, String oper){
		funcName = name;
		setVariables(var);
		setOperations(oper);
	}
	
	function(function func){
		funcName = func.getName();
		setOperations(func.getOperList());
		setVariables(func.getVarList());
	}
	
	//get the number of operands
	public int getVarNum() {
		return funcVariable.size();
	}

	public String getName() {
		return funcName;
	}
	
	public String getVarList() {
		return funcVarList;
	}
	
	public String getOperList() {
		return funcOperList;
	}
	
	//store the input String of operations into stack
	public void setOperations(String oper) {
		funcOperList = oper;
		String newLine = "";
		commandStack newComStack;
		//if "\n" occurs, the commands after "\n" is another line, must be stored in the next command stack
		while(!oper.equals("")) {
			if(oper.indexOf('\n') != -1) {
				newLine = oper.substring(0, oper.indexOf('\n')).trim();
				newComStack = new commandStack(newLine);
				funcOperation.addElement(newComStack);
				oper = oper.substring(oper.indexOf('\n')+1).trim();
			} else {
				newLine = oper;
				newComStack = new commandStack(newLine);
				funcOperation.addElement(newComStack);
				oper = "";
			}
		}
	}
	
	//store the input String of variables into stack
	public void setVariables(String var) {
		funcVarList = var;
		//if meet " ", means another variable
		while(!var.equals("")) {
			if(var.indexOf(' ') != -1) {
				funcVariable.add("\"" + var.substring(0, var.indexOf(' ')).trim());
				var = var.substring(var.indexOf(' ')+1).trim(); 
			} else {
				funcVariable.add("\"" + var.trim());
				var = "";
			}
		}
	}
	
	//run the function
	public String run(commandStack funcVarStack) throws IOException {
		String ret = "";
		int var_index = 0;
		String varName = "";
		String varValue = "";
		//passing parameters
		for(int i=0; i<getVarNum(); i++) {
			varName = funcVariable.get(i).getName();
			varValue = funcVarStack.get(i).getCommandName();
			funcVariable.get(i).changeValue(varValue);
		}
		//run each operations in order
		commandStack nextComStack;
		while(funcOperation.size()!=0) {
			nextComStack = funcOperation.get(0);
			ret = CommandAnalyze.judge(nextComStack, funcVariable, 0);
			funcOperation.remove(0);
			//if the function is stopped
			if(ret == "STOP!!!!") {
				return ret;
			}
		}
		return ret;
	}
}