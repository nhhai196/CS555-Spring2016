
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ProjectManager {

	public static void main(String[] args) throws Exception 
	{
		
		PrintWriter logWriter = new PrintWriter(new File("log.txt"));
		
		// PART 1 OF PROJECT
		
		logWriter.println("CS 555 SPRING 2016 PROJECT 1\r\n");
		
		logWriter.println("========== PART ONE ==========\r\n");
		
		// open input file
		logWriter.println("Reading from input.txt");
		Scanner s = new Scanner(new File("input.txt"));
		
		// read input file
		logWriter.println("    Reading key.");
		String key = s.nextLine();		// read key
		System.out.println("key: "+ key); 
		logWriter.println("    Reading number of test cases.");
		int numTest = s.nextInt();		// read num test cases
		ArrayList<SkipList> test = new ArrayList<SkipList>();
		System.out.println("Number of Test Cases: "+ numTest);
		for (int j = 0 ; j < numTest; j++) {
			logWriter.println("    Reading values for test case " + (j+1));
		    int numVars = s.nextInt(); 		// length of each test case
			ArrayList<Integer> vals = new ArrayList<Integer>();
			for(int i = 0; i < numVars; i++) {
				vals.add(s.nextInt());
			}
			logWriter.println("    Creating hash.");
			HashedObject h = new HashedObject(vals,key,true); //if true, then doing part 1
			logWriter.println("    Creating Skip List");
			test.add(new SkipList(numVars, vals, h));
		}
		s.close();
		logWriter.println("Printing each created list to output_i.txt");
		for(int i = 1; i <= test.size(); i++) {
			String f = "output_";
			f += i;
			f += ".txt";
			PrintWriter printer = new PrintWriter(new File(f));
			printer.print(test.get(i-1).printList());
			printer.close();
		}
		logWriter.println("Beginning searching for items in each list.");
		logWriter.println("Reading search.txt");
		s = new Scanner(new File("search.txt"));
		int numberOfSearchItems = s.nextInt();
		ArrayList<Integer> search = new ArrayList<Integer>();
		for(int i = 0; i < numberOfSearchItems; i++) {
			search.add(s.nextInt());
		}
		s.close();
		PrintWriter printer = new PrintWriter(new File("index.txt"));
		logWriter.println("Begin searching through each list.");
		for(int i = 0; i < test.size(); i++) {
			String out = "List ";
			out += (i+1);
			out += ": ";
			for(int val : search) {
				logWriter.println("    Searching for value " + val + " in List " + (i+1));
				out += test.get(i).lookupValueWithLog(val, logWriter);
				out += " ";
			}
			printer.println(out);
		}
		printer.close();
		
		// PART 2 OF PROJECT
		System.out.println("Part 2");
		logWriter.println("\r\n========== PART TWO ==========\r\n");
		
		logWriter.println("Beginning Integrity Verification part of project.");
		// read in file and contents
		// First modify the skip lists output in part 1
		// open input file
		
		logWriter.println("Reading from input.txt");
		s = new Scanner(new File("input_2.txt"));
		
		// read input file
		logWriter.println("    Reading key.");
		key = s.nextLine();		// read key
		System.out.println("key: "+ key); 
		logWriter.println("    Reading number of test cases.");
		numTest = s.nextInt();		// read num test cases
		test = new ArrayList<SkipList>();
		System.out.println("Number of Test Cases: "+ numTest);
		for (int j = 0 ; j < numTest; j++) {
			//logWriter.println("    Reading values for test case " + (j+1));
		    int numVars = s.nextInt(); 		// length of each test case
			ArrayList<Integer> vals = new ArrayList<Integer>();
			for(int i = 0; i < numVars; i++) {
				vals.add(s.nextInt());
			}
			logWriter.println("    Creating hash.");
			HashedObject h = new HashedObject(vals,key,false); //if true, then doing part 1
			logWriter.println("    Creating Skip List");
			SkipList sl = new SkipList(numVars, vals, h);
			sl.modify(1111);
			test.add(sl);
		}
		s.close();
		
		logWriter.println("Printing each modified list to output_m.txt");
		printer = new PrintWriter(new File("output_m.txt"));
		printer.println(key);
		for(int i = 0; i < test.size(); i++) {
			printer.print(test.get(i).printList());
			//test.get(i).modify(10);
			//printer.print(test.get(i).printList());
			//printer.println();
		}
		printer.close(); 
		
		// Reading input
		logWriter.println("Reading from input3.txt");
		s = new Scanner(new File("output_m.txt"));
		key = s.nextLine();
		System.out.println("key read from ouput_m.txt: "+ key); 
		
		ArrayList <ArrayList <Integer>> aa = new ArrayList <ArrayList <Integer>> ();
		s.nextLine();
		while (s.hasNextLine()){
			String a = s.nextLine();
			a = a.substring(9);
			a = a.trim();
			String b[] = a.split("\\s+");
			if (a.length() > 0){
				ArrayList <Integer> l = new ArrayList <Integer> ();
				for (int i = 0; i < b.length; i++){
					l.add(Integer.parseInt(b[i]));
					//System.out.println(Integer.parseInt(b[i]));
				}
				aa.add(l);
			}
		}
		
		// Add last level
		
		aa.add(null);
		System.out.println(aa.size());
		
		SkipList sl = new SkipList(aa, key);
		//System.out.println("embedded hash: " + sl.embeddedHash.substring(0, 15));
		//System.out.println(sl.printList());
		if (sl.corrupted(key)){
			System.out.print("Index of the corrupted item: ");
			System.out.println(sl.pinpointCorruptedItem(key));
		}
		else 
			System.out.println("Not corrupted");
		// create skip list */
		logWriter.close();
	}

}
