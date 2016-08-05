import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
	
	
	public static void main(String[] args) throws FileNotFoundException{
		
		ArrayList<Node> list= new ArrayList<Node> ();
		HashMap<String, Integer> map= new HashMap<>();	
		//int numProc=args[1];
		//String fileName=args[0];
		int numProc=4;
		String fileName = "largeinput.dot";
		InputProcessing ip=new InputProcessing(fileName);
		ip.processInput();
		list=ip.getListOfNodes();
		map=ip.getMap();
		
		for(Node n: list){
			System.out.println(n.getName()+ " weight: " +n.getWeight());
		}
		for(String name: map.keySet()){
			int index=map.get(name);
			System.out.println(name +" index: " + index);
		}
		
		
		
		//test case: a2,b3,d3,e2,c1,f4,g1,h2 using largeinput.dot
		ArrayList<Node> nodelist = new ArrayList<Node>();
		nodelist.add(list.get(0));
		nodelist.add(list.get(1));
		nodelist.add(list.get(3));
		nodelist.add(list.get(4));
		nodelist.add(list.get(2));
		nodelist.add(list.get(5));
		nodelist.add(list.get(6));
		nodelist.add(list.get(7));
		
		Algorithm al = new Algorithm(numProc);
		nodelist.get(0).setProcessor(2);
		nodelist.get(1).setProcessor(3);
		nodelist.get(2).setProcessor(3);
		nodelist.get(3).setProcessor(2);
		nodelist.get(4).setProcessor(1);
		nodelist.get(5).setProcessor(4);
		nodelist.get(6).setProcessor(1);
		nodelist.get(7).setProcessor(2);
		//al.getFinishTime(nodelist);
		System.out.println(al.getFinishTime(nodelist));
		
		
		
		//Other test case: a1, b2, c3, d4 using input.dot
		list.clear();
		fileName = "input.dot";
		InputProcessing ip1=new InputProcessing(fileName);
		ip1.processInput();
		list=ip1.getListOfNodes();
		map=ip1.getMap();
		
		for(Node n: list){
			System.out.println(n.getName()+ " weight: " +n.getWeight());
		}
		for(String name: map.keySet()){
			int index=map.get(name);
			System.out.println(name +" index: " + index);
		}
		
		Algorithm a2 = new Algorithm(numProc);
		list.get(0).setProcessor(1);
		list.get(1).setProcessor(2);
		list.get(2).setProcessor(3);
		list.get(3).setProcessor(4);
		System.out.println(a2.getFinishTime(list));
		
		
	}
	
	
	
}
