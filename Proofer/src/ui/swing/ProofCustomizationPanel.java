package ui.swing;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import geometry.proofs.Diagram;
import geometry.proofs.Preprocessor;
import geometry.proofs.ProofSolveRequestManager;
import geometry.proofs.ProofSolveRequestManager.Request;
import geometry.proofs.ProofSolver;

import ui.canvas.DiagramCanvas;

@SuppressWarnings("serial")
public class ProofCustomizationPanel extends JComponent {
	
	// Screen components
	private DiagramCanvas canvas;
	private FigureRelationListPanel figRelationListPanel;
	
	private ProofSolutionWindow solutionWindow;

	public ProofCustomizationPanel() {
		setSize(1200, 700);
		setLayout(new BorderLayout());
		
		canvas = new DiagramCanvas(this, new geometry.Dimension(700, 700));
		add(canvas, BorderLayout.CENTER);

		figRelationListPanel = new FigureRelationListPanel(this);
		add(figRelationListPanel, BorderLayout.EAST);
		
//		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//		splitPane.setRightComponent(figRelationListPanel);
//		splitPane.setLeftComponent(canvas);
//		add(splitPane, BorderLayout.CENTER);
		
		canvas.init();
	}
	
	public DiagramCanvas getCanvas() {
		return canvas;
	}
	
	public FigureRelationListPanel getFigureRelationListPanel() {
		return figRelationListPanel;
	}
	
	/**
	 * If this panel has not been proved yet, this will submit a proof solve
	 * request to the {@link ProofSolveRequestManager}. Otherwise, it will give
	 * focus to the proof solution window.
	 */
	public void requestSolveProof() {
//		if (solutionWindow == null) {
			Preprocessor preprocessor = new Preprocessor(canvas, figRelationListPanel);
			Diagram diagram = preprocessor.generateDiagram();
			ProofSolveRequestManager.requestSolveProof(new Request(diagram) {
				@Override
				public void onRequestCompleted(ProofSolver solver) {
					solutionWindow =
							new ProofSolutionWindow(ProofCustomizationPanel.this, solver);
				}
			});
//		} else {
			solutionWindow.setVisible(true); // Make sure window is visible
//			solutionWindow.requestFocus();
//		}
	}
}
