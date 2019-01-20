package main;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import ui.FigureRelationListPanel;
import ui.canvas.diagram.DiagramCanvas;


public class MainWindow extends Application {
	
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 600;
	
	private DiagramCanvas canvas;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop() {
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		Group group = new Group();
		Scene scene = new Scene(group, WIDTH, HEIGHT);
		
		final int relListWidth = (int) (WIDTH * 0.3);
		FigureRelationListPanel relListPanel = 
				new FigureRelationListPanel(scene, this, relListWidth, HEIGHT);
		
		final int canvasWidth = (int) (WIDTH * 0.7);
		canvas = new DiagramCanvas(canvasWidth, HEIGHT);
		
		HBox hbox = new HBox();
		hbox.getChildren().add(canvas.getCanvas());
		hbox.getChildren().add(relListPanel);		
		
		group.getChildren().add(hbox);
		//Setting the scene to Stage 
		primaryStage.setScene(scene);
		//Setting the title to Stage. 
		primaryStage.setTitle("Proofer (alpha)");
				
		// Initial draw
		canvas.redraw();
		
		//Displaying the stage 
		primaryStage.show();
	}
	
	public DiagramCanvas getCanvas() {
		return canvas;
	}
	
}
