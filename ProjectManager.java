
import java.util.ArrayList;
import java.util.Scanner;

public class ProjectManager {

	public static void main(String[] args) {
		
		Scanner s = new Scanner(System.in);
		int numVars = s.nextInt();
		ArrayList<Integer> vals = new ArrayList<Integer>();
		for(int i = 0; i < numVars; i++) {
			vals.add(s.nextInt());
		}
		s.close();

		String hash = "110110101010101011101010001001010110";
		HashedObject h = new HashedObject(hash);
		
		SkipList test = new SkipList(numVars, vals, h);
		test.printList();
		System.out.println(test.lookupValue(5));
		System.out.println(test.lookupValue(7));
	}

}
