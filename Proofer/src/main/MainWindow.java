package main;

import geometry.Dimension;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import ui.FigureRelationListPanel;
import ui.canvas.diagram.DiagramCanvas;


public class MainWindow extends Application {
	
	public static final float WIDTH = 1000f;
	public static final float HEIGHT = 600f;
	
	private DiagramCanvas canvas;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop() {
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// MIN SIZE FOR WINDOW
		primaryStage.setMinWidth(WIDTH);
		primaryStage.setMinHeight(HEIGHT + 40); // +40 to account for window toolbar
		
		Group group = new Group();
		Scene scene = new Scene(group, WIDTH, HEIGHT);
		
		// Create FigureRelationListPanel
		FigureRelationListPanel relListPanel = new FigureRelationListPanel(scene, this);
		
		// Create canvas
		// Width is left-over space from the FigureRelationListPanel
		final float canvasWidth = (float) (WIDTH - relListPanel.getWidth());
		canvas = new DiagramCanvas(canvasWidth, HEIGHT);
		
		// Add to hbox
		HBox hbox = new HBox();
		hbox.getChildren().add(canvas.getCanvas());
		hbox.getChildren().add(relListPanel);		
		
		// Add to group
		group.getChildren().add(hbox);
		//Setting the scene to Stage 
		primaryStage.setScene(scene);
		//Setting the title to Stage. 
		primaryStage.setTitle("Proofer (alpha)");
		
		/*
		 * RESIZING
		 */
		
		primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, 
					Number newValue) {
				Dimension newSize = new Dimension(
						(float) (newValue.floatValue() - relListPanel.getWidth()), 
						canvas.getSize().getHeight()
				);
				canvas.setSize(newSize);
			};
		});
				
		primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, 
					Number newValue) {
				Dimension newSize = new Dimension(
						canvas.getSize().getWidth(), 
						newValue.floatValue()
				);
				canvas.setSize(newSize);				
			};
		});

		//Displaying the stage 
		primaryStage.show();
	}
	
	public DiagramCanvas getCanvas() {
		return canvas;
	}
	
}
