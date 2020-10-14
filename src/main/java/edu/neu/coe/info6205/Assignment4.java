package edu.neu.coe.info6205;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import edu.neu.coe.info6205.union_find.UF;
import edu.neu.coe.info6205.union_find.UF_HWQUPC;
import edu.neu.coe.info6205.union_find.WQUPC;
import edu.neu.coe.info6205.util.Benchmark_Timer;

public class Assignment4 {

	private static final int RUNS_PER_EACH_N = 200;
	
	public Assignment4() {
		
	}
	
	public static void main (String[] args) {
		
		//Unused Pre-Function
    	UnaryOperator<Integer> pre = inp -> inp;
    	
    	//Benchmarking below functions
    	Consumer<Integer> consumerFunc1 = inp -> count(inp,1,0);
    	Consumer<Integer> consumerFunc2 = inp -> count(inp,1,1);
    	Consumer<Integer> consumerFunc3 = inp -> count(inp,1,2);
    	
    	//post function unused
    	Consumer<Integer> postFunc = inp -> System.out.print("");
    	
    	//No Compression
    	Benchmark_Timer<Integer> t1 = new Benchmark_Timer<>("WQU Benchmark",pre,consumerFunc1,postFunc);
    	
    	//Path Halving
    	Benchmark_Timer<Integer> t2 = new Benchmark_Timer<>("WQUPH Benchmark",pre,consumerFunc2,postFunc);
    	
    	//Path Compression
    	Benchmark_Timer<Integer> t3 = new Benchmark_Timer<>("WQUPC Benchmark",pre,consumerFunc3,postFunc);

    	try {
			FileWriter fw = new FileWriter("results/union_find/assignment4.csv");
			fw.write("N,Time_Uncompressed,AverageDepth_Uncompressed,Pairs_Uncompressed,Time_PathHalved,AverageDepth_PathHalved,Pairs_PathHalved,Time_Compressed,AverageDepth_Compressed,Pairs_Compressed,\n");
			
			// Run for 7 values of N
			
			for(int i=0;i<7;i++) {
				int k = (int)Math.pow(2, i);
				// Initial value of N = 10
				k = k*10;
				double[] temp;
				
				fw.write(k+",");
				fw.write(t1.run(k,RUNS_PER_EACH_N)+",");
				temp = calculateUnionFinds(k,0);
				
				fw.write(temp[0]+",");
				fw.write(temp[1]+",");
				fw.write(t2.run(k,RUNS_PER_EACH_N)+",");
				temp = calculateUnionFinds(k,1);
				
				fw.write(temp[0]+",");
				fw.write(temp[1]+",");
				fw.write(t3.run(k,RUNS_PER_EACH_N)+",");
				temp = calculateUnionFinds(k,2);
				
				fw.write(temp[0]+",");
				fw.write(temp[1]+"\n");
			}
			fw.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
	}

	
    /**
     * Returns the number of random pairs to connect all nodes.
     */
	
	private static int count(int n, int runs,int pc) {
		int size = n;
		double avg = 0;
		for(int i=0;i<runs;i++) {
			UF h = null;
			if (pc==0) h = new UF_HWQUPC(size,false);
			if (pc==1) h = new UF_HWQUPC(size,true);
			if (pc==2) h = new WQUPC(size);
			int noOfPairs=0;
			while(h.components()>1) {
				int[] temp = generateRandomPairs(size);
				h.connect(temp[0], temp[1]);
				noOfPairs++;
			}
			avg+= noOfPairs;
		}
		avg = avg/runs;
		return (int)avg;
	}
	
    /**
     * Returns a random pair of integers.
     */
	
	private static int[] generateRandomPairs(int n) {
		int[] res = new int[2];
		res[0] = (int)(Math.random()*n);
		res[1] = (int)(Math.random()*n);
		return res;
	}
	
    /**
     * Do union find on array of size n. Then calculate average distance to root
     */
	private static double[] calculateUnionFinds(int n,int pc) {
		int size = n;
		double noOfPairs = 0;
		double[] res = new double[2];
		double depth = 0;
		for(int i=0;i<RUNS_PER_EACH_N;i++) {
			UF h = null;
			if (pc==0) h = new UF_HWQUPC(size,false);
			if (pc==1) h = new UF_HWQUPC(size,true);
			if (pc==2) h = new WQUPC(size);
			while(h.components()>1) {
				int[] temp = generateRandomPairs(size);
				h.connect(temp[0], temp[1]);
				noOfPairs++;
			}
			if(pc==2) depth +=((WQUPC) h).avgDepth();
			else depth +=((UF_HWQUPC) h).avgDepth();
		}
		res[0] = depth/RUNS_PER_EACH_N;
		res[1] = noOfPairs/RUNS_PER_EACH_N;
		return res;
	}

}
