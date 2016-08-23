package schedulers;

import node.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Class: Algorithm
 * Description: Create branch and bound algorithm to solve the problem
 * Variable: numProc (number of processor)
 * @Author Jack Wong
 * Date: 05/08/16
 */
public class BranchAndBound {
	private ArrayList<Node> list;
	private ArrayList<Node> finalList;
	private int numProc;
	private int[] maxProcAlloc;
	private int[] currProcAlloc;
	private int finalFinishTime = Integer.MAX_VALUE;
	public BranchAndBound(int numProc,ArrayList<Node> list){
		this.numProc = numProc;
		this.list=list;
		maxProcAlloc = new int[list.size()];
		currProcAlloc = new int[list.size()];
		for(int i = 0; i < maxProcAlloc.length; i++){
			currProcAlloc[i] = 1;
			if ((i+1)<= numProc){
				maxProcAlloc[i] = i+1;
			}else{
				maxProcAlloc[i] = numProc;
			}
		}
	}
	/**
	 * Calulate the number of unique permutation for a given number of nodes
	 * @param n
	 * @return
	 */
	private long factorial(int n) {
   	 	if (n > 20) throw new IllegalArgumentException(n + " is out of range");
        long fact = 1;
        for (int i = 2; i <= n; i++) {
            fact *= i;
        }
        return fact;
	}
	/**
	 * Gernerate a permutation for given list of nodes, only valid permutation is returned.
	 * @param no of permutation
	 * @param linkedList : input list
	 * @param arrayList : output list
	 * @return
	 */
    private  ArrayList<Node> permutationGenerator(long no, LinkedList<Node> linkedList, ArrayList<Node> arrayList) {
        if (linkedList.isEmpty()) {
        	for(int i=0; i<arrayList.size(); i++){
        		Node n=arrayList.get(i);
        		ArrayList<Node> parents=new ArrayList<Node>(n.getParents().keySet());
        		for(int j=0; j<parents.size(); j++){
        			if(arrayList.indexOf(parents.get(j))>i){
        				return null;
        			}
        		}
        		
        	}
        	return  arrayList;
        }
        long subFactorial = factorial(linkedList.size() - 1);
        int index=(int) (no / subFactorial);
        arrayList.add(linkedList.get(index));
        linkedList.remove(index);
        return permutationGenerator((int) (no % subFactorial), linkedList, arrayList);
    }
    
    /**
     * Find all permutations for given list of nodes// this method is exposed for the sake of testing
     * @return
     */
    public ArrayList<ArrayList<Node>> permutation(){
    	ArrayList<ArrayList<Node>> permutations=new ArrayList<ArrayList<Node>>();
    	long fact=factorial(list.size());
    	for(int i=0; i<fact; i++){
    		ArrayList<Node> permutation=permutationGenerator(i,new LinkedList<>(list), new ArrayList<Node>() );
    		if(!(permutation==null)){
    			permutations.add(permutation);
    		}
    	}
    	return permutations;
    	
    }

    
    public void incAlloc(int index, int[] currentAlloc){
    	if(index == 0){
    		for(int i: currentAlloc){
    			i = 1;
    		}
    		return;
    	}else if (currentAlloc[index]+1 > maxProcAlloc[index]){
    		currentAlloc[index] = 1;
    		incAlloc(index - 1, currentAlloc);
    	}else{
    		currentAlloc[index] += 1;
    	}
    }
    
    public void testalloc(){
    	
    	do{
    		System.out.println("The ultimate finish time of this permutation is "+finalFinishTime);
    		for(int i = 0;i<currProcAlloc.length;i++){
    			System.out.print(currProcAlloc[i]);
    			list.get(i).setProcessor(currProcAlloc[i]);
    		}
    		System.out.println();
    		System.out.println("The final finish time is "+getFinishTime(list));
    		
    		
    		System.out.println(" \n");
    		int h = getFinishTime(list);
    		
    		/*for (Node node:list){
    			System.out.println("The set processor of "+node.getName()+" is set to be "+node.getProcessor());
    		}*/
    		incAlloc(currProcAlloc.length-1, currProcAlloc);
    	}while(testProc());
    	
    }
    public boolean testProc(){
    	for(int i =0; i < maxProcAlloc.length; i ++){
    		if(1 != currProcAlloc[i]){
    			return true;
    		}
    	}
    	return false;
    }
    
	/**
	 * @param nodelist
	 * @return the final finish time of the list of a specific order and allocation.
	 * This method is to calculate the finish time of the a specific path. It calculates the start time and finish time of each node in the list
	 * and return the final finish time of that path.
	 * 
	 */
	public int getFinishTime(ArrayList<Node> nodelist){
		int[] proc_time = new int[numProc];
		int lastFinishTime = 0;
		ArrayList<Node> returnList = new ArrayList<Node>();
		
		//Loop through all the node in the node list.
		for (Node node:nodelist){
			node.setStartTime(proc_time[node.getProcessor()-1]);
			int startTime = node.getStartTime();
			int finishTime;
			
			//Loop through every parents of the current node and calculate the start time.
			for (Map.Entry<Node, Integer> parent :node.getParents().entrySet()){
				//System.out.println("The current node is "+node.getName()+". The parent node is "+parent.getKey().getName() + " and the value is "+parent.getValue());
				if (node.getProcessor() != parent.getKey().getProcessor()){
					if(startTime < parent.getValue() + parent.getKey().getFinishTime()){
						startTime = parent.getValue() + parent.getKey().getFinishTime();
					}
				}
			}
			
			//Calculate the finish time of the current node and update the finish time
			finishTime = startTime + node.getWeight();
			node.setStartTime(startTime);
			node.setFinishTime(finishTime);
			proc_time[node.getProcessor()-1] = finishTime;
			
			//Not finished. It is to update the bound. If the lastFinishTime exceeds the current finish time, break the for loop and start with other path.
			if (finishTime > lastFinishTime){
				lastFinishTime = finishTime;
				
			}
			returnList.add(node);
			//System.out.println("The current node is "+node.getName() + " the start time is "+node.getStartTime()+" and the finishtime is "+node.getFinishTime()+" and the processor is "+node.getProcessor());
		}
		if (finalFinishTime > lastFinishTime){
			finalFinishTime = lastFinishTime;
			finalList.clear();
			for (int i = 0; i<returnList.size();i++){
				Node node = new Node(returnList.get(i));
				finalList.add(node);
			}
			//Collections.copy(finalList,returnList);
			/*finalList.clear();
			for (int i = 0;i<finalList.size();i++){
				Node node = returnList.get(i);
				finalList.add(node);
			}
			//finalList.addAll(returnList);*/
			System.out.println("Hello world");
		}
		/*for (Node node:finalList){
			System.out.println("The current node is "+node.getName() + " the start time is "+node.getStartTime()+" and the finishtime is "+node.getFinishTime()+" and the processor is "+node.getProcessor());
		}
		for (Node node:returnList){
			System.out.println("The return node is "+node.getName() + " the start time is "+node.getStartTime()+" and the finishtime is "+node.getFinishTime()+" and the processor is "+node.getProcessor());
		}*/
		return lastFinishTime;
		
	}
	public int getFinalFinishTime(){
		return finalFinishTime;
	}
	
	public void setFinalList(ArrayList<Node> finalList){
		this.finalList = finalList;
	}
	public ArrayList<Node> getFinalList(){
		/*for (Node node:finalList){
			System.out.println("The current node is "+node.getName() + " the start time is "+node.getStartTime()+" and the finishtime is "+node.getFinishTime()+" and the processor is "+node.getProcessor());
		}*/
		return finalList;
	}
}
