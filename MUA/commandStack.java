package src.mua;

import java.util.*;

//the stack to store a line of commands
//eg. make "a 5, separated into three command, stored in one commandStack
public class commandStack {
	private Vector<command> commands = new Vector<command>();
		
	commandStack(commandStack comstack){
		for(int i=0; i<comstack.size(); i++) {
			commands.addElement(comstack.get(i));
		}
	}
	
	commandStack(Vector<command> coms){
		for(int i=0; i<coms.size(); i++) {
			commands.addElement(coms.get(i));
		}
	}
	
	commandStack(){}
	
	//put a line of commands into stack
	commandStack(String line){
		line = line.replace(":", "thing \"").trim();
		while(!line.equals("")) {
			command newCom;
			if(line.charAt(0) == '[') {
				int comp = 0;
				for(int i=0; i<line.length(); i++) {
					if(line.charAt(i) == '[') {
						comp--;
					} else if(line.charAt(i) == ']') {
						comp++;
					}
					if(comp == 0) {
						newCom = new command((line.substring(0, i+1)).trim());
						commands.addElement(newCom);
						if(i == line.length() - 1) {
							line = "";
						}else {
							line = line.substring(i+1).trim();
						}
						break;
					}
				}
			}else if(line.charAt(0) == '(') {
				int comp = 0;
				for(int i=0; i<line.length(); i++) {
					if(line.charAt(i) == '(') {
						comp--;
					} else if(line.charAt(i) == ')') {
						comp++;
					}
					if(comp == 0) {
						newCom = new command((line.substring(0, i+1)).trim());
						commands.addElement(newCom);
						if(i == line.length() - 1) {
							line = "";
						}else {
							line = line.substring(i+1).trim();
						}
						break;
					}
				}
			}
			else if(line.indexOf(' ') != -1) {
				newCom = new command(line.substring(0, line.indexOf(' ')).trim());
				commands.addElement(newCom); 
				line = line.substring(line.indexOf(' ')+1).trim(); 
			} else {
				newCom = new command(line);
				commands.addElement(newCom);
				line = "";
			}
		}
	}
	
	//print commands in commandStack, for debug use
	public void print() {
		for(int i=0; i<commands.size(); i++) {
			System.out.println(commands.get(i).getCommandName());
		}
	}

	//return size
	public int size() {
		return commands.size();
	}

	//get the command according to index
	public command get(int i) {
		return commands.get(i);
	}

	//change an element at a certain index
	public void setElementAt(command newvar, int i) {
		commands.setElementAt(newvar, i);
	}

	//insert an element at a certain index
	public void insertElementAt(command newthing, int i) {
		commands.insertElementAt(newthing, i);
	}

	//remove an element at a certain index
	public void removeElementAt(int i) {
		commands.removeElementAt(i);
	}

	//return the subStack from begin_index(included) to end_index(excluded), similar to subString()
	public commandStack subStack(int begin, int end) {
		commandStack newStack = new commandStack();
		for(int i=0; i<end-begin; i++) {
			newStack.insertElementAt(commands.get(i+begin), i);
		}
		return newStack;
	}
	
	//return the subStack from begin_index(included) to the end of the stack, similar to subString()
	public commandStack subStack(int begin) {
		commandStack newStack = new commandStack();
		for(int i=0; i<commands.size()-begin; i++) {
			newStack.insertElementAt(commands.get(i+begin), i);
		}
		return newStack;
	}
	
}
