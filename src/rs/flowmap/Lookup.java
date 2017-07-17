package rs.flowmap;

import java.util.HashMap;
import java.util.Set;

/**
 * A HashTable based Lookup. Wrapps up a hashtable to be faster than using List.contains(...).
 * Read more: https://stackoverflow.com/questions/3307549/fastest-way-to-check-if-a-liststring-contains-a-unique-string
 * 
 * @author Ludwig Meysel
 * 
 * @version 16.07.2017
 */
public class Lookup<T> {
	private HashMap<T, Boolean> h;
	
	/**
	 * Creates a new object from the {@see Lookup} class.
	 */
	public Lookup() {
		h = new HashMap<T, Boolean>();
	}
	
	/**
	 * Gets a value indicating whether this lookup contains the specified element.
	 * @param object The element to check for.
	 * @return True, when the lookup contains the specified element, false otherwise.
	 */
	public boolean has(T object) {
		return h.containsKey(object);
	}
	
	/**
	 * Adds an element to the lookup.
	 * @param object The element to add.
	 */
	public void add(T object) {
		h.put(object, true);
	}
	
	/**
	 * @return
	 */
	public Set<T> getElements() {
		return h.keySet();
	}
}
