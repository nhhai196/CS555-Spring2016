
public class HashedObject {

	private int bitPosition;
	private String hash;
	
	public HashedObject(String h){
		bitPosition = 0;
		hash = new String(h);
	}
	
	public int getBit() {
		
		// returns 0 or 1 that is the current bit
		int bit;
		try {
			bit = Character.getNumericValue(hash.charAt(bitPosition));
		}
		catch(IndexOutOfBoundsException e) {
			return -1;
		}
		
		bitPosition++;
		
		return bit;
	}
	
	public void resetBitPosition() {
		bitPosition = 0;
	}
	
	public String getHash() {
		return new String(hash);
	}
	
	public boolean compareHash(String comp) {
		if(hash.compareTo(comp) == 0)
			return true;
		return false;
	}
}
