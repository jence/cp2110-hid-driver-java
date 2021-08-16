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
package jence.test;

import jence.driver.DriverListener;

/**
 * Our custom listener for this example.
 * prints out the data without and with mask.
 * Mask is the number of bits to be filtered and shown.
 * 
 * @author Ejaz Jamil
 *
 */
class CP2110Listener extends DriverListener {
	@Override
	public void onData() {
		System.out.println(">>>[DATA FROM "+getDeviceId()+", "+getSize()+" BYTE]");
		System.out.print("  0x");
		System.out.println(dataAsHex());
		mask(8*6,32);
		System.out.println("  HEX (32-bit): "+ dataAsHex());
		System.out.println("  Decimal (10 Digits): " + dataAsDecimal());
		System.out.println("<<<\n");
	}
}

