package edu.neu.coe.info6205.sort.par;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.Random;

class BottomUpPar{

	public int cutOff;
	public int[] array;
	public ExecutorService executorServicePool;

	public BottomUpPar(int cutOff, int[] array, int executorServicePoolSize) {
		this.cutOff = cutOff;
		this.array = array;
		executorServicePool = Executors.newFixedThreadPool(executorServicePoolSize);
	}

	private void merge(int lo,int mid,int hi) {
		if(mid>=hi) return;
		if(array[mid]>=array[mid-1]) return;
		int[] tempSave = new int[hi-lo+1];
		for(int i=lo;i<=hi;i++) {
			tempSave[i-lo] = array[i];
		}
		int l = 0;
		int m = mid-lo;
		int r = m;
		int c=lo;
		while(l<m || r<tempSave.length) {
			if(l>=m) array[c++] = tempSave[r++];
			else if(r>=tempSave.length) array[c++] = tempSave[l++];
			else if(tempSave[l]<=tempSave[r]) array[c++] = tempSave[l++];
			else array[c++] = tempSave[r++];
		}
	}


	public void sort() throws InterruptedException {
		int sortSegStart = 0;
		List<Callable<Void>> tasks = new ArrayList<>();
		//INITIAL SORTING OF cutOff SIZED SEGMENTS USING SYSTEM SORT
		while(sortSegStart<array.length) {
			final int start = sortSegStart;
			final int end = Math.min(sortSegStart+cutOff,array.length);
			tasks.add(new Callable<Void>() {
				public Void call() {
					sort(start,end);
					return null;
				}
			});
			sortSegStart+=cutOff;
		}
		executorServicePool.invokeAll(tasks);

		//Merging all the sorted segments
		int size = cutOff;
		while(size<=array.length) {
			int mergeSegStart = 0;
			while(mergeSegStart<array.length) {
				final int start = mergeSegStart;
				final int mid = Math.min(start+size,array.length-1);
				final int end = Math.min(start+(size*2)-1,array.length-1);
				tasks.add(new Callable<Void>() {
					public Void call() {
						merge(start,mid,end);
						return null;
					}
				});
				mergeSegStart+=size*2;
			}
			executorServicePool.invokeAll(tasks);
			size*=2;
		}
		executorServicePool.shutdown();

	}

	private void sort(int lo,int hi) {
		Arrays.sort(array, lo, hi);
	}
	
	static final int threadCount = 10;
	static final int minArraySize = 100000;
	static final int maxArraySize = 100000000;
	static final int minCutOff = 100000;
	static final int maxCutOff = 100000000;

	public static void main(String[] args) {
Random random = new Random();
		
		try {
			FileWriter writer = new FileWriter("results/parallel_sort/result.csv",false);
			writer.write("ArraySize,ThreadCount,CutOff,TotalTimeTaken\n");
			
			for(int size=minArraySize;size<=maxArraySize;size*=2) {
				for(int cutoff=minCutOff;cutoff<=maxCutOff;cutoff*=2) {
					if(cutoff>size) continue;
					int[] arr = new int[size];
					for(int i=0;i<arr.length;i++) arr[i] = random.nextInt(100000);
					BottomUpPar sorter = new BottomUpPar(cutoff,arr,threadCount);
					long startTime = System.currentTimeMillis();
					sorter.sort();
					long elapsedTime = System.currentTimeMillis()-startTime;
					
					writer.write(size+","+threadCount+","+cutoff+","+elapsedTime+"\n");
				}
			}
			writer.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
} 