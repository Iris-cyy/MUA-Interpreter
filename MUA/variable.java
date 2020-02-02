package src.mua;

public class variable {
	//varType: number, bool, word, list
	private String varName;
	private String varType;
	private double numValue; //if type is number, store value in numValue
	private boolean boolValue; //if type is bool, store value in boolValue
	private String wordName; //if type is word, store value in wordName
	private String listStr;
	
	//empty variable
	variable(String name){
		varName = name;
		varType = "";
	}
	
	//eg. make "a 6
	variable(String name, double value){
		varName = name;
		numValue = value;
		varType = "number";
	}
	
	//eg. make "a true
	variable(String name, boolean bool){
		varName = name;
		boolValue = bool;
		varType = "bool";
	}
	
	//eg. make "b "a, varType of b is word
	variable(String name, String str){
		varName = name;
		if(str.charAt(0) == '[') {
			changeListStr(str);
			varType = "list";
		}else {
			wordName = str;
			varType = "word";
		}
	}
	
	//copy construction
	variable(variable var) {
		varName = var.getName();
		varType = var.getType();
		if(varType == "number") {
			numValue = var.getNumValue();
		}else if(varType == "bool") {
			boolValue = var.getBoolValue();
		}else {
			wordName = var.getWordName();
		}
	}

	public String getName() {
		return varName;
	}

	public String getType() {
		return varType;
	}
	
	public double getNumValue() {
		return numValue;
	}

	public boolean getBoolValue() {
		return boolValue;
	}
	
	public String getWordName() {
		return wordName;
	}
	
	public String getValue() {
		String value;
		switch(varType) {
		case "word":
			value = wordName;
			break;
		case "number":
			value = numValue + "";
			break;
		case "bool":
			value = boolValue + "";
			break;
		default:
			value = "";
			break;
		}
		return value;
	}
	
	public double changeNumValue(double newValue) {
		numValue = newValue;
		return numValue;
	}
	
	public boolean changeBoolValue(boolean newValue) {
		boolValue = newValue;
		return boolValue;
	}
	
	public String changeWordName(String newName) {
		wordName = newName;
		return wordName;
	}
	
	public void changeType(String type) {
		varType = type;
	}
	
	public void changeValue(String value) {
		if(command.isNumber(value)) {
			varType = "number";
			changeNumValue(Double.valueOf(value));
		}else if(value == "true") {
			varType = "bool";
			changeBoolValue(true);
		}else if(value == "false") {
			varType = "bool";
			changeBoolValue(false);
		}else if(value.charAt(0) == '['){
			varType = "list";
			changeListStr(value);
		}else{
			varType = "word";
			wordName = value;
		}
	}

	public void changeListStr(String newList) {
		listStr = newList;
	}

	public String getListStr() {
		return listStr;
	}
}
