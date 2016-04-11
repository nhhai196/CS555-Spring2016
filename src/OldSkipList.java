import java.util.ArrayList;
import java.util.Random;


public class OldSkipList {
	private HashedObject myHash;
	
	private ArrayList<Integer> myList;
	
	// This field is the negative infinity node on level 0. It will always be
	// this.
	private Node bottomNegativeInfinity;
	
	// This field is the positive infinity node on level 0. It will always be
	// this.
	private Node bottomPositiveInfinity;
	
	// These two ArrayLists keep track of the negative and positive infinity nodes
	// on each level, with any given node in cell n to be considered level n. Level 0
	// will always have the bottomNegativeInfinity and the bottomPositiveInfinity.
	private ArrayList<Node> levelsLeft;
	private ArrayList<Node> levelsRight;
	
	// This node is a reference to the top of the list, which will always be the 
	// negative infinity node on the uppermost level.
	private Node top;
	
	// This integer is used exclusively by the insert method. More details below
	// at the method.
	private int iterator;
	
	// The constructor initializes a newly-constructed SkipList to be empty.
	// Remember that an empty skip list actually contains one level, numbered
	// "level 0," that contains only the special -INF and +INF nodes.
	
	// In this implementation of a SkipList, when an "empty" list is constructed,
	// level 0 is made, with a negative and positive infinity node. Since the table
	// has a name, the constructor must be passed a name for the list so that it will
	// be easy to see which list is which. The ArrayLists that keep track of the levels
	// are modified, adding level 0 to cell 0, and the top node referring to the top
	// of the list points to the appropriate negative infinity node at the top of the 
	// SkipList.
	public OldSkipList(int numElements, ArrayList<Integer> elements, HashedObject hash)
	{
		
		myHash = hash;
		myList = new ArrayList<Integer>(elements);
		
		bottomNegativeInfinity = new NegativeInfinityNode();
		bottomPositiveInfinity = new PositiveInfinityNode();
		bottomNegativeInfinity.next = bottomPositiveInfinity;
		bottomPositiveInfinity.prev = bottomNegativeInfinity;

		levelsLeft = new ArrayList<>();
		levelsRight = new ArrayList<>();
		levelsLeft.add(bottomNegativeInfinity);
		levelsRight.add(bottomPositiveInfinity);
		top = bottomNegativeInfinity;
	}
		
	// insert() takes a key/value pair as parameters, adding the key and its
	// associated value into the skip list.  If the key already exists in the
	// skip list, a DuplicateKeyException is thrown.
	
	// This implementation of insert() uses recursion to insert a given
	// key/value pair. As such, there are many local variable used to get
	// the job done.
	public void insert(int key, int value)
	throws DuplicateKeyException
	{
		// Here, if this is the very first insert to an "empty" list, a new
		// empty level above level 0 is created, making the proper references
		// to the level below it, while modifying level 0 infinity nodes to 
		// point upwards to it. The top field is then set to the appropriate
		// node, and the new level is added to the list of levels.
		if(levelsLeft.size() == 1)
		{
			Node negInfinity = new NegativeInfinityNode();
			Node posInfinity = new PositiveInfinityNode();
			
			negInfinity.below = levelsLeft.get(0);
			levelsLeft.get(0).above = negInfinity;
			posInfinity.below = levelsRight.get(0);
			levelsRight.get(0).above = posInfinity;
			negInfinity.next = posInfinity;
			posInfinity.prev = negInfinity;
			
			levelsLeft.add(negInfinity);
			levelsRight.add(posInfinity);
			top = negInfinity;
		}
		
		// Here, a temporary node is set to start at the top of the list, and it will
		// works its way across each level, and descend in the list to find the appropriate
		// place to insert the given key/value pair.
		Node tempNode = top;
		
		// This loop searches for either the node with the same key as the given key, or a node
		// with a key bigger than the given key and sets tempNode to the appropriate one.
		while(tempNode.compareToSearchKey(key) < 0)
			tempNode = tempNode.next;
		
		// If the key is found, an exception is thrown, as you cannot insert a duplicate key.
		if(tempNode.compareToSearchKey(key) == 0)
			throw new DuplicateKeyException();
		
		// Here, if a key bigger than the given key is found in a list, the tempNode backs up
		// to the previous node and makes some checks on the given situation.
		else
		{
			tempNode = tempNode.prev;
			
			// This if statement checks to see if the given tempNode is on level 0. If it is,
			// the method proceeds to make a random number (to decided whether or not to build
			// upon the node that is to be inserted). Furthermore, the method actually inserts
			// a new Normal Node into the list by changing the references of the nodes that the
			// new node will live between.
			if(tempNode.below == null)
			{
				int random = new Random().nextInt(2);
				
				Node tempNode2 = tempNode.next;
				Node newNode = new NormalNode(key, value);
				tempNode.next = newNode;
				newNode.prev = tempNode;
				newNode.next = tempNode2;
				tempNode2.prev = newNode;
				
				// Here, the iterator field is set to 0. It will be used to check whether
				// or not a build can be done yet; that is, if it randomly decides to build up
				// three levels, and there exists only three more levels, a new empty level
				// must be created above the last level of a build. There must always be a
				// completely empty level on top of the list.
				iterator = 0;
				
				// These two temporary nodes are only used if a build happens. They are used
				// like the temporary nodes above: to insert a node in between them.
				Node tempNode3;
				Node tempNode4;
				while(random == 0)
				{
					// Resets the random number to a new random to continually decided
					// whether to randomly build or not.
					random = new Random().nextInt(2);
					
					iterator++;
					
					tempNode3 = levelsLeft.get(iterator);
					
					while(tempNode3.compareToSearchKey(key) != 1)
						tempNode3 = tempNode3.next;
					
					tempNode4 = tempNode3;
					tempNode3 = tempNode3.prev;
					Node builtNode = new NormalNode(key, value);
					
					builtNode.below = newNode;
					newNode.above = builtNode;
					
					tempNode3.next = builtNode;
					builtNode.prev = tempNode3;
					builtNode.next = tempNode4;
					tempNode4.prev = builtNode;
					
					newNode = builtNode;
					
					// Here is where a new empty level is created if necessary.
					if(iterator == levelsLeft.size() - 1)
					{
						Node buildNegInfinity = new NegativeInfinityNode();
						Node buildPosInfinity = new PositiveInfinityNode();
						buildNegInfinity.below = levelsLeft.get(iterator);
						levelsLeft.get(iterator).above = buildNegInfinity;
						buildPosInfinity.below = levelsRight.get(iterator);
						levelsRight.get(iterator).above = buildPosInfinity;
						buildNegInfinity.next = buildPosInfinity;
						buildPosInfinity.prev = buildNegInfinity;
						levelsLeft.add(buildNegInfinity);
						levelsRight.add(buildPosInfinity);
					}
				}
			}
			
			// If the tempNode is not on the bottom level of the list, a recursive search
			// occurs on the node below tempNode. To do this, top is temporarily set to 
			// tempNode.below in order for the recursive search to happen.
			else
			{
				top = tempNode.below;
				insert(key, value);
			}
			
			// Regardless of what happens, top is finally reset to the appropriate node.
			top = levelsLeft.get(levelsLeft.size() - 1);
		}
	}
	

	// lookup() takes a key as a parameter and returns the value associated
	// with that key in the skip list.  If the key does not appear in the skip
	// list, a NoSuchKeyException is thrown.
	
	// The lookup method runs the same basic algorithm as insert:
	// it uses a temporary node to search for the node with the given
	// key in the list. If the node is found, its values are returned.
	// If a node with a bigger key is found that is not on level 0 of 
	// the list, then a recursive call is made, modifying top. Whether
	// or not the node exists, top is reset to the appropriate node,
	// and either the values are returned (if the node exists) or an
	// exception is thrown.
	public int lookup(int key)
	throws NoSuchKeyException
	{
		Node tempNode = top;
		
		while(tempNode.compareToSearchKey(key) < 0)
			tempNode = tempNode.next;
		if(tempNode.compareToSearchKey(key) == 1)
		{
			tempNode = tempNode.prev;
			if(tempNode.below != null)
			{
				top = tempNode.below;
				return lookup(key);
			}
			else 
			{
				top = levelsLeft.get(levelsLeft.size() - 1);
				throw new NoSuchKeyException();
			}
		}
		else
		{
			top = levelsLeft.get(levelsLeft.size() - 1);
			return tempNode.getValue();
		}
	}
	
	// This is a helper method for the remove() method. This method removes
	// any extraneous empty levels of the list, ensuring that there is only
	// at most one empty level above all other levels. This includes reverting
	// the list back to an empty list in the case of total removal from the list.
	public void removeLevels()
	{
		if(levelsLeft.size() - 2 > -1)
		{
			Node tempNode = levelsLeft.get(levelsLeft.size() - 2);
			int levelNumber = levelsLeft.size() - 2;
			int topLevel = levelsLeft.size() - 1;
			int nodeCount = 1;
			while(tempNode.next != null)
			{
				tempNode = tempNode.next;
				nodeCount++;
			}
			if(nodeCount==2)
			{
				levelsLeft.get(levelNumber).above = null;
				levelsRight.get(levelNumber).above = null;
				levelsLeft.get(topLevel).below = null;
				levelsLeft.get(topLevel).next = null;
				levelsRight.get(topLevel).below = null;
				levelsRight.get(topLevel).prev = null;
				levelsLeft.remove(topLevel);
				levelsRight.remove(topLevel);
				top = levelsLeft.get(levelNumber);
				removeLevels();
			}
			else return;
		}
	}
	
	
	// remove() takes a key as a parameter and removes it, along with its
	// associated value, from the skip list.  If the key does not appear in
	// the skip list, a NoSuchKeyException is thrown.	
	
	// The remove() method works much like the insert method, but in reverse.
	// Rather than inserting a node, it finds the node to be removes, and modifies
	// the nodes before and after the node to be removed, having them point around
	// the node being removed. If that node being removed has a tower, the whole tower
	// is removed, and any necessary modifications to the levels of the list are made.
	// If no such node exists, an exception is thrown. Top is always set back to the
	// appropriate node.
	public void remove(int key)
	throws NoSuchKeyException
	{
		Node tempNode = top;
		
		while(tempNode.compareToSearchKey(key) < 0)
			tempNode = tempNode.next;
		if(tempNode.compareToSearchKey(key) == 0)
		{
			if(tempNode.below != null)
			{
				Node tempNode2 = tempNode.below;
				Node tempNode3 = tempNode.prev;
				Node tempNode4 = tempNode.next;
				tempNode2.above = null;
				tempNode3.next = tempNode4;
				tempNode4.prev = tempNode3;
				tempNode.below = null;
				tempNode.prev = null;
				tempNode.next = null;
				tempNode.above = null;
				top = tempNode2;
				remove(key);
			}
			else
			{
				Node tempNode2 = tempNode.prev;
				Node tempNode3 = tempNode.next;
				tempNode2.next = tempNode3;
				tempNode3.prev = tempNode2;
				tempNode.below = null;
				tempNode.prev = null;
				tempNode.next = null;
				tempNode.above = null;
				top = levelsLeft.get(levelsLeft.size() - 1);
			}
			removeLevels();
		}
		else
		{
			tempNode = tempNode.prev;
			if(tempNode.below == null)
			{
				top = levelsLeft.get(levelsLeft.size() - 1);
				throw new NoSuchKeyException();
			}
			else
			{
				top = tempNode.below;
				remove(key);
			}
		}
		System.out.println("Number of levels: " + levelsLeft.size());
	}
	

	// update() takes a key and a new value as parameters.  If the key appears
	// in the skip list already, its value is replaced with the new value; if
	// not, a NoSuchKeyException is thrown instead.	
	
	// The update method works exactly the same way as the lookup method, but
	// modifies the given node instead of returning its values. Top is always
	// made to refer back to the appropriate node. If the given node to be 
	// updated does not exist, an exception is thrown.
	public void update(int key, int newValue)
	throws NoSuchKeyException
	{
		Node tempNode = top;
		
		while(tempNode.compareToSearchKey(key) < 0)
			tempNode = tempNode.next;
		if(tempNode.compareToSearchKey(key) == 1)
		{
			tempNode = tempNode.prev;
			if(tempNode.below != null)
			{
				top = tempNode.below;
				update(key, newValue);
			}
			else throw new NoSuchKeyException();
		}
		else
		{
			top = levelsLeft.get(levelsLeft.size() - 1);
			tempNode.setValue(newValue);
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
		public abstract int compareToSearchKey(int searchKey);
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
		
		public abstract int compareToSearchKey(int searchKey);
	}
	
	
	private static class NegativeInfinityNode
	extends InfinityNode
	{
		public NegativeInfinityNode()
		{
		}
		
		public int compareToSearchKey(int searchKey)
		{
			return -1;
		}
	}
	
	
	private static class PositiveInfinityNode
	extends InfinityNode
	{
		public PositiveInfinityNode()
		{
		}
		
		public int compareToSearchKey(int searchKey)
		{
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
		
		public int compareToSearchKey(int searchKey)
		{
			if (key < searchKey)
			{
				return -1;
			}
			else if (key > searchKey)
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
