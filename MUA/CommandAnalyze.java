package src.mua;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;

public class CommandAnalyze {
	private static varStack var = new varStack(); //the stack to store variables (global)
	private static funcStack func = new funcStack(); //the stack to store functions
	private static Scanner scan = new Scanner(System.in);
	
	static Vector<String> noOperand = new Vector<String>(); //operations with no operands
	static Vector<String> oneOperand = new Vector<String>(); //operations with one operand
	static Vector<String> twoOperand = new Vector<String>(); //operations with two operands
	static Vector<String> threeOperand = new Vector<String>(); //operations with three operands
	
	//init "noOperand", "oneOperand", "twoOperand", "threeOperand"
	public static void init() {
		noOperand.add("read");
		noOperand.add("readlist");
		noOperand.add("stop");
		noOperand.add("erall");
		noOperand.add("poall");
		
		oneOperand.add("thing");
		oneOperand.add("erase");
		oneOperand.add("isname");
		oneOperand.add("print");
		oneOperand.add("isnumber");
		oneOperand.add("isbool");
		oneOperand.add("islist");
		oneOperand.add("isword");
		oneOperand.add("isempty");
		oneOperand.add("not");
		oneOperand.add("output");
		oneOperand.add("export");
		oneOperand.add("run");
		oneOperand.add("first");
		oneOperand.add("last");
		oneOperand.add("butfirst");
		oneOperand.add("butlast");
		oneOperand.add("save");
		oneOperand.add("load");
		
		twoOperand.add("make");
		twoOperand.add("repeat");
		twoOperand.add("add");
		twoOperand.add("sub");
		twoOperand.add("mul");
		twoOperand.add("div");
		twoOperand.add("mod");
		twoOperand.add("eq");
		twoOperand.add("gt");
		twoOperand.add("lt");
		twoOperand.add("and");
		twoOperand.add("or");
		twoOperand.add("word");
		twoOperand.add("sentence");
		twoOperand.add("word");
		twoOperand.add("list");
		twoOperand.add("join");
		
		threeOperand.add("if");
	}
	
	//determine whether a string is a number
	public static boolean isNumber(String str) {
		for(int i=0; i<str.length(); i++) {
			if(!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	//determine whether a string is a complete list
	//TODO: for other operators
	public static boolean isComplete(String str) {
		int comp = 0;
		if(str.indexOf("[") != -1) {
			for(int i=0; i<str.length(); i++) {
				if(str.charAt(i) == '[') {
					comp--;
				}else if(str.charAt(i) == ']') {
					comp++;
				}
			}
			if(comp != 0) {
				return false;
			}
		}else {
			if(str.trim().length() >= 3 && str.trim().substring(0,3).equals("if ")) {
				if(countList(str) < 2) {
					return false;
				}
			}
			if(str.trim().length() >= 4 && str.trim().substring(0,4).equals("make")) {
				if(countCom(str) < 3) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean isOper(char c) {
		if(c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
			return true;
		}else {
			return false;
		}
	}
	
	//to export variables
	public static void exportVar(variable newVar) {
		var.add(newVar);
	}
	
	//to count the number of lists in a string of command
	public static int countList(String line) {
		int cnt = 0;
		int comp = 0;
		while(line != "") {
			if(line.charAt(0) == '[') {
				comp--;
			}else if(line.charAt(0) == ']') {
				comp++;
				if(comp == 0) {
					cnt++;
					comp = 0;
				}
			}
			if(line.length() != 1) {
				line = line.substring(1);
			}else {
				line = "";
			}
		}
		return cnt;
	}
	
	public static int countCom(String line) {
		commandStack newComStack = new commandStack(line);
		return newComStack.size();
	}
	
	public static String manageExpression(String expression, varStack vars) throws IOException {
		//expression with()
		expression = expression.trim();
		expression = expression.substring(1, expression.length()-1);
		expression = expression.replace("thing \"", ":");
		String result = "";
		//contains (), recursive
		for(int i=0; i<expression.length(); i++) {
			if(expression.charAt(i) == '(') {
				int comp = 0;
				for(int j=i; j<expression.length(); j++) {
					if(expression.charAt(j) == '(') {
						comp--;
					}else if(expression.charAt(j) == ')') {
						comp++;
					}
					if(comp == 0) {
						String newExp = expression.substring(i, j+1);
						result = manageExpression(newExp, vars);
						expression = expression.substring(0, i) + result + expression.substring(j+1);
						i--;
						break;
					}
				}
			}
		}
		//vars
		for(int i=0; i<expression.length(); i++) {
			if(expression.charAt(i) == ':') {
				String varName;
				for(int j=i+1; j<expression.length(); j++) {
					if(isOper(expression.charAt(j)) || j == expression.length()-1) {
						if(j == expression.length()-1) {
							varName = "\"" + expression.substring(i+1, j+1).trim();
							expression = expression.substring(0,i) + vars.getValue(varName);
						}else {
							varName = "\"" + expression.substring(i+1, j).trim();
							expression = expression.substring(0,i) + vars.getValue(varName) + expression.substring(j);
						}
						i--;
						break;
					}
				}
			}
		}
		//function
		for(int i=0; i<expression.length(); i++) {
			if(!Character.isDigit(expression.charAt(i)) && !isOper(expression.charAt(i)) && expression.charAt(i) != ' ' && expression.charAt(i) != '.') {
				String funcName;
				String retstr;
				commandStack funcVarStack;
				for(int j=i; j<expression.length();  j++) {
					if(expression.charAt(j) == ' ') {
						funcName = "\"" + expression.substring(i, j);
						//single variable
						for(int k=j+1; k<expression.length(); k++) {
							if(expression.charAt(k) == ' ' || k == expression.length()-1) {
								if(k == expression.length()-1) {
									funcVarStack = new commandStack(expression.substring(j+1, k+1));
									retstr = func.runFunc(funcName, funcVarStack);
									expression = expression.substring(0,i) + retstr;
								}else {
									funcVarStack = new commandStack(expression.substring(j+1, k+1));
									retstr = func.runFunc(funcName, funcVarStack);
									expression = expression.substring(0,i) + retstr + expression.substring(k);
								}
								i--;
								break;
							}
						}
						break;
					}
				}
			}
		}
		expression expr = new expression(expression);
		result = expr.calculate();
		return result;
	}
	
	//read in a line of command
	public boolean readin() throws IOException {
		if(scan.hasNext()) {
			String command = scan.nextLine().trim();
			if(command.equals("exit")) {
				return false;
			}else {
				if(!command.startsWith("//")) { //ignore comments
					//TODO: comments that are not in separate lines
					String next = "";
					//if the command is not complete, read in the next line
					while(!isComplete(command)) {
						if(scan.hasNext()) {
							next = scan.nextLine().trim();
							command += " " + next;
						}
					}
					//separate the line of commands individually and store in stack
					commandStack comStack = new commandStack(command);
					//run the commands
					addPi();
					judge(comStack, var, 0);
				}
				return true;
			}
		}else {
			return false;
		}
	}
	
	//TODO: error detect
	
	public static void addPi() throws IOException {
		commandStack comStack = new commandStack("make \"pi 3.14159");
		judge(comStack, var, 0);
	}

	//begin to run the commands in order
	public static String judge(commandStack oper_command, varStack oper_var, int begin_index) throws IOException {
		String ret = "";
		for(int i=begin_index; i<oper_command.size(); i++) {
			command nextCom = oper_command.get(i);
			//if a operator has all the operands it needs, it will run;
			//else it will wait for the expression at the place of an operand to figure out a result
			if(noOperand.contains(nextCom.getCommandName())) {
				ret = Operate(oper_command, oper_var, i); //run
				break;
			}else if(oneOperand.contains(nextCom.getCommandName())) {
				command next1 = oper_command.get(i+1);
				//check whether the next command is an operand
				if(!next1.getCommandType().equals("operator")) {
					ret = Operate(oper_command, oper_var, i);
					//if the whole line is not complete, it will run the next sentence
					if(begin_index == 0 && oper_command.size() != 0) {
						i = -1;
					}else {
						break;
					}
				}else {
					//run the next expression first
					ret = judge(oper_command, oper_var, i+1);
					i = begin_index-1;
				}
			}else if(twoOperand.contains(nextCom.getCommandName())) {
				command next1 = oper_command.get(i+1);
				if(next1.getCommandType().equals("operator")) {
					ret = judge(oper_command, oper_var, i+1);
					i = begin_index-1;
				}else {
					command next2 = oper_command.get(i+2);
					if(next2.getCommandType().equals("operator")) {
						ret = judge(oper_command, oper_var, i+2);
						i = begin_index-1;
					}else {
						ret = Operate(oper_command, oper_var, i);
						if(begin_index == 0 && oper_command.size() != 0) {
							i = -1;
						}else {
							break;
						}
					}
				}
			}else if(threeOperand.contains(nextCom.getCommandName())) {
				command next1 = oper_command.get(i+1);
				if(next1.getCommandType().equals("operator")) {
					ret = judge(oper_command, oper_var, i+1);
					i = begin_index-1;
				}else {
					command next2 = oper_command.get(i+2);
					if(next2.getCommandType().equals("operator")) {
						ret = judge(oper_command, oper_var, i+2);
						i = begin_index-1;
					}else {
						command next3 = oper_command.get(i+3);
						if(next3.getCommandType().equals("operator")) {
							ret = judge(oper_command, oper_var, i+3);
							i = begin_index-1;
						}else {
							ret = Operate(oper_command, oper_var, i);
							if(begin_index == 0 && oper_command.size() != 0) {
								i = -1;
							}else {
								break;
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	//run
	public static String Operate(commandStack oper_command, varStack oper_var, int begin_index) throws IOException {
		String retstr = "";
		command newCom;
		
		for(int i=begin_index; i<oper_command.size(); i++) {
			if(oper_command.get(i).getCommandType().equals("expression")) {
				String result = manageExpression(oper_command.get(i).getCommandName(), oper_var);
				newCom = new command(result);
				oper_command.setElementAt(newCom, i);
			}
		}

		//if the operator is a self-defined function
		if(func.isFunc("\"" + oper_command.get(begin_index).getCommandName())) {
			String funcName = "\"" + oper_command.get(begin_index).getCommandName();
			String funcVars = "";
			//pass parameters to the function in order
			for(int i=0; i<func.funcVarNum(funcName); i++) {
				funcVars += oper_command.get(begin_index+i+1).getCommandName() + " ";
			}			
			commandStack funcVarStack = new commandStack(funcVars);
			//run the function
			retstr = func.runFunc(funcName, funcVarStack);
			if(retstr.equals("") || retstr.equals("STOP!!!!")) {
				//no output or is stopped (in case of recursion) 
				for(int i=func.funcVarNum(funcName); i>=0; i--) {
					oper_command.removeElementAt(begin_index+i);
				}
			}else {
				newCom = new command(retstr);
				oper_command.setElementAt(newCom, begin_index);
				for(int i=func.funcVarNum(funcName); i>0; i--) {
					oper_command.removeElementAt(begin_index+i);
				}
			}
		}else if(oper_command.get(begin_index).getCommandType() == "operator") {
			switch(oper_command.get(begin_index).getCommandName()) {
			//TODO: wait, random, int, sqrt
			case "make":
				//make <name> <value> || make <name> [<list1> <list2>]
				command next1 = oper_command.get(begin_index+1);
				command next2 = oper_command.get(begin_index+2);
				if(next2.getCommandType().equals("list")) {
					//if the second operand is a list, the first operand is the name of a function
					//add the function to stack
					func.add(next1.getCommandName(), next2.getCommandName());
					//store the number of operands of the new function
					//TODO: if a function has more than three operands
					if(func.funcVarNum(next1.getCommandName()) == 1) {
						oneOperand.add(next1.getCommandName().substring(1));
					}else if(func.funcVarNum(next1.getCommandName()) == 2) {
						twoOperand.add(next1.getCommandName().substring(1));
					}else if(func.funcVarNum(next1.getCommandName()) == 3) {
						threeOperand.add(next1.getCommandName().substring(1));
					}else if(func.funcVarNum(next1.getCommandName()) == 0) {
						noOperand.add(next1.getCommandName().substring(1));
					}
				}else if(next2.getCommandType().equals("word")) {
					//eg. make "b "a -> thing "b == a, thing :b == value of a
					oper_var.add(next1.getCommandName(), next2.getCommandName());
				}else if(next2.getCommandType().equals("number")) {
					//eg. make "a 6 -> put 6 in a
					oper_var.add(next1.getCommandName(), next2.getNumValue());
				}else if(next2.getCommandType().equals("list")){
					oper_var.add(next1.getCommandName(), next2.getListString());
				}else{
					//eg. make "a true
					oper_var.add(next1.getCommandName(), next2.getBoolValue());
				}
				//remove the used commands
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "thing":
				//thing <name>
				command next = oper_command.get(begin_index+1);
				//get the next command 
				newCom = new command(oper_var.getValue(next.getCommandName()), oper_var.getType(next.getCommandName()));
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "erase":
				//erase <name>
				next = oper_command.get(begin_index+1);
				//remove the next command
				oper_var.remove(next.getCommandName());
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "isname":
				//isname <word>
				String result;
				//is a variable or a function, true, else, false
				if((oper_var.isname(oper_command.get(begin_index+1).getCommandName()).equals("true")) || (func.isFunc(oper_command.get(begin_index+1).getCommandName())) ) {
					result = "true";
				}else {
					result = "false";
				}
				newCom = new command(result, "bool");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "print":
				//print <value>
				next = oper_command.get(begin_index+1);
				if(next.getCommandType().equals("word")) {
					//print a word, eg. print "a --> a
					System.out.println(next.getCommandName().substring(1));
				}else if(next.getCommandType().equals("list")) {
					System.out.println(next.getCommandName().trim().substring(1, next.getCommandName().trim().length()-1));
				}
				else {
					//print number or bool, etc, eg. print 1
					System.out.println(next.getCommandName());
				}
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "read":
				//read in a number/word, classify it
				if(scan.hasNext()) {
					String input = scan.nextLine();
					if(isNumber(input)) {
						newCom = new command(input.trim(), "number");
					}else {
						newCom = new command('"'+input.trim(), "word");
					}
					oper_command.setElementAt(newCom, begin_index);
				}
				break;
				
			case "readlist":
				//read in a list (single layer, in one line)
				//TODO: may not be correct
				if(scan.hasNext()) {
					String input = scan.nextLine();
					newCom =new command(input.trim(), "list");
					oper_command.setElementAt(newCom, begin_index);
				}
				break;
				
			case "repeat":
				//repeat <number> <list>
				//loop
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				if(!next1.getCommandType().equals("number") || !next2.getCommandType().equals("list")){
					System.out.println("ERROR: repeat <number> <list>!");
				}
				int repeatNum = (int) next1.getNumValue(); //loop times
				int repeatCnt = 0; //the number of execution already take place
				commandStack newComStack;
				while(repeatCnt != repeatNum) {
					//create a new stack to store commands in the list, and run again
					newComStack = new commandStack(next2.getListString());
					retstr = judge(newComStack, oper_var, 0);
					repeatCnt++;
				}
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "if":
				next1 = oper_command.get(begin_index+1);
				command list1 = oper_command.get(begin_index+2);
				command list2 = oper_command.get(begin_index+3);			
				if(next1.getCommandName().equals("true")) {
					//run the commands in list1
					newComStack = new commandStack(list1.getListString());
					retstr = judge(newComStack, oper_var, 0);
				}else {
					//run the commands in list2
					newComStack = new commandStack(list2.getListString());
					retstr = judge(newComStack, oper_var, 0);
				}
				oper_command.removeElementAt(begin_index+3);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "run":
				next = oper_command.get(begin_index+1);
				newComStack = new commandStack(next.getListString());
				retstr = judge(newComStack, oper_var, 0);
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "word":
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				String newword;
				if(next2.getCommandType().equals("word")) {
					newword = next1.getCommandName().substring(1) + next2.getCommandName().substring(1);
				}else {
					newword = next1.getCommandName().substring(1) + next2.getCommandName();
				}
				newCom = new command("\"" + newword);
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);				
				break;
				
			case "sentence":
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				String newStr = "";
				if(next1.getCommandType().equals("word") && next2.getCommandType().equals("word")) {
					newStr = next1.getCommandName().substring(1) + " " + next2.getCommandName().substring(1);
				}else if(next1.getCommandType().equals("word") && next2.getCommandType().equals("list")) {
					newStr = next1.getCommandName().substring(1) + " " + next2.getCommandName().trim().substring(1, next2.getCommandName().trim().length()-1);
				}else if(next1.getCommandType().equals("list") && next2.getCommandType().equals("word")) {
					newStr = next1.getCommandName().trim().substring(1, next1.getCommandName().trim().length()-1) + " " + next2.getCommandName().substring(1);
				}else if(next1.getCommandType().equals("list") && next2.getCommandType().equals("list")) {
					newStr = next1.getCommandName().trim().substring(1, next1.getCommandName().trim().length()-1) + " " + next2.getCommandName().trim().substring(1, next2.getCommandName().trim().length()-1);
				}
				newCom = new command("[" + newStr + "]");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);	
				break;
			
			case "list":
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				newStr = "";
				if(next1.getCommandType().equals("word") && next2.getCommandType().equals("word")) {
					newStr = next1.getCommandName().substring(1) + " " + next2.getCommandName().substring(1);
				}else if(next1.getCommandType().equals("word") && next2.getCommandType().equals("list")) {
					newStr = next1.getCommandName().substring(1) + " " + next2.getCommandName().trim();
				}else if(next1.getCommandType().equals("list") && next2.getCommandType().equals("word")) {
					newStr = next1.getCommandName().trim() + " " + next2.getCommandName().substring(1);
				}else if(next1.getCommandType().equals("list") && next2.getCommandType().equals("list")) {
					newStr = next1.getCommandName().trim() + " " + next2.getCommandName().trim();
				}
				newCom = new command("[" + newStr + "]");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);	
				break;
			
			case "join":
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				newStr = "";
				if(next2.getCommandType().equals("word")) {
					newStr = next1.getCommandName().trim().substring(1, next1.getCommandName().trim().length()-1) + " " + next2.getCommandName().substring(1);
				}else {
					newStr = next1.getCommandName().trim().substring(1, next1.getCommandName().trim().length()-1) + " " + next2.getCommandName().trim();
				}
				newStr = newStr.trim();
				newCom = new command("[" + newStr + "]");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);	
				break;
				
			case "first":
				next = oper_command.get(begin_index+1);
				if(next.getCommandType().equals("word")) {
					//first "abc -> a (word)
					newCom = new command(next.getCommandName().substring(0, 2));
					
				}else if(next.getCommandType().equals("list")){
					//first [a b c] -> a (word)
					newComStack = new commandStack(next.getCommandName().trim().substring(1, next.getCommandName().trim().length()-1));
					//in the above example, a is recognized as an operator, not a word. but it should be a word
					if(newComStack.get(0).getCommandType().equals("operator")) {
						//it should be a word
						newCom = new command("\"" + newComStack.get(0).getCommandName());
					}else {
						//first [[a] b] -> [a] (list)
						newCom = newComStack.get(0);
					}
				}else {
					//first "123 -> 1 (word)
					newCom = new command("\"" + next.getCommandName().substring(0, 1));
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "last":
				//TODO: may have mistake, should be as "first"!
				next = oper_command.get(begin_index+1);
				if(next.getCommandType().equals("list")){
					newComStack = new commandStack(next.getCommandName().trim().substring(1, next.getCommandName().trim().length()-1));
					if(newComStack.get(newComStack.size()-1).getCommandType().equals("list")) {
						newCom = newComStack.get(newComStack.size()-1);
					}else {
						newCom = new command("[" + newComStack.get(newComStack.size()-1).getCommandName() + "]");
					}
				}else {
					newCom = new command("\"" + next.getCommandName().substring(next.getCommandName().length()-1));
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "butfirst":
				next = oper_command.get(begin_index+1);
				if(next.getCommandType().equals("word")) {
					newCom = new command("\"" + next.getCommandName().substring(2, next.getCommandName().trim().length()));
					
				}else if(next.getCommandType().equals("list")){
					newComStack = new commandStack(next.getCommandName().trim().substring(1, next.getCommandName().trim().length()-1));
					newStr = "";
						for(int i=1; i<newComStack.size(); i++) {
							newCom = newComStack.get(i);
							newStr += newCom.getCommandName() + " ";
						}
						newCom = new command("[" + newStr + "]");
				}else {
					newCom = new command("\"" + next.getCommandName().substring(1, next.getCommandName().trim().length()));
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "butlast":
				//TODO: may have mistakes, should be as "butfirst"
				next = oper_command.get(begin_index+1);
				if(next.getCommandType().equals("word")) {
					newCom = new command("\"" + next.getCommandName().substring(2, next.getCommandName().trim().length()-1));
					
				}else if(next.getCommandType().equals("list")){
					newComStack = new commandStack(next.getCommandName().trim().substring(1, next.getCommandName().trim().length()-1));
					newStr = "";
					if(newComStack.size() == 2 && newComStack.get(0).getCommandType().equals("list")) {
						newCom = newComStack.get(0);
					}else {
						for(int i=0; i<newComStack.size()-1; i++) {
							newCom = newComStack.get(i);
							newStr += newCom.getCommandName() + " ";
						}
						newCom = new command("[" + newStr + "]");
					}
				}else {
					newCom = new command("\"" + next.getCommandName().substring(1, next.getCommandName().trim().length()-1));
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "save":
				//TODO: may should save all the codes
				next = oper_command.get(begin_index+1);
				File fout = new File(next.getCommandName().substring(1));
				FileWriter fw = new FileWriter(fout);
				
				variable currentVar;
				function currentFunc;
				for(int i=0; i<oper_var.size(); i++) {
					currentVar = oper_var.get(i);
					newStr = "make " + currentVar.getName() + " " + currentVar.getValue() + "\n";
					fw.write(newStr);
				}
				for(int i=0; i<func.size(); i++) {
					currentFunc = func.get(i);
					newStr = "make " + currentFunc.getName() + " [[" + currentFunc.getVarList() + "] [" + currentFunc.getOperList() + "]]\n";
					fw.write(newStr);
				}
				fw.close();
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "load":
				next = oper_command.get(begin_index+1);
				FileReader fr = new FileReader(next.getCommandName().substring(1));
				BufferedReader br = new BufferedReader(fr);
				while((newStr = br.readLine()) != null) {
					newComStack = new commandStack(newStr);
					judge(newComStack, var, 0);
				}
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "erall":
				while(oper_var.size()!=0) {
					oper_var.removeElementAt(0);
				}
				while(func.size()!=0) {
					func.removeElementAt(0);
				}
				oper_command.removeElementAt(begin_index);
				break;
				
			case "poall":
				System.out.print("variables: ");
				for(int i=0; i<oper_var.size(); i++) {
					System.out.print(oper_var.get(i).getName());
					if(i != oper_var.size()-1) {
						System.out.print(", ");
					}
				}
				System.out.println();
				System.out.print("functions: ");
				for(int i=0; i<func.size(); i++) {
					System.out.print(func.get(i).getName());
					if(i != func.size()-1) {
						System.out.print(", ");
					}
				}
				System.out.println();
				oper_command.removeElementAt(begin_index);
				break;
				
			//determine whether the next command is a number/bool/word/list, judge by "commandType"
			case "isnumber":
				next1 = oper_command.get(begin_index+1);
				if(next1.getCommandType().equals("number")){
					result = "true";
				}else {
					result = "false";
				}
				newCom = new command(result, "bool");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "isbool":
				next1 = oper_command.get(begin_index+1);
				if(next1.getCommandType().equals("bool")){
					result = "true";
				}else {
					result = "false";
				}
				newCom = new command(result, "bool");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "islist":
				next1 = oper_command.get(begin_index+1);
				if(next1.getCommandType().equals("list")){
					result = "true";
				}else {
					result = "false";
				}
				newCom = new command(result, "bool");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "isword":
				next1 = oper_command.get(begin_index+1);
				if(next1.getCommandName().charAt(0) == '"') {
					result = "true";
				}else {
					result = "false";
				}
				newCom = new command(result, "bool");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "isempty":
				//judge whether a word/list is empty
				next1 = oper_command.get(begin_index+1);
				if(next1.getCommandType().equals("list")) {
					//if it is a list
					if(next1.getListString().equals("")) {
						//if list is as "[]"
						result = "true";
					}else {
						result = "false";
					}
				}else {
					//if it is a word (variable), if its "type" is not stored, it is empty 
					if(oper_var.getType(next1.getCommandName()).equals("")) {
						result = "true";
					}else {
						result = "false";
					}
				}
				newCom = new command(result, "bool");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "add": //add <number> <number> : number + number
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				newCom = new command(next1.getNumValue()+next2.getNumValue()+"", "number");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "sub": //sub <number> <number> : number - number
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				newCom = new command(next1.getNumValue()-next2.getNumValue()+"", "number");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "mul": //mul <number> <number> : number * number
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				newCom = new command(next1.getNumValue()*next2.getNumValue()+"", "number");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "div": //div <number> <number> : number / number
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				newCom = new command(next1.getNumValue()/next2.getNumValue()+"", "number");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "mod": //mod <number> <number> : number % number
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				newCom = new command(next1.getNumValue()%next2.getNumValue()+"", "number");
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "eq": //eq <number|word> <number|word> : <number/word> = <number/word>
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				if(next1.getCommandType().equals("word")) {
					if(next1.getCommandType().equals("word")) {
						if(oper_var.getNumValue(next1.getCommandName()) == oper_var.getNumValue(next2.getCommandName())){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}else {
						if(oper_var.getNumValue(next1.getCommandName()) == next2.getNumValue()){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}
				}else {
					if(next1.getCommandType().equals("word")) {
						if(next1.getNumValue() == oper_var.getNumValue(next2.getCommandName())){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}else {
						if(next1.getNumValue() == next2.getNumValue()){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "gt": //gt <number|word> <number|word> : <number/word> > <number/word>
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				if(next1.getCommandType().equals("word")) {
					if(next1.getCommandType().equals("word")) {
						if(oper_var.getNumValue(next1.getCommandName()) > oper_var.getNumValue(next2.getCommandName())){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}else {
						if(oper_var.getNumValue(next1.getCommandName()) > next2.getNumValue()){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}
				}else {
					if(next1.getCommandType().equals("word")) {
						if(next1.getNumValue() > oper_var.getNumValue(next2.getCommandName())){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}else {
						if(next1.getNumValue() > next2.getNumValue()){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "lt": //lt <number|word> <number|word> : <number/word> < <number/word>
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				if(next1.getCommandType().equals("word")) {
					if(next1.getCommandType().equals("word")) {
						if(oper_var.getNumValue(next1.getCommandName()) < oper_var.getNumValue(next2.getCommandName())){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}else {
						if(oper_var.getNumValue(next1.getCommandName()) < next2.getNumValue()){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}
				}else {
					if(next1.getCommandType().equals("word")) {
						if(next1.getNumValue() < oper_var.getNumValue(next2.getCommandName())){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}else {
						if(next1.getNumValue() < next2.getNumValue()){
							newCom = new command("true", "bool");
						}else {
							newCom = new command("false", "bool");
						}
					}
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "and": //and <bool> <bool> : <bool> && <bool>
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				if(next1.getBoolValue() && next2.getBoolValue()) {
					newCom = new command("true", "bool");
				}else {
					newCom = new command("false", "bool");
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);					
				break;
				
			case "or": //or <bool> <bool> : <bool> || <bool>
				next1 = oper_command.get(begin_index+1);
				next2 = oper_command.get(begin_index+2);
				if(next1.getBoolValue() || next2.getBoolValue()) {
					newCom = new command("true", "bool");
				}else {
					newCom = new command("false", "bool");
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+2);
				oper_command.removeElementAt(begin_index+1);					
				break;
				
			case "not": //not <bool> : !<bool>
				next = oper_command.get(begin_index+1);
				if(next.getBoolValue()) {
					newCom = new command("false", "bool");
				}else {
					newCom = new command("true", "bool");
				}
				oper_command.setElementAt(newCom, begin_index);
				oper_command.removeElementAt(begin_index+1);
				break;
				
			case "output": //function return value
				next = oper_command.get(begin_index+1);
				retstr = next.getCommandName();
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "export": //export a local variable to global
				next = oper_command.get(begin_index+1);
				//find the local variable in local var stack
				variable ex_var = new variable(oper_var.find(next.getCommandName())); 
				//add the variable in global var stack
				var.add(ex_var); 
				oper_command.removeElementAt(begin_index+1);
				oper_command.removeElementAt(begin_index);
				break;
				
			case "stop": //stop function
				retstr = "STOP!!!!"; //by passing a certain output
				break;
			}

		}
		return retstr;
	}
}
