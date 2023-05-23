package org.fpm.di.example;

import org.fpm.di.Binder;
import java.util.ArrayList;
import java.util.HashMap;

public class DummyBinder implements Binder {
    public ArrayList<Class<?>> classes= new ArrayList<>();
    public HashMap<Class<?>, Object> instances = new HashMap<>();
    public HashMap<Class<?>, Class<?>> implementations = new HashMap<>();


    @Override
    public <T> void bind(Class<T> clazz) {
        classes.add(clazz);
    }

    @Override
    public <T> void bind(Class<T> clazz, Class<? extends T> implementation) {
        implementations.put(clazz, implementation);
    }

    @Override
    public <T> void bind(Class<T> clazz, T instance) {
        instances.put(clazz, instance);
    }
}

