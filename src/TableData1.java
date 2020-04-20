
public class TableData1 {  // for single backbone

	private float value;
	private Key1[] keys;
	
	public TableData1(float value, Key1[] keys) {
		setValue(value);
		setKeys(keys);
	}
	
	public TableData1(float value) {
		setValue(value);
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public Key1[] getKeys() {
		return keys;
	}

	public void setKeys(Key1[] keys) {
		this.keys = keys;
	}

} // TableData1