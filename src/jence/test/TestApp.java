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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jence.driver.CP2110HidDriver;
import jence.driver.DriverException;
import jence.driver.DriverListener;


/**
 * This example may be used if you are planning to use keyboard
 * emulation. When you place the card on the device, the
 * data appears on the text field called Card ID.
 * 
 * In some RFID reader, true keyboard emulation is the default behavior. 
 * The there is some disadvantage due to the fact that the cursor must
 * be in the text field or on the editor for it to work properly.
 * But this driver gives more flexibility. You can get keyboard 
 * emulation programmatically and provides some security as well.
 * 
 * @author Ejaz Jamil, Soalib Inc.
 *
 */
public class TestApp extends JFrame {
	private JTextField txtCardUd;
	private JTextField textLibraryVersion;
	private JLabel lblNewLabel_2;
	private JTextField textVendorID;
	private JLabel lblNewLabel_3;
	private JTextField textProductID;
	private JLabel lblNewLabel_4;
	private JTextField textPath;
	private JLabel lblNewLabel_6;
	private JTextField textManufacturer;
	private JLabel lblNewLabel_7;
	private JTextField textProductDescription;
	private JLabel lblNewLabel_1;
	private JComboBox comboDevices;
	
	private CP2110HidDriver hid_ = null;

	class CP2110Listener extends DriverListener {
		@Override
		public void onData() {
			mask(8*6,32);
			txtCardUd.setText(dataAsDecimal());
		}
	}

	public TestApp() throws HeadlessException {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("CP2110 Test");
		getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
		
		lblNewLabel_1 = new JLabel("Connected Devices");
		getContentPane().add(lblNewLabel_1);
		
		comboDevices = new JComboBox();
		getContentPane().add(comboDevices);
		
		JLabel lblNewLabel = new JLabel("Library Version");
		getContentPane().add(lblNewLabel);
		
		textLibraryVersion = new JTextField();
		getContentPane().add(textLibraryVersion);
		textLibraryVersion.setColumns(10);
		
		lblNewLabel_2 = new JLabel("Vendor Id");
		getContentPane().add(lblNewLabel_2);
		
		textVendorID = new JTextField();
		getContentPane().add(textVendorID);
		textVendorID.setColumns(10);
		
		lblNewLabel_3 = new JLabel("Product ID");
		getContentPane().add(lblNewLabel_3);
		
		textProductID = new JTextField();
		getContentPane().add(textProductID);
		textProductID.setColumns(10);
		
		lblNewLabel_4 = new JLabel("Path");
		getContentPane().add(lblNewLabel_4);
		
		textPath = new JTextField();
		getContentPane().add(textPath);
		textPath.setColumns(10);
		
		lblNewLabel_6 = new JLabel("Manufacturer");
		getContentPane().add(lblNewLabel_6);
		
		textManufacturer = new JTextField();
		getContentPane().add(textManufacturer);
		textManufacturer.setColumns(10);
		
		lblNewLabel_7 = new JLabel("Product Description");
		getContentPane().add(lblNewLabel_7);
		
		textProductDescription = new JTextField();
		getContentPane().add(textProductDescription);
		textProductDescription.setColumns(10);
		
		JLabel lblCardId = new JLabel("Card ID");
		getContentPane().add(lblCardId);
		
		txtCardUd = new JTextField();
		txtCardUd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		getContentPane().add(txtCardUd);
		txtCardUd.setColumns(10);
		
		setVisible(true);
	    
		// TODO Auto-generated constructor stub
	}

	public TestApp(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	public TestApp(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public TestApp(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}

	public void setProperties(Properties p) {
		textLibraryVersion.setText(p.getProperty("version"));
		textVendorID.setText(p.getProperty("vendor"));
		textProductID.setText(p.getProperty("product"));
		textProductDescription.setText(p.getProperty("product.name"));
		textManufacturer.setText(p.getProperty("manufacturer"));
		textPath.setText(p.getProperty("path"));
	}
	
	public void setDevice(CP2110HidDriver hid) {
		hid_ = hid;
	}
	
	public void center() {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
	    this.setLocation(x, y);
	}
	
	public void setCombo(String[] devices) {
		comboDevices.removeAll();
		for(int i=0;i<devices.length;i++) {
			comboDevices.addItem(devices[i]);
		}
		if (comboDevices.getItemCount() > 0) {
			comboDevices.setSelectedIndex(0);
		}
	}

	public void listen() throws DriverException {
		String device = comboDevices.getName();
		hid_.listen(device,new CP2110Listener());
	}
}
