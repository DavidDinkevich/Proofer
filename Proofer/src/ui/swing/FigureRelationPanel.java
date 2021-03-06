package ui.swing;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import geometry.proofs.FigureRelation;
import geometry.proofs.FigureRelationType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

@SuppressWarnings("serial")
public class FigureRelationPanel extends JComponent {
	private LetterOnlyTextField figTextField0;
	private LetterOnlyTextField figTextField1;
	private JComboBox<FigureRelationType> relationBox;
	
	public FigureRelationPanel() {		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		
//		setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createEtchedBorder(),
//				BorderFactory.createEmptyBorder(8, 10, 8, 10))
//		);
		setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
		
		add(getFigTextField0(), gbc);
		++gbc.gridx;
		add(getRelationBox(), gbc);
		++gbc.gridx;
		add(getFigTextField1(), gbc);
	}
	
	public FigureRelationPanel(FigureRelationType type, String fig0, String fig1) {
		this();
		getFigTextField0().setText(fig0);
		getFigTextField1().setText(fig1);
		getRelationBox().setSelectedItem(type);
	}
	
	public FigureRelationPanel(FigureRelation relation) {
		this(relation.getRelationType(), relation.getFigure0().toString(),
				relation.getFigure1().toString());
	}
	
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o))
			return false;
		if (!(o instanceof FigureRelationPanel))
			return false;
		FigureRelationPanel panel = (FigureRelationPanel)o;
		return getFigTextField0().getText().equals(panel.getFigTextField0().getText())
				&& getFigTextField1().getText().equals(panel.getFigTextField1().getText())
				&& getRelationBox().getSelectedIndex() == panel.getRelationBox().getSelectedIndex();
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + getFigTextField0().getText().hashCode();
		result = 31 * result + getFigTextField1().getText().hashCode();
		result = 31 * result + getRelationBox().getSelectedIndex();
		return result;
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
		final boolean isRightRelation = ((FigureRelationType)relationBox.getSelectedItem())
				== FigureRelationType.RIGHT;
		return getFigTextField0().getText().length() > 0
				&& isRightRelation ? true : getFigTextField1().getText().length() > 0;
				// Relation type combo box will always have a value
	}

	public JTextField getFigTextField0() {
		if (figTextField0 == null) {
			figTextField0 = new LetterOnlyTextField(5);
			figTextField0.setBeepOnInvalidInput(true);
			figTextField0.setHorizontalAlignment(JTextField.CENTER);
		}
		return figTextField0;
	}
	
	public JTextField getFigTextField1() {
		if (figTextField1 == null) {
			figTextField1 = new LetterOnlyTextField(5);
			figTextField1.setBeepOnInvalidInput(true);
			figTextField1.setHorizontalAlignment(JTextField.CENTER);
		}
		return figTextField1;
	}
	
	public JComboBox<FigureRelationType> getRelationBox() {
		if (relationBox == null) {
			relationBox = new JComboBox<>(FigureRelationType.values());
			relationBox.setMaximumRowCount(FigureRelationType.values().length);
//			relationBox.setEditable(true);
		}
		return relationBox;
	}
}
