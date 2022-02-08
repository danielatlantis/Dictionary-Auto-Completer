package cs1501_p2;

import java.util.ArrayList;
import java.util.*;
import java.lang.*;
import java.io.*;

public class UserHistory implements Dict
{
  UHNode head;
  int wordCount = 0;
  char term = '^';
  ArrayList<String> prediction = new ArrayList<String>();
  ArrayList<Suggestion> sorter = new ArrayList<Suggestion>();
  ArrayList<String> content = new ArrayList<String>();
  ArrayList<Integer> contentWeight = new ArrayList<Integer>();
  String search = new String();
  String suggestion = new String();

  public UserHistory()
  {
    head = new UHNode('$'); // head holds no value and will only have a down
  }

  public UserHistory(File prev)
  {
    try
    {
      head = new UHNode('$'); // head holds no value and will only have a down
      Scanner sc  = new Scanner(prev);
      while(sc.hasNext()) // read through every word in file and insert into trie
      {
        String word = sc.next();
        int freq = sc.nextInt();
        this.add(word, freq);
      }
    }
    catch(FileNotFoundException e)
    {
      System.out.println("Error occurred in UserHistory!");
    }
  }

  // This method checks if a given word is already in the DLB, if so increases its frequency
  // if not then it adds the given word to the DLB.
  public void add(String key)
  {
    if(this.contains(key))
    {
      String s = key + "^";
      UHNode curr = head;
      for(int i = 0; i < key.length(); i++)
      {
        curr = this.findDown(curr, key.charAt(i));
      }
      curr = curr.getDown();
      curr.getDown().repeat(); // incriment the frequency of the word
      return;
    }
    String s = key + "^";
    UHNode curr = head;
    for(int i = 0; i < s.length(); i++)
    {
      curr = this.addDown(curr, s.charAt(i));
    }
    wordCount++;
    UHNode weight = new UHNode(1); // create the frequency node after the null terminator
    curr.setDown(weight);
    return;
  }

  // this add method is for adding words when retreiving from a file.
  public void add(String key, int freq)
  {
    if(this.contains(key))
    {
      String s = key + "^";
      UHNode curr = head;
      for(int i = 0; i < key.length(); i++)
      {
        curr = this.findDown(curr, key.charAt(i));
      }
      curr = curr.getDown();
      curr.getDown().repeat(); // incriment the frequency of the word
      return;
    }
    String s = key + "^";
    UHNode curr = head;
    for(int i = 0; i < s.length(); i++)
    {
      curr = this.addDown(curr, s.charAt(i));
    }
    wordCount++;
    UHNode weight = new UHNode(freq); // create the frequency node after the null terminator
    curr.setDown(weight);
    return;
  }

  // Method that adds a node at the end of a current levels most right reference
  public UHNode addRight(UHNode currNode, char c)
  {
    if(currNode == null) // row is empty
    {
       return new UHNode(c);
    }
    else
    {
      while(currNode.getRight() != null && currNode.getLet() != c) // traverse through row and see if the letter is already store
      {
        currNode = currNode.getRight();
      }
      if(currNode.getRight() == null && currNode.getLet() != c) // if letter is not in row and end is reached add it to the right
      {
        currNode.setRight(new UHNode(c));
        return currNode.getRight();
      }
      else // if the letter is in row just return the node
      {
        return currNode;
      }
    }
  }

  // Method that goes down the DLB one level and finds an empty spot to place the letter at that level
  public UHNode addDown(UHNode node, char c)
  {
    if(node.getDown() == null) // node has no down
    {
      node.setDown(new UHNode(c));
      return node.getDown();
    }
    else // node has a down
    {
      return this.addRight(node.getDown(), c); // add new node on the level down
    }
  }

  // Method that returns the letter if found on that level and null if not
  public UHNode findRight(UHNode node, char c)
  {
    while(node.getRight() != null && node.getLet() != c) // traverse row and see if letter is already stored
    {
      node = node.getRight();
    }
    if(node.getRight() == null && node.getLet() != c)
    {
      return null; // letter was not found so return null
    }
    return node; // letter was found so return node
  }

  // Method that returns the node of the letter on the level below and null if it is not found
  public UHNode findDown(UHNode node, char c)
  {
    if(node.getDown() != null)
    {
      if(node.getDown().getLet() == c)
      {
        return node.getDown();
      }
    }
    else
    {
      return null;
    }
    return this.findRight(node.getDown(), c);
  }

  // This method is searching for a word within the DLB and returning true if found and false otherwise.
  // First we travel down the trie to the last letter in the word and if the following sublevel contains
  // the terminating character then that word is in the DLB.
  public boolean contains(String key)
  {
    UHNode curr = head;
    for(int i = 0; i < key.length(); i++) // returns the node of the last letter in the word
    {
      curr = this.findDown(curr, key.charAt(i));
      if(curr == null)
      {
        return false;
      }
    }
    UHNode terminator = this.findDown(curr, term); // check if the terminating char is in the sublevel
    if(terminator == null) // if not, then word is not found
    {
      return false;
    }
    else // else the terminating char is in the sublevel and therefore is found
    {
      return true;
    }
  }

  // This method is checking to see if the following string is a prefix for another word in the DLB.
  // First we traverse the trie and find the node of the last letter in the string, then if
  // the terminating char is the only node in that sublevel then it is a word and not a prefix for any others
  public boolean containsPrefix(String pre)
  {
    UHNode curr = head;
    for(int i = 0; i < pre.length(); i++)
    {
      curr = this.findDown(curr, pre.charAt(i));
      if(curr == null)
      {
        return false;
      }
    }
    curr = curr.getDown();
    if(curr.getLet() == term && curr.getRight() == null)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  // This method is utlizing a global string called search that is tracking the string being produced after each call
  // and is determining whether that current string is a word, a prefix, both, or neither.
  // First we find the node of the last letter in the string and then check that node's children to determine the status
  public int searchByChar(char next)
  {
    UHNode curr = head;
    search = search + next;
    for(int i = 0; i < search.length(); i++)
    {
      curr = this.findDown(curr, search.charAt(i));
    }
    if(curr == null) // letter not stored
    {
      return -1;
    }
    curr = curr.getDown(); // go down to the node's children
    if(this.findRight(curr, term) != null && curr.getRight() != null) // if the child contains "^" and other char then it is both a prefix and a word
    {
      return 2;
    }
    else if(this.findRight(curr, term) == null) // if the child doesn't contain a "^" then it is just a prefix and not a word
    {
      return 0;
    }
    else if(this.findRight(curr, term) != null) // if the chiild contains only a "^" then it is just a word and not a prefix
    {
      return 1;
    }
    return -1;
  }

  // Method that reset the by character search by creating a new string object that is empty
  public void resetByChar()
  {
    search = new String(); // create new string object
    return;
  }

  // This Method returns an ArrayList of string that are suggestions based on a given prefix being entered.
  // First we find the node of the last letter in the prefix given and the proceed down the node's children
  // After the recusrion we sort the the sorter ArrayList in descending order and only select the 5 most
  // frequently used words and add them to prediction.
  public ArrayList<String> suggest()
  {
    prediction.clear();
    sorter.clear();
    UHNode curr = head;
    // get node at the end of the current prefix given
    for(int i = 0; i < search.length(); i++)
    {
      curr = this.findDown(curr, search.charAt(i));
    }
    UHNode end = curr.getDown();
    searchForWord(search, search, end);
    Collections.sort(sorter, Collections.reverseOrder(new SortAR()));
    for(int i = 0; i < sorter.size(); i++)
    {
      prediction.add(sorter.get(i).toString());
    }
    return prediction;
  }

  // This is the recursive part of the suggest method that fills the prediction ArrayList.
  public void searchForWord(String storage, String word, UHNode node)
  {
    //System.out.println(prediction);
    //System.out.println("size of ArrayList " + prediction.size());
    //System.out.println("current letter " + node.getLet());
    //System.out.println("Storage: " + storage);
    //System.out.println("Word: " + word);
    if(node.getLet() == term) // base case the node is the terminating char
    {
      if(!sorter.contains(word)) // check if the ArrayList contains the word
      {
        Suggestion s = new Suggestion(word, node.getDown().getFreq());
        //System.out.println("Added: " + word);
        sorter.add(s);
      }
      if(node.getRight() != null) // if node has siblings recurse upon them
      {
        //System.out.println("Going right of " + node.getLet());
        searchForWord(storage, word, node.getRight());
      }
      return;
    }
    else
    {
      storage = word;
      word = word + node.getLet(); // add letter to string
      //System.out.println("Going down from " + node.getLet());
      searchForWord(storage, word, node.getDown());
      if(node.getRight() != null) // if node has siblings then recurse upon them
      {
        //System.out.println("Going right of " + node.getLet());
        searchForWord(storage, storage, node.getRight());
      }
    }
    return;
  }

  // This method will traverse through the DLB and add every word to an ArrayList by calling a recursive method
  public ArrayList<String> traverse()
  {
    content.clear();
    UHNode node = head;
    String s = new String();
    String w = new String();
    recursive(node.getDown(), s, w, 0);
    return content;
  }

  // This is the recusive part of the traverse method and takes in three arguments:
  // A node to act upon, a string that is progressively being built, and a string to preserve the prefix
  public void recursive(UHNode node, String word, String storage, int weight)
  {
    System.out.println("Current Node is: " + node.getLet());
    if(node.getLet() == term) // base case the current node is the terminating char
    {
      weight = node.getDown().getFreq();
      Integer i = new Integer(weight);
      content.add(word); // add word to the ArrayList
      contentWeight.add(i);
      if(node.getRight() != null) // if the node has siblings recurse upon them
      {
        recursive(node.getRight(), word, storage, weight);
      }
      return;
    }
    else
    {
      storage = word;
      word = word + node.getLet(); // add letter to the word
      recursive(node.getDown(), word, storage, weight);
      if(node.getRight() != null) // before returning check and see if node has sibling then recurse upon them
      {
        recursive(node.getRight(), storage, storage, weight);
      }
      return;
    }
  }

  // Simple method that returns the amount of words currently in the DLB
  public int count()
  {
    return wordCount;
  }
}
