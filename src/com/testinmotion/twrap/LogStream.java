package com.testinmotion.twrap;

import java.io.*;

public class LogStream extends PrintStream {

	protected PrintStream parent;
	
	/* Testing
	public static void main(String[] args) throws IOException {
		LogStream ls = new LogStream(System.out, "logfile", true);
		System.setOut(ls);
		System.setErr(ls);
		System.out.println("This is a normal message");
		System.err.println("This is an error message");
	}
	*/

	// Main constructor
	public LogStream (PrintStream orig, OutputStream os, boolean flush) throws IOException {
		super(os,true);
		parent = orig;
	}
	
	// Construct a LogStream given an existing Stream, a filename,
	// and a boolean to control the flush
	public LogStream (PrintStream orig, String fn, boolean flush) throws IOException {
		this(orig, new FileOutputStream(fn), flush);
	}
	
	// Return true if either stream has an error
	public boolean checkError() {
		return parent.checkError() || super.checkError();
	}
	
	// Override write
	public void write(int x) {
		parent.write(x);	// write to stdout
		super.write(x);		// write to log file
	}
	
	// Override write 2
	public void write(byte[] x, int o, int l) {
		parent.write(x, o, l);
		super.write(x, o, l);
	}
	
	// Close both streams
	public void close() {
		parent.close();
		super.close();
	}
	
	// Flush both streams
	public void flush() {
		parent.flush();
		super.flush();
	}
}
