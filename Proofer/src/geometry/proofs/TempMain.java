package geometry.proofs;

import geometry.proofs.ProofSolveRequestManager.Request;

public class TempMain {
	
	public static void main(String[] args) {
		
		Diagram diag = new Diagram();

		diag.addFigure(new TriangleFigure("ACF"));
		diag.addFigure(new TriangleFigure("ACB"));
		diag.addFigure(new SegmentFigure("FB"));
		
		diag.addFigureRelationPair(FigureRelationType.BISECTS, "AC", "FB");
		
		diag.setProofGoal(FigureRelationType.CONGRUENT, "FC", "CB");
		
		ProofSolveRequestManager.requestSolveProof(new Request(diag) {
			@Override
			public void onRequestCompleted(ProofSolver solver) {
				System.out.println(solver.solve());
			}
		});
	}
	
}
