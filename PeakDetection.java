
package com.example.asa.gaitrecog;

import android.content.Intent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PeakDetection {
	static final int PEAKSREQ = 10;
	static final double MULTIPLIER = 0.9;
	
	static ArrayList<Double> al;
	static ArrayList listofpeaks; 
	static ArrayList<Double> al_x;
	static ArrayList<Double> al_z;
	static double highestpeak; 
	static double peaklimit = Double.MAX_VALUE;
	
	static double multiplier = 0.9;
	static double step       = 0.05;
	 
	public static void detectPeaks(){
		int passcount = 0;
		
		highestpeak = findMaxValue(Double.MAX_VALUE);
		System.out.println("Peak: " + highestpeak);
		do{
			do{
				findPeaksInRange(multiplier * highestpeak , highestpeak);
				multiplier -= step;
			} while( (listofpeaks.size() < PEAKSREQ) && (multiplier >= 0.7) );
			
			if( listofpeaks.size()> PEAKSREQ ){
				break;
			}
			else{
				highestpeak = findMaxValue(highestpeak);
				multiplier = MULTIPLIER;
				passcount++;
			}
		}while( passcount < 3 );
	
	} 
	
	public static double findMaxValue(double peaklimit){
		// Finding the global maximum
		double max = Double.MIN_VALUE;
		int indexofmax = -1;
		for(int i=0; i<al.size(); i++){
			if(al.get(i)>max && al.get(i)<peaklimit){
				max = al.get(i);
				indexofmax = i;
			}
		}

        return max;
	}
	
	public static void findPeaksInRange(double min, double max){
		//-- Figuring out peaks lying between the threshold and highest peak passed to it
		listofpeaks = new ArrayList();
		
		for(int i=1; i<al.size()-1; i++){
			double current = al.get(i);
			if( 	(al.get(i-1) < current) &&
					(al.get(i+1) < current) &&
					(current >= min)		&&
					(current <=  max) ){
				
				listofpeaks.add(new String(i + " " + current));
				i += 50;
			}
		}
		
		//* Listing out the peaks
		for(int i = 0; i <listofpeaks.size(); i++)
			System.out.println(listofpeaks.get(i));
		
	}

    public static void initiate(String Name, String Directory, String ButtonType, String User) {

        // Read from file
        FileInputStream textFile;
        BufferedReader br = null;
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            textFile = new FileInputStream(root.getAbsolutePath() + "//gait//GaitData" + Name + ".txt");
            br = new BufferedReader(new InputStreamReader(textFile));

            // Convert to arraylist
            String currentline;
            String splitvalues[];
            al = new ArrayList();
            al_x = new ArrayList();
            al_z = new ArrayList();
            while( (currentline = br.readLine()) != null){
                splitvalues = currentline.split("\\ ");
                al.add(new Double(splitvalues[3]));
                al_x.add(new Double(splitvalues[1]));
                al_z.add(new Double(splitvalues[2]));
            }

           detectPeaks();

            new Windowing(al, al_x, al_z, listofpeaks,Name,Directory,ButtonType,User);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

class Windowing{
	// This needs reference to ArrayList al and ArrayList listofpeaks

	// calculate differences between indices
	// find their median
	// keep those pairs with +- 10% of median
	
	static final int CONSECSTEPS = 4;
	
	int[] differenceslist;
	int	 median;
	boolean[] windowstochoose;
	int windowcount;
	ArrayList windows[],wind;
	ArrayList windowboundaries;
	
	ArrayList listofpeaks;
	ArrayList al;
	ArrayList al_x;
	ArrayList al_z;
	
	Windowing(ArrayList al, ArrayList al_x,ArrayList al_z, ArrayList listofpeaks, String UserName, String DirectoryName, String Button_Type, String user){
		this.al = al;
		this.al_x = al_x;
		this.al_z = al_z;
		this.listofpeaks = listofpeaks;
		
		// takes list of peaks, a pair at a time, and records the start and end indices corresponding to the 'al' ArrayList
		findDifferences();
		median = findMedian(differenceslist);
		findDifferences(); // because otherwise findMedian() would've spoilt the order
		markPairs();
		
		createWindows();
        fillArrayLists();
		//displayWindows();

        DTW_Calculation dtw = new DTW_Calculation();
        dtw.receiveArrayList(wind,UserName);
        dtw.calculate_Average_Distances();
        dtw.find_Accepted_Cycles();
        //dtw.display_ArrayList();
        dtw.find_Mean();
        dtw.store_Mean_Cycle(DirectoryName,Button_Type,user);

	}
	
	// finding out differences in distance between two consec peaks in arraylist
	void findDifferences(){
		differenceslist = new int[listofpeaks.size()-1];
		for(int i = 0; i < listofpeaks.size()-1; i++){
			int a = Integer.parseInt(listofpeaks.get(i).toString().split(" ")[0]);
			int b = Integer.parseInt(listofpeaks.get(i+1).toString().split(" ")[0]);
			differenceslist[i] = b-a;
		}
	}
	
	int findMedian(int[] a){
		java.util.Arrays.sort(a);
		int middle = a.length/2;
		int median = 0; //declare variable 
		if (a.length%2 == 1) 
			median = a[middle];
		else
			median = a[middle-1];
		//System.out.println("Median: " + median);
		return median;	
	}
	
	void markPairs(){
		windowstochoose = new boolean[listofpeaks.size()-1];
		windowcount = 0;
		
		for(int i = 0; i < windowstochoose.length; i++){
			//windowstochoose[i] = true;
			if(differenceslist[i] >= 0.9*median && differenceslist[i] <= 1.1*median){
				windowstochoose[i] = true;
				windowcount++;
			}	
			else
				windowstochoose[i] = false;
		}
	}
	
	void createWindows(){
		// writes boundaries of the windows to be created
		// boundaries written into "windowboundaries"
		
		windows = new ArrayList[windowcount];
        wind = new ArrayList();
		for(int i = 0; i < windows.length; i++){
			windows[i] = new ArrayList();
		}
		
		windowboundaries = new ArrayList();
		
		int currentwindow = 0;
		for(int i = 0; i < windowstochoose.length; i++){
			if(windowstochoose[i]){
				// note that "i" here is upto length of "windowstochoose", but instead references to "listofpeaks"
				int startindex = Integer.parseInt(listofpeaks.get(i).toString().split(" ")[0]);
				int endindex   = Integer.parseInt(listofpeaks.get(i+1).toString().split(" ")[0]) - 1;
				
				windowboundaries.add(startindex + " " + endindex);
			}
		}
	}
	
	// not using this right now
	boolean checkConsecutiveSteps(){
		int consecstepcount = 0;
		
		for(int i = 0; i < windowboundaries.size() - 1; i++){
			int a = Integer.parseInt(windowboundaries.get(i).toString().split(" ")[1]);
			int b = Integer.parseInt(windowboundaries.get(i+1).toString().split(" ")[0]);
			
			if(a+1 == b){
				consecstepcount++;
			}
			else{
				consecstepcount = 0;
			}
			if(consecstepcount == CONSECSTEPS){
				return true;
			}
		}
		return false;	
	}
	
	void fillArrayLists(){
		// TODO: name this function, define what it does exactly
		
		// Adding reading into an arraylist of arraylists
		// there should be an alternative and better way to get an entire range, check that
		
		ArrayList currentwindow;
		
		for(int i = 0; i < windowboundaries.size(); i++){
			currentwindow = new ArrayList();
			int startindex = Integer.parseInt(windowboundaries.get(i).toString().split(" ")[0]);
			int endindex   = Integer.parseInt(windowboundaries.get(i).toString().split(" ")[1]);

			for(int currentindex = startindex; currentindex <= endindex; currentindex++){
				// figure this out, right now sending to system.out
				currentwindow.add(al.get(currentindex));	
				//System.out.println(al.get(currentindex));
			}
			windows[i] = currentwindow;
            wind.add(currentwindow);
		}
	}
	
	void displayWindows(){
		for(int i = 0; i < windows.length; i++){
			//ArrayList currwindow = windows[i];
            ArrayList currwindow = (ArrayList) wind.get(i);
			System.out.println("Win " + i);
			for(int j = 0; j < currwindow.size(); j++){
				System.out.println(currwindow.get(j));
			}
		}
	}
		
}




