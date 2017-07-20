package util;

/**
 * Implementors of this interface are entities that can be stored
 * in {@link IDList}s. They keep a list of {@link IDAgent}s, each
 * representing the {@link Identifiable}'s location in an
 * {@link IDList}. {@link Identifiable}s cannot contain
 * multiple {@link IDAgent}s belonging to the same {@link IDList}.
 * @author David Dinkevich
 */
public interface Identifiable extends Iterable<IDAgent> {
	public void addId(IDAgent agent);
	public IDAgent removeId(long listCode);
	public boolean removeId(IDAgent agent);
	public IDAgent getAgent(long listCode);
	public IDAgent getAgent(int index);
	public boolean containsAgentInManager(long listCode);
}
