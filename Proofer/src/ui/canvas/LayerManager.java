package ui.canvas;

import java.util.ArrayList;

import ui.canvas.selection.Knob;
import ui.canvas.selection.Selector;

public final class LayerManager {
	public static final String DEFAULT_LAYER = "Default";
	
	private static ArrayList<Layer> layers;
		
	static {
		layers = new ArrayList<>();
		
		/*
		 * Add layers - layers cannot be changed after the program
		 * has been launched.
		 */
		
		addLayer(new Layer(DEFAULT_LAYER, Layer.LOWEST_PRIORITY));
		addLayer(new Layer(GraphicsShape.LAYER_NAME, Layer.LOWEST_PRIORITY+1));
		addLayer(new Layer(Selector.LAYER_NAME, Layer.LOWEST_PRIORITY+2));
		addLayer(new Layer(Knob.LAYER_NAME, Layer.LOWEST_PRIORITY+3));
	}
	
	// Do not instantiate a layer manager
	private LayerManager() {
		throw new AssertionError("Do not instantiate a LayerManager!");
	}
	
	public static Layer[] getLayers() {
		return layers.toArray(new Layer[layers.size()]);
	}
	
	public static int getLayerCount() {
		return layers.size();
	}
	
	public static Layer getLayer(String name) {
		for (Layer lay : layers) {
			if (lay.equals(name)) {
				return lay;
			}
		}
		return null;
	}
	public static Layer getLayer(int priority) {
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).getPriority() == priority) {
				return layers.get(i);
			}
		}
		return null;
	}
	/**
	 * Add a {@link Layer} to the list of {@link Layer}s. This {@link Layer} may
	 * not already be stored, and its priority level cannot be taken by another
	 * {@link Layer} already in the list.
	 * @param layer the {@link Layer} to add
	 * @return true if the {@link Layer} was successfully added, false otherwise.
	 */
	private static boolean addLayer(Layer layer) {
		if (!layers.contains(layer)) {
			// Is priority level already taken
			for (Layer lay : layers) {
				// Priority already contained
				if (lay.getPriority() == layer.getPriority()) {
					return false;
				}
			}
			layers.add(layer);
			return true;
		}
		return false;
	}
//	private static boolean removeLayer(String name) {
//		return layers.remove(new Layer("Name", -1)); // Priority level doesn't matter here, only name.
//	}
//	private static boolean removeLayer(int priority) {
//		for (int i = 0; i < layers.size(); i++) {
//			Layer lay = layers.get(i);
//			if (lay.getPriority() == priority) {
//				layers.remove(i);
//				return true;
//			}
//		}
//		return false;
//	}
}
