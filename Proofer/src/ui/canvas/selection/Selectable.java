package ui.canvas.selection;

/**
 * Any object who implements this interface has the option to
 * allow itself to be selected.
 * @author David Dinkevich
 */
public interface Selectable {	
	public boolean getAllowSelections();
	public void setAllowSelection(boolean selectable);
	public boolean isSelected();
	public void selectWithSelector(Selector<?, ?> sel);
	public Selector<?, ?> deselect();
	public Selector<?, ?> getSelector();
}