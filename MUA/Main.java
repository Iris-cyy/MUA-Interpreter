package src.mua;

import java.io.IOException;
import java.util.*;

public class Main {
	public static void main(String[] args) throws IOException {
		CommandAnalyze analyzer = new CommandAnalyze();
		analyzer.init();
		while(analyzer.readin()) {
			analyzer = new CommandAnalyze();
		}
	}

}
 