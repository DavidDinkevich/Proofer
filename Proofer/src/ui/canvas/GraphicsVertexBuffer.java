package ui.canvas;

import java.util.ArrayList;
import java.util.List;

import geometry.shapes.Vertex;
import geometry.shapes.VertexBuffer;

/**
 * A class that renders the vertices in a {@link VertexBuffer}.
 * @author David Dinkevich
 */
public class GraphicsVertexBuffer implements Drawable {
	
	private VertexBuffer buffer;
	
	public GraphicsVertexBuffer(VertexBuffer buff) {
		buffer = buff;
	}
	
	@Override
	public void draw(AdvancedCanvas c) {
		// Avoid duplicate characters (don't want to draw twice)
		List<Character> toIgnore = new ArrayList<>();
		// For each vertex
		for (Vertex vertex : buffer) {
			// If the name of the vertex is on the ignore list, skip
			if (toIgnore.contains(vertex.getNameChar()))
				continue;
			// If the character is contained more than once in the buffer,
			// add it to the ignore list to avoid it later
			if (buffer.getInstanceCount(vertex.getNameChar()) > 1) {
				toIgnore.add(vertex.getNameChar());
			}
			
			GraphicsVertex gv = new GraphicsVertex(
					StyleManager.getVertexLabelBrush(), vertex);
			gv.draw(c);
		}
	}

	public VertexBuffer getVertexBuffer() {
		return buffer;
	}

	public void setVertexBuffer(VertexBuffer buffer) {
		this.buffer = buffer;
	}

}
