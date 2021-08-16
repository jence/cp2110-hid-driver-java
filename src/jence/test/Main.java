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

import java.awt.Rectangle;

import jence.driver.CP2110HidDriver;


/**
 * This is a demonstration of how to use the #link CP2110HidDriver class.
 * There are two example in one. To display the output on the console
 * comment out the test.listen() line.
 * 
 * @author Ejaz Jamil, Soalib Inc.
 *
 */
public class Main {

	/**
	 * 
	 */
	public Main() {
	}

	public static final void main(String[] args) {
		try {
			CP2110HidDriver hid = new CP2110HidDriver();
			String version = CP2110HidDriver.getVersion();
			System.out.println(version);
			int devices = CP2110HidDriver.getDeviceCount(0, 0);
			System.out.println("Devices = " + devices);
			
			if (devices > 0) {
				System.out.println("Number of attached devices: "+devices);
				
				String[] deviceString = CP2110HidDriver.listDevices();
				if (deviceString == null || deviceString.length == 0) {
					System.out.println("No valid device ID found.");
					return;
				}
					
				TestApp test = new TestApp();
				test.setDevice(hid);
				test.setBounds(new Rectangle(400,320));
				test.center();
				
				for(int i=0;i<deviceString.length;i++) {
					System.out.println("Device String["+i+"]="+deviceString[i]);
				}
				
				// connect to the first device. If multiple device needs to be connected
				// and used at the same time, you should separate instances of the
				// CP2110HidDriver class and connect to each device separately.
				hid.connect(deviceString[0], 9600, 8, 'N', 1, false);
				test.setCombo(deviceString);
				System.out.println(hid.getProperties());
				test.setProperties(hid.getProperties());
				
				// To print out on console, use this line.
				//hid.listen(deviceString[0],new CP2110Listener());
				
				// to emulate keyboard, use this method.
				test.listen();
			} else {
				System.out.println("No connected HID devices found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
