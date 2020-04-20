import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Utilities {  // for single backbone
	protected static float cutoff;


	// From Test.java  *might not be updated
	public static int[] convertIntegers(ArrayList<Integer> Y_list) {
	    int[] Y = new int[Y_list.size()];
	    Iterator<Integer> iterator = Y_list.iterator();
	    for (int i = 0; i < Y.length; i++)
	    {
	        Y[i] = iterator.next().intValue();
	    }
	    return Y;
	} // convertIntegers

	public static short[] convertShorts(ArrayList<Short> Y_list) {
	    short[] Y = new short[Y_list.size()];
	    Iterator<Short> iterator = Y_list.iterator();
	    for (int i = 0; i < Y.length; i++)
	    {
	        Y[i] = iterator.next().shortValue();
	    }
	    return Y;
	} // convertIntegers


	// From Test2.java     *might not be updated
	public static int computeLayer3(int[] B, ArrayList<ArrayList<Integer>> vertices, boolean[] I1, boolean[] I2) {
		int layer = 0;
		int b1 = 0, b2 = 0;
	    for(int b = 0; b < B.length; b++) {
	    	if(B[b] <= vertices.get(0).get(vertices.get(0).size()-1))
	    		b1++;
	    	else
	    		b2++;
	    }
	    int[] B1 = null;
    	B1 = new int[b1];
    	for(int i = 0; i < b1; i++) {
    		B1[i] = B[i];
    	}

	    int[] B2 = null;
    	B2 = new int[b2];
    	for(int i = b1, j = 0; i < B.length; i++, j++) {
    		B2[j] = B[i];
    	}

	    int v = 0;
		for(int b = 0; b < b1; b++) {
			if(I1[b]) {
				int vertex = vertices.get(0).get(v);
				while(vertex < B1[b]) {
					layer++;
					v++;
					if(v >= vertices.get(0).size())  break;
					vertex = vertices.get(0).get(v);
				}
			}
			else { v = vertices.get(0).indexOf(B1[b]); }
			v++;
		}
		if(I1[b1]) {
			if(b1 != 0) {
				v = vertices.get(0).indexOf(B1[b1-1]) + 1;
				while(v < vertices.get(0).size()) {
					layer++;
					v++;
				}
			}
			else {
				layer += vertices.get(0).size();
			}
		}
		System.out.println("Layer(1) is: " + layer);
	 // for first backbone
	    /////////////CHANGES MADE HERE: transfer them over to Optimal Skt////////////////////
		v = 0;
		for(int b = 0; b < b2; b++) {
			if(I2[b]) {
				int vertex = vertices.get(1).get(v);
				while(vertex < B2[b]) {
					layer++;
					v++;
					if(v >= vertices.get(1).size())  break;
					vertex = vertices.get(1).get(v);
				}
			}
			else { v = vertices.get(1).indexOf(B2[b]); }
			v++;
		}
		if(I2[b2]) {
			if(b2 != 0) {
				v = vertices.get(1).indexOf(B2[b2-1]) + 1;
				while(v < vertices.get(1).size()) {
					layer++;
					v++;
				}
			}
			else {
				layer += vertices.get(1).size();
			}
		}
		System.out.println("Layer(2) is " + layer);
	 // for second backbone
		return layer;
	} // computeLayer3


	// From Test3.java     *might not be updated
	static void findI1s(final int length, int[] B) {
	    final long max = 1 << length;
	    for (long i = 0; i < max; i++) {
	        long currentNumber = i;
	        final int[] buffer = new int[length];
	        boolean[] I1 = new boolean[length]; // I1 to be passed to next method
	        int bufferPosition = buffer.length;
	        while (bufferPosition > 0) {
	            buffer[--bufferPosition] = (int) (currentNumber & 1);
	            currentNumber >>>= 1;
	        }
	        for(int x = 0; x < buffer.length; x++) {
	        	if(buffer[x] == 1) {
	        		I1[x] = true;
	        	}
	        	else {
	        		I1[x] = false;
	        	}
	        	System.out.print(buffer[x]);
	        }
		    System.out.println();
	    }
	} // findI1s

	
	public static boolean[] adjustIs(boolean[] I, short[] B, short n) {
		if((B.length > 0) && (B[0] == 0)) {
			I[0] = false;
		}
		for(short b = 0; b < B.length-1; b++) {
			if(B[b] == B[b+1] - 1) {
				I[b+1] = false;
			}
		}
		if((B.length > 0) && (B[B.length-1] == n-1)) {
			I[B.length] = false;
		}
		for(int p = B.length+1; p < I.length; p++) {
			I[p] = false;
		}
		return I;
	} // adjustIs


	public static int[] adjustChoices(int[] choices, byte k, boolean[] I_A) {
		for(int i = 0; i < choices.length; i++) {
			if(choices[i] > 1) {
				choices[i] = choices[i]-1;
				break;
			}
			else {
				if(I_A[i]) {
					choices[i] = (i == 0 || i == k+1) ? k : k-1;
				}
			}
		}
		return choices;
	}


	public static boolean isBaseCase(boolean[] I) {
		for(boolean i : I) {
			if(i) return false;
		}
		return true;
	}
	public static boolean isBaseCase(boolean[] I1, boolean[] I2) {
		for(int i = 0; i < I1.length; i++) {
			if(I1[i] || I2[i]) return false;
		}
		return true;
	}

	public static long factorial(int n) {
		long result = 1;
		for(int i = 1; i <= n; i++) {
			result = result * i;
		}
		return result;
	} // factorial


	public static short[] createY(short[] B, boolean[] I, short n) {
		int bag_Size = B.length;
		ArrayList<Short> Y_List = new ArrayList<Short>();
		if(I[0]) {
			for(short b = 0; b < B[0]; b++) {
				Y_List.add(b);
			}
		}
		for(int i = 1; i < bag_Size; i++) {
			if(I[i]) {
				for(short b = (short) (B[i-1]+1); b < B[i]; b++) {
					Y_List.add(b);
				}
			}
		}
		if(I[bag_Size]) {
			for(short b = B[bag_Size-1]; b < n; b++) {
				Y_List.add(b);
			}
		}
		short[] Y = Utilities.convertShorts(Y_List);
		return Y;
	}


	public static short[] createX(short[] B, short y, short numVertices) {   /////////////////
	// find which backbone and which x's border y. create set X
		short[] X = null;
		ArrayList<Short> X_List = new ArrayList<Short>();
		int interval = -1;
		short lastVertex = (short)(numVertices-1);

		if(y >= 0 && y < B[0])  interval = 0;
		else if (y > B[B.length-1]) {
			interval = B.length;
		}
		else {
			for(int i = 0; i < B.length-1; i++) {
				if(B[i] < y && y < B[i+1]) {
					interval = i+1;
				}
			}
		}
		for(int b = 0; b < B.length; b++) {
			if(interval == 0 && b == 0) {
				if(B[b] > lastVertex) {
					X_List.add(B[b]);
				}
				else {
					continue;
				}
			}
			else if(interval == B.length && b == B[B.length-1]) {
				continue;
			}
			if(b == interval) {
				if(B[b] > lastVertex) {
					X_List.add(B[b]);
				}
				else {
					continue;
				}
			}
			else if(b == interval-1) {
				continue;
			}
			else {
				X_List.add(B[b]);
			}
		}
   		X = convertShorts(X_List);
		return X;
	} // createX


	public static short[] createA(short[] B, short x, short y) {
		short[] A = new short[B.length];
		A[0] = y;
		for(int a = 1, b = 0; a < A.length;) {
			if(B[b] == x) b++;
			else {
				A[a] = B[b];
				a++;
				b++;
			}
		}
		Arrays.sort(A);
		return A;
	} // createA

	public static short[] createC(short[] A, short y) {
		short[] C = new short[A.length-1];
		for(int a = 0, c = 0; a < A.length && c < C.length; a++) {
			if(A[a] != y) {
				C[c] = A[a];
				c++;
			}
		}
		return C;
	} // createC


	public static short[][] createCp(short[] A, int k, int bagSize) {
		short[][] Cp = new short[k+1][bagSize];
		for(int a = 0; a < A.length; a++) {
			Cp[a] = Utilities.createC(A, A[a]);
		}
		return Cp;
	}

	public static void printArray(int[] array) {
		for(int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}

	public static void printArray(short[] array) {
		for(int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}

	public static void printArray(short[] array, int offset) {
		for(int i = 0; i < array.length; i++) {
			int elem = array[i] + offset;
			System.out.print(elem + " ");
		}
		System.out.println();
	}

	public static void printArray(boolean[] array) {
		for(int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
		System.out.println();
	}

	public static void printArray(boolean[][] array) {
		for(int i = 0; i < array.length; i++) {
			System.out.print("|");
			for(int j = 0; j < array[i].length; j++) {
				System.out.print(array[i][j] + "\t");
			}
			System.out.println("|");
		}
	}

	public static void printArray(int[][] array) {
		for(int i = 0; i < array.length; i++) {
			System.out.print("|");
			for(int j = 0; j < array[i].length; j++) {
				System.out.print(array[i][j] + "\t");
			}
			System.out.println("|");
		}
	}

	public static void printArray(short[][] array) {
		for(int i = 0; i < array.length; i++) {
			System.out.print("|");
			for(int j = 0; j < array[i].length; j++) {
				System.out.print(array[i][j] + "\t");
			}
			System.out.println("|");
		}
	}


	public static short findInterval(short [] B, short y){
		short interval = -1;
		for(short i = 0; i<B.length; i++) {
			if(y < B[i]) {
				interval = i;
				return interval;
			}
		}
		if(interval == -1) interval = (short)B.length; // case for when y is greater than last element in array
		return interval;
	} // end of findInterval


	public static short findxIndex(short[] B, short x) {
		short xIndex = -1;
		for(short b = 0; b < B.length; b++) {
			if(B[b] == x)  xIndex = b;
		}
		return xIndex;
	} // findxIndex


	public static float newEdges(short y, short[] C, InputData input) {
		float newEdges = 0;
		for(int c = 0; c < C.length; c++) {
			newEdges += input.getPairwiseMI(y, C[c]);  // (1) Edge weight sum  //// change this to getEdge
		}
		return newEdges;
	}

	public static float rootEdges(short[] C, InputData input) {
		float rootEdges = 0;
		for(int c = 0; c < C.length-1; c++) {
			for(int d = c+1; d < C.length; d++) {
				rootEdges += input.getPairwiseMI(C[c], C[d]);   //// change this to getEdge
			}
		}
		return rootEdges;
	}

	public static float function(short y, short[] C, InputData input, int option, float optionArg) {
		switch(option) {
		case 1: return Utilities.newEdges(y, C, input);
		case 2: return Utilities.MI(y, C, input);
		case 3: return Utilities.pairwiseMI(y, C, input);
		case 4: return Utilities.averagePairwiseMI(y, C, input);
		case 5: return Utilities.pairwiseCutoffMI(y, C, input, optionArg);
		default: return 0;
		}
	}

	public static float rootFunction(short[] C, InputData input, int option, float optionArg) {
		if(option == 1) {
			return rootEdges(C, input);
		}
		return rootMI(C, input, option, optionArg);
	}


	public static float MI(short y, short[] C, InputData input) {  // (2) normal MI(y, C)
		return input.getMutualInformation(y, C, C.length);
	}


	public static float pairwiseMI(short y, short[] C, InputData input) {  // (3) maximum single edge pairwise MI
		float value = -1;
		for(int c = 0; c < C.length; c++) {
			float temp = input.getPairwiseMI(y, C[c]);
			if(temp > value)  value = temp;
		}
		return value;
	}


	public static float averagePairwiseMI(short y, short[] C, InputData input) {  // (4) average pairwise MI
		float value = 0;
		for(int c = 0; c < C.length; c++) {
			value += input.getPairwiseMI(y, C[c]);
		}
		value = value / C.length;
		return value;
	}

	
	public static float pairwiseCutoffMI(short y, short[] C, InputData input, float cutoff) {  // (5) maximum single edge pairwise MI with cutoff value
		float value = 0;
		for(int c = 0; c < C.length; c++) {
			float temp = input.getPairwiseMI(y, C[c]);
			if(temp > value)  value = temp;
		}
		if(value < cutoff) {
			return 0;
		}
		return value;
	}

/*
	public static float function6(short y, short[] C, InputData input) {  // (6) normal MI(y, C) with penalty per low pairwise MI
		float penalty = (float) .1;
		float value = input.getMutualInformation(y, C, C.length);
		for(int c = 0; c < C.length; c++) {
			if(input.getPairwiseMI(y, C[c]) < cutoff)  value -= penalty;
		}
		return value;
	}
*/

	public static float rootMI(short[] B, InputData input, int option, float fcnArg) {
		float value = 0;
		for(int b = 1; b < B.length; b++) {
			short y = B[b];
			short[] C = new short[b];
			for(int c = 0; c < b; c++) {
				C[c] = B[c];
			}
			float temp = Utilities.function(y, C, input, option, fcnArg);
			value += temp;
		}
		return value;
	}


	public static short findY(short[] B, short[] A) {  // this method determines which vertex was y during the runtime of the algorithm given only B and A
		short y = -1;
		for(int a = 0; a < A.length; a++) {
			short current = A[a];
			boolean found = false;
			for(int b = 0; b < B.length; b++) {
				if(current == B[b])  found = true;
			}
			if(!found) {
				y = current;
				break;
			}
		}
		return y;
	}


	public static short findY(short[] B, Key1[] keys, short numVertices) {
		short[] B1 = keys[0].retrieveB((byte)B.length, numVertices);
		for(int b = 0; b < B1.length; b++) {
			if(B[b] != B1[b]) {
				return findY(B, B1);
			}
		}
		short[] B2 = keys[1].retrieveB((byte)B.length, numVertices);
		return findY(B, B2);
	}


	public static short[] addElem(short[] B, short y, short interval) {
		short[] A = new short[B.length+1];
		for(int a = 0; a < interval; a++) {
			A[a] = B[a];
		}
		A[interval] = y;
		for(int a = interval + 1; a < A.length; a++) {
			A[a] = B[a-1];
		}
		return A;
	}


	public static short[] addElem(short[] B, short y) {
		short interval = Utilities.findInterval(B, y);
		return Utilities.addElem(B, y, interval);
	}


	public static short[] copyArray(short[] newB, short[] oldB) {
		for(int b = 0; b < newB.length; b++) {
			newB[b] = oldB[b];
		}
		return newB;
	}


	public static boolean[] copyArray(boolean[] newI, boolean[] oldI) {
		for(int i = 0; i < newI.length; i++) {
			newI[i] = oldI[i];
		}
		return newI;
	}


	public static short[][] copyArray2D(short[][] newC, short[][] oldC) {
		for(int c = 0; c < newC.length; c++) {
			newC[c] = Utilities.copyArray(newC[c], oldC[c]);
		}
		return newC;
	}


	public static boolean[][] copyArray2D(boolean[][] newI, boolean[][] oldI) {
		for(int i = 0; i < newI.length; i++) {
			newI[i] = Utilities.copyArray(newI[i], oldI[i]);
		}
		return newI;
	}


	public static double max(double... values) {
        double max = Double.NEGATIVE_INFINITY;
        for(double x : values) {
            if(x > max)  max = x;
        }
        return max;
    }

	public static float max(float... values) {
        float max = Float.NEGATIVE_INFINITY;
        for(float x : values) {
            if(x > max)  max = x;
        }
        return max;
    }
	
	public static long min(long x, long y) {
		if(x < y)  return x;
		else       return y;
	}


	public static boolean isStrictlyIncreasing(short[] B) {
		boolean isIncreasing = true;
		for(int b = 0; b < B.length-1; b++) {
        	for(int c = b+1; c < B.length; c++) {
        		if(B[b] >= B[c]) {
        			isIncreasing = false;
        		}
        	}
        }
		return isIncreasing;
	}


	public static long nCr(long a, long b) {
		float value = 1;
		b = Utilities.min(b, a-b);
		for(int i = 1; i <= b; i++) {
			value /= i;
		}
		for(long i = a - b + 1; i <= a; i++) {
			value *= i;
		}
		long intValue = (long)value;
		return intValue;
	}


	public static int computeLayer(short[] B, boolean[] I, short numVertices) {
		return computeLayer(B, I, 0, numVertices-1);
	}
	public static int computeLayer(short[] B, boolean[] I, int firstV, int lastV) {
		int B_size = B.length;
		int layer = 0;
		if(I[0]) {
			layer += B[0] - firstV;
		}
		for(int b = 1; b < B_size; b++) {
			if(I[b]) {
				layer += B[b] - B[b-1] - 1;
			}
		}
		if(I[B_size]) {
			layer += lastV - B[B_size-1];
		}
		return layer;
	} // computeLayer

} // Utilities