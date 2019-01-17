package ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import geometry.proofs.Diagram;
import geometry.proofs.Preprocessor;
import geometry.proofs.ProofSolveRequestManager;
import geometry.proofs.ProofSolveRequestManager.Request;
import geometry.proofs.ProofSolver;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import main.MainWindow;

public class FigureRelationListPanel extends VBox {
	
	private MainWindow mainWindow;
	
	private List<FigureRelationPanel> panels;
	
//	private BorderPane borderPane;
	private VBox panelVBox;
//	private HBox buttonPanel;
	private FlowPane buttonPanel;
	
	private Button addButton, removeButton, solveButton;
	
	private FigureRelationPanel proofPanel;

	public FigureRelationListPanel(MainWindow win, int w, int h) {
		mainWindow = win;
		panels = new ArrayList<>();
		setBackground(new Background(new BackgroundFill(
				Color.rgb(242, 242, 242), CornerRadii.EMPTY, new Insets(0))));
		
		setPrefSize(w, h);
		
		/*
		 * PANELS LIST PANEL
		 */
				
		panelVBox = new VBox();
        ScrollPane scroller = new ScrollPane(panelVBox);
        scroller.setFitToWidth(true);
        final double scrollerHeight = h * 0.9;
        scroller.setPrefHeight(scrollerHeight);
        scroller.setMaxHeight(scrollerHeight);
        TitledPane givenPane = new TitledPane("Given", scroller);
        getChildren().add(givenPane);

        /*
         * PROVE PANEL
         */
        proofPanel = new FigureRelationPanel();
        proofPanel.setPadding(new Insets(10));
        TitledPane proofPane = new TitledPane("To Prove", proofPanel);
        getChildren().add(proofPane);
        
        /*
         * BUTTON PANEL
         */
        
//		buttonPanel = new HBox(10);
        buttonPanel = new FlowPane();
        buttonPanel.setHgap(2);
//		buttonPanel.setPadding(new Insets(5, 10, 5, 10));
//		buttonPanel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        getChildren().add(buttonPanel);
		
		addButton = new Button("Add");
		addButton.setOnAction(e -> {
			addEmptyFigureRelationPanels(1);
		});
		buttonPanel.getChildren().add(addButton);
		
		removeButton = new Button("Remove");
		removeButton.setOnAction(e -> {
			removeFigureRelationPanels(1);
		});
		buttonPanel.getChildren().add(removeButton);
		
		solveButton = new Button("Solve");
//		solveButton.setStyle("-fx-background-color: green");
		solveButton.setOnAction(e -> {
			Preprocessor preprocessor = new Preprocessor(mainWindow.getCanvas(), this);
			Diagram diagram = preprocessor.generateDiagram();
			ProofSolveRequestManager.requestSolveProof(new Request(diagram) {
				@Override
				public void onRequestCompleted(ProofSolver solver) {
					System.out.println(solver.getResult());
				}
			});

		});
		buttonPanel.getChildren().add(solveButton);
		
		// Initial panel
		addEmptyFigureRelationPanels(1);
		
//		this.addEventFilter(FocusEvent., e -> {
//			
//		});
	}
	
	public void addFigureRelationPanel(FigureRelationPanel panel) {
		if (panels.add(panel)) {
	        panel.setPadding(new Insets(10, 5, 10, 5));
			panelVBox.getChildren().add(panel);
			removeButton.setDisable(panels.isEmpty());
		}
	}
	
	public void addFigureRelationPanels(Collection<FigureRelationPanel> panels) {
		for (FigureRelationPanel panel : panels) {
			addFigureRelationPanel(panel);
		}
	}
	
	public void addEmptyFigureRelationPanels(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Number of panels must be >= 0");
		for (int i = 0; i < amount; i++) {
			addFigureRelationPanel(new FigureRelationPanel());
		}
	}
	
	public void removeFigureRelationPanels(int num) {
		for (int i = 0; i < num; i++) {
			if (panels.remove(panels.get(panels.size()-1))) {
				panelVBox.getChildren().remove(panels.size());
			}
		}
		removeButton.setDisable(panels.isEmpty());
	}
	
	public List<FigureRelationPanel> getFigureRelationPanels() {
		return Collections.unmodifiableList(panels);
	}
	
	public FigureRelationPanel getProofGoalPanel() {
		return proofPanel;
	}

	
}
