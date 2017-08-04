package ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;

import net.miginfocom.swing.MigLayout;

import util.Utils;

@SuppressWarnings("serial")
public class FigureRelationListPanel extends JComponent {
	private ProofCustomizationPanel proofCustomizationPanel;
	
	private List<FigureRelationPanel> panels;
	private FigureRelationPanel proofGoalPanel;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JButton addButton;
	private JButton removeButton;
	private JButton solveButton;
	
	private Border spaceBorder;
	
	public FigureRelationListPanel(ProofCustomizationPanel proofCustomizationPanel) {
		this.proofCustomizationPanel = proofCustomizationPanel;
		
		spaceBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		
		setLayout(new BorderLayout());
		setBorder(spaceBorder);
		panels = new ArrayList<>();
		
		JScrollPane scrollPane = new JScrollPane(getTopPanel());
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
		
		add(getBottomPanel(), BorderLayout.SOUTH);
		
		// Add default figure relation pair panel
		addButton.doClick(0);
	}
	
	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
//			topPanel.setBorder(BorderFactory.createCompoundBorder(
//					spaceBorder, BorderFactory.createTitledBorder("Given")));
			topPanel.setBorder(BorderFactory.createTitledBorder("Given"));
			MigLayout migLay = new MigLayout();
			topPanel.setLayout(migLay);
		}
		return topPanel;
	}
	
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
			bottomPanel.setBorder(spaceBorder);
			// The bottom panel is composed of two inner-panels
			JPanel northPanel = new JPanel(new BorderLayout());
			northPanel.setBorder(BorderFactory.createTitledBorder("Prove"));
			proofGoalPanel = new FigureRelationPanel();
			northPanel.add(proofGoalPanel, BorderLayout.CENTER);
			
			JPanel southPanel = new JPanel();
			southPanel.setBorder(BorderFactory.createEmptyBorder(13, 0, 0, 0));
			southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
			southPanel.add(getAddPanelButton());
			southPanel.add(getRemovePanelButton());
			southPanel.add(Box.createHorizontalGlue());
			southPanel.add(getSolveButton());
			
			JPanel specCharsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			specCharsPanel.setBorder(BorderFactory.createTitledBorder("Special Characters"));
			specCharsPanel.add(new SpecialCharButton(Utils.DELTA, "Triangle symbol"));
			specCharsPanel.add(new SpecialCharButton(Utils.ANGLE_SYMBOL, "Angle symbol"));
			
			bottomPanel.add(northPanel);
			bottomPanel.add(specCharsPanel);
			bottomPanel.add(southPanel);
		}
		return bottomPanel;
	}
	
	public JButton getAddPanelButton() {
		if (addButton == null) {
			addButton = new JButton("Add");
			addButton.addActionListener(e -> {
				addEmptyFigureRelationPairPanels(1);
			});
		}
		return addButton;
	}
	
	public JButton getRemovePanelButton() {
		if (removeButton == null) {
			removeButton = new JButton("Remove");
			removeButton.addActionListener(e -> {
				panels.remove(panels.size()-1);
				topPanel.remove(panels.size());
				topPanel.repaint();
				// If there are no more panels, disable the remove button
				if (panels.isEmpty()) {
					removeButton.setEnabled(false);
				}
			});
		}
		return removeButton;
	}
	
	public JButton getSolveButton() {
		if (solveButton == null) {
			solveButton = new JButton("Solve");
			solveButton.setBackground(Color.GREEN.darker());
			solveButton.addActionListener(e -> {
				proofCustomizationPanel.requestSolveProof();
			});
			
		}
		return solveButton;
	}
	
	public FigureRelationPanel getProofGoalPanel() {
		return proofGoalPanel;
	}
	
	public boolean addFigureRelationPairPanel(FigureRelationPanel panel) {
		if (!panels.add(panel))
			return false;
		// Add to main panel
		final int ROW = panels.size() - 1;
		topPanel.add(panel, "cell 0 " + ROW); // Have to use this restraint bc "wrap" doesn't work
		validate(); // Repaint
		getRemovePanelButton().setEnabled(true);
		return true;
	}
	
	public void addFigureRelationPairPanels(Collection<FigureRelationPanel> panels) {
		for (FigureRelationPanel panel : panels) {
			addFigureRelationPairPanel(panel);
		}
	}
	
	public void addEmptyFigureRelationPairPanels(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Number of panels must be >= 0");
		for (int i = 0; i < amount; i++) {
			addFigureRelationPairPanel(new FigureRelationPanel());
		}
	}
	
	public boolean removeFigureRelationPairPanel(FigureRelationPanel panel) {
		if (!panels.remove(panel))
			return false;
		// Remove from main panel
		return true;
	}
	
	public void removeFigureRelationPairPanels(Collection<FigureRelationPanel> panels) {
		for (FigureRelationPanel panel : panels) {
			removeFigureRelationPairPanel(panel);
		}
	}
	
	public boolean containsFigureRelationPairPanel(FigureRelationPanel panel) {
		return panels.contains(panel);
	}
	
	public boolean containsAllFigureRelationPairPanels(Collection<FigureRelationPanel> panels) {
		for (FigureRelationPanel panel : panels) {
			if (!containsFigureRelationPairPanel(panel))
				return false;
		}
		return true;
	}
	
	public List<FigureRelationPanel> getFigureRelationPairPanels() {
		return Collections.unmodifiableList(panels);
	}
	
	public ProofCustomizationPanel getProofCustomizationPanel() {
		return proofCustomizationPanel;
	}
	
	/**
	 * Set the parent {@link ProofCustomizationPanel} of this panel.
	 * @param proofCustomizationPanel the new parent panel
	 * @return the previous parent window
	 */
	public ProofCustomizationPanel setProofCustomizationPanel(
			ProofCustomizationPanel proofCustomizationPanel) {
		ProofCustomizationPanel oldPanel = this.proofCustomizationPanel;
		this.proofCustomizationPanel = proofCustomizationPanel;
		return oldPanel;
	}
	
	
	private static class SpecialCharButton extends JButton {
		private static Object oldFocusOwner;
		
		static {
			// Be updated about when different components grab focus
			FocusManager.getCurrentKeyboardFocusManager().
			addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getOldValue() instanceof JTextField)
						oldFocusOwner = event.getOldValue();
				}
			});
		}
				
		public SpecialCharButton(String ch, String toolTipText) {
			super(ch);
			setToolTipText(toolTipText);
			
			addActionListener(e -> {
				if ((oldFocusOwner != null) && (oldFocusOwner instanceof JTextField)) {
					JTextField textField = (JTextField)oldFocusOwner;
					try {
						textField.getDocument().insertString(
								textField.getCaretPosition(), ch, null);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					textField.grabFocus();
				} else {
					// Beep if no text field is focused
					Toolkit.getDefaultToolkit().beep();
				}
			});
		}
	}
}
