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
	
	public static enum Type {
		GIVEN, GOAL
	}
	
	private Type type;
	
	private TextField fig0TextField, fig1TextField;
	private ComboBox<String> relationBox;
	
	public FigureRelationPanel(Type type) {
		super(10);
		this.type = type;
		
		setPadding(new Insets(10, 2, 10, 2));
		
		fig0TextField = new FigureTextField();
		fig0TextField.setPrefColumnCount(5);
		fig1TextField = new FigureTextField();
		fig1TextField.setPrefColumnCount(5);
		
		// Get vals for combo box
		ObservableList<String> vals = FXCollections.observableArrayList();
		for (FigureRelationType relType : FigureRelationType.values()) {
			// Don't add isosceles to panels of type GIVEN
			if (type == Type.GIVEN && relType == FigureRelationType.ISOSCELES) {
				continue;
			}
			vals.add(relType.toString());
		}
		
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
		// First text field MUST have text no matter what
		if (getFigTextField0().getText().isEmpty()) {
			return false;
		}
		// If it is a single figure relation, second text field will be blank
		if (getRelationType().isSingleFigureRelation())
			return true;
		// If not, make sure the second field has text
		return !getFigTextField1().getText().isEmpty();
	}
	
	public Type getType() {
		return type;
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
