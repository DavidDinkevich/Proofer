package ui.canvas;

import geometry.Dimension;
import geometry.shapes.Ellipse;

public final class StyleManager {
	
	private static Brush defaultFigureBrush;
	
	private static Brush strokeFigureBrush;
	
	/**
	 * The brush with which a figure is painted when it is
	 * highlighted
	 */
	private static Brush highlightedFigureBrush;
	
	private static Brush invisibleHiddenFigureBrush;
	
	private static Brush selectionContainerBrush;
	
	private static Brush canvasGridBrush;
	
	private static Brush selectorBrush;
	
	private static final GraphicsEllipse knobBody;
		
	private static Brush uiRelationMakerBrush;
	
	private static Brush vertexLabelBrush;
	
	static {		
		Brush.Builder brushBuilder = new Brush.Builder();
		
		brushBuilder.getFill().set(Brush.PINK).setA(0.4f);
		brushBuilder.getStroke().set(Brush.PINK);
		brushBuilder.setStrokeWeight(0.4f);
		defaultFigureBrush = brushBuilder.buildBrush();
		
		brushBuilder.getStroke().set(Brush.PINK);
		brushBuilder.setStrokeWeight(2f);
		strokeFigureBrush = brushBuilder.buildBrush();
		
		// Slightly less transparent
		brushBuilder.getFill().setA(0.5f);
		brushBuilder.setStrokeWeight(0.1f);
		highlightedFigureBrush = brushBuilder.buildBrush();
		
		// Slightly more transparent
		brushBuilder.getFill().setA(0.25f);
		brushBuilder.setStrokeWeight(0.01f);
		invisibleHiddenFigureBrush = brushBuilder.buildBrush();
		
		brushBuilder.getFill().set(255, 255, 0).setA(0.3f);
		brushBuilder.getStroke().set(255, 220, 0).setA(0f);
		brushBuilder.setStrokeWeight(0);
		selectionContainerBrush = brushBuilder.buildBrush();
		
		brushBuilder.getStroke().set(0, 1f);
		brushBuilder.setStrokeWeight(0.1f);
		canvasGridBrush = brushBuilder.buildBrush();
		
		brushBuilder.getFill().set(0);
		brushBuilder.getStroke().set(Brush.LIGHT_BLUE);
		brushBuilder.setStrokeWeight(4f);
		selectorBrush = brushBuilder.buildBrush();
				
		brushBuilder.getFill().set(Brush.BLUE);
		brushBuilder.getStroke().set(255);
		brushBuilder.setStrokeWeight(1f);
		knobBody = new GraphicsEllipse(
				brushBuilder.buildBrush(), new Ellipse(new Dimension(17f))
		);
		
		brushBuilder.getStroke().set(Brush.LIGHT_BLUE);
		brushBuilder.setStrokeWeight(4f);
		uiRelationMakerBrush = brushBuilder.buildBrush();
		
		brushBuilder.getFill().set(Brush.BLACK);
		vertexLabelBrush = brushBuilder.buildBrush();
		
	}
	
	// Suppress default constructor for noninstantiability
	private StyleManager() {
		throw new AssertionError("Don't instantiate an object of this class.");
	}
	
	public static Brush getDefaultFigureBrush() {
		return defaultFigureBrush;
	}
	public static void setDefaultFigureBrush(Brush brush) {
		defaultFigureBrush = brush;
	}
	
	public static Brush getStrokeFigureBrush() {
		return strokeFigureBrush;
	}

	public static void setStrokeFigureBrush(Brush brush) {
		strokeFigureBrush = brush;
	}

	public static Brush getSelectorBrush() {
		return selectorBrush;
	}
	public static void setSelectorBrush(Brush newBrush) {
		if (newBrush == null)
			throw new NullPointerException();
		selectorBrush = newBrush;
	}
	
	public static Brush getSelectionContainerBrush() {
		return selectionContainerBrush;
	}
	public static void setSelectionContainerBrush(Brush newBrush) {
		if (newBrush == null)
			throw new NullPointerException();
		selectionContainerBrush = newBrush;
	}
	
	public static Brush getCanvasGridBrush() {
		return canvasGridBrush;
	}
	public static void setCanvasGridBrush(Brush newBrush) {
		if (newBrush == null)
			throw new NullPointerException();
		canvasGridBrush = newBrush;
	}
	
	public static Brush getHighlightedFigureBrush() {
		return highlightedFigureBrush;
	}
	public static void setHighlightedFigureBrush(Brush brush) {
		if (brush == null)
			throw new NullPointerException();
		highlightedFigureBrush = brush;
	}
	
	public static Brush getInvisibleHiddenFigureBrush() {
		return invisibleHiddenFigureBrush;
	}

	public static void setInvisibleHiddenFigureBrush(Brush brush) {
		invisibleHiddenFigureBrush = brush;
	}

	public static GraphicsEllipse getKnobBody() {
		return knobBody;
	}

	public static Brush getUIRelationMakerBrush() {
		return uiRelationMakerBrush;
	}

	public static void setUIRelationMakerBrush(Brush brush) {
		uiRelationMakerBrush = brush;
	}
	
	public static Brush getVertexLabelBrush() {
		return vertexLabelBrush;
	}
	
	public static void setVertexLabelBrush(Brush brush) {
		vertexLabelBrush = brush;
	}
	
}
