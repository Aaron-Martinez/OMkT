//package org.uga.rnainformatics.oskt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class ForkAlgo1 extends RecursiveAction {

	Omkt1 Omkt;
//	List<Key1> layer;
//	byte k;
	int start;
	int end;
//	short n;
	int threshold;
	int layerNum;

//	public ForkAlgo1(Omkt1 Omkt, List<Key1> layer, short n, int threshold, int layerNum) {
	public ForkAlgo1(Omkt1 Omkt, int start, int end, int threshold, int layerNum) {
		this.Omkt = Omkt;
//		this.layer = layer;
//		this.k = Omkt.k;
//		this.n = n;
		this.start = start;
		this.end = end;
		this.threshold = threshold;
		this.layerNum = layerNum;
	}

	@Override
	protected void compute() {

//		int size = layer.size();
//		if(size <= threshold) {
//			for(Key1 key : layer) {
//				Omkt.find_opt_SKT(key, layerNum);
//			}
//		}
//		else {
//			int half = size/2;
//			List<Key1> list1 = layer.subList(0, half);
//			List<Key1> list2 = layer.subList(half, size);
//			ForkJoinTask.invokeAll(new ForkAlgo1(Omkt, list1, n, threshold, layerNum),
//								   new ForkAlgo1(Omkt, list2, n, threshold, layerNum));
//		}


		int size = end - start;
		if(size <= threshold) {
			Omkt.iterateKeys(start, end, layerNum);
		}
		else {
			int mid = (start + end)/2;
//			ForkJoinTask.invokeAll(new ForkAlgo1(Omkt, n, threshold, layerNum),
//					new ForkAlgo1(Omkt, n, threshold, layerNum));
			ForkJoinTask.invokeAll(new ForkAlgo1(Omkt, start, mid, threshold, layerNum),
					new ForkAlgo1(Omkt, mid, end, threshold, layerNum));
		}

	}

//	public static void function(Omkt1 Omkt, List<Key1> layer, short numVertices, int threshold, int layerNum) {
	public static void function(Omkt1 Omkt, int start, int end, int threshold, int layerNum) {
//		ForkJoinPool fjPool = ForkJoinPool.commonPool();
		int ps = Runtime.getRuntime().availableProcessors();
		ForkJoinPool fjPool = new ForkJoinPool(ps > 8 ? ps - ps / 8 : ps);
		if(threshold < 5)  threshold = 5;
		try {
//			fjPool.invoke(new ForkAlgo1(Omkt, layer, numVertices, threshold, layerNum));
			fjPool.invoke(new ForkAlgo1(Omkt, start, end, threshold, layerNum));
		}
		finally {
			fjPool.shutdown();
		}
	}
}