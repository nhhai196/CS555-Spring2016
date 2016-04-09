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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

public class SkipList
{
	
	private ArrayList<HashedObject> myHash;
	
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
	
	public SkipList(int numElements, ArrayList<Integer> elements, ArrayList<HashedObject> hash)
	{
		
		myHash = hash;
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
				int bit = myHash.get(0).getBit();
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
	
	public void printList() {
		System.out.println(myHash.get(0).getHash());
		for(int i = 0; i < levelsLeft.size(); i++) {
			System.out.print("Level ");
			System.out.print(i+1);
			if(i+1 < 10) {
				System.out.print(" ");
			}
			System.out.print(": ");
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
							System.out.print(" ");
						}
						System.out.print(" ");
					}
				}
				else {
					prevKey = temp.prev.getKey();
					for(int j = prevKey + 1; j < key; j++) {
						int len = String.valueOf(myElements.get(j)).length();
						for(int k = 0; k < len; k++) {
							System.out.print(" ");
						}
						System.out.print(" ");
					}
				}
				System.out.print(temp.getValue());
				System.out.print(" ");
				temp = temp.next;
			}
			System.out.println();
		}
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
	
	public String corrupted() throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException{
		String concat = "";
		// Get the pointer to the first level of the skip list 
		Node temp = levelsLeft.get(0);
		temp = temp.next;
		
		while(temp.next != null) {
			concat += Integer.toString(temp.getValue());
			temp = temp.next;
		}
		
		HashedObject firstHash = myHash.get(0);
		if (firstHash.getHash().equals(firstHash.hmac_sha1(concat, firstHash.getKey())))
			return "No";
		else
			return "Yes";		
	}

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
	
	public int pinpointCorruptedItem() throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException{
		String result = "";
		for (int i = 1; i < myHash.size(); i++){
			HashedObject currentHash = myHash.get(i);
			if (currentHash.equals(currentHash.hmac_sha1(get_items_with_same_bit(i-1), currentHash.getKey())))
				result += "0";
			else 
				result += "1";
		}
		
		if (result != "")
			return Integer.parseInt(result, 2);
		else 
			return 0;
	}
	
	// For part 2
	public String getEmbddedHash (){
		String result = "";
		for (int i = 0; i < levelsLeft.size(); i++){
			Node temp = levelsLeft.get(i);
			temp = temp.next;
			while(temp.next != null) {
				if (temp.above == null){
					result += "0";
				}
				else 
					result += "1";
				
				temp = temp.next;
			}	
		}
		
		int a = result.length();
		int b = myHash.get(0).getHash().length();
		if ( a <= b ){
			if (result.equals(myHash.get(0).getHash().substring(0, a)))
				return "No";
			else 
				return "Yes";
		}
		else {
			if (result.equals(result.substring(0, b)))
				return "No";
			else 
				return "Yes";			
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
		public abstract int compareToSearchKey(int index);
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

		public int compareToSearchKey(int index) {
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

		public int compareToSearchKey(int index) {
			return 1;
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
		
		public int compareToSearchKey(int index)
		{
			if (key < index)
			{
				return -1;
			}
			else if (key > index)
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
