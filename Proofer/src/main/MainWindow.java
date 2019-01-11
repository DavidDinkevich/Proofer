package main;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class MainWindow extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop() {
		System.exit(0);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		//Setting the title to Stage. 
		primaryStage.setTitle("Sample application");
		
		Canvas canvas = new Canvas(1000, 600);
		canvas.getGraphicsContext2D().setFill(Paint.valueOf("red"));
		canvas.getGraphicsContext2D().fillRect(0, 0, 1000, 600);
		
		Group group = new Group();
		group.getChildren().add(canvas);
		
		Scene scene = new Scene(group, 1000, 600);
		
		//Setting the scene to Stage 
		primaryStage.setScene(scene);
		       
		//Displaying the stage 
		primaryStage.show();

		       
	}
}






