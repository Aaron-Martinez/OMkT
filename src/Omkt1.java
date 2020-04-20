import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Omkt1 {

	short n;
	byte k;
	int bag_Size;
	int I_Size;
	ArrayList<List<Key1>> layers;
	LookupTable1 MTable;
	InputData input;
	String outputfile;
	BufferedWriter output;
	int option;
	float fcnArg;
	public boolean DEBUG;
	boolean writeOutput = true;

	public Omkt1(OmktDriver driver) {
		n = driver.getNumVertices();
		k = driver.getBk();
		bag_Size = k;
		I_Size = bag_Size+1;
		MTable = new LookupTable1(0, n, k);
		input = driver.getInput();
		outputfile = driver.getDst();
		option = driver.getOption();
		fcnArg = driver.getFcnArg();
		this.DEBUG = driver.DEBUG;
	}
	
	public short getNumVertices() {
		return n;
	}
	
	public ArrayList<List<Key1>> getLayers() {
		return layers;
	}
	
	
	public void run() {
		short[] B = new short[bag_Size];
		boolean[] I = new boolean[I_Size];

		// Keep track of best solution
		short[] bestB = new short[bag_Size];
		boolean[] bestI = new boolean[I_Size];
		float maximum = 0;
		long numBags = (long) Math.pow(getNumVertices(), k);
		if(DEBUG)  System.out.println("Pre-computing layers...");
		long startLayers = System.currentTimeMillis();
		initializeLayers();
		computeLayers(numBags);
		long endLayers = System.currentTimeMillis();
		if(DEBUG) {
			System.out.println("Layers computation finished");
			System.out.println("**Start of algorithm**");
		}
		long startAlgo = System.currentTimeMillis();

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* 		Parallelism instructions:
	* 
	* The following lines are the DP algorithm portion of the program. 
	* (1) To run using fork/join, comment out the code block marked with parallel 
	* streams and uncomment the for loop marked fork/join.
	* (2) To run using parallel streams, comment out the code block marked fork/join
	* and uncomment the parallel streams code block
	* (3) To run the program without parallelism, follow option (2) instructions and
	* then comment out only the line:  .parallelStreams()
	* 
	* Note: fork/join has worked fastest in my testing. Different numbers can be played
	* with on the line:  int threadsPerProcessor = __;  to change the "chunk size"
	* threshold of the fork/join method.
	*/
		

//		getLayers()
//		.forEach(ee -> {
//			if(DEBUG) System.out.print("Layer " + getLayers().indexOf(ee) + " size: " + ee.size());
//			ee
//			.parallelStream()
//			.forEach(e -> {
//				find_opt_SKT(e, getLayers().indexOf(ee));
//			});
//			// try catch
//			if(DEBUG) System.out.println("    -complete");
//		});


		for(List<Key1> layer : getLayers()) {
			int layerNum = getLayers().indexOf(layer);
			if(DEBUG)  System.out.print("Layer " + getLayers().indexOf(layer) + " size: " + layer.size());
			int p = Runtime.getRuntime().availableProcessors();
			int threadsPerProcessor = 30;				// Test different numbers here to look for best results
			final int threshold = (layer.size() / p) / threadsPerProcessor;
//			ForkAlgo1.function(this, layer, getNumVertices(), threshold);
//			ForkAlgo1.function(this, layer, getNumVertices(), threshold, layerNum);
			ForkAlgo1.function(this, 0, layer.size(), threshold, layerNum);
			if(DEBUG)  System.out.println("    -complete");
		}

	////////////////////////////////////////////////////////////////////////////////////////////////////////

		long endAlgo = System.currentTimeMillis();
		
		//  Find the Optimal B and I by searching top layer  //
		// add try catch
		int topLayer = getNumVertices() - bag_Size;
		Key1 myKey = getLayers().get(topLayer).get(0);
		bestB = myKey.retrieveB(bag_Size, getNumVertices());
		bestI = myKey.retrieveI(I_Size);
		maximum = MTable.getValue(myKey).getValue();
		for(int l = 1, size = getLayers().get(topLayer).size(); l < size; l++) {
			myKey = getLayers().get(topLayer).get(l);
			if(maximum < MTable.getValue(myKey).getValue()) {
				maximum = MTable.getValue(myKey).getValue();
				bestB = Utilities.copyArray(bestB, myKey.retrieveB(bag_Size, getNumVertices()));
				bestI = Utilities.copyArray(bestI, myKey.retrieveI(I_Size));
			}
		}
		
		// Print results
		if(DEBUG) {
			System.out.print  ("bestB  = ");  Utilities.printArray(bestB, 1);				
			System.out.print  ("bestI = ");  Utilities.printArray(bestI);
			System.out.println("## Total weight: " + maximum);
		}

		if(writeOutput) {
			writeResults(getNumVertices(), bestB, bestI, maximum);
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		if(DEBUG) {
//			System.out.println("Layers computation took  " + (endLayers - startLayers)/1000 + " seconds");
//			System.out.println("DP algorithm took        " + (endAlgo - startAlgo)/1000 + " seconds");
//		}
	} // run
	
	
	void initializeLayers() {		
		layers = new ArrayList<List<Key1>>();
		for(int L = 1; L <= n-k+1; L++) {
			List<Key1> layer = Collections.synchronizedList(new ArrayList<Key1>());
			layers.add(layer);
		}
	} // initializeLayers
	
	
	void computeLayers(long numBags) {
		if(DEBUG)  System.out.println("NumBags = " + numBags);
		int p = Runtime.getRuntime().availableProcessors();
		int threadsPerProcessor = 30;				// Test different numbers here to look for best results
		int threshold = (int) ((numBags / p) / threadsPerProcessor);
		ForkLayers1.preComputeLayers(this, getNumVertices(), bag_Size, threshold);
	} // computeLayers
	
	
	public void assignLayer(short[] B, boolean[] I, int layer) {
		Key1 newKey = new Key1(k, getNumVertices(), B, I);
		getLayers().get(layer).add(newKey);
	}

//	public void assignLayer(Key1 key, int layer) {
//		getLayers().get(layer).add(key);
//	}

	public void iterateKeys(int start, int end, int layerNum) {
		List<Key1> layer = getLayers().get(layerNum);
		for(int i = start; i < end; i++) {
			Key1 key = layer.get(i);
			find_opt_SKT(key, layerNum);
		}
	}

	public void find_opt_SKT(Key1 key, int layerNum) {
		short[] B = key.retrieveB(k, n);
//		short[] B = key.getB();
		boolean[] I = key.retrieveI((byte)(k+1));
		if(Utilities.isBaseCase(I)) {
			MTable.setValue(key, new TableData1(0));
			return;
		}		
		float sum = 0;
		float max = Float.NaN;
		short[][] Cp;
		short[][] bestCp;
		boolean[][] Ip;
		boolean[][] bestIp;
		float rootValue = Utilities.rootFunction(B, input, option, fcnArg);
		//  Keep track of best solution  //
		bestCp = new short[k+1][bag_Size];
		bestIp = new boolean[k+1][I_Size];
		
		short[] Y = createY(B, I, getNumVertices(), layerNum);
		for(short y = 0; y < Y.length; y++) {
			float value = Utilities.function(Y[y], B, input, option, fcnArg);
			short interval = Utilities.findInterval(B, Y[y]);
			short[] A = new short[bag_Size+1];
			boolean[] I_A = new boolean[I_Size+1];
			
			A = Utilities.addElem(B, Y[y], interval);
			for(int i = 0; i < interval; i++) { 
				I_A[i] = I[i];
			}
			I_A[interval] = true;
			I_A[interval+1] = true;
			for(int i = interval + 2; i < I_A.length; i++) {
				I_A[i] = I[i-1];
			}
			I_A = Utilities.adjustIs(I_A, A, getNumVertices());
			Cp = Utilities.createCp(A, k, bag_Size);
			int[] CpKeys = new int[k+1];
			for(int i = 0; i < CpKeys.length; i++) {
				CpKeys[i] = (int)Key1.computeB_key(k, n, Cp[i]);
			}			
			
			int[] choices = new int[k+2];
			int numChoices = 1;
			if(I_A[0]) { 
				numChoices *= k;
				choices[0] = k;
			}
			else {
				choices[0] = -1;
			}
			for(int i = 1; i < I_A.length-1; i++) {
				if(I_A[i]) {
					numChoices *= (k-1);
					choices[i] = (k-1);
				}
				else {
					choices[i] = -1;
				}
			}
			if(I_A[I_A.length-1]) {
				numChoices *= k;
				choices[I_A.length-1] = k;
			}
			else {
				choices[I_A.length-1] = -1;
			}
			
			for(int i = 0; i < numChoices; i++) {
				Ip = new boolean[k+1][I_Size];
				for(int p = 0; p < k+2; p++) {
					int q = (p == 0 || p == k+1) ? k : k-1;
					if(I_A[p]) {
						int r = choices[p]-1;
						if(p+1+r < k+1) {
							Ip[p+1+r][p] = true;
						}
						else {
							Ip[p+r-q-1][p-1] = true;
						}
					}
				}
				choices = Utilities.adjustChoices(choices, k, I_A);
				for(int s = 0; s < Cp.length; s++) {
					sum += MTable.getValue(CpKeys[s], Ip[s]).getValue();
				}
				
				if(Y.length == (int)(getNumVertices() - bag_Size)) {  // If we are on the last layer, add up score of starting bag
					sum += rootValue;
				}
				else {
					sum += value;
				}
				
				if(max <= sum || Float.isNaN(max))  {
					max = sum;
					bestCp = Utilities.copyArray2D(bestCp, Cp);
					bestIp = Utilities.copyArray2D(bestIp, Ip); 
				}
				sum = 0;	
			}		
		} // for y in Y
		
		
		Key1[] keyArray = new Key1[bestCp.length];
		for(int i = 0; i < keyArray.length; i++)  {
			bestIp[i] = Utilities.adjustIs(bestIp[i], bestCp[i], n);
			keyArray[i] = new Key1(k, getNumVertices(), bestCp[i], bestIp[i]);
		}
		TableData1 data = new TableData1(max, keyArray);
		MTable.setValue(key, data);
				
	} // find_opt_SKT
	
	
	/*
	 * Returns an array of shorts that represents the vertices left to choose from with the
	 * given B, I
	 * More detailed description in algorithm PDF
	 */
	public static short[] createY(short[] B, boolean[] I, short n, int layerNum) {
		int bag_Size = B.length;
		short[] Y = new short[layerNum];
		short y = 0;
//		ArrayList<Short> Y_List = new ArrayList<Short>();
		if(I[0]) {
			for(short b = 0; b < B[0]; b++) {
//				Y_List.add(b);
				Y[y] = b;
				y++;
			}
		}
		for(int i = 1; i < bag_Size; i++) {
			if(I[i]) {
				for(short b = (short) (B[i-1]+1); b < B[i]; b++) {
//					Y_List.add(b);
					Y[y] = b;
					y++;
				}
			}
		}
		if(I[bag_Size]) {
			for(short b = (short)(B[bag_Size-1]+1); b < n; b++) {
//				Y_List.add(b);
				Y[y] = b;
				y++;
			}
		}
//		short[] Y = Utilities.convertShorts(Y_List);
		return Y;
	} // createY

	
	// Write results to output file //
	void writeResults(short n, short[] C, boolean[] I, float maximum) {

		// We calculate the root bag differently
		Key1[] keys = MTable.getValue(C, I).getKeys();
		short y = Utilities.findY(C, keys, n);
		short[] B = Utilities.addElem(C, y);
	
		float rootValue = Utilities.rootFunction(B, input, option, fcnArg);
		String rootStr = String.format("%.4f", rootValue);
		output = null;
		try {
			File file = new File(outputfile);
			FileWriter fw = new FileWriter(file);
			output = new BufferedWriter(fw);
		    for(int b = 0; b < B.length; b++) {
	    		String vStr = Integer.toString(B[b]+1);
	    		output.write(vStr);
	    		output.write("\t");
	    	}
		    output.write(rootStr);
		    output.flush();
		    output.write("\n");
		    for(Key1 key : keys) {
				writeResults(n, key.retrieveB(k, n), key.retrieveI((byte)(k+1)));
			}
		} // try
		catch (IOException e) {
		    e.printStackTrace();
		}
	} // writeResults
		
		
	void writeResults(short n, short[] C, boolean[] I) throws IOException { 
		if(! Utilities.isBaseCase(I)) {
			Key1[] keys = MTable.getValue(C, I).getKeys();
			short y = Utilities.findY(C, keys, n);
			float value = Utilities.function(y, C, input, option, fcnArg);
			for(int c = 0; c < C.length; c++) {
				short v = C[c];
				String vStr = Integer.toString(v+1);
				output.write(vStr);
				output.write("\t");
			}
			String yStr = Integer.toString(y+1);
			String valueStr = String.format("%.4f", value);
			output.write(yStr);
			output.write("\t");
			output.write(valueStr);
			output.write("\n");
			output.flush();
			
			for(Key1 key : keys) {
				writeResults(n, key.retrieveB(k, n), key.retrieveI((byte) (k+1)));
			}
		} // if(!baseCase)
	} // writeResults

} // Omkt1
