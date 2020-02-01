package src.mua;

import java.util.*;

public class command {
	//type: number, bool, list, word, operator
	private String commandName;
	private String commandType;
	private double numValue;
	private boolean boolValue;
	private String listString;
	private String exprString;
	
	command(){}
	
	command(String name, String type){
		commandName = name;
		commandType = type;
		if(type.equals("number")) {
			numValue = Double.valueOf(name);
		}else if(type.equals("bool")) {
			if(name.equals("true")) {
				boolValue = true;
			}else {
				boolValue = false;
			}
		}else if(type.equals("list")) {
			listString = commandName.substring(1, commandName.length()-1);
		}else if(type.equals("expression")) {
			exprString = commandName.substring(1, commandName.length()-1);
		}
	}
	
	command(String name){
		commandName = name;
		analyzeType(name);
		if(commandType.equals("number")) {
			numValue = Double.valueOf(commandName);
		}else if(commandType.equals("bool")) {
			if(name.equals("true")) {
				boolValue = true;
			}else {
				boolValue = false;
			}
		}else if(commandType.equals("list")) {
			listString = commandName.substring(1, commandName.length()-1);
		}else if(commandType.equals("expression")) {
			exprString = commandName.substring(1, commandName.length()-1);
		}
	}
	
	//if a String is a number or not
	public static boolean isNumber(String str) {
		int dotNum = 0;
		for(int i=0; i<str.length(); i++) {
			if(!Character.isDigit(str.charAt(i))) {
				if(str.charAt(i) != '.') {
					return false;
				}else {
					dotNum++;
				}
			}
		}
		if(dotNum > 1) {
			return false;
		}
		return true;
	}
	
	public void analyzeType(String name) {
		char firstChar = name.charAt(0);
		if( (firstChar >= '0' && firstChar <= '9') || firstChar == '-') {
			//number eg.6
			commandType = "number";
		}else if(firstChar == '"') {
			if(isNumber(name.substring(1))) {
				//number eg."6
				commandName = name.substring(1);
				commandType = "number";
			}else {
				//word eg."ab
				commandType = "word";
			}
		}else if(name.equals("true") || name.equals("false")) {
			//boolean (true/false)
			commandType = "bool";
		}else if(firstChar == '['){
			//list
			commandType = "list";
		}else if(firstChar == '(') {
			//expression
			commandType = "expression";
		}else if(name.indexOf('.') != -1 && name.trim().substring(name.indexOf('.')).equals(".mua")) {
			commandType = "file";
		}else {
			//operator
			commandType = "operator";
		}
	}
	
	public String getCommandName() {
		return commandName;
	}
	
	public String getCommandType() {
		return commandType;
	}
	
	public double getNumValue() {
		return numValue;
	}
	
	public boolean getBoolValue() {
		return boolValue;
	}
	
	public String getListString() {
		return listString;
	}
	
	public String getExprString() {
		return exprString;
	}
	
	public void setCommandType(String type) {
		commandType = type;
		if(type.equals("number")) {
			numValue = Double.valueOf(commandName);
		}else if(type.equals("bool")) {
			if(commandName.equals("true")) {
				boolValue = true;
			}else {
				boolValue = false;
			}
		}else if(type.equals("list")){
			listString = commandName.substring(1, commandName.length()-1);
		}else if(type.equals("expression")) {
			exprString = commandName.substring(1, commandName.length()-1);
		}
	}
}