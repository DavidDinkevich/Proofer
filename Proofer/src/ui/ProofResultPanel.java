package ui;

import geometry.proofs.FigureRelation;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ProofResultPanel extends VBox {
	
	private ProofTable ptable;
	private CheckBox colorBranchesBox;
	
	public ProofResultPanel(FigureRelation[] traceback) {		
		// Background
		setBackground(new Background(new BackgroundFill(
				Color.rgb(242, 242, 242), CornerRadii.EMPTY, new Insets(0))));
		
		// PROOF TABLE
		ptable = new ProofTable(traceback);
		getChildren().add(ptable);
		
		// COLOR BRANCHES CHECKBOX
		colorBranchesBox = new CheckBox("Color Branches");
		colorBranchesBox.setSelected(true);
		colorBranchesBox.selectedProperty().addListener(e -> {
			ptable.setColorBranches(colorBranchesBox.isSelected());
		});
		
		FlowPane buttonPane = new FlowPane(colorBranchesBox);
		buttonPane.setPadding(new Insets(10, 10, 10, 10));
		getChildren().add(buttonPane);
	}
	
}
