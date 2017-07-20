package ui.canvas;

import java.util.ArrayList;
import java.util.HashMap;

import util.IDList;

/**
 * A container to store {@link GraphicsShape}s and sort them in their
 * respective {@link Layer}s.
 * @author David Dinkevich
 */
public class RenderList implements Drawable {	
	private ArrayList<IDList<GraphicsShape<?>>> renderList;
	private HashMap<String, Long> listCodes;
	
	public RenderList() {
		renderList = new ArrayList<>();
		listCodes = new HashMap<>();
		// Fill the render list with all existing layers
		for (Layer lay : LayerManager.getLayers()) {
			IDList<GraphicsShape<?>> list = new IDList<>();
			renderList.add(list); // Add list for layer
			listCodes.put(lay.getName(), list.getListCode()); // Store list code
		}
	}
	
	@Override
	public void draw(Canvas c) {
		for (IDList<GraphicsShape<?>> idMan : renderList) {
			for (int i = 0; i < idMan.count(); i++) {
				idMan.get(i).draw(c);
			}
		}
	}
	
	/**
	 * Add a {@link GraphicsShape} to the list of its {@link Layer}.
	 */
	public void add(GraphicsShape<?> o) {
		getLayerList(o.getLayer()).addObject(o);
	}
	
	/**
	 * Remove a {@link GraphicsShape} from the list of its {@link Layer}.
	 * @return true if the {@link GraphicsShape} was contained in the list
	 * and successfully removed. False otherwise.
	 */
	public boolean remove(GraphicsShape<?> o) {
		return getLayerList(o.getLayer()).removeObject(o);
	}
	
	/**
	 * Get the {@link IDList} that stores the {@link GraphicsShape}s of the given
	 * {@link Layer}.
	 */
	public IDList<GraphicsShape<?>> getLayerList(String layerName) {
		long listCode = listCodes.get(layerName);
		for (int i = 0; i < renderList.size(); i++) {
			IDList<GraphicsShape<?>> list = renderList.get(i);
			if (list.getListCode() == listCode) {
				return list;
			}
		}
		return null;
	}
	
	public GraphicsShape<?> get(String layerName, int index) {
		return getLayerList(layerName).get(index);
	}
	
	public int getLayerCount() {
		return renderList.size();
	}
	
	public int getObjectCount() {
		int count = 0;
		for (IDList<?> list : renderList) {
			count += list.count();
		}
		return count;
	}
	
	public boolean contains(GraphicsShape<?> o) {
		IDList<GraphicsShape<?>> list = getLayerList(o.getLayer());
		if (list == null)
			return false;
		return list.contains(o);
	}
}