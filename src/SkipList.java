// SkipList.java
//
// ICS 23 / CSE 23 Summer 2012
// Project #3: Always Changing Probably
//
// Below is a skeleton of a SkipList class where each key is an int and each
// value is an ArrayList<Integer>.  This data structure can be used to
// implement the database tables in this project, with each key/value pair
// being one row in the table, where the key is the element stored in the
// key column and the value is an ArrayList of Integers that are the values
// in each of the named columns.
//
// The skeleton suggests a basic design for your class, which you're free to
// use or discard at your discretion, though I suggest you stick with it, as
// I've already successfully implemented the SkipList according to this design
// and found it to work well.

// Alex Block
// 71103773

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SkipList
{
	
	private HashedObject myHash;
	public String embeddedHash;
	
	private ArrayList<Integer> myElements;
	
	private Node bottomNegativeInfinity;

	private Node bottomPositiveInfinity;
	
	// These two ArrayLists keep track of the negative and positive infinity nodes
	// on each level, with any given node in cell n to be considered level n. Level 0
	// will always have the bottomNegativeInfinity and the bottomPositiveInfinity.
	private ArrayList<Node> levelsLeft;
	private ArrayList<Node> levelsRight;
	
	private Node top;
	private Node searchNode;
	private int numberOfLevels;
	private int numberOfElements;
	
	public SkipList(int numElements, ArrayList<Integer> elements, HashedObject hash)
	{
		myHash = hash;
		embeddedHash = hash.getHash();
		myElements = new ArrayList<Integer>(elements);
		
		bottomNegativeInfinity = new NegativeInfinityNode();
		bottomPositiveInfinity = new PositiveInfinityNode();
		bottomNegativeInfinity.next = bottomPositiveInfinity;
		bottomPositiveInfinity.prev = bottomNegativeInfinity;

		levelsLeft = new ArrayList<>();
		levelsRight = new ArrayList<>();
		levelsLeft.add(bottomNegativeInfinity);
		levelsRight.add(bottomPositiveInfinity);
		top = bottomNegativeInfinity;
		numberOfLevels = 1;
		numberOfElements = numElements;
		searchNode = null;
		initList();
	}
	
	// Another construtor for constructing skip list in part 2
	public SkipList(ArrayList <ArrayList<Integer>> llelements, String key) throws Exception{
		myHash = new HashedObject(llelements.get(0), key, false);
		myElements = new ArrayList<Integer>(llelements.get(0));
		
		bottomNegativeInfinity = new NegativeInfinityNode();
		bottomPositiveInfinity = new PositiveInfinityNode();
		bottomNegativeInfinity.next = bottomPositiveInfinity;
		bottomPositiveInfinity.prev = bottomNegativeInfinity;

		levelsLeft = new ArrayList<>();
		levelsRight = new ArrayList<>();
		levelsLeft.add(bottomNegativeInfinity);
		levelsRight.add(bottomPositiveInfinity);
		top = bottomNegativeInfinity;
		numberOfLevels = 1;
		numberOfElements = llelements.get(0).size();
		searchNode = null;
		embeddedHash = initList2(llelements);
		System.out.println("Number of levels: " + numberOfLevels);
	}
	
	private String initList2(ArrayList <ArrayList<Integer>> llelements){
		int loop = llelements.size();
		System.out.println(loop);
		String embedhash = "";
		for (int j = 0; j < loop-1; j++){
			//System.out.println("building level " + (j+1) );
			if(numberOfLevels == 1) {
				Node temp = top;
				int key = 0;
				for(int elem : myElements) {
					Node create = new NormalNode(key, elem);
					temp.next = create;
					create.prev = temp;
					temp = temp.next;
					key++;
				}
				Node last = levelsRight.get(numberOfLevels-1);
				last.prev = temp;
				temp.next = last;
				Node negInf = new NegativeInfinityNode();
				Node posInf = new PositiveInfinityNode();
				
				negInf.next = posInf;
				posInf.prev = negInf;
				
				negInf.below = top;
				top.above = negInf;
				posInf.below = last;
				last.above = posInf;
				levelsLeft.add(negInf);
				levelsRight.add(posInf);
				top = negInf;
				numberOfLevels++;
				//initList2(llelements);
			}
			else {
				Node upper = top;
				Node upperEnd = top.next;
				Node lower = upper.below;
				lower = lower.next;
				ArrayList <Integer> eles = llelements.get(numberOfLevels - 1);
	
				for (int i = 0; i < eles.size(); i++){
					while(lower.next != null) {
						if (lower.getValue() == eles.get(i)){
							// build
							Node buildNode = new NormalNode(lower.getKey(), lower.getValue());
							buildNode.below = lower;
							lower.above = buildNode;
							buildNode.prev = upper;
							upper.next = buildNode;
							upper = upper.next;
							
							embedhash += "1";
							lower = lower.next;
							break;
						}
						else
						{
							embedhash += "0";
							lower = lower.next;
						}
						
					}
				}
				upper.next = upperEnd;
				upperEnd.prev = upper;
				//System.out.println("building level " + numberOfLevels );
				if(upper.prev != null) { // things were added; need to build up and recurse.
					
					Node negInf = new NegativeInfinityNode();
					Node posInf = new PositiveInfinityNode();
					
					negInf.next = posInf;
					posInf.prev = negInf;
					top.above = negInf;
					upperEnd.above = posInf;
					negInf.below = top;
					posInf.below = upperEnd;
					levelsLeft.add(negInf);
					levelsRight.add(posInf);
					top = negInf;
					numberOfLevels++;
				}
			}
		}
		System.out.println("Embedded hash: " + embedhash.substring(0, 20));
		return embedhash;
	}
	
	private void initList() {
		
		if(numberOfLevels == 1) {
			Node temp = top;
			int key = 0;
			for(int elem : myElements) {
				Node create = new NormalNode(key, elem);
				temp.next = create;
				create.prev = temp;
				temp = temp.next;
				key++;
			}
			Node last = levelsRight.get(numberOfLevels-1);
			last.prev = temp;
			temp.next = last;
			Node negInf = new NegativeInfinityNode();
			Node posInf = new PositiveInfinityNode();
			
			negInf.next = posInf;
			posInf.prev = negInf;
			
			negInf.below = top;
			top.above = negInf;
			posInf.below = last;
			last.above = posInf;
			levelsLeft.add(negInf);
			levelsRight.add(posInf);
			top = negInf;
			numberOfLevels++;
			initList();
		}
		else {
			Node upper = top;
			Node upperEnd = top.next;
			Node lower = upper.below;
			lower = lower.next;
			while(lower.next != null) {
				int bit = myHash.getBit();
				if(bit == -1) {
					bit = new Random().nextInt(2);
					if(bit == 1) {
						// build
						Node buildNode = new NormalNode(lower.getKey(), lower.getValue());
						buildNode.below = lower;
						lower.above = buildNode;
						buildNode.prev = upper;
						upper.next = buildNode;
						upper = upper.next;
					}
				}
				else if(bit == 1) {
					// build
					Node buildNode = new NormalNode(lower.getKey(), lower.getValue());
					buildNode.below = lower;
					lower.above = buildNode;
					buildNode.prev = upper;
					upper.next = buildNode;
					upper = upper.next;
				}
				lower = lower.next;
			}
			upper.next = upperEnd;
			upperEnd.prev = upper;
			if(upper.prev != null) { // things were added; need to build up and recurse.
				
				Node negInf = new NegativeInfinityNode();
				Node posInf = new PositiveInfinityNode();
				
				negInf.next = posInf;
				posInf.prev = negInf;
				top.above = negInf;
				upperEnd.above = posInf;
				negInf.below = top;
				posInf.below = upperEnd;
				levelsLeft.add(negInf);
				levelsRight.add(posInf);
				top = negInf;
				numberOfLevels++;
				initList();
			}
		}
	}
	
	public String printList() {
		String out = "";
		out += myHash.getHash() + "\r\n";
		for(int i = 0; i < levelsLeft.size(); i++) {
			out += "Level ";
			out += (i+1);
			if(i+1 < 10) {
				out +=" ";
			}
			out += ": ";
			Node temp = levelsLeft.get(i);
			temp = temp.next;
			int key;
			int prevKey;
			while(temp.next != null) {
				key = temp.getKey();
				if(temp.prev.prev == null) {
					for(int j = 0; j < key; j++) {
						int len = String.valueOf(myElements.get(j)).length();
						for(int k = 0; k < len; k++) {
							out += " ";
						}
						out += " ";
					}
				}
				else {
					prevKey = temp.prev.getKey();
					for(int j = prevKey + 1; j < key; j++) {
						int len = String.valueOf(myElements.get(j)).length();
						for(int k = 0; k < len; k++) {
							out += " ";
						}
						out += " ";
					}
				}
				out += temp.getValue();
				out += " ";
				temp = temp.next;
			}
			out += "\r\n";
		}
		return out;
	}
	// returns the index/key + 1 of the value	
	public int lookupValue(int value) {
		
		searchNode = top;
		while(searchNode.compareToSearchValue(value) < 0) {
			searchNode = searchNode.next;
		}
		if(searchNode.compareToSearchValue(value) == 1) {
			searchNode = searchNode.prev;
			if(searchNode.below != null) { // not the bottom of the list
				top = searchNode.below;
				return lookupValue(value);
			}
			else { // not in the list
				searchNode = null;
				top = levelsLeft.get(numberOfLevels - 1);
				return -1;
			}
		}
		else {
			int key = searchNode.getKey();
			searchNode = null;
			return key + 1;
		}
	}
	
	public int lookupValueWithLog(int value, PrintWriter p) {
		searchNode = top;
		int returnValue = -1;
		for(int i = numberOfLevels - 1; i >= 0; i--) {
			p.println("        At level " + i);
			p.println("        Beginning search through this level");
			while(searchNode.compareToSearchValue(value) < 0) {
				p.println("            Value being searched for less than value of current node. Moving to next node");
				searchNode = searchNode.next;
			}
			if(searchNode.compareToSearchValue(value) == 1) {
				p.println("        Found node with greater value than value being searched for. Moving to previous node with value "
						+ "less than search value.");
				searchNode = searchNode.prev;
				if(searchNode.below != null) { // not the bottom of the list
					p.println("        There is a node below the current one in the skip list.");
					try {
						int val = searchNode.getValue();
						p.println("        Moving down to next level, starting at node with value " + val);
					}
					catch(Exception e) {
						p.println("        Moving down to next level, starting at node with value MINUS INFINITY.");
					}
					searchNode = searchNode.below;
				}
				else { // at the bottom. Not in the list
					p.println("        At bottom of the skip list. Value was not found in skip list.");
					searchNode = null;
					break;
				}
			}
			else {
				int key = searchNode.getKey();
				p.println("        Found value in list at index " + key);
				searchNode = null;				
				returnValue = key + 1;
				break;
			}
		}
		return returnValue;
	}
	
	public boolean corrupted(String key) throws Exception {
		if (embeddedHash.subSequence(0, 160).equals(myHash.hashArray[0]))
			return false;
		else 
			return true;
	}
	
	/*
	public int lookupbyIndex (int index){
		searchNode = top;
		while(searchNode.compareToSearchKey(index) < 0) {
			searchNode = searchNode.next;
		}
		if(searchNode.compareToSearchKey(index) == 1) {
			searchNode = searchNode.prev;
			if(searchNode.below != null) { // not the bottom of the list
				top = searchNode.below;
				return lookupbyIndex(index);
			}
			else { // not in the list
				searchNode = null;
				top = levelsLeft.get(numberOfLevels - 1);
				return -1;
			}
		}
		else {
			int value = searchNode.getValue();
			searchNode = null;
			return value;
		}
	}
	*/
	/*
	public String get_items_with_same_bit(int pos){
		String result = "";
		for (int i = 0; i < numberOfElements; i++){
			String s =  Integer.toBinaryString(i);
			if (s.length() > pos){
				if (s.charAt(s.length() - pos - 1) == '1'){
					result += Integer.toUnsignedString(lookupbyIndex(i));
				}
			}
		}
		
		return result;
	}
	*/
	public int pinpointCorruptedItem(String key) throws Exception {
		String result = "";
		String [] hashList = embeddedHash.split("(?<=\\G.{160})");
		int l = myHash.getHashArray().length;
		if (hashList.length < l)
			throw new Exception("n is too small, embeded less than 1 + log n hashes");
		else {
			for (int i = 1; i < l; i++){
				 String temp = myHash.getHashArray()[i];
				 //System.out.println(temp);
				 //System.out.println(hashList[i]);
				if (temp.equals(hashList[i])) 
					result += "0";
				else 
					result += "1";
			}
		}
		
		// reverse the result 
		result = new StringBuilder(result).reverse().toString();
		
		if (result != "")
			return Integer.parseInt(result, 2);
		else 
			return 0;
	}
	
	// This function is just for testing 
	public void modify(int key) throws Exception {
		if (key >= numberOfElements)
			throw new Exception("out of range");
		else {
			Node temp = levelsLeft.get(0);
			temp = temp.next;
			for (int i = 0; i < numberOfElements; i++){
				if (temp.getKey() != key){
					temp = temp.next;
				}
				else break;
			}
			
			// modify the columns with index k of the skip list 
			while(temp != null) {
				temp.setValue(temp.getValue() +1); // increase value by 1
				temp = temp.above;
			}
		}
	}
	
	// There are three kinds of nodes in a skip list.  All kinds of nodes
	// have references that point to the node before, after, above, and
	// below that node.  But, other than that, there are differences between
	// the three kinds of nodes:
	//
	//   * Normal nodes contain a key/value pair.  Comparing a search key
	//     to a normal node is done by comparing the search key to the key
	//     in the node, so that a node containing the key 50 will be
	//     considered to come before the search key 60, while a node
	//     containing the key 70 will be considered to come after the
	//     search key 40.
	//
	//   * -INF nodes do not contain a key/value pair, so it is impossible
	//     to ask such a node for its key or its value.  Comparing a search
	//     key to a -INF node always has the same result: a -INF node is
	//     considered to come before any search key.
	//
	//   * +INF nodes are somewhat like -INF nodes, in that they contain no
	//     key/value pair.  However, when comparing a search key to a +INF
	//     node, the +INF node is always considered to come after any search
	//     key.
	//
	// I've provided a hierarchy of node classes that address these issues.
	// The abstract class Node forms the basis for all kinds of nodes,
	// containing the four references (prev, next, above, and below).  The
	// class NormalNode represents a normal node, with a key/value pair.
	// The abstract class InfinityNode forms the basis for the two kinds of
	// infinity nodes (-INF and +INF).  Finally, the classes NegativeInfinityNode
	// and PositiveInfinityNode represent -INF and +INF nodes, respectively.
	
	private abstract static class Node
	{
		public Node prev;
		public Node next;
		public Node above;
		public Node below;

		// Because nodes are interdependent, it's easier for all four references
		// to be set to null at construction time, then filled in one by one
		// afterward.
		public Node()
		{
			prev = null;
			next = null;
			above = null;
			below = null;
		}

		// getKey() returns the key stored in this node.  If the node is an
		// infinity node, an UnsupportedOperationException is thrown, since you
		// can't get a key out of an infinity node.
		public abstract int getKey();

		// getValue() returns the value stored in this node.  If the node is
		// an infinity node, an UnsupportedOperationException is thrown, since
		// you can't get a value out of an infinity node.
		public abstract int getValue();

		// setValue() changes the value stored in this node.  If the node is
		// an infinity node, an UnsupportedOperationException is thrown, since
		// you can't get a value out of an infinity node.
		public abstract void setValue(int value);

		// compareToSearchKey() compares the key in this node to the given
		// search key, returning a negative number if this key is less than
		// the search key, zero if they are equal, and a positive number if
		// this key is greater than the search key.  This method is handy
		// for implementing several parts of the skip list without having to
		// specially handle the cases of -INF and +INF (since polymorphism
		// will cause the appropriate version of compareToSearchKey() to be
		// called automatically).
		public abstract int compareToSearchValue(int searchKey);
		
		public abstract int compareToSearchKey(int index) ;
	}


	private abstract static class InfinityNode
	extends Node
	{
		public InfinityNode()
		{
		}
		
		public int getKey()
		{
			throw new UnsupportedOperationException();
		}
		
		public int getValue()
		{
			throw new UnsupportedOperationException();
		}
		
		public void setValue(int value)
		{
			throw new UnsupportedOperationException();
		}
		
		public abstract int compareToSearchValue(int searchKey);
	}
	
	
	private static class NegativeInfinityNode
	extends InfinityNode
	{
		public NegativeInfinityNode()
		{
		}
		
		public int compareToSearchValue(int searchKey)
		{
			return -1;
		}

		@Override
		public int compareToSearchKey(int index) {
			// TODO Auto-generated method stub
			return -1;
		}
	}
	
	
	private static class PositiveInfinityNode
	extends InfinityNode
	{
		public PositiveInfinityNode()
		{
		}
		
		public int compareToSearchValue(int searchKey)
		{
			return 1;
		}

		@Override
		public int compareToSearchKey(int index) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	
	private static class NormalNode
	extends Node
	{
		private int key;
		private int value;

		public NormalNode(int key, int value)
		{
			this.key = key;
			this.value = value;
		}
				
		public int getKey()
		{
			return key;
		}
				
		public int getValue()
		{
			return value;
		}
		
		public void setValue(int value)
		{
			this.value = value;
		}
		
		public int compareToSearchValue(int searchValue)
		{
			if (value < searchValue)
			{
				return -1;
			}
			else if (value > searchValue)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}


		public int compareToSearchKey(int index) {
			if (value < index)
			{
				return -1;
			}
			else if (value > index)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}
}
