/************************************************************/
/* Author: Dylan Tomza, Gabe Bawden                         */
/* Major: Computer Science, Mathematics                     */
/* Creation Date: April 29th, 2024                          */
/* Due Date: April 29th, 2024                               */
/* Course: CS211-02                                         */
/* Professor Name: Professor Shimkanon                      */
/* Assignment: Final Project                                */
/* Filename: PerlinNoise.java                               */
/* Purpose: This file houses the PerlinNoise class to		*/
/* 		   generate the height map used by the Terrain class*/
/************************************************************/

package application; //needed for javafx

//importing math package
import java.lang.Math;

public class PerlinNoise
{
	//create field for seed
	private int seed;
	
	/***************************************************************************/
	/* Function name: PerlinNoise(constructor)
	/* Description: creates PerlinNoise object
	/* Parameters: int - s: seed for PerlinNoise
	/* Return Value: void
	/***************************************************************************/
	PerlinNoise(int s)
	{
		seed = s; //set seed equal to s
	}

	/***************************************************************************/
	/* Function name: getRandVector
	/* Description: generates a psuedo-random vector based on x-y value and seed
	/* Parameters: int - ix: x value of gridpoint, int - iy: y value of gridpoint
	/* Return Value: double[] - array of length 2 representing a gradient vector of length 1
	/***************************************************************************/
	private double[] getRandVector(int ix, int iy)
	{
		int w = 8 * Integer.SIZE; //calculates the width of an integer in bits
	        int s = w / 2; //calculates half the width of an integer to be used as a shift value
	        int a = ix + seed * 1000; //calculates a and b using ix, iy, and seed so vector values are repeatable
	        int b = iy + seed * 1000;
	        
	        a *= 3284157443L; //multiplies a by a large prime number 
	 
	        /*performs a bitwise OR operation on a when left shifted by s
	         * and a when right shifted by (w-s). A bitwise XOR is then
	         * performed on b and what is returned by the original OR
	         */
	        b ^= a << s | a >>> (w - s); 
	        b *= 1911520717L; //multiplies b by a large prime number
	 
	        /*performs a bitwise OR operation on b when left shifted by s
	         * and b when right shifted by (w-s). A bitwise XOR is then
	         * performed on a and what is returned by the original OR
	         */
	        a ^= b << s | b >>> (w - s);
	        a *= 2048419325L * seed; //multiplies a by a large prime number and the seed
	
	        /*uses sin(a) and cos(a) to create x and y components of a psuedo-random
	         * vector with length 1
	         */
	        double[] vector = {Math.sin(a), Math.cos(a)};
	 
	        return vector; //returns psuedo-randomly generated vector
	}
	
	/***************************************************************************/
	/* Function name: calcDotProd
	/* Description: calculates the dot product between a gradient and a displacement vector
	/* Parameters: int - ix: x value of gridpoint, int - iy: y value of gridpoint
	/*             double - x: x value point being calculated, double - y value of point being calculated
	/* Return Value: double[] - the dot product of the two vectors
	/***************************************************************************/
	private double calcDotProd(int ix, int iy, double x, double y)
	{
		//calls the getRandVector method to generate a random vector and store it in an array
		double[] vector = getRandVector(ix, iy);
		
		double dx = ix - x; //calculates the distance in the x direction from the gridpoint
		double dy = iy - y; //calculates the distance in the y direction from the gridpoint
		//these distances will be the components of a the displacement vector from the gridpoint to the x-y point
		
		return (dx * vector[0] + dy * vector[1]); //calculuates and returns the dot product of the two vectors
	}
	
	/***************************************************************************/
	/* Function name: interpolate
	/* Description: interpolates two dot products and a distance to calculate height at that point
	/* Parameters: double - a: dot prod of displacement vector and first gradient
	/*			   double - b: dot prod of displacement vector and second gradient
	/*             double - dist: distance in x or y distance from point to gridpoints
	/* Return Value: double - the height of the x-y point
	/***************************************************************************/
	private double interpolate(double a, double b, double dist)
	{
		//uses a cubic interpolation to smoothen the perlin noise values
		return a - (3*(dist*dist) - 2*(dist*dist*dist))*(a - b);
	}
	
	private double getPerlinVal(double x, double y)
	{
		int x0 = (int)x; //gridpoint x0 value
		int y0 = (int)y; //gridpoint y0 value
		int x1 = x0 + 1; //gridpoint x1 value
		int y1 = y0 + 1; //gridpoint y1 value
		//these values can represent the 4 closest gripoints to any x-y point
		//(x0,y0),(x0,y1),(x1,y0),(x1,y1)
		
		double dx = x - x0; //calculates the distance between x and x0
		double dy = y - y0; //calculates the distance between y and y0
	
		//calls calcDotProd for the gridpoints containing y0 and interpolates them with the distance in the x direction
		double dot1 = calcDotProd(x0, y0, x, y);
		double dot2 = calcDotProd(x1, y0, x, y);
		double ix0 = interpolate(dot1, dot2, dx);
		
		//calls calcDotProd for the gridpoints containing x0 and interpolates them with the distance in the y direction
		double dot3 = calcDotProd(x0, y1, x, y);
		double dot4 = calcDotProd(x1, y1, x, y);
		double ix1 = interpolate(dot3, dot4, dx);
		
		//interpolates the two values returned by the previous interpolations
		double perlinVal = interpolate(ix0, ix1, dy);
		
		return perlinVal; //return the perlin value
	}
	
	/***************************************************************************/
	/* Function name: getFractalPerlinVal
	/* Description: returns the final fractal value of the height at an x-y point
	/* Parameters: int - x: x value of the point, int - y: y value of the point
	/*             int - scale: how zoomed in the graph is,
	/*             int - numOctaves: the number of layers for the fractal perlin value
	/* Return Value: double - the final fractal value for the height of the x-y point
	/***************************************************************************/
	public double getFractalPerlinVal(int x, int y, int scale, int numOctaves)
	{
			double val = 0; //set the current perlin value equal to 0
			
			//set the current frequency and amplitude to 1
			double freq = 1;
			double amp = 1;
			
			//iterate over the number of octaves to generate layers for fractal perlin noise
			for (int k = 0; k < numOctaves; k++)
			{
				val += getPerlinVal(x * freq/scale, y * freq/scale) * amp; //add the current perlin value to the total
				
				freq *= 2; //multiply frequency by 2 for more precision
				amp /= 2; //divide amplitude by 2 for less impact on final value
			}
			
			//if-else to limit the value to being between -1 and 1
			if (val > 1)
			{
				val = 1;
			}
			else if (val < -1)
			{
				val = -1;
			}

		return val; //return the final fractal perlin value
	}
}
