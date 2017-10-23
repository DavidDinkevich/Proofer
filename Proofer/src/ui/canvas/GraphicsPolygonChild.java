package ui.canvas;

/**
 * Represents a child in a {@link GraphicsPolygon}. 
 * Can be rendered to the screen by the {@link Drawable#draw(Canvas)} method.
 * @author David Dinkevich
 */
public class GraphicsPolygonChild implements Drawable {
	private GraphicsTriangle parentTri;
	private String childName;
	
	public GraphicsPolygonChild(GraphicsTriangle tri, String childName) {
		parentTri = tri;
		this.childName = childName;
	}
	
	@Override
	public void draw(Canvas c) {
		
	}
	
	public String getChildName() {
		return childName;
	}
	
	public GraphicsTriangle getParentPolygon() {
		return parentTri;
	}
}
