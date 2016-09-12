package edu.uic.cloud_computing;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.*;

/**
 * Created by gabe on 9/11/16.
 */
public class SynchronizedListQueue<T> implements Iterator{
    private List<T> queue;
    private int index = 0;

    public SynchronizedListQueue() {
        queue = new ArrayList<>();
    }



    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        if (index < queue.size() )
            return true;
        else
            return false;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public Object next() {
        Object object = queue.get(index);
        index++;
        return object;
    }

    @Override
    public void remove() {
        queue.remove(index);
    }

    public synchronized T getNext() throws InterruptedException{
        while(queue.isEmpty()) {
           wait();
        }

        T object = queue.get(0);
        queue.remove(0);
        return object;

    }

    public synchronized void addAll( List<T> t) {
        queue.addAll(t);
        notifyAll();
    }

    public synchronized void add( T t)  {
        queue.add(t);
        notifyAll();
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized void DisplayContents() throws IOException{
        synchronized (queue) {
            System.out.println("Size: " + size());
            for( T t: queue){
                System.out.println(  ((Message)t).toPrettyString()   );
            }
        }
    }
}

