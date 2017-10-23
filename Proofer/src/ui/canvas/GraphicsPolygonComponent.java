package ui.canvas;

/**
 * Represents a {@link Segment} or {@link Angle} in a {@link GraphicsPolygon}. 
 * Can be rendered to the screen by the {@link Drawable#draw(Canvas)} method.
 * @author David Dinkevich
 */
public class GraphicsPolygonComponent implements Drawable {
	private GraphicsTriangle parentTri;
	private String componentName;
	
	public GraphicsPolygonComponent(GraphicsTriangle tri, String componentName) {
		parentTri = tri;
		this.componentName = componentName;
	}
	
	@Override
	public void draw(Canvas c) {
		
	}
	
	public String getComponentName() {
		return componentName;
	}
	
	public GraphicsTriangle getParentPolygon() {
		return parentTri;
	}
}
