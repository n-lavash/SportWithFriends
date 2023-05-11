package com.example.sportwithfriends.pojo;

import java.util.List;

public class Chat {
    private String messageNameRecipient;
    private String messageAvatarUrlRecipient;
    private List<Message> listOfMessages;

    public Chat(String messageNameRecipient, String messageAvatarUrlRecipient, List<Message> listOfMessages) {
        this.messageNameRecipient = messageNameRecipient;
        this.messageAvatarUrlRecipient = messageAvatarUrlRecipient;
        this.listOfMessages = listOfMessages;
    }

    public Chat() {
    }

    public String getMessageNameRecipient() {
        return messageNameRecipient;
    }

    public void setMessageNameRecipient(String messageNameRecipient) {
        this.messageNameRecipient = messageNameRecipient;
    }

    public String getMessageAvatarUrlRecipient() {
        return messageAvatarUrlRecipient;
    }

    public void setMessageAvatarUrlRecipient(String messageAvatarUrlRecipient) {
        this.messageAvatarUrlRecipient = messageAvatarUrlRecipient;
    }

    public List<Message> getListOfMessages() {
        return listOfMessages;
    }

    public void setListOfMessages(List<Message> listOfMessages) {
        this.listOfMessages = listOfMessages;
    }
}
