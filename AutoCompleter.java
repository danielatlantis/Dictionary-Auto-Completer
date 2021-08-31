package cs1501_p2;

import java.util.ArrayList;
import java.util.*;
import java.lang.*;
import java.io.*;


public class AutoCompleter implements AutoComplete_Inter
{
  File hist;
  File dict;
  DLB dictTrie;
  UserHistory histTrie;
  ArrayList<String> prediction = new ArrayList<String>();
  ArrayList<String> dictPredict = new ArrayList<String>();
  String word = new String();

  // Initiate the AutoCompleter that is given a dictionary and a UserHistory file.
  // Populate the dictionary DLB with the content of the file.
  public AutoCompleter(String dictionary, String history)
  {
    hist = new File(history);
    dict = new File(dictionary);
    dictTrie = new DLB();
    histTrie = new UserHistory(hist);
    try
    {
      Scanner sc  = new Scanner(dict);
      while(sc.hasNextLine()) // read through every word in dictionary and insert
      {
        dictTrie.add(sc.nextLine());
      }
    }
    catch(FileNotFoundException e)
    {
      System.out.println("Error occurred in Dictionary fill!");
    }
  }

// Initiate an AutoCompleter when given only a dictionary file.
// Therefore we create a new UserHistory file for the data to be stored.
  public AutoCompleter(String dictionary)
  {
    hist = new File("src/main/resoruces/hist.txt");
    dict = new File(dictionary);
    dictTrie = new DLB();
    histTrie = new UserHistory();
    try
    {
      Scanner sc  = new Scanner(dict);
      while(sc.hasNextLine()) // read through every word in dictionary and insert
      {
        dictTrie.add(sc.nextLine());
      }
    }
    catch(FileNotFoundException e)
    {
      System.out.println("Error occurred in Dictionary fill!");
    }
  }

// This method is used to gather predictions for the current word being generated
// one character at time. First we pull from the UserHistory DLB and then the dictionary
// DLB and then we eliminate any repeated words and shrink the ArrayList size to 5.
  public ArrayList<String> nextChar(char next)
  {
    System.out.println("Next Char: " + next);
    prediction.clear();
    word = word + next;
    int resultH = histTrie.searchByChar(next);
    int resultD = dictTrie.searchByChar(next);
    switch(resultH) // result of search by char for UserHistory
    {
      case -1:
        histTrie.resetByChar();
        break;
      case 0:
        prediction = histTrie.suggest(); // prefix therefore make suggestion
        break;
      case 1:
        break;
      case 2:
        prediction = histTrie.suggest(); // word & prefix so make suggestions
        break;
    }
    switch(resultD) // result of search by char for dictionary
    {
      case -1:
        dictTrie.resetByChar();
        break;
      case 0:
        dictPredict = dictTrie.suggest();
        for(int i = 0; i < prediction.size(); i++)
        {
          for(int j = 0; j < dictPredict.size(); j++)
          {
            if(prediction.get(i).equals(dictPredict.get(j)))
            {
              dictPredict.remove(j);
            }
          }
        }
        prediction.addAll(dictPredict); // prefix therefore make suggestions
        break;
      case 1:
        break;
      case 2:
        dictPredict = dictTrie.suggest();
        for(int i = 0; i < prediction.size(); i++)
        {
          for(int j = 0; j < dictPredict.size(); j++)
          {
            if(prediction.get(i).equals(dictPredict.get(j)))
            {
              dictPredict.remove(j);
            }
          }
        }
        prediction.addAll(dictPredict); // word & prefix so make suggestions
        break;
    }
    if(prediction.size() > 5) // if there are more than 5 predictions than trim
    {
      prediction.subList(4, prediction.size() - 1).clear();
    }
    return prediction;
  }

// This method is used to communicate when a word has been selected. Once selected
// the word is added to the UserHistory DLB or it's frequency is incrimented.
// Also the current searchByChar have to be reset for the next word.
  public void finishWord(String curr)
  {
    System.out.println("Selected: " + curr);
    histTrie.add(curr);
    histTrie.resetByChar();
    dictTrie.resetByChar();
    word = new String();
    return;
  }

  // This method is used to save the current state of the UserHistory DLB by storing
  // the data in a file in the format "<word> <frequency>"
  public void saveUserHistory(String fname)
  {
    try
    {
      File save = new File(fname);
      FileWriter fw = new FileWriter(fname);
      BufferedWriter bw = new BufferedWriter(fw);
      ArrayList<String> fullList = new ArrayList<String>(); // used to store the words
      for(int i = 0; i < histTrie.sorter.size(); i++)
      {
        fullList.add(histTrie.sorter.get(i).toString());
      }
      ArrayList<Integer> weightList = new ArrayList<Integer>(); // used to the store the corresponding frequency
      for(int i = 0; i < histTrie.sorter.size(); i++)
      {
        weightList.add(new Integer(histTrie.sorter.get(i).weight));
      }
      for(int i = 0; i < histTrie.count(); i++)
      {
        bw.write(fullList.get(i) + " ");
        bw.write(weightList.get(i) + "\n");
      }
      bw.close();
    }
    catch(IOException e)
    {
      System.out.println("Error occured when saving user history");
    }
    return;
  }
}
