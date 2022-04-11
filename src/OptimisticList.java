import java.util.Iterator;
import java.util.concurrent.Semaphore;

public class OptimisticList<T extends Comparable<T>> implements Iterable<T>, Iterator<T> {
    private Node<T> head;
    private ThreadLocal<Node<T>> iter = new ThreadLocal<>();
    private Semaphore sem = new Semaphore(1);
    private boolean first = false;

    public OptimisticList() {}

    public boolean add(T item) {
        if(!first) {
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if(head == null && !first) {
                    head = new Node<>(item);
                    return true;
                }
            } finally {
                sem.release();
                first = true;
            }
        }
        while(true) {
            Node<T> pred = head;
            Node<T> curr = head.next();
            while(curr != null && item.compareTo(curr.value()) > 0) {
                pred = curr;
                curr = pred.next();
            }
            try {
                pred.lockInterruptibly();
                if(curr != null)
                    curr.lockInterruptibly();
                if(this.validate(pred, curr)) {
                    if(curr != null && curr.value().compareTo(item) == 0)
                        return false;
                    else {
                        Node<T> node = new Node<>(item);
                        node.setNext(curr);
                        pred.setNext(node);
                        return true;
                    }
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } finally {
                pred.unlock();
                if(curr != null)
                    curr.unlock();
            }
        }
    }

    private boolean validate(Node<T> pred, Node<T> curr) {
        Node<T> node = head;
        if(pred == null)
            return true;
        while(node.value().compareTo(pred.value()) <= 0) {
            if(node.value().equals(pred.value())) {
                return (pred.next() == null && curr == null) || pred.next().equals(curr);
            }
            node = node.next();
        }
        return false;
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
        iter.set(iter.get().next());
        return value;
    }

    public void print() {
        Node<T> node = head;
        while(node != null) {
            System.out.printf("%s, ", node.value());
            node = node.next();
        }
        System.out.println();
    }
}
