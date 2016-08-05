import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
	
	
	
	public static void main(String[] args) throws FileNotFoundException{
		
		ArrayList<Node> list= new ArrayList<Node> ();
		HashMap<String, Integer> map= new HashMap<>();	
		//int numProc=args[1];
		//String fileName=args[0];
		int numProc=2;
		String fileName="input.dot";
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
		
		
	}
	
	
	
}
