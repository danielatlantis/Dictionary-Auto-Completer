package cs1501_p2;

import java.io.Serializable;

public class UHNode {

// an int that represents the amount of times that a word has been added to the UserHistory DLB.
// Only stored after the terminating character.
	private int freq;

	private char let;

	private UHNode right;

	private UHNode down;

	public UHNode(char c) {
		this.let = c;

		this.right = null;
		this.down = null;
	}

	public UHNode(int freq) {
		this.freq = freq;

		this.right = null;
		this.down = null;
	}

	public char getLet() {
		return let;
	}

	public int getFreq() {
		return freq;
	}

	public UHNode getRight() {
		return right;
	}

	public UHNode getDown() {
		return down;
	}

	public void repeat() {
		freq++;
	}

	public void setRight(UHNode r) {
		right = r;
	}

	public void setDown(UHNode d) {
		down = d;
	}
}
