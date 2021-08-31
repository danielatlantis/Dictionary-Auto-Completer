package cs1501_p2;

import java.util.*;
import java.lang.*;
import java.io.*;

// This class is used to compare the frequency of words in an ArrayList
public class SortAR implements Comparator<Suggestion>
{
  public int compare(Suggestion a, Suggestion b)
  {
    return a.weight - b.weight;
  }
}
