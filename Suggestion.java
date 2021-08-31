package cs1501_p2;

// This class is a Suggestion that contains a word found in the UserHistory DLB and its frequency
public class Suggestion
{
  public String word;
  public int weight;

  public Suggestion(String s, int freq)
  {
    word = s;
    weight = freq;
  }

  public String toString()
  {
    return word;
  }

}
