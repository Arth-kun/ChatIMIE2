package com.chatimie.arthurcouge.chatimie;

public class Message {
    private String message;
    private String pseudo;
    private String hour;

    public Message(String message, String pseudo, String hour) {
        this.message = message;
        this.pseudo = pseudo;
        this.hour = hour;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
