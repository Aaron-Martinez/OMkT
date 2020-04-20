//package org.uga.rnainformatics.oskt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class ForkLayers1 extends RecursiveAction {

	Omkt1 Omkt;
	int high;
	int low;
	byte bagSize;
	short n;
	int threshold;

	public ForkLayers1(Omkt1 Omkt, short n, byte bagSize, int low, int high, int threshold) {
		this.Omkt = Omkt;
		this.n = n;
		this.bagSize = bagSize;
		this.low = low;
		this.high = high;
		this.threshold = threshold;
	}

	@Override
	protected void compute() {

		int size = high - low;
		if(size < threshold) {
			short[] B = new short[bagSize];
			for(long i = low; i < high; i++) {
				B = Key1.retrieveB(bagSize, n, i);
				ArrayList<Byte> iList= new ArrayList<Byte>();
				// Check for any bags with repeated vertices or vertices in non-ascending order and skip them
				if(Utilities.isStrictlyIncreasing(B)) {
					for(byte m = 0; m < Math.pow(2, bagSize+1); m++) {
						byte Isize = (byte) (bagSize+1);
						boolean[] I = Key1.retrieveI(Isize, m);
						I = Utilities.adjustIs(I, B, n);
						byte adjustedIKey = Key1.computeI_key(bagSize, n, I);
						if(! iList.contains(adjustedIKey)) {
							iList.add(adjustedIKey);
							int layer = Utilities.computeLayer(B, I, n);
							Omkt.assignLayer(B, I, layer);
						}
					}
				}
			} // for numBags
		}
		else {
			int mid = (high+low)/2;
			ForkJoinTask.invokeAll(new ForkLayers1(Omkt, n, bagSize, low, mid, threshold), new ForkLayers1(Omkt, n, bagSize, mid, high, threshold));
		}
	}

	public static void preComputeLayers(Omkt1 Omkt, short n, int bag_Size, int threshold) {
		byte bagSize = (byte)bag_Size;
		int ps = Runtime.getRuntime().availableProcessors();
		ForkJoinPool fjpool = new ForkJoinPool(ps > 8 ? ps - ps / 8 : ps);
		if(threshold < 10)  threshold = 10;
		int numBags = (int) Math.pow(n, bagSize);
		try {
			fjpool.invoke(new ForkLayers1(Omkt, n, bagSize, 0, numBags, threshold));
		}
		finally {
			fjpool.shutdown();
		}
	}
}
