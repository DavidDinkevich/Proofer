package ui.canvas.diagram;

import java.util.Map;

import ui.canvas.Canvas;
import ui.canvas.Drawable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A container to store {@link Drawable}s and sort them in their
 * respective {@link Layer}s.
 * @author David Dinkevich
 */
public class RenderList implements Drawable {	
	private Map<UIDiagramLayers, List<Drawable>> list;
	
	public RenderList() {
		// LinkedHashMap to maintain insertion order
		list = new LinkedHashMap<>();
	}
	
	@Override
	public void draw(Canvas c) {
		for (List<Drawable> list : list.values()) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).draw(c);
			}
		}
	}
	
	protected Map<UIDiagramLayers, List<Drawable>> getList() {
		return list;
	}
	
	/**
	 * Add a {@link Drawable} to the list of its layer.
	 */
	public boolean addDrawable(Drawable o) {
		return getLayerList(o.getLayer()).add(o);
	}
	
	/**
	 * Remove a {@link Drawable} from the list of its layer.
	 * @return true if the {@link Drawable} was contained in the list
	 * and successfully removed. False otherwise.
	 */
	public boolean removeDrawable(Drawable o) {
		return getLayerList(o.getLayer()).remove(o);
	}
	
	/**
	 * Add a layer to this {@link RenderList}.
	 * @param lay the name of the layer
	 * @return true if the layer was added, false otherwise
	 */
	// Package private
	boolean addLayer(UIDiagramLayers lay) {
		if (list.containsKey(lay)) {
			return false;
		}
		list.put(lay, new ArrayList<>());
		return true;
	}
	
	/**
	 * Remove a layer from this {@link RenderList}.
	 * @param name the name of the layer to remove
	 * @return true if the layer was successfully removed
	 */
	// Package private
	boolean removeLayer(UIDiagramLayers name) {
		return list.remove(name) != null;
	}
	
	/**
	 * Get the {@link List} that stores the {@link Drawable}s of the given
	 * layer.
	 */
	public List<Drawable> getLayerList(UIDiagramLayers layer) {
		return list.get(layer);
	}
	
	/**
	 * Clear the list that corresponds to the given {@link UIDiagramLayers}.
	 * @param layer the layer to be cleared
	 */
	public void clearLayerList(UIDiagramLayers layer) {
		List<Drawable> layerList = getLayerList(layer);
		
		if (layerList == null)
			throw new IllegalArgumentException("Their is no corresponding "
					+ "list for the given UIDiagramLayer.");
		
		layerList.clear();
	}
	
	public int getLayerCount() {
		return list.size();
	}
	
	public int getDrawableCount() {
		int count = 0;
		for (List<?> list : list.values()) {
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
}