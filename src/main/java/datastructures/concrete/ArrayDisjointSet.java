package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
	// Note: do NOT rename or delete this field. We will be inspecting it
	// directly within our private tests.
	private int[] pointers;

	// However, feel free to add more methods and private helper methods.
	// You will probably need to add one or two more fields in order to
	// successfully implement this class.
	private IDictionary<T, Integer> dict;
	private int val;

	public ArrayDisjointSet() {
		dict = new ChainedHashDictionary<>();
		pointers = new int[val];
	}

	@Override
	public void makeSet(T item) {
		// already a part of this disjoint set somewhere
		if (dict.containsKey(item)) {
			throw new IllegalArgumentException();
		}
		dict.put(item, val);
		int[] temp = new int[val + 1];
		for (int i = 0; i < val; i++) {
			temp[i] = pointers[i];
		}
		temp[val] = -1;
		pointers = temp;
		val += 1;
	}

	@Override
	public int findSet(T item) {
		// the item is not contained inside this disjoint set
		if (!dict.containsKey(item)) {
			throw new IllegalArgumentException();
		}
		int root = dict.get(item);
		while (pointers[root] > 0) {
			root = pointers[root];
		}
		// change pointer
		int k = dict.get(item);
		while (pointers[k] > 0) {
			pointers[k] = root;
			k = pointers[k];
		}
		return root;
	}

	@Override
	public void union(T item1, T item2) {
		if (!dict.containsKey(item1) || !dict.containsKey(item2) || findSet(item1) == findSet(item2)) {
			throw new IllegalArgumentException();
		}
		if (pointers[findSet(item1)] >= pointers[findSet(item2)]) {
			pointers[findSet(item1)] = findSet(item2);
			pointers[findSet(item2)] -= 1;
		} else {
			pointers[findSet(item2)] = findSet(item1);
			pointers[findSet(item1)] -= 1;
		}
	}
}
