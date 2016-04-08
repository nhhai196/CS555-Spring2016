import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashedObject {
	private int bitPosition;
	private String input;
	private byte[] key;
	private String hash;
	public HashedObject(String i, String k) 
	    throws UnsupportedEncodingException, 
	            NoSuchAlgorithmException, InvalidKeyException
	{
	    bitPosition = 0;
	    input = new String(i);
	    key = getKeyByte(k); //convert string key into bye key
	    hash = hmac_sha1(input, key);
	}
	//convert string key "1000100101010" to byte
	private byte[] getKeyByte(String key) throws InvalidKeyException{
	byte[] result;
	
	if(key.length()%8 != 0)
	    throw new InvalidKeyException("length of key must be multiple of 8");
	
	String[] keySplit = key.split("(?<=\\G.{8})"); //split every 8 characters
	result = new byte[keySplit.length]; 
	for (int i = 0; i < keySplit.length; i++)
	{
	    result[i] = (byte) Integer.parseInt(keySplit[i],2);
	}
	return result;
	}
	
	//convert bytes[] into bitstring array
	private String toBinary(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
		    sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
		return sb.toString();
	}
	
	//hash with byte key
	public String hmac_sha1(String input, byte[] keyString) 
	throws UnsupportedEncodingException, NoSuchAlgorithmException, 
	       InvalidKeyException 
	{
	
	SecretKeySpec key = new SecretKeySpec(keyString, "HmacSHA1");
	Mac mac = Mac.getInstance("HmacSHA1");
	mac.init(key);
	byte[] bytes = mac.doFinal(input.getBytes("ASCII"));
	return toBinary(bytes);
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
	
	public byte[] getKey(){
		return key;
	}
}
