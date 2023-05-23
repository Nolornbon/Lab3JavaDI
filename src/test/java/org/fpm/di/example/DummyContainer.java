package org.fpm.di.example;

import org.fpm.di.Container;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DummyContainer implements Container {
    private final DummyBinder binder;

    public DummyContainer(DummyBinder binder) {
        this.binder = binder;
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        try {
            if (binder.instances.containsKey(clazz)) {
                return clazz.cast(binder.instances.get(clazz));
            }
            if (binder.implementations.containsKey(clazz)) {
                Class<?> implementationClass = binder.implementations.get(clazz);
                return clazz.cast(getComponent(implementationClass));
            }
            T instance = createInstance(clazz);
            if (clazz.isAnnotationPresent(Singleton.class)) {
                binder.instances.put(clazz, instance);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T createInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // Перевіряємо наявність конструктора, поміченого анотацією @Inject
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?> injectConstructor = findInjectConstructor(constructors);

        if (clazz.isAnnotationPresent(Singleton.class)) {
            // Повертаємо перший екземпляр для класів з анотацією @Singleton
            if (binder.instances.containsKey(clazz)) {
                return clazz.cast(binder.instances.get(clazz));
            }
        }

        if (injectConstructor != null) {
            // Отримуємо параметри конструктора
            Class<?>[] parameterTypes = injectConstructor.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                parameters[i] = getComponent(parameterTypes[i]);
            }

            Constructor<T> castConstructor = clazz.getDeclaredConstructor(parameterTypes);
            T instance = castConstructor.newInstance(parameters);

            if (clazz.isAnnotationPresent(Singleton.class)) {
                // Зберігаємо перший екземпляр для класів з анотацією @Singleton
                binder.instances.put(clazz, instance);
            }
            return instance;
        } else {
            // Якщо немає конструктора з анотацією @Inject, використовуємо стандартний конструктор
            return clazz.getDeclaredConstructor().newInstance();
        }
    }

    private Constructor<?> findInjectConstructor(Constructor<?>[] constructors) {
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return constructor;
            }
        }
        return null;
    }
}

