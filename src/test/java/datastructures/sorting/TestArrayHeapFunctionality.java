package datastructures.sorting;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import misc.BaseTest;
import misc.exceptions.EmptyContainerException;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import org.junit.Test;

//import java.util.*;
/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout = SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());
    }

    @Test(timeout = SECOND)
    public void testBasicInsert() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 5; i > 0; i--) {
            heap.insert(i);
        }
        assertEquals(5, heap.size());
        assertEquals(1, heap.peekMin());
    }

    @Test(timeout = SECOND)
    public void testPeekEmpty() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.peekMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // Do nothing: this is OK
        }
    }

    @Test(timeout = SECOND)
    public void testInsertNull() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.insert(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Do nothing: this is OK
        }
    }

    @Test(timeout = SECOND)
    public void testRemoveEmpty() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.removeMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // Do nothing: this is OK
        }
    }

    @Test(timeout = SECOND)
    public void testBasicRemove() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 5; i++) {
            heap.insert(i);
        }
        heap.removeMin();
        assertEquals(heap.peekMin(), 1);
    }

    @Test(timeout = SECOND)
    public void testMultipleRemove() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 200; i++) {
            heap.insert(i);
        }
        for (int i = 0; i < 200; i++) {
            heap.removeMin();
        }
        try {
            heap.removeMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // Do nothing: this is OK
        }
    }

    @Test(timeout = SECOND)
    public void testPeek() {
        // large amount of data
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 200; i++) {
            heap.insert(i);
        }
        for (int i = 0; i < 200; i++) {
            heap.removeMin();
        }
        try {
            heap.peekMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // Do nothing: this is OK
        }
    }
}
