package ui;

import geometry.proofs.FigureRelationType;
import geometry.proofs.ProofUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;


public class FigureRelationPanel extends HBox {
	
	private TextField fig0TextField, fig1TextField;
	private ComboBox<String> relationBox;
	
	public FigureRelationPanel() {
		super(10);
		setPadding(new Insets(10, 2, 10, 2));
		
		fig0TextField = new FigureTextField();
		fig0TextField.setPrefColumnCount(5);
		fig1TextField = new FigureTextField();
		fig1TextField.setPrefColumnCount(5);
		
		// Get vals for combo box
		ObservableList<String> vals = FXCollections.observableArrayList();
		for (FigureRelationType type : FigureRelationType.values())
			vals.add(type.toString());
		
		relationBox = new ComboBox<String>(vals);
		relationBox.getSelectionModel().select(0);
		
		
		getChildren().addAll(fig0TextField, relationBox, fig1TextField);
	}
	
	/**
	 * Get whether this contains enough information to represent
	 * a complete {@link FigureRelation} (all text fields contain
	 * text).
	 * @return true if this is filled out completely, false otherwise.
	 */
	public boolean hasContent() {
		// Get whether the relation declares an angle/triangle to be "right". In this case,
		// the second text box would be left blank
		final boolean isRightRelation = getRelationType() == FigureRelationType.RIGHT;
		return getFigTextField0().getText().length() > 0
				&& isRightRelation ? true : getFigTextField1().getText().length() > 0;
				// Relation type combo box will always have a value
	}

	public FigureRelationType getRelationType() {
		return ProofUtils.toFigureRelationType(relationBox.getValue());
	}
	
	public TextField getFigTextField0() {
		return fig0TextField;
	}

	public TextField getFigTextField1() {
		return fig1TextField;
	}

	public ComboBox<String> getRelationBox() {
		return relationBox;
	}
	
}
