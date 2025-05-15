/************************************************************/
/* Author: Aslan Ali, Dylan Tomza, Gabe Bawden              */
/* Major: Computer Science, Mathematics                     */
/* Creation Date: April 29th, 2024                          */
/* Due Date: April 29th, 2024                               */
/* Course: CS211-02                                         */
/* Professor Name: Professor Shimkanon                      */
/* Assignment: Final Project                                */
/* Filename: Widget.java                                    */
/* Purpose: This file houses the widget class and the       */
/* 			contents of the widget object create by main    */
/************************************************************/

package application; //needed for javafx

//importing necessary packages
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import java.util.Random;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import java.awt.Desktop;
import java.io.File;  

public class Widget
{
	//create fields for gridPane, padding, and inset
	private GridPane gridPane;
	private int padding;
	private int inset;
	
	/***************************************************************************/
	/* Function name: Widget(constructor)
	/* Description: creates a new widget object based on given parameters
	/* Parameters: GridPane - g: to hold buttons, sliders, etc, int - p: padding, int i: insets
	/* Return Value: void
	/***************************************************************************/
	public Widget(GridPane g, int p, int i)
	{
		gridPane = g;
		padding = p;
		inset = i;
	}

	/***************************************************************************/
	/* Function name: display
	/* Description: displays the window to the screen
	/* Parameters: Stage - primaryStage: stage to be displayed on screen
	/* Return Value: void
	/***************************************************************************/
	public void display(Stage primaryStage)
	{
		Random rand = new Random();

		//Labels for seed, sea level, erosion level, map scale, size, detail level, and generate 3-D model
		Label seedLabel = new Label("Seed:");
		Label seaLabel = new Label("Sea Level:");
		Label erosionLabel = new Label("Erosion Level:");
		Label mapScaleLabel = new Label("Map Scale:");
		Label sizeLabel = new Label("Size:");
		Label numOctavesLabel = new Label("Detail Level:");
		Label generateModelLabel = new Label("Generate 3-D Model");
		
		//TextFields for seed value and size
		TextField seedVal = new TextField();
		TextField size = new TextField();
		//Buttons generating random see, generating map, and opening the 3-D model
		Button generateSeed = new Button("Random Seed");
		Button generateMap = new Button("Generate Map");
		Button openModel = new Button("Open 3-D Model");
		
		//CheckBox to enable/disable generating a 3-D model
		CheckBox generateModel = new CheckBox();
		
		//Sliders for erosion level, sea level, map scale, number of octaves
		Slider erosionlvl = new Slider(0, 24, 12);
		erosionlvl.setShowTickMarks(true); //displays tick marks
		erosionlvl.setShowTickLabels(true); //displays tick labels
		erosionlvl.setSnapToTicks(true); //only allows tick mark values to be selected
		erosionlvl.setMajorTickUnit(6); //sets major tick unit to 6
		erosionlvl.setMinorTickCount(5); //sets minor tick count to 5
		
		Slider sealvl = new Slider(0, 1, 0.1);
		sealvl.setShowTickMarks(true); //displays tick marks
		sealvl.setShowTickLabels(true); //displays tick labels
		sealvl.setSnapToTicks(true); //only allows tick mark values to be selected
		sealvl.setMajorTickUnit(.5); //sets major tick unit to .5
		sealvl.setMinorTickCount(4); //sets minor tick count to 4
		
		Slider mapScale = new Slider(100, 500, 300);
		mapScale.setShowTickMarks(true); //displays tick marks
		mapScale.setShowTickLabels(true); //displays tick labels
		mapScale.setSnapToTicks(true); //only allows tick mark values to be selected
		mapScale.setMajorTickUnit(200); //sets major tick unit to 200
		mapScale.setMinorTickCount(4); //sets minor tick count to 4
		
		Slider numOctaves = new Slider(1,15 ,8);
		numOctaves.setShowTickMarks(true); //displays tick marks
		numOctaves.setShowTickLabels(true); //displays tick labels
		numOctaves.setSnapToTicks(true); //only allows tick mark values to be selected
		numOctaves.setMajorTickUnit(7); //sets major tick unit to 7
		numOctaves.setMinorTickCount(6); //sets minor tick count to 6
				
		//adding all labels, buttons, sliders, etc. to inner grid pane
		GridPane gridPaneInner = new GridPane();
		gridPaneInner.add(seedLabel, 0, 0);
		gridPaneInner.add(seedVal, 1, 0);
		gridPaneInner.add(generateSeed, 1, 1);
		gridPaneInner.add(sizeLabel, 0, 2);
		gridPaneInner.add(size, 1,2);
		gridPaneInner.add(erosionLabel, 0, 3);
		gridPaneInner.add(erosionlvl, 1, 3);
		gridPaneInner.add(seaLabel, 0, 4);
		gridPaneInner.add(sealvl, 1, 4);
		gridPaneInner.add(mapScaleLabel, 0, 5);
		gridPaneInner.add(mapScale, 1, 5);
		gridPaneInner.add(numOctavesLabel, 0, 6);
		gridPaneInner.add(numOctaves, 1, 6);
		gridPaneInner.add(generateModelLabel, 0, 7);
		gridPaneInner.add(generateModel,1, 7);
		gridPaneInner.add(generateMap, 1, 8);
		gridPaneInner.add(openModel, 1, 9);

		StackPane imageHolder = new StackPane();
		gridPane.add(imageHolder, 1, 0);
		
		//sets the padding between gridPane items
		gridPaneInner.setVgap(padding);
		gridPaneInner.setHgap(padding);
		
		//adds the inner grid pane to the outer grid pane and sets outer grid pane padding
		gridPane.add(gridPaneInner, 0, 0);
		gridPane.setHgap(padding);

		//sets the insets for the outer grid pane (distance from border of window)
		Insets insets = new Insets(inset, inset, inset, inset);
		gridPane.setPadding(insets);

		Scene scene2 = new Scene(gridPane, 825, 520); //creates new scene object
		scene2.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); //gets style info from css file
		primaryStage.setTitle("Procedural Terrain Generator"); //sets title of application
		//sets the scene to scene2 and displays it to the screen
		primaryStage.setScene(scene2);
		primaryStage.show();
		
		
		//event handler for when "Random Seed" is clicked, sets the number in the seed textbox to a random int
		generateSeed.setOnAction(e -> {seedVal.setText(String.valueOf(rand.nextInt()));});
		
		//holds a 1 or 0 depending on whether a model was created in the most recent terrain generation
		int[] newModel = {0};
		
		//event handler for when "Generate Map" is clicked
		generateMap.setOnAction(e -> {
			try 
			{
				//creates variables to be passed into the Terrain constructor and sets
				//them equal to the appropriate values
				int userSeed = Integer.parseInt(seedVal.getText());
				double userSeaLevel = sealvl.getValue();
				int userErosionLevel = (int)erosionlvl.getValue();
				int userMapScale = (int)mapScale.getValue();
				int userSize = Integer.parseInt(size.getText());
				int userNumOctaves = (int)numOctaves.getValue();
				
				//prevents bug where sea level = 0 caused where the water would be to be black
				if(userSeaLevel ==0)
				{
					userSeaLevel = .01;
				}
				
				//creates new WritableImage obj, a PixelWriter obj to color it, and an ImageView obj to view it
				WritableImage image = new WritableImage(userSize,userSize);
				PixelWriter pixelWriter = image.getPixelWriter();
				ImageView imageView = new ImageView(image);

				//sets the width and height of the ImageView obj to 500
				imageView.setFitHeight(500);
				imageView.setFitWidth(500);
				
				//creates new terrain object using the inputted parameters
				Terrain userTerrain = new Terrain(userSeed, userMapScale, userNumOctaves, userSize, userSeaLevel);
				
				//calls the fillHeightMap, erosionIterations, and drawImage methods to generate and display the terrain
				userTerrain.fillHeightMap();
				userTerrain.erosionIterations(userErosionLevel);
				userTerrain.drawImage(pixelWriter);
				
				//checks if generateModel was selected
				if(generateModel.isSelected() == true)
				{
					//if it was selected, set newModel[0] to 1 and create the model
					newModel[0] = 1;
					userTerrain.createModel();
				}
				else
				{
					//if it was not selected, set newModel[0] to 0
					newModel[0] = 0;
				}
				imageHolder.getChildren().setAll(imageView);

			}catch(Exception e1)
			{
				Alert dg1 = new Alert(Alert.AlertType.WARNING); //creates new alert box
				dg1.setTitle("Error"); //sets title to alert box
				dg1.setContentText("Please enter only numeric values for the seed"
						           + "\n and only positive integers for the size"); //sets text of the alert
				dg1.show();	//displays alert box
				e1.printStackTrace(); //prints error messages
			}
		});
		
		openModel.setOnAction(e -> {
			try
			{
				//check if a newModel was created
				if(newModel[0] == 0)
				{
					//if no new model was created, throw an exception
					throw new Exception("1/0");
				}
				else 
				{
					//if a new model was created, open it
					Desktop desktop = Desktop.getDesktop(); //creates desktop obj
					File file = new File("map.obj"); //creates file obj using "map.obj"
					desktop.open(file); //opens the file
				}
				
			}
			catch(Exception e2)
			{
				Alert dg2 = new Alert(Alert.AlertType.WARNING); //creates new alert box
				dg2.setTitle("Error"); //sets title to alert box
				dg2.setContentText("You must generate a 3-D model before opening one."); //sets text of alert
				dg2.show();	//displays alert box
				e2.printStackTrace(); //prints error messages
			}
		});
	}
}
