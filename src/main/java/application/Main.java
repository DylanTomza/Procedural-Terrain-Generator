/************************************************************/
/* Author: Aslan Ali, Dylan Tomza, Gabe Bawden              */
/* Major: Computer Science, Mathematics                     */
/* Creation Date: April 29th, 2024                          */
/* Due Date: April 29th, 2024                               */
/* Course: CS211-02                                         */
/* Professor Name: Professor Shimkanon                      */
/* Assignment: Final Project                                */
/* Filename: Main.java                                      */
/* Purpose: This file will create a widget object and       */
/* 			display the program to the screen               */
/************************************************************/

package application; //needed for javafx

//importing necessary packages
 import javafx.application.Application;
 import javafx.stage.Stage;
 import javafx.scene.layout.GridPane;
 
 public class Main extends Application {
		
	 	public static void main(String[] args)
	 	{
	 		launch(args);
	 	}
	 	public void start(Stage primaryStage) {
			try 
			{
				GridPane gridpane = new GridPane(); //creates new GridPane obj
				Widget widget = new Widget(gridpane, 10, 10); //creates new Widget object
				widget.display(primaryStage); //calls the display method to display the window on screen
				
			} catch(Exception e) 
			{
				e.printStackTrace(); //prints error message if any occur
			}
		}
}