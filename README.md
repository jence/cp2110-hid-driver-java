# cp2110-hid-driver-java
CP2110 chip (SiLabs) HID Driver for Java.

The driver is designed to work with Windows (64bit), Linux (64bit) and MacOSX.
In order to use this driver to use with Windows 32 bit, replace the two DLLs
in project folder with the same DLL names found inside the x86 folder.

To distribute this driver in your application, make sure that the DLLs and the
shared libraries are found in the java search path.

In your own source code, you do not need the package soalib.test. Therefore,
you may delete this package and only use the package soalib.jni.

Ejaz Jamil
Jence

