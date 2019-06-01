package ui;

import geometry.proofs.FigureRelation;
import geometry.proofs.FigureRelationType;
import geometry.proofs.ProofUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import static geometry.proofs.FigureRelationType.CONGRUENT;


public class FigureRelationPanel extends HBox {
	
	public static enum Type {
		GIVEN, GOAL
	}
	
	private Type type;
	
	private TextField fig0TextField, fig1TextField;
	private ComboBox<String> relationBox;
	
	public FigureRelationPanel(Type type, FigureRelationType relType, String f0, String f1) {
		super(10);
		this.type = type;
		
		setPadding(new Insets(10, 2, 10, 2));
		
		fig0TextField = new FigureTextField(f0);
		fig0TextField.setPrefColumnCount(5);
		fig1TextField = new FigureTextField(f1);
		fig1TextField.setPrefColumnCount(5);
		
		// Get vals for combo box
		ObservableList<String> vals = FXCollections.observableArrayList();
		for (String relTypeName : ProofUtils.getUserFigureRelationTypes()) {
			FigureRelationType t = ProofUtils.toFigureRelationType(relTypeName);
			// Don't add isosceles to panels of type GIVEN
			if (type == Type.GIVEN && t == FigureRelationType.ISOSCELES) {
				continue;
			}
			vals.add(relTypeName);
		}
		
		relationBox = new ComboBox<String>(vals);
		relationBox.setMinWidth(150);
		relationBox.getSelectionModel().select(relType.toString());
		relationBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, 
					String oldValue, String newValue) {
				// If we've changed to a single-figure relation, get rid of the
				// second figure field and expand the first figure field
				if (getRelationType().isSingleFigureRelationType()) {
					getChildren().remove(fig1TextField);
					fig0TextField.setPrefColumnCount(15);
				}
				// If we're changing back from a single-figure relation, add the second
				// figure field
				else if (FigureRelationType.valueOf(oldValue.toUpperCase())
						.isSingleFigureRelationType()) {
					getChildren().add(fig1TextField);
					fig0TextField.setPrefColumnCount(5);
				}
			}
		});
		
		getChildren().addAll(fig0TextField, relationBox, fig1TextField);
	}
	
	public FigureRelationPanel(Type type) {
		this(type, CONGRUENT, "", "");
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
		if (getRelationType().isSingleFigureRelationType())
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
