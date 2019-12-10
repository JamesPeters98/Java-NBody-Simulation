package com.jamesdpeters.helpers;

import java.util.concurrent.CopyOnWriteArrayList;

public class LimitedCopyOnWriteArrayList<T> extends CopyOnWriteArrayList<T> {

    int limit;

    public LimitedCopyOnWriteArrayList(int limit){
        this.limit = limit;
    }

    @Override
    public boolean add(T o) {
        while(size() >= limit){
            remove(0);
        }
        return super.add(o);
    }
}
