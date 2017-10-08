package ui.canvas;

import geometry.Dimension;
import geometry.shapes.Ellipse;
import processing.core.PApplet;

public final class StyleManager {
	public static final int RED, BLUE, LIGHT_BLUE, GREEN, YELLOW, ORANGE,
							PURPLE, PINK;
	
	private static Brush defaultFigureBrush;
	
	/**
	 * The brush with which a figure is painted when it is
	 * highlighted
	 */
	private static Brush highlightedFigureBrush;
	
	private static Brush selectionContainerBrush;
	
	private static Brush canvasGridBrush;
	
	private static Brush selectorBrush;
	
	private static final GraphicsEllipse knobBody;
	
	private static TextFont textFont;
	
	private static Brush uiRelationMakerBrush;
	
	static {
		RED = makeColor(255, 0, 0);
		BLUE = makeColor(0, 0, 255);
		LIGHT_BLUE = makeColor(50, 150, 255);
		GREEN = makeColor(0, 255, 0);
		YELLOW = makeColor(255, 255, 0);
		ORANGE = makeColor(255, 100, 0);
		PURPLE = makeColor(150, 50, 255);
		PINK = makeColor(255, 0, 255);
		
		Brush.Builder brushBuilder = new Brush.Builder().setFill(StyleManager.PINK)
				.setStrokeWeight(2.5f).setStroke(StyleManager.PINK).setAlpha(75);
		defaultFigureBrush = brushBuilder.buildBrush();
		
		brushBuilder.setAlpha(130f); // Slightly less transparent
		highlightedFigureBrush = brushBuilder.buildBrush();
		
		brushBuilder.setFill(makeColor(255, 255, 0)).setAlpha(80)
			.setStroke(makeColor(255, 220, 0)).setStrokeWeight(2);
		selectionContainerBrush = brushBuilder.buildBrush();
		
		brushBuilder.setFill(0).setAlpha(255).setStroke(0).setStrokeWeight(0.05f);
		canvasGridBrush = brushBuilder.buildBrush();
		
		brushBuilder.setFill(0).setStroke(LIGHT_BLUE).setStrokeWeight(4f).setRenderFill(false);
		selectorBrush = brushBuilder.buildBrush();
				
		brushBuilder.setFill(BLUE).setStroke(255).setStrokeWeight(2f).setRenderFill(true);
		knobBody = new GraphicsEllipse(
				brushBuilder.buildBrush(), new Ellipse(new Dimension(12f))
		);
		
		brushBuilder.setStrokeWeight(4f);
		uiRelationMakerBrush = brushBuilder.buildBrush();
		
		textFont = new TextFont(16);
	}
	
	// Suppress default constructor for noninstantiability
	private StyleManager() {
		throw new AssertionError("Don't instantiate an object of this class.");
	}
	
	
	/*
	 * Making colors
	 */
	
	public static int makeColor(int r, int g, int b) {
		return new PApplet().color(r, g, b);
	}
	public static int makeColor(int r, int g, int b, float a) {
		return new PApplet().color(r, g, b, a);
	}
	
	public static Brush getDefaultFigureBrush() {
		return defaultFigureBrush;
	}
	public static void setDefaultFigureBrush(Brush brush) {
		defaultFigureBrush = brush;
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
	
	public static GraphicsEllipse getKnobBody() {
		return knobBody;
	}

	public static Brush getUIRelationMakerBrush() {
		return uiRelationMakerBrush;
	}

	public static void setUIRelationMakerBrush(Brush brush) {
		uiRelationMakerBrush = brush;
	}
	
	public static TextFont getTextFont() {
		return textFont;
	}
	public static void setTextFont(TextFont textFont) {
		StyleManager.textFont = textFont;
	}
}
