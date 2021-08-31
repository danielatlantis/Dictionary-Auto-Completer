/**
 * A driver for CS1501 Project 2
 * @author	Dr. Farnan
 */
package cs1501_p2;

import java.io.File;
import java.util.ArrayList;

public class App {
	public static void main(String[] args) {
		String dict_fname = "build/resources/test/dictionary.txt";
		String uhist_state_fname = "build/resources/test/uhist_state.p2";

		AutoCompleter ac = new AutoCompleter(dict_fname);


		ArrayList<String> sugs = ac.nextChar('d');
		String[] expected = new String[] {"definite", "dict", "dictionary"};
		System.out.println("Predictions:");
		int c = 0;
		for (String p : sugs) {
			System.out.printf("\t%d: %s\n", ++c, p);
		}
		System.out.println();
		ac.finishWord("dictionary");
		sugs = ac.nextChar('d');
		System.out.println("Predictions:");
		c = 0;
		for (String p : sugs) {
			System.out.printf("\t%d: %s\n", ++c, p);
		}
		System.out.println();
		ac.finishWord("dip");
		ac.finishWord("dip");
		sugs = ac.nextChar('d');
		System.out.println("Predictions:");
		c = 0;
		for (String p : sugs) {
			System.out.printf("\t%d: %s\n", ++c, p);
		}

		ArrayList<String> content = new ArrayList<String>();
		content = ac.histTrie.traverse();
		System.out.println("Content of UserHistory:");
		c = 0;
		for (String p : content) {
			System.out.printf("\t%d: %s\n", ++c, p);
		}
		System.out.println();


		ac.saveUserHistory(uhist_state_fname);
		ac = new AutoCompleter(dict_fname, uhist_state_fname);
		content = ac.histTrie.traverse();
		System.out.println("Content of UserHistory after save:");
		c = 0;
		for (String p : content) {
			System.out.printf("\t%d: %s\n", ++c, p);
		}
		System.out.println();
		sugs = ac.nextChar('d');
		System.out.println("Predictions:");
		c = 0;
		for (String p : sugs) {
			System.out.printf("\t%d: %s\n", ++c, p);
		}
		System.out.println();
		/*
		String eng_dict_fname = "build/resources/main/dictionary.txt";
		String uhist_state_fname = "build/resources/main/uhist_state.p2";

		AutoCompleter ac;
		File check = new File(uhist_state_fname);
		if (check.exists()) {
			System.out.println("File does exist");
			ac = new AutoCompleter(eng_dict_fname, uhist_state_fname);
		}
		else {
			System.out.println("File doesn't exist");
			ac = new AutoCompleter(eng_dict_fname);
		}


		printPredictions(ac, 't');
		printPredictions(ac, 'h');
		printPredictions(ac, 'e');
		printPredictions(ac, 'r');
		printPredictions(ac, 'e');

		String word = "thereabout";
		System.out.printf("Selected: %s\n\n", word);
		ac.finishWord(word);

		printPredictions(ac, 't');
		printPredictions(ac, 'h');
		printPredictions(ac, 'e');
		printPredictions(ac, 'r');
		printPredictions(ac, 'e');

		ac.saveUserHistory(uhist_state_fname);
	*/
	}

	private static void printPredictions(AutoCompleter ac, char next) {
		System.out.printf("Entered: %c\n", next);

		ArrayList<String> preds = ac.nextChar(next);

		System.out.println("Predictions:");
		int c = 0;
		for (String p : preds) {
			System.out.printf("\t%d: %s\n", ++c, p);
		}
		System.out.println();
	}
}
