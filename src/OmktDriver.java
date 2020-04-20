//package org.uga.rnainformatics.oskt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */

public class OmktDriver {
	
	private byte bk;
	private InputStream is;
	private String dst;
	private int option;
	private float fcnArg;
	public boolean DEBUG;
	private InputData input;
	private short numVertices;

	public OmktDriver(byte bk, InputStream is, String dst, int option, float fcnArg, boolean DEBUG) {
		this.bk = bk;
		this.is = is;
		this.dst = dst;
		this.option = option;
		this.fcnArg = fcnArg;
		this.DEBUG = DEBUG;
	}

	public InputStream getIs() {
		return is;
	}

	public byte getBk() {
		return bk;
	}
	
	public String getDst() {
		return dst;
	}
	
	public int getOption() {
		return option;
	}
	
	public float getFcnArg() {
		return fcnArg;
	}
	
	public InputData getInput() {
		return input;
	}

	public short getNumVertices() {
		return numVertices;
	}

	public void setNumVertices(short numVertices) {
		this.numVertices = numVertices;
	}

	private Omkt1 oskt1init() {
		Omkt1 oskt = new Omkt1(this);
		return oskt;
	}
	

	public int preprocessing() {
		if(option == 5) {
			Utilities.cutoff = fcnArg;
		}
		input = new InputData(this);
		return input.setData();
	}

	public void run() {
		int v = preprocessing();
		if(DEBUG)  System.out.println("n = " + numVertices + ", k = " + bk);
		if(v != -1) {
			oskt1init().run();
		}
		if(DEBUG)  System.out.println("Done");
	}
	
	public static void help(String USAGE) {
		System.out.println(
			  "Options for Objective Function:\n"
			+ " 1| Edge Weight Sum\n"
			+ " 2| Mutual Information MI(C, y)\n"
			+ " 3| Maximum Single Edge Pairwise MI\n"
			+ " 4| Average Pairwise MI\n"
			+ " 5| Maximum Single Edge Pairwise MI with Cutoff Value\n");
		System.out.println("Instructions for use -\n " + USAGE);
	}

	public static void main(String[] args) throws FileNotFoundException {

		int argC = args.length;
		String USAGE = "USAGE: \n"
					+  "(1a)  java OmktDriver [k] [inputFilePath] [outputFilePath] [option]\n"
					+  "(1b)  java OmktDriver [k] [inputFilePath] [outputFilePath] [option] [\"Debug\"]\n"
					+  "(2a)  java OmktDriver [k] [inputFilePath] [outputFilePath] [option] [optionArg]\n"
					+  "(2b)  java OmktDriver [k] [inputFilePath] [outputFilePath] [option] [optionArg] [\"Debug\"]\n"
					+  "For option details, type:  java OmktDriver help\n";
		boolean DEBUG = false;
		if(argC > 0 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h"))) {
			OmktDriver.help(USAGE);
		}
		else if(argC < 4 || argC > 7) {
			System.out.println(USAGE);
		}
		else {
			InputStream is = null;
			try {
				byte k = Byte.parseByte(args[0]);
				String inputFilePath = args[1];
				is = new FileInputStream(new File(inputFilePath));
				String outputFilePath = args[2];
				int option = Integer.parseInt(args[3]);
				float optionArg;
				if(args[argC-1].equalsIgnoreCase("d") || args[argC-1].equalsIgnoreCase("debug")) {
					DEBUG = true;
				}
				if((argC > 4 && !DEBUG) || argC > 5) {
					optionArg = Float.parseFloat(args[4]);
				}
				else {
					optionArg = -1;
				}
				OmktDriver driver = new OmktDriver(k, is, outputFilePath, option, optionArg, DEBUG);
				long start = System.currentTimeMillis();
				driver.run();
				long end = System.currentTimeMillis();
				if(DEBUG) {
					int total = (int)(end - start);
					int milliseconds = total % 1000;
					int seconds = total / 1000;
					int minutes = seconds / 60;
					seconds = seconds % 60;
					if(minutes > 0) {
						System.out.println("Total time: " + minutes + " minutes, "
											+ seconds + " seconds, "
											+ milliseconds + " milliseconds");

					}
					else if(seconds > 0) {
						System.out.println("Total time: " + seconds + " seconds "
											+ milliseconds + " milliseconds");
					}
					else {
						System.out.println("Total time: " + milliseconds + " milliseconds");
					}
				}
			}
			catch(FileNotFoundException e) {
				System.out.println("Error: file not found");
			}
			catch(NumberFormatException e) {
				System.out.println("Error with number format");
				System.out.println(USAGE);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					is.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			} // close InputStream is
		}
	} // main

} // OmktDriver
