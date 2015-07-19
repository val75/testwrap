package com.testinmotion.twrap;

import java.io.*;

public class TeePrintStream extends PrintStream {

	protected PrintStream parent;
	protected String fileName;
	
	public static void main(String[] args) throws IOException {
		TeePrintStream ts = new TeePrintStream(System.err, "err.log", true);
		System.setErr(ts);
		System.err.println("An error message");
		ts.close();
	}

	public TeePrintStream (PrintStream orig, OutputStream os, boolean flush) throws IOException {
		super(os,true);
		fileName = "(opened Stream)";
		parent = orig;
	}
	
	public TeePrintStream(PrintStream orig, OutputStream os) throws IOException {
		this(orig, os, true);
	}
	
	public TeePrintStream(PrintStream os, String fn) throws IOException {
		this(os, fn, true);
	}
	
	public TeePrintStream(PrintStream orig, String fn, boolean flush) throws IOException {
		this(orig, new FileOutputStream(fn), flush);
	}
	
	public boolean checkError() {
		return parent.checkError() || super.checkError();
	}
	
	public void write (int x) {
		parent.write(x);
		super.write(x);
	}
	
	public void write(byte[] x, int o, int l) {
		parent.write(x, o, l);
		super.write(x, o, l);
	}
	
	public void close () {
		parent.close();
		super.close();
	}
	
	public void flush () {
		parent.flush();
		super.flush();
	}
}
