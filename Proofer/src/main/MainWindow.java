package main;

import geometry.Dimension;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ui.FigureRelationListPanel;
import ui.canvas.diagram.DiagramCanvas;


public class MainWindow extends Application {
	
	public static final double DEF_WIDTH = 1050;
	public static final double DEF_HEIGHT = 600;
	
	private static final double DEF_FIG_REL_PANEL_WIDTH = 360;
	
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
		primaryStage.setMinWidth(DEF_WIDTH);
		primaryStage.setMinHeight(DEF_HEIGHT + 45); // +40 to account for window toolbar
		
		Group group = new Group();
		Scene scene = new Scene(group, DEF_WIDTH, DEF_HEIGHT);
		
		// Create FigureRelationListPanel
		FigureRelationListPanel relListPanel = 
				new FigureRelationListPanel(scene, this, DEF_FIG_REL_PANEL_WIDTH);

		// Create canvas
		// Width is left-over space from the FigureRelationListPanel
		final float canvasWidth = (float) (DEF_WIDTH - DEF_FIG_REL_PANEL_WIDTH);
		canvas = new DiagramCanvas(canvasWidth, (float) DEF_HEIGHT);
		
		// Add to hbox
		HBox hbox = new HBox();
		hbox.getChildren().add(relListPanel);
		hbox.getChildren().add(canvas.getCanvas());
		
		// Add to group
		group.getChildren().add(hbox);
		//Setting the scene to Stage 
		primaryStage.setScene(scene);
		//Setting the title to Stage. 
		primaryStage.setTitle("Proofer (alpha)");
		
		// KILL PROGRAM WHEN THIS WINDOW IS CLOSED
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		        System.exit(0);
		    }
		});
				
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
						newValue.floatValue() - 50
				);
				canvas.setSize(newSize);
				relListPanel.setPrefHeight(newValue.doubleValue());
			};
		});
		
		primaryStage.sizeToScene();

		// Initial drawing of canvas
		canvas.redraw();
		
		//Displaying the stage 
		primaryStage.show();
	}
	
	public DiagramCanvas getCanvas() {
		return canvas;
	}
	
}
