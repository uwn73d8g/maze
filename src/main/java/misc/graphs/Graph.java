package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops,
 * parallel edges, and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a
 * graph. We stick with supporting just a few, core set of operations needed for
 * the remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
	// NOTE 1:
	//
	// Feel free to add as many fields, private helper methods, and private
	// inner classes as you want.
	//
	// And of course, as always, you may also use any of the data structures
	// and algorithms we've implemented so far.
	//
	// Note: If you plan on adding a new class, please be sure to make it a private
	// static inner class contained within this file. Our testing infrastructure
	// works by copying specific files from your project to ours, and if you
	// add new files, they won't be copied and your code will not compile.
	//
	//
	// NOTE 2:
	//
	// You may notice that the generic types of Graph are a little bit more
	// complicated then usual.
	//
	// This class uses two generic parameters: V and E.
	//
	// - 'V' is the type of the vertices in the graph. The vertices can be
	// any type the client wants -- there are no restrictions.
	//
	// - 'E' is the type of the edges in the graph. We've contrained Graph
	// so that E *must* always be an instance of Edge<V> AND Comparable<E>.
	//
	// What this means is that if you have an object of type E, you can use
	// any of the methods from both the Edge interface and from the Comparable
	// interface
	//
	// If you have any additional questions about generics, or run into issues while
	// working with them, please ask ASAP either on Piazza or during office hours.
	//
	// Working with generics is really not the focus of this class, so if you
	// get stuck, let us know we'll try and help you get unstuck as best as we can.
	private IDictionary<V, IList<E>> dict;
	private IList<E> edges;

	/**
	 * Constructs a new graph based on the given vertices and edges.
	 *
	 * @throws IllegalArgumentException
	 *             if any of the edges have a negative weight
	 * @throws IllegalArgumentException
	 *             if one of the edges connects to a vertex not present in the
	 *             'vertices' list
	 */
	public Graph(IList<V> vertices, IList<E> edges) {
		dict = new ChainedHashDictionary<>();
		this.edges = edges;
		for (V vertice : vertices) {
			dict.put(vertice, new DoubleLinkedList<>());
		}
		for (E edge : edges) {
			if (edge.getWeight() < 0) {
				throw new IllegalArgumentException();
			}
			if (!vertices.contains(edge.getVertex1()) || !vertices.contains(edge.getVertex2())) {
				throw new IllegalArgumentException();
			}
			dict.get(edge.getVertex1()).add(edge);
			dict.get(edge.getVertex2()).add(edge);
		}
	}

	/**
	 * Sometimes, we store vertices and edges as sets instead of lists, so we
	 * provide this extra constructor to make converting between the two more
	 * convenient.
	 */
	public Graph(ISet<V> vertices, ISet<E> edges) {
		// You do not need to modify this method.
		this(setToList(vertices), setToList(edges));
	}

	// You shouldn't need to call this helper method -- it only needs to be used
	// in the constructor above.
	private static <T> IList<T> setToList(ISet<T> set) {
		IList<T> output = new DoubleLinkedList<>();
		for (T item : set) {
			output.add(item);
		}
		return output;
	}

	/**
	 * Returns the number of vertices contained within this graph.
	 */
	public int numVertices() {
		return dict.size();
	}

	/**
	 * Returns the number of edges contained within this graph.
	 */
	public int numEdges() {
		return edges.size();
	}

	/**
	 * Returns the set of all edges that make up the minimum spanning tree of this
	 * graph.
	 *
	 * If there exists multiple valid MSTs, return any one of them.
	 *
	 * Precondition: the graph does not contain any unconnected components.
	 */
	public ISet<E> findMinimumSpanningTree() {
		ISet<E> mst = new ChainedHashSet<E>();
		IDisjointSet<V> initial = new ArrayDisjointSet<>();
		// sort all the edges
		IList<E> ed = Searcher.topKSort(numEdges(), edges);
		// create disjoint vertex
		for (KVPair<V, IList<E>> vertice : dict) {
			initial.makeSet(vertice.getKey());
		}
		for (E e : ed) {
			if (initial.findSet(e.getVertex1()) != initial.findSet(e.getVertex2())) {
				initial.union(e.getVertex1(), e.getVertex2());
				mst.add(e);
			}
		}
		return mst;
	}

	/**
	 * Returns the edges that make up the shortest path from the start to the end.
	 *
	 * The first edge in the output list should be the edge leading out of the
	 * starting node; the last edge in the output list should be the edge connecting
	 * to the end node.
	 *
	 * Return an empty list if the start and end vertices are the same.
	 *
	 * @throws NoPathExistsException
	 *             if there does not exist a path from the start to the end
	 */
	public IList<E> findShortestPathBetween(V start, V end) {
		IList<E> path = new DoubleLinkedList<>();
		if (start.equals(end)) {
			return path;
		}

		// dijkstra's algorithm
		ISet<V> visited = new ChainedHashSet<>();
		IPriorityQueue<Path<V, E>> heap = new ArrayHeap<Path<V, E>>();
		Path<V, E> ini = new Path<>(start, 0, new DoubleLinkedList<E>());
		heap.insert(ini);

		while (!heap.isEmpty() && heap.peekMin().endVertex != end) {
			Path<V, E> cur = heap.removeMin();
			if (visited.contains(cur.endVertex)) {
				continue;
			}
			for (E e : dict.get(cur.endVertex)) {
				if (!visited.contains(e.getOtherVertex(cur.endVertex))) {
					IList<E> curP = new DoubleLinkedList<>();
					for (E edge: cur.path) {
						curP.add(edge);
					}
					curP.add(e);
					Path<V, E> over = new Path<V, E>(e.getOtherVertex(cur.endVertex), 
							cur.cost + e.getWeight(), curP);
					heap.insert(over);
				}
			}
			visited.add(cur.endVertex);
		}
		if (heap.isEmpty()) {
			throw new NoPathExistsException();
		}
		return heap.peekMin().getPath();
	}

	private static class Path<V, E> implements Comparable<Path<V, E>> {
		public V endVertex;
		public Double cost;
		public IList<E> path;

		public Path(V endVertex, double cost, IList<E> path) {
			this.endVertex = endVertex;
			this.cost = cost;
			this.path = path;
		}

		@Override
		public int compareTo(Path<V, E> o) {
			return this.cost.compareTo(o.cost);
		}

		public IList<E> getPath() {
			return path;
		}

	}
}
