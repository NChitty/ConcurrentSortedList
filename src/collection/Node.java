package collection;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node<T extends Comparable<T>> {
    private T value = null;
    boolean head = false;
    boolean tail = false;
    private AtomicMarkableReference<Node<T>> next = new AtomicMarkableReference<>(null, false);
    public Node(T value) {
        this.value = value;
    }

    public Node(boolean head, boolean tail) {
        this.head = head;
        this.tail = tail;
    }

    public AtomicMarkableReference<Node<T>> next() {
        return this.next;
    }

    public void setNext(Node<T> ln) {
        this.next.set(ln, false);
    }

    public T value() {
        return this.value;
    }

    public void setValue(T item) {
        this.value = item;
    }

    public int compareTo(T item) {
        return head ? -1 : tail ? 1 : this.value.compareTo(item);
    }
} 
