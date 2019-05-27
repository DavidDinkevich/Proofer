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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import main.MainWindow;

public class FigureRelationListPanel extends VBox {
		
	private MainWindow mainWindow;
	private Scene scene;
	
	private List<FigureRelationPanel> panels;
	
//	private BorderPane borderPane;
	private ScrollPane panelVBoxScroller;
	private VBox panelVBox;
//	private HBox buttonPanel;
	private FlowPane buttonPanel;
	
	private Button addButton, removeButton, solveButton;
	
	private FigureRelationPanel proofObjectivePanel;
	
	public FigureRelationListPanel(Scene scene, MainWindow win, double defWidth) {
		mainWindow = win;
		this.scene = scene;
		panels = new ArrayList<>();
		setBackground(new Background(new BackgroundFill(
				Color.rgb(242, 242, 242), CornerRadii.EMPTY, new Insets(0))));

		setMaxWidth(defWidth);

		// Create panel vbox
		createPanelVBox();
		// Create proof objective panel
		createObjectivePanel();
		// Create button panel
		createButtonPanel();

		// Window maintenance
        handleWindowMaintenance();
        
		// Initial panel
		addEmptyFigureRelationPanels(1);
	}
	
	/*
	 * INITIALIZATION METHODS
	 */
	
	private void createPanelVBox() {
		panelVBox = new VBox();
        panelVBoxScroller = new ScrollPane(panelVBox);
        panelVBoxScroller.setFitToWidth(true);
        TitledPane givenPane = new TitledPane("Given", panelVBoxScroller);
        getChildren().add(givenPane);
	}
	
	private void createObjectivePanel() {
		proofObjectivePanel = new FigureRelationPanel(FigureRelationPanel.Type.GOAL);
        proofObjectivePanel.setPadding(new Insets(10));
        
        // Enable solve button if the proof objective panel is partially filled out
        EventHandler<KeyEvent> handler = e-> {
        	final boolean disable = proofObjectivePanel.getFigTextField0().getText().isEmpty()
        			|| proofObjectivePanel.getFigTextField1().getText().isEmpty();
        	solveButton.setDisable(disable);
        };
        proofObjectivePanel.getFigTextField0().addEventHandler(KeyEvent.KEY_RELEASED, handler);
        proofObjectivePanel.getFigTextField1().addEventHandler(KeyEvent.KEY_RELEASED, handler);
        /////
        
        TitledPane proofPane = new TitledPane("To Prove", proofObjectivePanel);
        getChildren().add(proofPane);
	}
	
	private void createButtonPanel() {
        buttonPanel = new FlowPane();
        buttonPanel.setHgap(2);
//		buttonPanel.setPadding(new Insets(5, 10, 5, 10));
//		buttonPanel.setBorder(new Border(new BorderStroke(Color.BLACK, 
//        	BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
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
		
		// SOLVE BUTTON
		createSolveButton();
		buttonPanel.getChildren().add(solveButton);
	}
	
	private void createSolveButton() {
		solveButton = new Button("Solve");
		// Disabled by default (proof objective panel is empty
		solveButton.setDisable(true);
//		solveButton.setStyle("-fx-background-color: green");
		solveButton.setOnAction(e -> {
			Diagram diagram = Preprocessor.generateDiagram(mainWindow.getCanvas(), this);
			ProofSolveRequestManager.requestSolveProof(new Request(diagram) {
				@Override
				public void onRequestCompleted(ProofSolver solver) {
					if (solver.getResult()) {
						// Launch proof-result window
			            Stage stage = new Stage();
			            stage.setTitle("Result");
			            Group group = new Group();
			            group.getChildren().add(new ProofResultPanel(solver.getTraceback()));
			            stage.setScene(new Scene(group));
			            stage.show();
					} else {
						// Display dialog showing that proof is not solvable
						Alert alert = new Alert(AlertType.INFORMATION, "The given proof is not "
								+ "solvable", ButtonType.OK);
						alert.showAndWait();
					}
				}
			});
		});
	}
	
	private void handleWindowMaintenance() {
		/*
		 * Prevents canvas from not being able to regain focus after it
		 * is lost
		 */
		scene.focusOwnerProperty().addListener(new ChangeListener<Node>() {
			@Override
			public void changed(ObservableValue<? extends Node> arg0, 
					Node oldNode, Node newNode) {
				// If the user clicks on a ScrollPane, TitledPane, or Button,
				// then focus will be auto given to AdvancedCanvas
				if (
						   newNode instanceof ScrollPane
						|| newNode instanceof TitledPane
						|| newNode instanceof Button
				) {
					// Give focus to canvas
		            mainWindow.getCanvas().getCanvas().requestFocus();
				}				
			}
		});
		
		/*
		 * Handle resizing
		 */
		
		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> obs, Number oldHeight, 
					Number newHeight) {
				final double defPanelVBoxHeight = 400;
				final double diff = newHeight.doubleValue() - MainWindow.DEF_HEIGHT;
				panelVBoxScroller.setPrefHeight(defPanelVBoxHeight + diff);
			}	
		});				
	}
	
	/*
	 * END INITIALIZATION METHODS
	 */
	
	public void addFigureRelationPanel(FigureRelationPanel panel) {
		if (panels.add(panel)) {
	        panel.setPadding(new Insets(10, 7, 10, 7));
			panelVBox.getChildren().add(panel);
			removeButton.setDisable(panels.isEmpty());
			
			// When tab key is pressed in the second textfield in the last panel in the list, 
			// a new empty panel is automatically added
			panel.getFigTextField1().addEventHandler(KeyEvent.KEY_PRESSED, e -> {
				// Tab + shift is not down + this is the last panel in the list
				if (e.getCode() == KeyCode.TAB && !e.isShiftDown()
					&& panels.get(panels.size()-1).equals(panel)) {
					
					addEmptyFigureRelationPanels(1);
				}
			});
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
			addFigureRelationPanel(new FigureRelationPanel(FigureRelationPanel.Type.GIVEN));
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
		return proofObjectivePanel;
	}

	
}
