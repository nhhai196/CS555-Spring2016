
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProjectManager {

	public static void main(String[] args) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
		Scanner s = new Scanner(System.in);
                System.out.print("Key:");
                String key = s.nextLine(); // key
		int numVars = s.nextInt(); //number
		ArrayList<Integer> vals = new ArrayList<Integer>();
                String input = "";
		for(int i = 0; i < numVars; i++) {
			vals.add(s.nextInt());
                        input+= vals.get(i);
		}
		s.close();
//		String hash = "110110101010101011101010001001010110";
		HashedObject h = new HashedObject(input,key);
		HashedObject h1 = new HashedObject ("25",key);
		HashedObject h2 = new HashedObject ("35",key);
		
		ArrayList<HashedObject> k = new ArrayList<HashedObject> ();
		k.add(h);
		
		SkipList test = new SkipList(numVars, vals, k);
		test.printList();
		System.out.println(test.lookupValue(5));
		System.out.println(test.lookupValue(7));
		System.out.println(test.corrupted());
		System.out.println(test.lookupbyIndex(0));
		System.out.println(test.get_items_with_same_bit(0));
		//System.out.println(test.get_items_with_same_bit(1));
		k.add(h1);
		k.add(h2);
		test = new SkipList(1234, vals, k);
		System.out.println(test.pinpointCorruptedItem());
		
		//System.out.println(test.get_items_with_same_bit(1));
	}
}
