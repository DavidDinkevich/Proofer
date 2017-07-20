package ui.canvas;

/**
 * A {@link Layer} stores to attributes: a name, and a priority level.
 * {@link Layer}s with higher priority levels will be rendered before
 * others with lower priority levels.
 */
public class Layer {
	public static final int LOWEST_PRIORITY = 0;
	
	private String name;
	private int priority;
	
	public Layer(String name, int priority) {
		this.name = name;
		this.priority = priority;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Layer))
			return false;
		Layer lay = (Layer)o;
		return priority == lay.priority && lay.name.equals(name);
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + priority;
		result = 31 * result + name.hashCode();
		return result;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
}