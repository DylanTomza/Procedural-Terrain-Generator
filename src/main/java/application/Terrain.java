/************************************************************/
/* Authors: Gabe Bawden, Dylan Tomza */
/* Major: Mathematics, Computer Science */
/* Creation Date: April 18th, 2024 */
/* Due Date: April 29th, 2024 */
/* Course: CS211-02 */
/* Professor Name: Professor Shimkanon */
/* Assignment: Final Project */
/* Filename: terrain.java */
/* Purpose: This class contains fields and methods needed for generating the */
/* procedural terrain. It has methods for setting the heightmap using perlin noise,*/
/* performing erosion, and generating 2D images and 3D models.*/
/* Sources: Some of the functions used in this class were ported to Java */
/* from Gabe's C project. These functions are noted in their headers. */
/************************************************************/

package application;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Terrain
{
	private int scale;
	private int numOctaves;
	private int width;
	private int height;
	int seed;
	
	private double seaLevel;
	double heightMap[];
	
	/***************************************************************************/
	/* Function name: Terrain (Constructor) */
	/* Description: Creates a terrain object using a set of parameters provided */
	/* Parameters: int – s1: The seed used in the generation */
	/* 			   int - scale1: The scale that the perlin noise will use in creating the heightmap */
	/* 			   int - o: The number of octaves that the perlin noise will use in creating the heightmap*/
	/* 			   int - w: The width/height that the heightmap will have*/
	/* 			   double - sL: The sea level for the terrain*/
	/* Return Value: void*/
	/***************************************************************************/
	Terrain(int s1, int scale1, int o, int w, double sL)
	{
		//Setting the fields to the given parameter variables
		this.seed = s1;
		this.scale = scale1;
		this.numOctaves = o;
		this.width = w;
		this.height = w;
		this.seaLevel = sL;
		
		//Creating the heightmap array based on the given width/height
		this.heightMap = new double[this.width * this.height];
	}
	
	/***************************************************************************/
	/* Function name: fillHeightMap */
	/* Description: Sets the initial heightmap for the terrain object using perlin noise */
	/* Parameters: None */
	/* Return Value: void */
	/***************************************************************************/
	public void fillHeightMap()
	{
		//Creating a perlin noise object
		PerlinNoise perlin = new PerlinNoise(this.seed);
		
		//Initializing the heightmap using values from the perlin noise object
		for (int i = 0; i < this.width; i++)
		{
			for (int j = 0; j < this.height; j++)
			{				
				this.heightMap[i + this.width * j] = 3 * (perlin.getFractalPerlinVal(i, j, this.scale, this.numOctaves) + 0.01);
			}
		}
	}
	
	/***************************************************************************/
	/* Author: Gabe Bawden */
	/* Function name: terrainGetNeighbors */
	/* Description: Retrieves the indices of the surrounding eight neighbors of a given point on the heightmap */
	/* Parameters: int – point: The index of the point whose neighbors will be retrieved */
	/* Return Value: int[] – An array of the indices of the neighbors of the given point */
	/***************************************************************************/
	private int[] terrainGetNeighbors(int point)
	{
		//Creating an array of the indices of the neighbors
		int[] neighbors = new int[8];
		
		//Setting the array index for each of the point's neighbors
		neighbors[0] = point + 1 + this.width; // Top Right
		neighbors[1] = point + 1; // Middle Right
		neighbors[2] = point + 1 - this.width; // Bottom Right
		neighbors[3] = point - this.width; // Bottom Middle
		neighbors[4] = point - 1 - this.width; // Bottom Left
		neighbors[5] = point - 1; // Middle Left
		neighbors[6] = point - 1 + this.width; // Top Left
		neighbors[7] = point + this.width; // Top Middle

		//Retrieving the x and y coordinates of the point from its index value
		int x = point % this.width;
		int y = point / this.width;

		//Setting the neighbor indices that go out of bounds of the heightmap to -1
		if (x == 0)
		{
			neighbors[4] = -1;
			neighbors[5] = -1;
			neighbors[6] = -1;
		}
		else if (x == this.width - 1)
		{
			neighbors[0] = -1;
			neighbors[1] = -1;
			neighbors[2] = -1;
		}
		if (y == 0)
		{
			neighbors[2] = -1;
			neighbors[3] = -1;
			neighbors[4] = -1;
		}
		else if (y == this.height - 1)
		{
			neighbors[0] = -1;
			neighbors[6] = -1;
			neighbors[7] = -1;
		}
		
		//Returning the neighbors array
		return neighbors;
	}
	
	/***************************************************************************/
	/* Author: Gabe Bawden */
	/* Function name: erodePosition */
	/* Description: Runs an erosion algorithm on a given position within the heightmap */
	/* Parameters: int – position: The index of the position within the heightmap to run the erosion on */
	/* Return Value: void */
	/***************************************************************************/
	private void erodePosition(int position)
	{
		//Setting the current index to the given position
		int currentIndex = position;

		//Running the erosion algorithm if the height at the given position is above sea level
		if (this.heightMap[currentIndex] > this.seaLevel)
		{
			//Boolean value indicating whether the algorithm should continue running or not
			boolean flow = true;

			//Measure of the amount of water in the simulated drop of water
			double water = 0;

			//Continuing to run the algorithm if the drop is allowed to keep flowing
			while (flow)
			{
				//Getting the height at the current position
				double currentHeight = this.heightMap[currentIndex];

				//Getting the current position's neighbors
				int[] neighbors = this.terrainGetNeighbors(currentIndex);

				//Initializing the lowestHeight and lowestIndex values
				double lowestHeight = 2147483647;
				int lowestIndex = -1;

				//Finding which of the point's neighbors has the lowest height
				for (int i = 0; i < neighbors.length; i++)
				{
					//Making sure that the neighbor is not out of bounds
					if (neighbors[i] != -1)
					{
						double neighborHeight = this.heightMap[neighbors[i]];

						//Setting flow to false if the current position has a neighbor below sea level
						if (neighborHeight <= this.seaLevel)
						{
							flow = false;
						}
						
						if (neighborHeight < lowestHeight)
						{
							lowestHeight = neighborHeight;
							lowestIndex = i;
						}
					}
				}

				//Preventing the lowest height from being below sea level
				if (lowestHeight <= this.seaLevel)
				{
					lowestHeight = this.seaLevel + 0.001;
				}
				
				//Calculating the slope based on the current and lowest heights
				double slope = currentHeight - lowestHeight;
				
				//Diving the slope by the square root of two if the neighbor is diagonal
				if (lowestIndex == 0 || lowestIndex == 2 || lowestIndex == 4 || lowestIndex == 6)
				{
					slope /= 1.41421356; //Square root of 2
				}

				//Performing erosion if the slope is greater than 0
				if (slope > 0)
				{
					//Calculating the point's new, lowered height
					double heightNew = this.heightMap[currentIndex] - slope * slope * 0.6;

					//Preventing the new height from dropping below its lowest neighbor's height
					heightNew = Math.max(heightNew, lowestHeight + 0.001);

					//Setting the new height at the current position
					this.heightMap[currentIndex] = heightNew;

					//Performing erosion on the current point's neighbors
					for (int i = 0; i < neighbors.length; i++)
					{
						//Making sure that the neighbor is not out of bounds and that it is not below sea level
						if (i != lowestIndex && neighbors[i] != -1 && this.heightMap[neighbors[i]] > this.seaLevel)
						{
							//Calculating the neighbor's new, lowered height
							double heightNewNeighbor = this.heightMap[neighbors[i]] - slope * slope * 0.6 * Math.min(water, 1.0);

							//Preventing the new height from dropping below its lowest neighbor's height
							heightNewNeighbor = Math.max(heightNewNeighbor, lowestHeight + 0.001);

							//Setting the new height at the neighbor's position
							this.heightMap[neighbors[i]] = heightNewNeighbor;
						}
					}

					//Increasing the amoung of water in the drop
					water += 0.01;
				}
				
				//If the slope is less than 0, increase the current point's height to just above its lowest neighbor's height
				if ((flow) && (slope <= 0))
				{
					this.heightMap[currentIndex] = lowestHeight + 0.001;
				}

				//Changing the current position to the lowest neighbor's index
				currentIndex = neighbors[lowestIndex];
			}

			//Depositing sediment into the ocean after the erosion loop has finished
			
			//Calculating the velocity based on the amount of water in the drop
			int velocity = (int)(water * 100);

			//Continue the deposition while the velocity is greater than 0
			while (velocity > 0)
			{
				//Getting the height at the current position
				double currentHeight = this.heightMap[currentIndex];

				//Getting the current position's neighbors
				int[] neighbors = this.terrainGetNeighbors(currentIndex);

				//Initializing the lowestHeight, highestHeight, and lowestIndex values
				double lowestHeight = 2147483647;
				double highestHeight = -2147483647;
				int lowestIndex = -1;

				//Finding which of the point's neighbors have the lowest and heighest heights
				for (int i = 0; i < neighbors.length; i++)
				{
					//Making sure that the neighbor is not out of bounds
					if (neighbors[i] != -1)
					{
						//Retrieving the neighbor's height
						double heightNeighbor = this.heightMap[neighbors[i]];

						if (heightNeighbor < lowestHeight)
						{
							lowestHeight = heightNeighbor;
							lowestIndex = i;
						}
						
						if (heightNeighbor > highestHeight)
						{
							highestHeight = heightNeighbor;
						}
					}
				}

				//Calculating the point's new, increased height
				double heightNew = this.heightMap[currentIndex] + 1.0 / (250 * velocity * velocity);
				
				//Preventing the new height from rising above its highest neighbor's height
				this.heightMap[currentIndex] = Math.min(heightNew, highestHeight - 0.001);

				//Stopping the deposition algorithm if the lowest height is greater than the current height
				if (lowestHeight >= currentHeight)
				{
					break;
				}

				//Changing the current position to the lowest neighbor's index
				currentIndex = neighbors[lowestIndex];

				//Decrementing the velocity
				velocity--;
			}
		}
	}
	
	/***************************************************************************/
	/* Author: Gabe Bawden */
	/* Function name: erosionIterations */
	/* Description: Runs iterations of the erosion algorithm on random points within the heightmap */
	/* Parameters: int – erosionLevel: Value determining how many points the erosion algorithm should be run on */
	/* Return Value: void */
	/***************************************************************************/
	public void erosionIterations(int erosionLevel)
	{
		//Calculating the amount of erosion iterations based on the erosion level and width and height
		int n = erosionLevel * this.width * this.height;
		
		//Creating Random object from the Terrain object's seed
		Random rand = new Random(this.seed);
		
		//Creating an array representing every point on the heightmap
		int[] list = new int[this.width * this.height];

		//Initializing the array to hold every possible index of the heightmap
		for (int i = 0; i < this.width * this.height; i++)
		{
			list[i] = i;
		}
		
		//Shuffling these indices to prevent artifacts from forming
		for (int i = 0; i < this.width * this.height; i++)
		{
			int x = rand.nextInt(this.width);
			int y = rand.nextInt(this.height);

			int hold = list[x + this.width * y];
			list[x + this.width * y] = list[i];
			list[i] = hold;
		}
		
		//Initializing the index to 0
		int index = 0;
		
		//Running the erosion algorithm n times
		for (int i = 0; i < n; i++)
		{
			this.erodePosition(list[index]);

			index++;

			//Reseting the index to 0 once every other point has been eroded on
			if (index >= this.width * this.height)
			{
				index = 0;
			}
		}
	}
	
	/***************************************************************************/
	/* Function name: setPixel */
	/* Description: Sets the rgb values of a single pixel within a PixelWriter object at a given position */
	/* Parameters: PixelWriter – img: The object to which the pixel color is to be written */
	/* 			   int - x: The x position of the pixel to be written */
	/* 			   int - y: The y position of the pixel to be written */
	/* 			   int - r: The r color value of the pixel to be written */
	/* 			   int - g: The g color value of the pixel to be written */
	/* 			   int - b: The b color value of the pixel to be written */
	/* Return Value: void */
	/***************************************************************************/
	private static void setPixel(PixelWriter img, int x, int y, int r, int g, int b)
	{
		//Creating a Color object
		Color color = new Color(r / 255.0, g / 255.0, b / 255.0, 1);
		
		//Setting the color at the specified position
		img.setColor(x,y, color);
	}
	
	/***************************************************************************/
	/* Author: Gabe Bawden */
	/* Function name: drawImage */
	/* Description: Uses the terrain object's heightmap to generate an image */
	/* Parameters: PixelWriter – pw: The PixelWriter object to which the the image will be created */
	/* Return Value: void */
	/***************************************************************************/
	public void drawImage(PixelWriter pw)
	{				
		//Looping over every position within the heightmap
		for (int i = 0; i < this.width; i++)
		{
			for (int j = 0; j < this.height; j++)
			{
				//Retrieving the height at the current position
				double z = this.heightMap[i + this.width * j];

				//Setting a maximum height value
				double maxHeight = 3.0f;
				
				//Setting the current height to 0 if it is below 0
				if (z < 0)
				{
					z = 0;
				}

				//Coloring the position's corresponding pixel position based on its height
				if (z <= this.seaLevel)
				{
					double m = (this.seaLevel - z) / (this.seaLevel - 0);
					m = 1 - m;
					m = (m + 1) / 2;

					setPixel(pw, i, j, (int)(20 * m), (int)(40 * m), (int)(220 * m));
				}
				else if (z < this.seaLevel + 0.01)
				{
					setPixel(pw, i, j, 238, 214, 175);
				}
				else if (z < this.seaLevel + 0.2f)
				{
					double m = (this.seaLevel + 0.2f - z) / (0.2f - 0.01f);
					m = 1 - m;
					m = (m + 1) / 2;

					setPixel(pw, i, j, (int)(34 * m), (int)(139 * m), (int)(34 * m));
				}
				else if (z < this.seaLevel + 0.4f)
				{
					double m = (this.seaLevel + 0.4f - z) / (0.4f - 0.2f);
					m = (m + 1) / 2;

					setPixel(pw, i, j, (int)(60 * m), (int)(139 * m), (int)(34 * m));
				}
				else if (z < this.seaLevel + 0.6f)
				{
					double m = (this.seaLevel + 0.6f - z) / (0.6f - 0.4f);
					m = (m + 1) / 2;

					setPixel(pw, i, j,  (int)(35 * m), (int)(65 * m), (int)(20 * m));
				}
				else if (z < this.seaLevel + 0.8f)
				{
					double m = (this.seaLevel + 0.8f - z) / (0.8f - 0.6f);
					m = (m + 1) / 2;

					setPixel(pw, i, j, (int)(25 * m), (int)(30 * m), (int)(10 * m));
				}
				else if (z < this.seaLevel + 1.0)
				{
					double m = (this.seaLevel + 1.0f - z) / (1.0f - 0.8f);
					m = (m + 1) / 2;

					setPixel(pw, i, j, (int)(20 * m), (int)(15 * m), (int)(10 * m));
				}
				else if (z < this.seaLevel + maxHeight)
				{
					double m = (this.seaLevel + maxHeight - z) / (maxHeight - 1.0f);
					m = 1 - m;
					m = (m + 0.5f) / 1.5f;

					setPixel(pw, i, j, (int)(255 * m), (int)(255 * m), (int)(255 * m));
				}
				else
				{
					setPixel(pw, i, j, 255, 255, 255);
				}
			}
		}
	}
	
	/***************************************************************************/
	/* Function name: createModel */
	/* Description: Uses the terrain object's heightmap to generate a 3D model */
	/* Parameters: None */
	/* Return Value: void */
	/***************************************************************************/
	public void createModel() 
	{
		try
		{
			//Creating an obj file
			File obj = new File("map.obj");
			FileWriter fw = new FileWriter(obj);
			
			//Looping over every position within the heightmap to set the vertices
			for (int i = 0; i < this.width; i++) 
			{
	            for (int j = 0; j < this.height; j++) 
	            {
	            	//Creating a vertex with an x,y,z position
	            	fw.write("v " + i + " " + 20 * this.heightMap[i + this.width * j] + " " + j + "\n");
	            }
	        }

			//Looping over every position within the heightmap to set the triangles
			for (int i = 0; i < width - 1; i++) 
			{
	            for (int j = 0; j < height - 1; j++) 
	            {
	            	//Creating two triangles for every square on the grid using the grid's corresponding vertices
	                fw.write("f " + (int)(i * width + j + 1) + " " + (int)(i * width + j + 1 + height) + " " 
	                		+ (int)(i * width + j + 2 + height) + "\n");
	                fw.write("f " + (int)(i * width + j + 1) + " " + (int)(i * width + j + 2) + " " 
	                		+ (int)(i * width + j + 2 + height) + "\n");
	            }
	        }
			
			//Closing the FileWriter object
			fw.close();
		}
		catch (Exception error2)
		{
			//Catching any errors
			error2.printStackTrace();
		}
	}
}