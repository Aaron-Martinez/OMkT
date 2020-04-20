
public class LookupTable1 {  // for single backbone


	short n;
	byte k;
	TableData1[][] table;
	
	public LookupTable1(int start, int end, int k) {
		this.n = (short) (end);
		this.k = (byte) k;
		table = new TableData1[(int) Utilities.nCr(n, k)][(int) Math.pow(2, k+1)];
	}
	
	public void setValue(short[] B, boolean[] I, TableData1 data) {
		int bValue = (int) Key1.computeB_key(k, n, B);
		byte iValue = Key1.computeI_key(k, n, I);
		setValue(bValue, iValue, data);
	} // setValue
	
	public void setValue(Key1 key, TableData1 data) {
		int bValue = (int)key.getB_key();
		byte iValue = key.getI_key();
		try{
		setValue(bValue, iValue, data);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("\nbValue = " + bValue);
			System.out.println("iValue = " + iValue);
			Utilities.printArray(key.retrieveB(k, n));
			Utilities.printArray(key.retrieveI(k));
			System.exit(1);
		}
	}
	
	public void setValue(int bValue, boolean[] I, TableData1 data) {
		byte iValue = Key1.computeI_key(k, n, I);
		setValue(bValue, iValue, data);
	}
	
	public void setValue(short[] B, byte iValue, TableData1 data) {
		int bValue = (int) Key1.computeB_key(k, n, B);
		setValue(bValue, iValue, data);
	}
	
	public void setValue(int bValue, byte iValue, TableData1 data) {
		table[bValue][iValue] = data;
	}
	
	public TableData1 getValue(short[] B, boolean[] I) {
		int bValue = (int) Key1.computeB_key(k, n, B);
		byte iValue = Key1.computeI_key(k, n, I);
		return getValue(bValue, iValue);
	} // getValue
	
	public TableData1 getValue(Key1 key) {
		int bValue = (int)key.getB_key();
		byte iValue = key.getI_key();
		return getValue(bValue, iValue);
	}
	
	public TableData1 getValue(int bValue, boolean[] I) {
		byte iValue = Key1.computeI_key(k, n, I);
		return getValue(bValue, iValue);
	}
	
	public TableData1 getValue(short[] B, byte iValue) {
		int bValue = (int) Key1.computeB_key(k, n, B);
		return getValue(bValue, iValue);
	}
		
	public TableData1 getValue(int bValue, byte iValue) {
		return table[bValue][iValue];
	}
	
} // LookUpTable1