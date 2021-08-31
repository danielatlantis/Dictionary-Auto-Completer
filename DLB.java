package cs1501_p2;

import java.util.ArrayList;
import java.util.*;
import java.lang.*;
import java.io.*;

public class DLB implements Dict
{
  DLBNode head;
  int wordCount = 0;
  ArrayList<String> prediction = new ArrayList<String>();
  ArrayList<String> content = new ArrayList<String>();
  String search = new String();
  String suggestion = new String();
  char term = '^';

  public DLB()
  {
    head = new DLBNode('$'); // head holds no value and will only have a down
  }

// This method adds a given word to the DLB
  public void add(String key)
  {
    String s = key + "^"; // word with terminator
    //System.out.println(s);
    DLBNode curr = head;
    for(int i = 0; i < s.length(); i++)
    {
      curr = this.addDown(curr, s.charAt(i));
      //System.out.println(curr.getLet());
    }
    wordCount++;
    return;
  }

// Method that adds a node at the end of a current levels most right reference
  public DLBNode addRight(DLBNode currNode, char c)
  {
    if(currNode == null) // row is empty
    {
       return new DLBNode(c);
    }
    else
    {
      while(currNode.getRight() != null && currNode.getLet() != c) // traverse through row and see if the letter is already store
      {
        currNode = currNode.getRight();
      }
      if(currNode.getRight() == null && currNode.getLet() != c) // if letter is not in row and end is reached add it to the right
      {
        currNode.setRight(new DLBNode(c));
        return currNode.getRight();
      }
      else // if the letter is in row just return the node
      {
        return currNode;
      }
    }
  }

// Method that goes down the DLB one level and finds an empty spot to place the letter at that level
  public DLBNode addDown(DLBNode node, char c)
  {
    if(node.getDown() == null) // node has no down
    {
      node.setDown(new DLBNode(c));
      return node.getDown();
    }
    else // node has a down
    {
      return this.addRight(node.getDown(), c); // add new node on the level down
    }
  }

// Method that returns the letter if found on that level and null if not
  public DLBNode findRight(DLBNode node, char c)
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
  public DLBNode findDown(DLBNode node, char c)
  {
    if(node.getDown() != null)
    {
      if(node.getDown().getLet() == c)
      {
        return node.getDown();
      }
    }
    return this.findRight(node.getDown(), c);
  }

// This method is searching for a word within the DLB and returning true if found and false otherwise.
// First we travel down the trie to the last letter in the word and if the following sublevel contains
// the terminating character then that word is in the DLB.
  public boolean contains(String key)
  {
    DLBNode curr = head;
    for(int i = 0; i < key.length(); i++) // returns the node of the last letter in the word
    {
      curr = this.findDown(curr, key.charAt(i));
      if(curr == null)
      {
        return false;
      }
    }
    DLBNode terminator = this.findDown(curr, term); // check if the terminating char is in the sublevel
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
    DLBNode curr = head;
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
      return false;
    }
    else
    {
      return true;
    }
  }

// This method is utlizing a global string called search that is tracking the string being produced after each call
// and is determining whether that current string is a word, a prefix, both, or neither.
// First we find the node of the last letter in the string and then check that node's children to determine the status
  public int searchByChar(char next)
  {
    DLBNode curr = head;
    search = search + next;
    //System.out.println(search);
    for(int i = 0; i < search.length(); i++)
    {
      curr = this.findDown(curr, search.charAt(i));
    }
    if(curr == null) // letter not stored
    {
      return -1;
    }
    curr = curr.getDown(); // go down to the node's children
    //System.out.println(curr.getLet());
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
// Then we call our recursive method and then return the prediction ArrayList.
  public ArrayList<String> suggest()
  {
    prediction.clear();
    DLBNode curr = head;
    // get node at the end of the current prefix given
    for(int i = 0; i < search.length(); i++)
    {
      curr = this.findDown(curr, search.charAt(i));
    }
    DLBNode end = curr.getDown();
    searchForWord(search, search, end);
    return prediction;
  }

// This is the recursive part of the suggest method that fills the prediction ArrayList.
  public void searchForWord(String storage, String word, DLBNode node)
  {
    while(prediction.size() < 5)
    {
      //System.out.println(prediction);
      //System.out.println("size of ArrayList " + prediction.size());
      //System.out.println("current letter " + node.getLet());
      //System.out.println("Storage: " + storage);
      //System.out.println("Word: " + word);
      if(node.getLet() == term) // base case we've reached a terminating character and found a word
      {
        if(!prediction.contains(word)) // make sure word is not already in the ArrayList
        {
          //System.out.println("Added: " + word);
          prediction.add(word);
        }
        if(node.getRight() != null) // if sublevel has other words then recurse
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
        if(node.getRight() != null) // before return we must check if node has siblings
        {
          //System.out.println("Going right of " + node.getLet());
          searchForWord(storage, storage, node.getRight());
        }
        return;
      }
    }
    return;
  }

// This method will traverse through the DLB and add every word to an ArrayList by calling a recursive method
  public ArrayList<String> traverse()
  {
    content.clear();
    DLBNode node = head;
    String s = new String();
    String w = new String();
    recursive(node.getDown(), s, w);
    return content;
  }

// This is the recusive part of the traverse method and takes in three arguments:
// A node to act upon, a string that is progressively being built, and a string to preserve the prefix
  public void recursive(DLBNode node, String word, String storage)
  {
    if(node.getLet() == term) // base case the current node is the terminating char
    {
      content.add(word); // add word to the ArrayList
      if(node.getRight() != null) // if the node has siblings recurse upon them
      {
        recursive(node.getRight(), word, storage);
      }
      return;
    }
    else
    {
      storage = word;
      word = word + node.getLet(); // add letter to the word
      recursive(node.getDown(), word, storage);
      if(node.getRight() != null) // before returning check and see if node has sibling then recurse upon them
      {
        recursive(node.getRight(), storage, storage);
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
