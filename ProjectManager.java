
import java.util.ArrayList;
import java.util.Scanner;

public class ProjectManager {

	public static void main(String[] args) {
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
		
		SkipList test = new SkipList(numVars, vals, h);
		test.printList();
		System.out.println(test.lookupValue(5));
		System.out.println(test.lookupValue(7));
	}

}
