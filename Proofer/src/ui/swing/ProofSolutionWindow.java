package ui.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JFrame;

import geometry.proofs.ProofSolver;

@SuppressWarnings("serial")
public class ProofSolutionWindow extends JFrame {
	private ProofSolver solver;
	private Component parentComponent;
	
	public ProofSolutionWindow(Component parentComponent, ProofSolver solver) {
		super("Solution");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(350, 300);
		setLocationRelativeTo(parentComponent);
		
		this.parentComponent = parentComponent;
		this.solver = solver;
		
		getContentPane().setBackground(solver.solve() ? Color.GREEN : Color.RED);
		
		setVisible(true);
	}
	
	public ProofSolutionWindow(ProofSolver solver) {
		this(null, solver);
	}
	
	
	public ProofSolver setProofSolver(ProofSolver solver) {
		ProofSolver old = this.solver;
		this.solver = solver;
		return old;
	}
	
	public ProofSolver getProofSolver() {
		return solver;
	}

	public Component getParentComponent() {
		return parentComponent;
	}

	public Component setParentComponent(Component parentComponent) {
		Component old = this.parentComponent;
		this.parentComponent = parentComponent;
		return old;
	}
}
