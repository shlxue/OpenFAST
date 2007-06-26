package org.openfast;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class ByteUtil {
	
	/**
	 * 
	 * @param bitString in the format of space separated 8-bit bytes (i.e. "01010101 10101010") 
	 * @return byte array representation of the bit string
	 */
	public static byte[] convertBitStringToFastByteArray(String bitString)
	{
		if (bitString.length() == 0) return new byte[0];
		String[] bitStrings = bitString.split(" ");
		byte[] bytes = new byte[bitStrings.length];
		for (int i=0; i<bitStrings.length; i++)
		{
			bytes[i] = (byte) Integer.parseInt(bitStrings[i], 2);
		}
		return bytes;
	}

	public static byte[] convertHexStringToByteArray(String hexString) {
		if (hexString.length() == 0) return new byte[0];
		String[] hexStrings = hexString.split(" ");
		byte[] bytes = new byte[hexStrings.length];
		for (int i=0; i<hexStrings.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hexStrings[i], 16);
		}
		return bytes;
	}
	

	/**
	 * 
	 * @param bytes byte array
	 * @return space separated 8-bit string encoding of byte (i.e. "01010101 10101010")
	 */
	public static String convertByteArrayToBitString(byte[] bytes) {
		return convertByteArrayToBitString(bytes, bytes.length);
	}


	public static String convertByteArrayToBitString(byte[] bytes, int length) {
		if (bytes.length == 0) return "";
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<length; i++)
		{
			String bits = Integer.toString(bytes[i] & 0xFF, 2);
			for (int j=0; j<8-bits.length(); j++)
				builder.append('0');
			builder.append(bits).append(' ');
		}
		builder.deleteCharAt(builder.length()-1);
		return builder.toString();
	}


	public static InputStream createByteStream(String bitString) {
		return new ByteArrayInputStream(convertBitStringToFastByteArray(bitString));
	}


	public static InputStream createByteStreamFromHexBytes(String hexString) {
		return new ByteArrayInputStream(convertHexStringToByteArray(hexString));
	}

}
