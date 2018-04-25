package com.example.gron.hunternet;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.Date;

public class Message implements IMessage {
    String id;
    String text;
    Author author;
    Date createdAt;
    Message(String id, String text, Author author, Date createdAt) {
        this.id=id;
        this.text=text;
        this.author=author;
        this.createdAt=createdAt;
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public String getText() {
        return text;
    }
    @Override
    public Author getUser() {
        return author;
    }
    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}
