package collection;
import java.util.Iterator;

public class LockFreeList<T extends Comparable<T>> implements Iterable<T>, Iterator<T> {
    private Node<T> head;
    private Node<T> tail;
    private ThreadLocal<Node<T>> iter = new ThreadLocal<>();

    public LockFreeList() {
        head = new Node<>(true, false);
        tail = new Node<>(false, true);
        head.setNext(tail);
    }

    public boolean add(T item) {
        while(true) {
            Window window = find(head, item);
            Node<T> pred = window.pred, curr = window.curr;
            if(curr.compareTo(item) == 0)
                return false;
            else {
                Node<T> node = new Node<>(item);
                node.setNext(curr);
                if(pred.next().compareAndSet(curr, node, false, false));
                    return true;
            }
        }
    }

    public boolean remove(T item) {
        boolean snip;
        while(true) {
            Window window = find(head, item);
            Node<T> pred = window.pred, curr = window.curr;
            if(curr.compareTo(item) != 0)
                return false;
            else {
                Node<T> succ = curr.next().getReference();
                snip = curr.next().compareAndSet(succ, succ, false, true);
                if(!snip)
                    continue;
                pred.next().compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public T pop() {
        boolean snip;
        while(true) {
            T item = head.next().getReference().value();
            Window window = find(head, item);
            Node<T> pred = window.pred, curr = window.curr;
            if(curr.compareTo(item) != 0)
                return null;
            else {
                Node<T> succ = curr.next().getReference();
                snip = curr.next().compareAndSet(succ, succ, false, true);
                if(!snip)
                    continue;
                pred.next().compareAndSet(curr, succ, false, false);
                return item;
            }
        }
    }

    public boolean contains(T item) {
        boolean[] marked = {false};
        Node<T> curr = head;
        while(curr.compareTo(item) < 0) {
            curr = curr.next().getReference();
            curr.next().get(marked);
        }
        return curr.compareTo(item) == 0 && !marked[0];
    }

    @Override
    public Iterator<T> iterator() {
        iter.set(head);
        return this;
    }

    @Override
    public boolean hasNext() {
        return iter.get().next() == null;
    }

    @Override
    public T next() {
        T value = iter.get().value();
        iter.set(iter.get().next().getReference());
        return value;
    }

    public boolean isEmpty() {
        return head.next().getReference().equals(tail);
    }

    public void print() {
        Node<T> node = head;
        while(node != null) {
            System.out.printf("%s, ", node.value());
            node = node.next().getReference();
        }
        System.out.println();
    }

    class Window {
        public Node<T> pred, curr;
        Window(Node<T> myPred, Node<T> myCurr) {
            pred = myPred;
            curr = myCurr;
        }
    }

    public Window find(Node<T> head, T item) {
        Node<T> pred = null, curr = null, succ = null;
        boolean[] marked = {false};
        boolean snip;
        retry: while(true) {
            pred = head;
            curr = pred.next().getReference();
            while(true) {
                succ = curr.next().get(marked);
                while(marked[0]) {
                    snip = pred.next().compareAndSet(curr, succ, false, false);
                    if(!snip) continue retry;
                    curr = succ;
                    succ = curr.next().get(marked);
                }
                if(curr.compareTo(item) >= 0)
                    return new Window(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }
}
