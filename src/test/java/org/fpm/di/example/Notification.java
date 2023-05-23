package org.fpm.di.example;

import javax.inject.Inject;

public class Notification {

    private final Message Messaging;
    @Inject
    public Notification(Message Message) {
        this.Messaging = Message;
    }

    public Message getMessaging() {return Messaging;}
}