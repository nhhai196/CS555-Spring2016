import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.util.ArrayList;
import java.util.Arrays;

public class HashedObject {
	private int bitPosition;
	private ArrayList<Integer> input;
	private byte[] key;
	private String hash;
	String[] hashArray;
	//Constructor for part 2
	public HashedObject(ArrayList<Integer> elements, String k, boolean part1) throws Exception {
		bitPosition = 0;
		hash = ComputeHash(elements, k);
		hashArray = hash.split("(?<=\\G.{160})"); //split hash every 160 characters
		if(part1) //if part 1 is true, hash contains 160 characters.
			hash = hash.substring(0,160);
	}
	
	//get array of LogN + 1 hashes
	public String[] getHashArray() {
		return hashArray;
	}
	
	//convert binary string to bytes
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
	//convert bytes[] into binary string array
	private String toBinary(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
		    sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
		return sb.toString();
	}
//Compute hash
	private String ComputeHash(ArrayList<Integer> elements, String keyString) throws Exception
	{
		int length = elements.size();
		int logn = Integer.toBinaryString(length).length();
		String[] AllHash = new String[logn+1];
		Arrays.fill(AllHash, ""); //fill with empty string
		String result = "";
		for(int i = 0; i < length; i++)
		{
			AllHash[0] += elements.get(i);
			String temp = String.format("%"+logn+"s", Integer.toBinaryString(i)).replace(' ', '0');
			temp = new StringBuilder(temp).reverse().toString(); //reverse string
			for(int j = 1; j < logn+1; j++)
			{	
				if(temp.charAt(j-1) == '1')
					AllHash[j] += elements.get(i);
			}
		}
		
		for(int j = 0; j < logn+1; j++)
		{	
			result += hmac_sha1(AllHash[j], keyString);
		}
		
		return result;

	}
	
	//hash with byte key
	public String hmac_sha1(String input, String keyString)
	throws UnsupportedEncodingException, NoSuchAlgorithmException, 
	       InvalidKeyException 
	{
		byte[] keyByte = getKeyByte(keyString); //get keybyte
		SecretKeySpec key = new SecretKeySpec(keyByte, "HmacSHA1");
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
	
	public byte[] getKey()
	{
		return key;
	}
}
