import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Node<T extends Comparable<T>> implements Lock {
    private Node<T> next = null;
    private T value = null;
    private Semaphore lock = new Semaphore(1, true);

    public Node(T value) {
        this.value = value;
    }

    public Node() {

    }

    public Node<T> next() {
        return this.next;
    }

    public void setNext(Node<T> ln) {
        this.next = ln;
    }

    public T value() {
        return this.value;
    }

    @Override
    public void lock() {
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.acquire();   
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() {
        lock.release();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    public void setValue(T item) {
        this.value = item;
    }
} 
