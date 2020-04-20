
public class Key1 {  // for single backbone

//	public short[] B;
	private long B_key;
	private long B_altKey;
	private byte I_key;
	
	public Key1(byte k, short numVertices, short[] B, boolean[] I) {
		setB_key(computeB_key(k, numVertices, B));
		setI_key(computeI_key(k, numVertices, I));
		setB_altKey(computeB_altKey(k, numVertices, B));
//		this.B = new short[B.length];
//		for(int b = 0; b < B.length; b++) {
//			this.B[b] = B[b];
//		}
	}
	
	
	public static short[] retrieveB(int bagSize, short numVertices, long B_key) {
		short[] B = new short[bagSize];
		long bKey = B_key;
		for(int b = B.length-1; b >= 0; b--) {
			B[b] = (short) (bKey % numVertices);
			bKey /= numVertices;
		}
		return B;
	}
	
	public static short[] retrieveB(byte bagSize, short numVertices, long B_key) {
		short[] B = new short[bagSize];
		long bKey = B_key;
		for(int b = B.length-1; b >= 0; b--) {
			B[b] = (short) (bKey % numVertices);
			bKey /= numVertices;
		}
		return B;
	}
	
	public short[] retrieveB(int bagSize, short numVertices) {
		return retrieveB(bagSize, numVertices, getB_altKey());
	}
	
	public short[] retrieveB(byte bagSize, short numVertices) {
		return retrieveB(bagSize, numVertices, getB_altKey());
	}
	
	public static boolean[] retrieveI(byte Isize, byte I_key) {
		boolean[] I = new boolean[Isize];
		for(int i = I.length-1; i >= 0; i--) {
			if(((I_key >> i) & 1) == 1) {
				I[i] = true;
			}
			else {
				I[i] = false;
			}
		}
		return I;
	}
	
	public boolean[] retrieveI(int I_Size) {
		return Key1.retrieveI((byte)I_Size, getI_key());
	}
	
	public boolean[] retrieveI(byte I_Size) {
		return Key1.retrieveI(I_Size, getI_key());
	}
	
	public static long computeB_altKey(byte k, short numVertices, short[] B) {
		long bAltKey = 0;
		for(int b = 0; b < B.length; b++) {
			bAltKey += Math.pow(numVertices, b) * B[k-b-1];
		}
		return bAltKey;
	}
	
	public static long computeB_key(byte k, short numVertices, short[] B) {
//		if(k == 2) {
//			return Utilities.nCr(numVertices, k) - (numVertices-B[0])*(numVertices-B[0]-1)/2 + (B[1]-B[0]-1);
//		}

		long bKey = 0;
		int row = numVertices, col = k;
		bKey += Utilities.nCr(row, col) - Utilities.nCr(row - B[0], col);
		row = row - B[0] - 1;
		col--;
		for(int i = 1; i < k; i++) {
			int numSkips = B[i] - B[i-1] - 1;
			bKey += Utilities.nCr(row, col) - Utilities.nCr(row - numSkips, col);
			row = row - numSkips - 1;
			col--;
		}
		return bKey;

/*
		long bKey = Utilities.nCr(numVertices, k) - Utilities.nCr(numVertices-B[0], k);
		for(int i = 0; i < k-2; i++) {
			bKey += (Utilities.nCr(numVertices-B[i]-1, k-i-1) - Utilities.nCr(numVertices-B[i+i], k-i-1));
			
//			float temp1 = 1 / Utilities.factorial(k-i-1);
//			float temp2 = temp1;
//			for(int j = numVertices-B[i]-k+i+1, m = numVertices-B[i]-1; j <= m; j++) {
//				temp1 *= j;
//			}
//			for(int j = numVertices-B[i+i]-k+i+2, m = numVertices-B[i+1]; j <= m; j++) {
//				temp2 *= j;
//			}
//			bKey += (long)temp1 + (long)temp2;
		}

		bKey += (B[k-1] - B[k-2] - 1);
		return bKey;
*/
	}
	
	public static long PT(long a, long b) {  // returns the number at row a and column b in Pascal's Triangle (assuming they start at 1 not 0)
		return Utilities.nCr(a-1, b-1);
	}
	
	public static byte computeI_key(byte k, short numVertices, boolean[] I) {
		byte iKey = 0;
		for(int i = I.length-1; i >= 0; i--) {
			if(I[i]) {
				iKey += Math.pow(2, i);
			}
		}
		return iKey;
	}
	
	@Override
	public boolean equals(Object other) {
		boolean equal = false;
	    if((other == null) || (getClass() != other.getClass())){
	        equal = false;
	    }
	    else {
	    	Key1 otherKey = (Key1)other;
//	    	equal = B_key == otherKey.getB_key() && I_key == otherKey.getI_key();
	    	equal = B_altKey == otherKey.getB_altKey() && I_key == otherKey.getI_key();
	    }
		return equal;
	}

	public long getB_key() {
		return B_key;
	}

	private void setB_key(long b_Key) {
		B_key = b_Key;
	}
	
	public long getB_altKey() {
		return B_altKey;
	}

	private void setB_altKey(long b_altKey) {
		B_altKey = b_altKey;
	}

	public byte getI_key() {
		return I_key;
	}

	private void setI_key(byte i_key) {
		I_key = i_key;
	}

//	public short[] getB() {
//		short[] B = new short[this.B.length];
//		for(int b = 0; b < B.length; b++) {
//			B[b] = this.B[b];
//		}
//		return B;
//	}
	
} // Key1
