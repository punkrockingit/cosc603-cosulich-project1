package cosulichProject1;

import java.util.Scanner;

/* Author: William
 * COSC 603 APG 
 * Project 1
 * 
 * This program is for computing national fire danger ratings
 * The fire load index data needed for the calculations are:
 * dry: dry bulb temperature
 * wet: wet bulb temperature
 * isnow: some positive non zero number if there is snow on the ground
 * wind: the current wind speed in miles per hour
 * buo: the last build up index
 * iherb: the current herb state of the district 1=cured, 2= transmitted, 3= green
 * 
 * The data that gets returned from the this program is 
 * df: the drying factor
 * ffm: fine fuel moisture
 * grass: grass spread index
 * timber: timber spread index
 * fload: fire load rating (man-hour base)
 * buo: build up index
*/


public class FireDanger {

    public static void main(String[] args) {
    	
    	Scanner scan = new Scanner(System.in);
    	System.out.println ("Enter Dry Bulb Temperature:");
    	float dry = scan.nextFloat();
    	System.out.println ("Enter Wet Bulb Temperature:");
    	float wet = scan.nextFloat();
    	System.out.println ("Enter value for precip:");
    	float precip = scan.nextFloat();    	
    	System.out.println ("Enter value for snow:");
    	float isnow = scan.nextFloat();
    	System.out.println ("Enter the current wind speed(MPH):");
    	float wind = scan.nextFloat();
    	System.out.println ("Enter the last value of the build up index:");
    	float buo = scan.nextFloat();
    	System.out.println ("Enter the current herd state of the district (1=Cured, 2=Transition, 3=Green):");
    	float iherb = scan.nextFloat();
    	scan.close();
    	
    	
    	FireDanger MR = new FireDanger();  
    	float[] val = MR.DangerSub(dry, wet, isnow, precip, wind, buo, iherb);
    	System.out.println("Drying Factor:" + val[0]);
    	System.out.println("Fine fuel Factor:" + val[1]);
    	System.out.println("Adjusted Fuel Moisture:" + val[2]);
    	System.out.println("Grass Spread Index:" + val[3]);
    	System.out.println("Timber Spread Index:" + val[4]);
    	System.out.println("Fire Load Rating:" + val[5]);
    	System.out.println("Build Up Index:" + val[6]);
    }

    public float[] DangerSub(float dry1, float wet1, float isnow1, float precip1, float wind1, float buo1, float iherb1){

    	float dry = dry1; 
    	float wet = wet1; 
    	float isnow = isnow1; 
    	float precip = precip1; 
    	float wind = wind1; 
    	float buo = buo1;
    	float iherb = iherb1;
     
       	//float wet, isnow, wind, buo, iherb, precip, dif;
    	float df, ffm, adfm, grass, timber, fload, dif; 
    	ffm=99;
    	adfm = 99;
    	df= 0;
    	fload = 0;
    	grass = 0;
    	timber = 0;
    	
    	float[] A = new float[4];
    	float[] B = new float[4];
    	float[] C = new float[3];
    	float[] D = new float[6];
    	/*
    	 * These are the table values that are used in computing the danger ratings
    	 */
    	A[0] = -0.185900f;
    	A[1] = -0.85900f;
    	A[2] = -0.59660f;
    	A[3] = -0.077373f;
    	B[0] = 30.0f;
    	B[1] = 19.2f;
    	B[2] = 13.8f;
    	B[3] = 22.5f;
    	C[0] = 4.5f;
    	C[1] = 12.5f;
    	C[2] = 27.5f;
    	D[0] = 16.0f;
    	D[1] = 10.0f;
    	D[2] = 7.0f;
    	D[3] = 5.0f;
    	D[4] = 4.0f;
    	D[5] = 3.0f;
    	
    	/*
    	 * Checks if there is snow on the ground or not
    	 */
    	if (isnow >= 0.00) {
    		/*Computing the the spread indexes and fire load*/
    		dif=dry-wet;
    		int i;
    		i= 1;
    		do {
    			if (dif-C[i]<=0.00){
    			 break;	
    			}
    		i++;	
    		}while(i==3);
    		ffm=(float) (B[i]*Math.exp(A[i]*dif));
    		/*This finds the drying factor for the day*/
    		do {
    			if (ffm-D[i]>0.00){
    			 break;	
    			}
    		i++;	
    		}while(i==6);
    		df=i-1;
    		/*Tests to see if the fire fuel moisture is one or less if the fire fuel moisture is one or less we set it to one
    		Then we adds 5 percent to fine fuel moisture for each herb stage greater than one*/
    		if (ffm-1<0.00) {
    			ffm=1;
    			
    			ffm=ffm+(iherb-1)*5;
    		}
    		else {
    			ffm=ffm+(iherb-1)*5;
    		}
    		/*Here we must adjust the BUI for precipitation before we add the drying factor*/
    		if (precip-.1 <=0.00) {
    			/*The correction for rain if there was any, we add today's drying factor to obtain the current build up index*/
    			buo=buo+df;
    		}
    		else {
    			/*If precipitation exceeded .1 inches, we must reduce the build up index by the amount equal to the rain fall */
    			buo = (float) (-50*Math.log(1-(1-Math.exp(-buo/50))*Math.exp(-1.175*(precip-1))));
    		}
    		if (buo < 0.00) {
    			buo = (float)0.00;
    		}
    		/*The correction for rain if there was any, we add today's drying factor to obtain the current build up index*/
    		buo = buo+df;
    		/*We have to adjust the grass spread  index for the heavy fuel lags
    		 * The result will be the timber spread index 
    		 * The adjusted fuel moisture, adfm, adjusted for heavy fuels, will be captured*/
    		adfm = (float)(.9*ffm+.5+9.5*Math.exp(-buo/50));
    		/*Test to see if the fuel moisture are greater than 30 percent
    		 * If they are, set their indexes values to 1*/
    		if (adfm-30>=0.00){
    			if (ffm-30>=0.00){
    				/*Fine fuel moisture is greater than 30 percent, therefore we have to set the grass and timber spread indexes to one*/
    				grass=1;
    				timber=1;
    				return new float[]{df, ffm, adfm, grass, timber, fload, buo};
    			}else {
    				timber = 1;
    			}
    			/*Test to see if wind is greater than 14 MPH*/
    			if (wind-14>=0.00) {
    				grass = (float)(.00918*(wind+14)*Math.pow((33-ffm),(1.65-3)));
    				if (grass-99<=0.00){
    					if (timber<=0.00) {
    						return new float[]{df, ffm, adfm, grass, timber, fload, buo};
    					}
    					else {
    						if (buo<=0.00){
    							return new float[]{df, ffm, adfm, grass, timber, fload, buo};
    						}
    						else {
    							fload = (float)(1.75*Math.log10(timber)+.32*Math.log10(buo)-1.640);
    							if (fload<=0.00) {
    								fload = 0;
    								return new float[]{df, ffm, adfm, grass, timber, fload, buo};
    							} 
    							else {
    								fload = 10 * fload;
    								return new float[]{df, ffm, adfm, grass, timber, fload, buo};
    							}
    						}
    					}
    				}
    				else{
    					grass = 99;
    					if (timber-99<=0.00) {
    						//
    						if (timber>=0.00) {
    							return new float[]{df, ffm, adfm, grass, timber, fload, buo};
        					}
        					else {
        						if (buo>=0.00){
        							return new float[]{df, ffm, adfm, grass, timber, fload, buo};
        						}
        						else {
        							fload = (float)(1.75*Math.log10(timber)+.32*Math.log10(buo)-1.640);
        							if (fload>=0.00) {
        								fload = 0;
        								return new float[]{df, ffm, adfm, grass, timber, fload, buo};
        							} 
        							else {
        								fload = 10 * fload;
        								return new float[]{df, ffm, adfm, grass, timber, fload, buo};
        							}
        						}
        					}
    						//
    					}
    					else {
    						timber = 99;
    						/* We now have to compute the grass and timber spread indexes of the national fire danger rating system.
    						 * We have the build up index and now we will compute the fire load rating*/
    						if (timber>=0.00) {
    							return new float[]{df, ffm, adfm, grass, timber, fload, buo};
        					}
        					else {
        						if (buo>=0.00){
        							return new float[]{df, ffm, adfm, grass, timber, fload, buo};
        						}
        						else {
        							fload = (float)(1.75*Math.log10(timber)+.32*Math.log10(buo)-1.640);
        							if (fload>=0.00) {
        								fload = 0;
        								return new float[]{df, ffm, adfm, grass, timber, fload, buo};
        							} 
        							else {
        								fload = (float)Math.pow(10, fload);
        								return new float[]{df, ffm, adfm, grass, timber, fload, buo};
        							}
        						}
        					}    						
    					}
    				}
    			}
    			else {
    				grass = (float)(.01312*(wind+6)*Math.pow((33-ffm),(1.65-3)));
    			}
    			
    			
    		}
    	}
    	/*if snow*/
    	else {
    		grass = 0;
    		timber = 0;
    		if (precip-.1 <= 0) {
    			return new float[]{df, ffm, adfm, grass, timber, fload, buo};
    		}
    		else {
    			buo = (float) (-50*Math.log(1-(1-Math.exp(-buo/50))*Math.exp(-1.175*(precip-1))));
    			if (buo>0) {
    				return new float[]{df, ffm, adfm, grass, timber, fload, buo};
    			}
    			else {
    				buo = 0;
    				return new float[]{df, ffm, adfm, grass, timber, fload, buo};
    			}
    		}
    	}
    	return new float[]{df, ffm, adfm, grass, timber, fload, buo};
    } //End Method
}