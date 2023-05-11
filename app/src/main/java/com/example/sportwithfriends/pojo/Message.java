package com.example.sportwithfriends.pojo;

public class Message implements Comparable<Message> {
    private String messageRecipient;
    private String idAuthor;
    private String idRecipient;
    private String messageText;
    private long messageTime;

    public Message(String messageRecipient, String idAuthor, String idRecipient, String messageText, long messageTime) {
        this.messageRecipient = messageRecipient;
        this.idAuthor = idAuthor;
        this.idRecipient = idRecipient;
        this.messageText = messageText;
        this.messageTime = messageTime;
    }

    public Message() {
    }

    public String getMessageRecipient() {
        return messageRecipient;
    }

    public void setMessageRecipient(String messageRecipient) {
        this.messageRecipient = messageRecipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(String idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getIdRecipient() {
        return idRecipient;
    }

    public void setIdRecipient(String idRecipient) {
        this.idRecipient = idRecipient;
    }


    @Override
    public int compareTo(Message o) {
        if (messageTime > o.messageTime)
            return 1;
        else if (messageTime < o.messageTime)
            return -1;
        return 0;
    }
}
