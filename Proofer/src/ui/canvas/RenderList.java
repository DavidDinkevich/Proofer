package ui.canvas;

import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A container to store {@link Drawable}s and sort them in their
 * respective {@link Layer}s.
 * @author David Dinkevich
 */
public class RenderList implements Drawable {	
	private Map<String, List<Drawable>> renderList;
	
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
		for (List<Drawable> list : renderList.values()) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).draw(c);
			}
		}
	}
	
	/**
	 * Add a {@link Drawable} to the list of its {@link Layer}.
	 */
	public void addDrawable(Drawable o) {
		getLayerList(o.getLayer()).add(o);
	}
	
	/**
	 * Remove a {@link Drawable} from the list of its {@link Layer}.
	 * @return true if the {@link Drawable} was contained in the list
	 * and successfully removed. False otherwise.
	 */
	public boolean removeDrawable(Drawable o) {
		return getLayerList(o.getLayer()).remove(o);
	}
	
	/**
	 * Get the {@link List} that stores the {@link Drawable}s of the given
	 * {@link Layer}.
	 */
	public List<Drawable> getLayerList(String layerName) {
		return renderList.get(layerName);
	}
	
	public Drawable get(String layerName, int index) {
		return getLayerList(layerName).get(index);
	}
	
	public int getLayerCount() {
		return renderList.size();
	}
	
	public int getDrawableCount() {
		int count = 0;
		for (List<?> list : renderList.values()) {
			count += list.size();
		}
		return count;
	}
	
	public boolean contains(Drawable o) {
		List<Drawable> list = getLayerList(o.getLayer());
		if (list == null)
			return false;
		return list.contains(o);
	}

	@Override
	public String getLayer() {
		return DiagramCanvas.Layers.DEFAULT.toString();
	}
}