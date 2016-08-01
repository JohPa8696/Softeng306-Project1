import java.util.ArrayList;
import java.util.Scanner;

public class Scheduling {
	public static void main(String[] args){
		
		ArrayList<Node> list= new ArrayList<Node> ();
		int numProc=2;
		int[] lastFinAt= new int[numProc];
		//Scan  and process input
		Scanner scan= new Scanner(System.in);
		scan.nextLine();
		while(scan.hasNext()){
			String line= scan.nextLine();
			System.out.println(line);
			if( line.equals("}")){
				break;
			}
			String[] parts=line.split("\t");
			//System.out.println(parts.length);
			if(!line.contains(">")){
				String name = parts[0].trim();
				parts[2]=parts[2].substring(1,parts[2].length()-3);
				int weight=Integer.parseInt(parts[2].substring(parts[2].lastIndexOf("=")+1).trim());
				Node n= new Node(name, weight);
				list.add(n);
			}else{
				String parent=parts[0].substring(0, parts[0].indexOf(">")-2).trim();
				String child=parts[0].substring(parts[0].indexOf(">")+1).trim();
				parts[1]=parts[1].substring(1,parts[1].length()-3);
				int weight=Integer.parseInt(parts[1].substring(parts[1].lastIndexOf("=")+1).trim());
				Node p= null;
				Node c= null; 
				for(int i =0; i<list.size();++i){
					if (parent.equals(list.get(i).getName())){
						p=list.get(i);
					}else if( child.equals(list.get(i).getName())){
						c=list.get(i);
					}
				}
				p.setChildren(c);
				c.setDependencies(p);
				c.setWeightOfDepencies(weight);
			}
		}
		scan.close();
		//Find optimal path
		/*for(Node n: list){
			System.out.println(n.getName());
			System.out.println(n.getWeight());
			System.out.println(n.getWeightOfDepencies().toString());

		}
		*/
		for( int i=0; i<list.size(); ++i){
			Node n= list.get(i);
			if(n.getDependencies().isEmpty()){
				int earliestFreeProcessor=0;
				int earliestStartTime=lastFinAt[0];
				for(int j=1; j<lastFinAt.length; ++j){
					
					if(lastFinAt[j]<earliestStartTime){
						earliestStartTime=lastFinAt[j];
						earliestFreeProcessor=j;
					}
				}
				n.setProcessor(earliestFreeProcessor);
				lastFinAt[earliestFreeProcessor]+=n.getWeight();
			}
			ArrayList<Node> children=n.getChildren();
			//if it has at least 1 child
			int indexOfN=children.get(0).getDependencies().indexOf(n);
			int additionalCost=children.get(0).getWeight()+children.get(0).getWeightOfDepencies().get(indexOfN);
			int maxAddCost=additionalCost;
			int nextNode=0; //index of children that goes to the same processor as the parent node
			for(int j=1; j<children.size(); ++j){
				Node c=children.get(j);
				indexOfN=c.getDependencies().indexOf(n);
				additionalCost=c.getWeight()+c.getWeightOfDepencies().get(indexOfN);
				if(additionalCost>maxAddCost){
					maxAddCost=additionalCost;
					nextNode=j;
				}
			}
			children.get(nextNode).setProcessor(n.getProcessor());
			lastFinAt[n.getProcessor()]+=children.get(nextNode).getWeight();
			/**
			 * HOW WOULD IT DO IF A HAS MORE THAN 2 CHILDREN
			 */
			
			
			
		}
		
		
		
	}
	
	
	
}
