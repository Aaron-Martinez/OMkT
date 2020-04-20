//package org.uga.rnainformatics.oskt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class InputData {

	float[][] mutualInformation;
	float[][] edges;
	OmktDriver driver;
	short n;
	byte k;
	
	public InputData(OmktDriver driver) {
		this.driver = driver;
	}
	
	public int setData() {
		k = driver.getBk();
		String x = "";
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(driver.getIs()))) {
			// get vertices number from 1st line
			String line = reader.readLine();
			if(line.startsWith("//")) {
				line = reader.readLine();
			}
			if (line.startsWith("##")) {
				x = line.split("=")[1];
				n = Short.parseShort(x);
			} 
			else {
				n = -1;
				driver.setNumVertices(n);
				System.out.println("Error: first line of input file should have format -  ## v=n");
				return -1;
			}
			driver.setNumVertices(n);
			mutualInformation = new float[k][];
			line = reader.readLine();
			
			int currentK = 1;
			mutualInformation[0] = new float[(int)Math.pow(n, 2)];
			while(line != null) {
				if(line.length() > 0) {
					if(line.startsWith("## k=")) {
						currentK = Integer.parseInt(line.split("=")[1]);
						mutualInformation[currentK-1] = new float[(int)Math.pow(n, currentK+1)];
						line = reader.readLine();
						continue;
					}
					else if(line.startsWith("##")) {
						line = reader.readLine();
						continue;
					}
			    	String[] lineData = line.split("\\s+");
			    	if(lineData.length != currentK+2) {
			    		System.out.println("Invalid # of columns in data file");
			    	}
			    	short[] C = new short[currentK];
			    	short y;
			    	float MI;
			    	try {
			    		for(int c = 0; c < currentK; c++) {
			    			C[c] = (short) (Integer.parseInt(lineData[c])-1);
			    		}
			    		y = (short) (Integer.parseInt(lineData[currentK])-1);
			    		MI = Float.parseFloat(lineData[currentK+1]);
			    		setMutualInformation(y, C, MI, currentK);
			    	}
		    		catch (NumberFormatException e) {
		    			System.out.println("Error: number format exception on line:");
		    			System.out.println(line);
		    			return -1;
		    		}
			    	catch (ArrayIndexOutOfBoundsException e) {
			    		System.out.println("Error: array out of bounds on MI for k = " + currentK);
	    				System.out.println(line);
	    				return -1;
			    	}
				}
	    		line = reader.readLine();
			} // while
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		driver.setNumVertices(n);
		return n;
	} // setData


	public float getEdge(int v1, int v2) {
		try {
			if(v1 < v2) {
				return edges[v1][v2];
			}
			else {
				return edges[v2][v1];
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	} // getMutualInformation
	
	public void setEdge(int v1, int v2, float edgeWt) {
		v1--;
		v2--;
		try {
			if(v1 < v2) {
				edges[v1][v2] = edgeWt;
			}
			else {
				edges[v2][v1] = edgeWt;
			}
		}
		catch(ArrayIndexOutOfBoundsException e) {
			
		}
	} // setEdge

	
	public float getMutualInformation(short y, short[] C, int kValue) {
		try {
			int index = 0;
			for(int c = 0; c < C.length; c++) {
				index += C[c] * Math.pow(n, c);
			}
			index += y * Math.pow(n, C.length);
			return mutualInformation[kValue-1][index];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	} // getMutualInformation
	
	public void setMutualInformation(short y, short[] C, float value, int kValue) {
		try {
			int index = 0;
			for(int c = 0; c < kValue; c++) {
				index += C[c] * Math.pow(n, c);
			}
			index += y * Math.pow(n, kValue);
			mutualInformation[kValue-1][index] = value;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			
		}
	} // setMutualInformation
	
	public float getPairwiseMI(short y, short c) {
		try {
			int index = c + (y * n);
			return mutualInformation[0][index];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}
	
} // InputData
