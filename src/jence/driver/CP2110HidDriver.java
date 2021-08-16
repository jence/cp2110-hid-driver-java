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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

/**
 * This class designed to use Silicon Labs CP2110 chip sets, which is a HID
 * class to UART IC. This chip does not emulate as keyboard, instead it presents
 * as HID Class device, which generally represent that the communication between
 * the HID and chip is by way or reports.
 * 
 * Silicon Labs provides the drivers for this class in three OSes. This driver
 * should work for all the OSes as long as the driver is able to load the 
 * driver DLL or shared objects. 
 * 
 * @author Ejaz Jamil, Soalib Inc.
 * @version 1.0
 */
public class CP2110HidDriver {
	private interface IHIDUart extends Library {
		public byte HidUart_GetLibraryVersion(byte[] major, byte[] minor, boolean[] release);
		public byte HidUart_GetHidLibraryVersion(byte[] major, byte[] minor, boolean[] release);
		public byte HidUart_GetNumDevices(int[] numDevices, short vid, short pid);
		public byte HidUart_GetString(int deviceNum, short vid, short pid, byte[] deviceString, int options);
		public byte HidUart_IsOpened(long device);
		public byte HidUart_Open(long[] device, int deviceNum, short vid, short pid);
		public byte HidUart_GetPartNumber(long device, byte[] partNumber, byte[] version);
		public byte HidUart_SetUartConfig(long device, int baudRate, byte dataBits, byte parity, byte stopBits, byte flowControl);
		public byte HidUart_GetUartStatus(long device, short[] transmitFifoSize, short[] receiveFifoSize, byte[] errorStatus, byte[] lineBreakStatus);
		public byte HidUart_GetUartConfig(long device, int[] baudRate, byte[] dataBits, byte[] parity, byte[] stopBits, byte[] flowControl);
		public byte HidUart_SetTimeouts(long device, int readTimeout, int writeTimeout);
		public byte HidUart_GetOpenedString(long device, byte[] deviceString, int options);
		public byte HidUart_Close(long device);
		public byte HidUart_Read(long device, byte[] buffer, int numBytesToRead, int[] numBytesRead);
	}
	
// The following commented list of function names are exposed by DLL.
// Only a small part is implemented. The functions that is implemented 
// has a [X] next to it.
/*
[SLABHIDDevice.dll]

HidDevice_CancelIo
HidDevice_Close
HidDevice_FlushBuffers
HidDevice_GetAttributes
HidDevice_GetFeatureReportBufferLength
HidDevice_GetFeatureReport_Control
HidDevice_GetHandle
HidDevice_GetHidAttributes
HidDevice_GetHidGuid
HidDevice_GetHidIndexedString
HidDevice_GetHidLibraryVersion
HidDevice_GetHidString
HidDevice_GetIndexedString
HidDevice_GetInputReportBufferLength
HidDevice_GetInputReport_Control
HidDevice_GetInputReport_Interrupt
HidDevice_GetMaxReportRequest
HidDevice_GetNumHidDevices
HidDevice_GetOutputReportBufferLength
HidDevice_GetString
HidDevice_GetTimeouts
HidDevice_IsOpened
HidDevice_Open
HidDevice_SetFeatureReport_Control
HidDevice_SetOutputReport_Control
HidDevice_SetOutputReport_Interrupt
HidDevice_SetTimeouts
*/

/*
[SLABHIDtoUART.dll]

CP2114_CreateOtpConfig
CP2114_GetDacRegisters
CP2114_GetDeviceCaps
CP2114_GetDeviceStatus
CP2114_GetOtpConfig
CP2114_GetPinConfig
CP2114_GetRamConfig
CP2114_GetVersions
CP2114_ReadOTP
CP2114_SetBootConfig
CP2114_SetDacRegisters
CP2114_SetPinConfig
CP2114_SetRamConfig
CP2114_WriteOTP
HidUart_CancelIo
	[X] HidUart_Close
HidUart_FlushBuffers
HidUart_GetAttributes
HidUart_GetHidGuid
	[X] HidUart_GetHidLibraryVersion
HidUart_GetIndexedString
	[X] HidUart_GetLibraryVersion
HidUart_GetLock
HidUart_GetManufacturingString
	[X] HidUart_GetNumDevices
HidUart_GetOpenedAttributes
HidUart_GetOpenedIndexedString
	[X] HidUart_GetOpenedString
	[X] HidUart_GetPartNumber
HidUart_GetPinConfig
HidUart_GetProductString
HidUart_GetSerialString
	[X] HidUart_GetString
HidUart_GetTimeouts
	[X] HidUart_GetUartConfig
HidUart_GetUartEnable
HidUart_GetUartStatus
HidUart_GetUsbConfig
	[X] HidUart_IsOpened
	[X] HidUart_Open
	[X] HidUart_Read
HidUart_ReadLatch
HidUart_Reset
HidUart_SetLock
HidUart_SetManufacturingString
HidUart_SetPinConfig
HidUart_SetProductString
HidUart_SetSerialString
	[X] HidUart_SetTimeouts
	[X] HidUart_SetUartConfig
HidUart_SetUartEnable
HidUart_SetUsbConfig
HidUart_StartBreak
HidUart_StopBreak
HidUart_Write
HidUart_WriteLatch
*/
	private static boolean CONSOLE = true;
	
	public static final String PROPERTY_VERSION = "version";
	public static final String PROPERTY_VENDOR = "vendor";
	public static final String PROPERTY_PRODUCT = "product";
	public static final String PROPERTY_PART = "part";
	public static final String PROPERTY_PATH = "path";
	public static final String PROPERTY_MANUFACTURER = "manufacturer";
	public static final String PROPERTY_PRODUCT_NAME = "product.name";

	private static final int HID_UART_SUCCESS				= 0x00;
	private static final int HID_UART_DEVICE_NOT_FOUND		= 0x01;
	private static final int HID_UART_INVALID_HANDLE			= 0x02;
	private static final int HID_UART_INVALID_DEVICE_OBJECT	= 0x03;
	private static final int HID_UART_INVALID_PARAMETER		= 0x04;
	private static final int HID_UART_INVALID_REQUEST_LENGTH	= 0x05;

	private static final int HID_UART_READ_ERROR				= 0x10;
	private static final int HID_UART_WRITE_ERROR			= 0x11;
	private static final int HID_UART_READ_TIMED_OUT			= 0x12;
	private static final int HID_UART_WRITE_TIMED_OUT		= 0x13;
	private static final int HID_UART_DEVICE_IO_FAILED		= 0x14;
	private static final int HID_UART_DEVICE_ACCESS_ERROR	= 0x15;
	private static final int HID_UART_DEVICE_NOT_SUPPORTED	= 0x16;

	private static final int HID_UART_UNKNOWN_ERROR			= 0xFF;
	
	// Product String Types
	private static final int HID_UART_GET_VID_STR			= 0x01;
	private static final int HID_UART_GET_PID_STR			= 0x02;
	private static final int HID_UART_GET_PATH_STR			= 0x03;
	private static final int HID_UART_GET_SERIAL_STR			= 0x04;
	private static final int HID_UART_GET_MANUFACTURER_STR	= 0x05;
	private static final int HID_UART_GET_PRODUCT_STR		= 0x06;

	// String Lengths
	private static final int HID_UART_DEVICE_STRLEN			= 260;
	
	public static final int READ_TIMEOUT					= 200;
	public static final int WRITE_TIMEOUT					= 2000;
	
	// Error Status
	private static final int HID_UART_PARITY_ERROR			= 0x01;
	private static final int HID_UART_OVERRUN_ERROR			= 0x02;

	// Line Break Status
	private static final int HID_UART_LINE_BREAK_INACTIVE	= 0x00;
	private static final int HID_UART_LINE_BREAK_ACTIVE		= 0x01;

	// Data Bits
	private static final int HID_UART_FIVE_DATA_BITS			= 0x00;
	private static final int HID_UART_SIX_DATA_BITS			= 0x01;
	private static final int HID_UART_SEVEN_DATA_BITS		= 0x02;
	private static final int HID_UART_EIGHT_DATA_BITS		= 0x03;

	// Parity
	private static final int HID_UART_NO_PARITY				= 0x00;
	private static final int HID_UART_ODD_PARITY			= 0x01;
	private static final int HID_UART_EVEN_PARITY			= 0x02;
	private static final int HID_UART_MARK_PARITY			= 0x03;
	private static final int HID_UART_SPACE_PARITY			= 0x04;

	// Stop Bits
	// Short = 1 stop bit
	// Long  = 1.5 stop bits (5 data bits)
//	       = 2 stop bits (6-8 data bits)
	private static final int HID_UART_SHORT_STOP_BIT			= 0x00;
	private static final int HID_UART_LONG_STOP_BIT			= 0x01;

	// Flow Control
	private static final int HID_UART_NO_FLOW_CONTROL		= 0x00;
	private static final int HID_UART_RTS_CTS_FLOW_CONTROL	= 0x01;

	// Read/Write Limits
	public static final int HID_UART_MIN_READ_SIZE			= 1;
	public static final int HID_UART_MAX_READ_SIZE			= 32768;
	public static final int HID_UART_MIN_WRITE_SIZE			= 1;
	public static final int HID_UART_MAX_WRITE_SIZE			= 4096;

	public static final int READ_SIZE						= 1000;
	
	//typedef long jint;
	//typedef DWORD = __int64 = jlong;
	//typedef signed char jbyte;
	
	private static IHIDUart hid = null;
	
	long	m_hidUart;
	byte	m_partNumber;
	byte	m_version;
	long	m_hNotifyDevNode;
	String  m_vid;
	String  m_pid;
	String  m_product;
	String  m_mfg;
	String  m_path;

	/**
	 * Gets the description of the status code.
	 * 
	 * @param status status id.
	 * @return a description of status as String.
	 */
	private static String getStatus(int status)
	{
		String statusStr = "Unknown status";

		switch (status)
		{
		case HID_UART_SUCCESS:					statusStr = "Success";					break;
		case HID_UART_DEVICE_NOT_FOUND:			statusStr = "Device not found";			break;
		case HID_UART_INVALID_HANDLE:			statusStr = "Invalid handle";			break;
		case HID_UART_INVALID_DEVICE_OBJECT:	statusStr = "Invalid device object";	break;
		case HID_UART_INVALID_PARAMETER:		statusStr = "Invalid parameter";		break;
		case HID_UART_INVALID_REQUEST_LENGTH:	statusStr = "Invalid request length";	break;

		case HID_UART_READ_ERROR:				statusStr = "Read error";				break;
		case HID_UART_WRITE_ERROR:				statusStr = "Write error";				break;
		case HID_UART_READ_TIMED_OUT:			statusStr = "Read timed out";			break;
		case HID_UART_WRITE_TIMED_OUT:			statusStr = "Write timed out";			break;
		case HID_UART_DEVICE_IO_FAILED:			statusStr = "Device I/O failed";		break;
		case HID_UART_DEVICE_ACCESS_ERROR:		statusStr = "Device access error";		break;
		case HID_UART_DEVICE_NOT_SUPPORTED:		statusStr = "Device not supported";		break;

		case HID_UART_UNKNOWN_ERROR:			statusStr = "Unknown error";			break;
		}

		return statusStr;
	}

	/**
	 * Checks if the status is Valid. If not, throws exception with status message.
	 * 
	 * @param status status id.
	 * @throws DriverException
	 */
	private static void checkValidity(int status) throws DriverException {
		if (hid == null)
			throw new DriverException("Library not loaded.");
		if (status != HID_UART_SUCCESS)
			throw new DriverException(getStatus(status));
	}

	/**
	 * Internal method used to strip NULL returned by the device.
	 * 
	 * @param s bytes read.
	 * @return null removed String.
	 */
	private static String stripNull(byte[] s) {
		try {
			int nullIndex = 0;
			while(s[nullIndex++] != 0)
				if (nullIndex > s.length)
					return new String(s,"UTF-8");
			// System.out.println("Null Index = " + nullIndex);
			return new String(s,0, --nullIndex,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets the version of the library.
	 * 
	 * @return version as String.
	 * @throws DriverException
	 */
	public static String getVersion() throws DriverException {
		checkValidity(0);
		byte[] major = {0}, minor = {0};
		boolean[] release = {false};
		//hid.HidDevice_GetNumHidDevices((short)0, (short)0);
		byte status = hid.HidUart_GetLibraryVersion(major, minor, release);
		checkValidity(status);
		String version = "LIB:" + major[0]+"."+minor[0]+ " " + ((release[0]) ? "Release" : "Debug");
		status = hid.HidUart_GetHidLibraryVersion(major, minor, release);
		version += ", HID:" + major[0]+"."+minor[0]+ " " + ((release[0]) ? "Release" : "Debug") ;
		return version;
	}

	/**
	 * Gets the number of connected devices. 
	 * 
	 * @param vid Provide specific vendor ID, if known. Otherwise pass zero for all vendors.
	 * @param pid Provide specific product ID, if known. Otherwise pass zero for all products.
	 * @return number of devices connected.
	 * @throws DriverException
	 */
	public static int getDeviceCount(int vid, int pid) throws DriverException {
		checkValidity(0);
		int[] devices = {0};
		int status = hid.HidUart_GetNumDevices(devices, (short)vid, (short)pid);
		checkValidity(status);
		return devices[0];
	}

	/**
	 * Internal function used to get an internal String.
	 * 
	 * @param index device index.
	 * @param vid vendor id. Provide 0, if not known.
	 * @param pid product id. Provide 0, if not known.
	 * @param strType string type, predefined in the driver.
	 * @return the String value.
	 * @throws DriverException
	 */
	private static String getString(int index, int vid, int pid, int strType) throws DriverException {
		checkValidity(0);
		byte[] deviceString = new byte[HID_UART_DEVICE_STRLEN];
		int status = hid.HidUart_GetString(index, (short)vid, (short)pid, deviceString, strType);
		checkValidity(status);
		return stripNull(deviceString);
	}

	/**
	 * Similar to {@link #getString(int, int, int, int)}, but this one works for open
	 * devices.
	 * 
	 * @param device
	 * @param strType
	 * @return
	 * @throws DriverException
	 */
	private String getOpenedString(long device, int strType) throws DriverException {
		checkValidity(0);
		byte[] deviceString = new byte[HID_UART_DEVICE_STRLEN];
		int status = hid.HidUart_GetOpenedString(device, deviceString, strType);
		checkValidity(status);
		return stripNull(deviceString);
	}

	/**
	 * Opens a device at index.
	 * 
	 * @param index device index.
	 * @param vid vendor id. Provide 0 if not known.
	 * @param pid product id. Provide 0 if not known.
	 * @return status id.
	 * @throws DriverException
	 */
	private long open(int index, int vid, int pid) throws DriverException {
		checkValidity(0);
		long[] hiduar = {0};
		int status = hid.HidUart_Open(hiduar, index, (short)vid, (short)pid);
		checkValidity(status);
		return hiduar[0];
	}

	/**
	 * List all the devices currently attached to the PC of CP2110 type.
	 * 
	 * @return an array of device id as String.
	 * @throws DriverException
	 */
	public static String[] listDevices() throws DriverException {
		ArrayList<String> list = new ArrayList<String>();
		int n = getDeviceCount(0, 0);
		for (int i = 0; i < n; i++)
		{
			// Search through all HID devices for a matching serial string
			String deviceString = getString(i, 0, 0, HID_UART_GET_SERIAL_STR);
			if (deviceString != null && deviceString.trim().length() > 0) {
				list.add(deviceString.trim());
			}
		}
		return (String[])list.toArray(new String[]{});
	}

	/**
	 * Checks if the device represented by the device id is connected.
	 * 
	 * @param device a valid device id.
	 * @return <true>, if connected.
	 */
	public boolean isConnected(String device) {
		int status = hid.HidUart_IsOpened(m_hidUart);
		if (status == HID_UART_SUCCESS)
			return true;
		return false;
	}

	/**
	 * Connect to a device identified by the device id.
	 * 
	 * @param deviceid a valid device ID. Valid device ID can be found by calling {@link #listDevices()}.
	 * @param baudRate One of valid baud rates. Valid rates are: 9600, 19200, 38400, 57600 and 115200.
	 * @param dataBits valid values are 5, 6, 7, 8. Typical is 8 bits.
	 * @param parity valid values are 'N' = no parity, 'O' =  Odd parity, 'E' = even parity, 'S' = space parity,
	 * 'M' = mark parity. 'N' is more common.
	 * @param stopBits valid values are 1 or 2. 1 is more common.
	 * @param flow <false> for no flow control. <true> is not frequently used.
	 * @throws DriverException
	 */
	public void connect(String deviceid, int baudRate, int dataBits, int parity, int stopBits, boolean flow) throws DriverException
	{
		// connect(deviceString, 9600, HID_UART_EIGHT_DATA_BITS, HID_UART_NO_PARITY, HID_UART_SHORT_STOP_BIT, HID_UART_NO_FLOW_CONTROL);
		dataBits -= 5;
		switch(parity) {
		case 'N': parity = HID_UART_NO_PARITY; break;
		case 'E': parity = HID_UART_ODD_PARITY; break;
		case 'O': parity = HID_UART_EVEN_PARITY; break;
		case 'M': parity = HID_UART_MARK_PARITY; break;
		case 'S': parity = HID_UART_SPACE_PARITY; break;
		}
		stopBits = stopBits - 1;
		int flowControl = (flow) ? HID_UART_NO_FLOW_CONTROL : HID_UART_RTS_CTS_FLOW_CONTROL;

		try {
			int n = getDeviceCount(0, 0);
			if (n == 0)
				throw new DriverException("Device not found for "+deviceid);
			
			for (int i = 0; i < n; i++)
			{
				// Search through all HID devices for a matching serial string
				String deviceString = getString(i, 0, 0, HID_UART_GET_SERIAL_STR);
				// Found a matching device
				if (deviceid.equals(deviceString)) {
					// Open the device
					m_hidUart = open(i, 0, 0);
					break;
				}
			}
			
			if (m_hidUart == 0) {
				throw new DriverException("No device found.");
			}
	
			// Found and opened the device
			// Get part number and version
			byte[] partno = {0}, version = {0};
			int status = hid.HidUart_GetPartNumber(m_hidUart, partno, version);
			checkValidity(status);
			m_partNumber = partno[0];
			m_version = version[0];
	
			// Got part number
			// Configure the UART
			status = hid.HidUart_SetUartConfig(m_hidUart, baudRate, (byte)dataBits, (byte)parity, (byte)stopBits, (byte)flowControl);
			checkValidity(status);
	
			// Confirm UART settings
			int[] vBaudRate = {0};
			byte[] vDataBits = {0};
			byte[] vParity = {0};
			byte[] vStopBits = {0};
			byte[] vFlowControl = {0};
	
			status = hid.HidUart_GetUartConfig(m_hidUart, vBaudRate, vDataBits, vParity, vStopBits, vFlowControl);
	
			if (vBaudRate[0] != baudRate ||
				vDataBits[0] != dataBits ||
				vParity[0] != parity ||
				vStopBits[0] != stopBits ||
				vFlowControl[0] != flowControl) {
				status = HID_UART_INVALID_PARAMETER;
				checkValidity(status);
			}
	
			// Configured the UART
			// Set short read timeouts for periodic read timer
			// Set longer write timeouts for user transmits
			status = hid.HidUart_SetTimeouts(m_hidUart, READ_TIMEOUT, WRITE_TIMEOUT);
			checkValidity(status);
	
			// Fully connected to the device
			m_vid = getOpenedString(m_hidUart, HID_UART_GET_VID_STR);
			m_pid = getOpenedString(m_hidUart, HID_UART_GET_PID_STR);
			m_product = getOpenedString(m_hidUart, HID_UART_GET_PRODUCT_STR);
			m_mfg = getOpenedString(m_hidUart, HID_UART_GET_MANUFACTURER_STR);
			m_path = getOpenedString(m_hidUart, HID_UART_GET_PATH_STR);

			if (CONSOLE) {
				// Output the connection status to the status bar
				System.out.println("Vendor ID = "+m_vid+", Product ID = "+m_pid);
				System.out.println("Part Number = "+m_partNumber+", Version = "+m_version);
				System.out.println("Path = "+ m_path);
				System.out.println("Manufacturer = "+ m_mfg);
				System.out.println("Product = "+m_product);
				System.out.println("Connected to "+ deviceid);
				
				System.out.println("Connected to "+deviceid);
			}
	
		} catch (Throwable t) {
			// Disconnect
			if (m_hidUart != 0) {
				hid.HidUart_Close(m_hidUart);
				m_hidUart = 0;
			}
			// Notify the user that an error occurred
			if (CONSOLE) {
				System.out.println("Failed to connect to "+deviceid);
			}
		} finally {

		}
	}

	/**
	 * Closes the open connection when the last {@link #connect(String, int, int, int, int, boolean)} method was 
	 * successfully called.
	 * 
	 * @throws DriverException
	 */
	void disconnect() throws DriverException {
		int status = hid.HidUart_Close(m_hidUart);
		checkValidity(status);
	}

	/**
	 * Reads the data within the timeout period.
	 * 
	 * @return an array of data read.
	 * @throws DriverException
	 */
	public synchronized byte[] read() throws DriverException
	{
		byte[] buffer = new byte[READ_SIZE];
		int[] numBytesRead = {0};
		// Receive UART data from the device (up to 1000 bytes)
		int status = hid.HidUart_Read(m_hidUart, buffer, READ_SIZE, numBytesRead);

		// HidUart_Read returns HID_UART_SUCCESS if numBytesRead == numBytesToRead
		// and returns HID_UART_READ_TIMED_OUT if numBytesRead < numBytesToRead
		if (status == HID_UART_SUCCESS || status == HID_UART_READ_TIMED_OUT)
		{
			if (numBytesRead[0] == 0)
				return null;
			// Output received data to the receive window
			return Arrays.copyOf(buffer, numBytesRead[0]);
		}
		checkValidity(status);
		throw new DriverException(getStatus(status));
	}
	
	/**
	 * Sets read write timeout for the device. Default read timeout is {@link #READ_TIMEOUT}
	 * and default write timeout is {@link #WRITE_TIMEOUT}. 
	 * 
	 * @param readTimeout timeout in milliseconds.
	 * @param writeTimeout timeout in milliseconds.
	 * @throws DriverException
	 */
	public void setReadWriteTimeout(int readTimeout, int writeTimeout) throws DriverException {
		int status = hid.HidUart_SetTimeouts(m_hidUart, readTimeout, writeTimeout);
		checkValidity(status);
	}

	/**
	 * Attach a listener to each device. This method should be called in a thread.
	 * The listener may be used to update GUI, etc.
	 * 
	 * @param deviceString the identifier string returned by the device.
	 * @param listener your custom listener. If listener is not provided (i.e, null, will print out the data
	 * on the console.
	 * @throws DriverException
	 */
	public void listen(String deviceString, DriverListener listener) throws DriverException {
		while(true) {
			byte[] buffer = read();
			
			if (buffer != null && buffer.length > 0) {
				if (listener != null) {
					listener.setData(deviceString, DriverListener.CardType.LF, buffer);
				} else {
					System.out.println(">>>[DATA FROM "+deviceString+", "+buffer.length+" BYTE]");
					System.out.print("  0x");
					BigInteger big = new BigInteger(buffer);
					System.out.println(big.toString(16));
					big = big.shiftRight(16);
					System.out.println(big.toString(16));
					BigInteger b = new BigInteger("FFFFFFFF",16);
					System.out.println(b.toString(16));
					big = big.and(b);
					System.out.println(big.toString(16));
					long val = big.longValue();
					System.out.println("  HEX (32-bit): "+ Long.toString(val, 16));
					System.out.println("  Decimal (10 Digits): " + val);
					System.out.println("<<<\n");
				}
			}
		}
	}

	/**
	 * Gets the product parameters as properties. The property names are available
	 * with prefix PROPERTY_ as constant.
	 * 
	 * @return property object.
	 * @throws DriverException
	 */
	public Properties getProperties() throws DriverException {
		Properties p = new Properties();
		p.setProperty(PROPERTY_VERSION, getVersion());
		p.setProperty(PROPERTY_VENDOR, m_vid);
		p.setProperty(PROPERTY_PRODUCT, m_pid);
		p.setProperty(PROPERTY_PART, m_partNumber+"."+m_version);
		p.setProperty(PROPERTY_PATH, m_path);
		p.setProperty(PROPERTY_MANUFACTURER, m_mfg);
		p.setProperty(PROPERTY_PRODUCT_NAME, m_product);
		return p;
	}
	
	/**
	 * Construct the object by loading the library into memory.
	 */
	public CP2110HidDriver() {
		if (hid == null) {
			hid = (IHIDUart)Native.load("SLABHIDtoUART.dll", IHIDUart.class);
		}
	}

}
