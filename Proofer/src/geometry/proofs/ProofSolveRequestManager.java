package geometry.proofs;

import java.util.LinkedList;
import java.util.Queue;

public final class ProofSolveRequestManager {

	private static Queue<Request> queue = new LinkedList<>();
	private static boolean isProcessingRequest;
	
	private ProofSolveRequestManager() {
		throw new AssertionError("Do not instantiate an object of this class!");
	}
	
	public static void requestSolveProof(Request request) {
		queue.add(request);
		solveNextProof();
	}
	
	private static void solveNextProof() {
		if (!isProcessingRequest && !queue.isEmpty()) {
			isProcessingRequest = true;
			Request current = queue.poll();
			ProofSolver solver = new ProofSolver(current.getDiagram());
			solver.solve();
			current.onRequestCompleted(solver);
			isProcessingRequest = false;			
			solveNextProof();
		}
	}
	
	
	public static abstract class Request {
		private Diagram diagram;
		
		public Request(Diagram diagram) {
			this.diagram = diagram;
		}
		
		public Diagram getDiagram() {
			return diagram;
		}
		
		public Diagram setDiagram(Diagram newDiagram) {
			Diagram old = diagram;
			diagram = newDiagram;
			return old;
		}
		
		public abstract void onRequestCompleted(ProofSolver solver);
	}
}
