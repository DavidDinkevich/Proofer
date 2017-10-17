package ui.canvas;

import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A container to store {@link GraphicsShape}s and sort them in their
 * respective {@link Layer}s.
 * @author David Dinkevich
 */
public class RenderList implements Drawable {	
	private Map<String, List<GraphicsShape<?>>> renderList;
	
	public RenderList() {
		// LinkedHashMap to maintain insertion order
		renderList = new LinkedHashMap<>();
		// Fill the render list with all existing layers
		for (Layer lay : LayerManager.getLayers()) {
			renderList.put(lay.getName(), new ArrayList<>());
		}
	}
	
	@Override
	public void draw(Canvas c) {
		for (List<GraphicsShape<?>> list : renderList.values()) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).draw(c);
			}
		}
	}
	
	/**
	 * Add a {@link GraphicsShape} to the list of its {@link Layer}.
	 */
	public void add(GraphicsShape<?> o) {
		getLayerList(o.getLayer()).add(o);
	}
	
	/**
	 * Remove a {@link GraphicsShape} from the list of its {@link Layer}.
	 * @return true if the {@link GraphicsShape} was contained in the list
	 * and successfully removed. False otherwise.
	 */
	public boolean remove(GraphicsShape<?> o) {
		return getLayerList(o.getLayer()).remove(o);
	}
	
	/**
	 * Get the {@link List} that stores the {@link GraphicsShape}s of the given
	 * {@link Layer}.
	 */
	public List<GraphicsShape<?>> getLayerList(String layerName) {
		return renderList.get(layerName);
	}
	
	public GraphicsShape<?> get(String layerName, int index) {
		return getLayerList(layerName).get(index);
	}
	
	public int getLayerCount() {
		return renderList.size();
	}
	
	public int getObjectCount() {
		int count = 0;
		for (List<?> list : renderList.values()) {
			count += list.size();
		}
		return count;
	}
	
	public boolean contains(GraphicsShape<?> o) {
		List<GraphicsShape<?>> list = getLayerList(o.getLayer());
		if (list == null)
			return false;
		return list.contains(o);
	}
}