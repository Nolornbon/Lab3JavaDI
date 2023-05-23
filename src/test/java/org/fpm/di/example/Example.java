package org.fpm.di.example;

import org.fpm.di.Container;
import org.fpm.di.Environment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Example {


    private Container container;

    @Before
    public void setUp() {
        Environment env = new DummyEnvironment();
        container = env.configure(new MyConfiguration());
    }


    @Test
    public void shouldInjectSingleton() {
        assertSame(container.getComponent(MySingleton.class), container.getComponent(MySingleton.class));
    }

    @Test
    public void shouldInjectPrototype() {
        assertNotSame(container.getComponent(MyPrototype.class), container.getComponent(MyPrototype.class));
    }

    @Test
    public void shouldBuildInjectionGraph() {
        /*
        binder.bind(A.class, B.class);
        binder.bind(B.class, new B());
        */
        final B bAsSingleton = container.getComponent(B.class);
        assertSame(container.getComponent(A.class), bAsSingleton);
        assertSame(container.getComponent(B.class), bAsSingleton);
    }

    @Test
    public void shouldBuildInjectDependencies() {
        final UseA hasADependency = container.getComponent(UseA.class);
        assertSame(hasADependency.getDependency(), container.getComponent(B.class));
    }


    @Test
    public void testBuildGraphInjection(){
        final SMS sms = container.getComponent(SMS.class);
        final Notification notification = container.getComponent(Notification.class);
        final Message message = container.getComponent(Message.class);
        assertNotNull(sms);
        assertNotNull(notification);
        assertNotNull(message);

        assertSame(container.getComponent(SMS.class), sms);
        assertSame(container.getComponent(Message.class), message);
        assertSame(sms, message);
        assertSame(sms, notification.getMessaging());
        assertSame(message, notification.getMessaging());
    }

    @Test
    public void testBuildInjectDependencies(){
        final Notification notification1 = container.getComponent(Notification.class);
        final Notification notification2 = container.getComponent(Notification.class);
        assertSame(notification1.getMessaging(),container.getComponent(SMS.class));
        assertSame(notification1.getMessaging(),container.getComponent(Message.class));
        assertSame(notification1.getMessaging(), notification2.getMessaging());
        assertNotSame(notification1, notification2);
    }


    @Test
    public void testSingletonInjection(){
        final SMS sms1 = container.getComponent(SMS.class);
        final SMS sms2 = container.getComponent(SMS.class);
        final Message message1 = container.getComponent(Message.class);
        final Message message2 = container.getComponent(Message.class);
        assertSame(message1,message2);
        assertSame(sms1, sms2);
    }

    @Test
    public void testSingletonInjection2(){
        final SMS sms = container.getComponent(SMS.class);
        assertSame(container.getComponent(Message.class), sms);
    }

    @Test
    public void shouldCreateNewInstance() {
        final SMS expectedSMS = new SMS();
        final SMS actualSMS = container.getComponent(SMS.class);
        assertNotSame(expectedSMS, actualSMS);
    }

}
