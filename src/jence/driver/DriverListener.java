/**
 * The MIT LICENSE (MIT):
 * 
 * Copyright © 2021 Ejaz Jamil, Jence.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the “Software”), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
 * of the Software, and to permit persons to whom the Software is furnished to do 
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 * 
 */
package jence.driver;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * This is a default listener used with the driver class.
 * The caller just need to call one method {@link #setData(String, CardType, byte[])}
 * to initialize the listener. 
 * 
 * @author Ejaz Jamil, Soalib Inc.
 *
 */
public abstract class DriverListener {
	public static enum CardType {
		LF,
		NFC,
		EPC_GEN_2
	};

	private String deviceid_ = null;
	private CardType cardType_;
	private byte[] data;
	private int maskBitStart_ = -1, maskBitLen_ = -1;
	BigInteger bigdata = null;
	BigInteger mask = null;

	/**
	 * Default constructor.
	 */
	public DriverListener() {
	}

	/**
	 * The caller of this listener should set the data to the listener
	 * by calling this method. This method will call the {@link #onData()}
	 * method.
	 * 
	 * @param device device id as String.
	 * @param ct card type. Should be one of {@link CardType#LF},
	 * {@link CardType#NFC} and {@link CardType#EPC_GEN_2}.
	 * @param d data to set as byte array.
	 */
	public void setData(String device, CardType ct, byte[] d) {
		cardType_ = ct;
		data = d;
		bigdata = new BigInteger(data);
		deviceid_ = device;
		onData();
	}

	/**
	 * The user should extend this class and implement this method.
	 * This method will be called automatically when data is set.
	 */
	abstract protected void onData();

	/**
	 * Gets data size.
	 * 
	 * @return size in bytes.
	 */
	public int getSize() {
		if (data == null)
			return 0;
		return data.length;
	}

	/**
	 * Gets card type.
	 * 
	 * @return card type is one of {@link CardType#LF},
	 * {@link CardType#NFC} and {@link CardType#EPC_GEN_2}
	 */
	public CardType getCardType() {
		return cardType_;
	}

	/**
	 * Bit mask for data. For example, if the actual data is 0x12345678
	 * but the user is interested is bytes 2 and 3 (i.e., 3456), then
	 * the mask should be the beginning bit number (NOT bit index) of byte 3, 
	 * or 24 and bit length should be 16 (2 bytes). This method will filter out 
	 * the bits and right shift the bits to align with bit zero.
	 * 
	 * @param maskStartBitPosition bit position of the starting bit. This is not the
	 * index (which is zero based). For example, if the first bit is desired, then
	 * mask start number is 1 and bit length is also 1.
	 * @param maskBitLength number of bits to mask.
	 */
	public void mask(int maskStartBitPosition, int maskBitLength) {
		if (maskStartBitPosition < 0 || maskBitLength < 0) {
			mask = null;
			return;
		}
		maskBitStart_ = maskStartBitPosition;
		maskBitLen_ = maskBitLength;
		char[] exmarks = new char[maskBitLength];
		Arrays.fill(exmarks, '1');
		mask = new BigInteger(new String(exmarks), 2);
		//System.out.println(mask.toString(16));
	}

	/**
	 * Internal method that applies the mask.
	 * 
	 * @return BigInteger object.
	 */
	private BigInteger applyMask() {
		int n = maskBitStart_ - maskBitLen_;
		//System.out.println(bigdata.toString(16));
		BigInteger big = bigdata.shiftRight(n);
		//System.out.println(big.toString(16));
		big = big.and(mask);
		//System.out.println(big.toString(16));
		return big;
	}

	/**
	 * Gets the device ID that generated the data.
	 * @return device id as String.
	 */
	public String getDeviceId() {
		return deviceid_;
	}

	/**
	 * Gets data as array of byte after applying mask.
	 * 
	 * @return array of byte.
	 */
	public byte[] dataAsBytes() {
		if (mask != null) {
			BigInteger big = applyMask();
			return big.toByteArray();
		}
		return bigdata.toByteArray();
	}

	/**
	 * Gets data as hex after applying mask. 
	 * 
	 * @return data as hex. Does not contain 0x prefix.
	 */
	public String dataAsHex() {
		if (mask != null) {
			BigInteger big = applyMask();
			return big.toString(16);
		}
		return bigdata.toString(16);
	}

	/**
	 * Gets data as decimal after applying mask.
	 * 
	 * @return decimal data.
	 */
	public String dataAsDecimal() {
		if (mask != null) {
			BigInteger big = applyMask();
			return big.toString(10);
		}
		return bigdata.toString(10);
	}
}

