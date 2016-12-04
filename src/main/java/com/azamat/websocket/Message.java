package com.azamat.websocket;

import java.time.LocalTime;

/**
 * Created by azamat on 11/30/16.
 */
public class Message {
    //тип пакета, определяет содержимое объекта (сообщение, результаты, идентификатор)
    private Type type;
    private String id;
    private Boolean connection;
    //выбор игрока (камень, ножныцы или бумага)
    private PlayerChoice choice;
    //результат игры (победа, ничья, поражение)
    private Result result;
    //счет за несколько игр
    private Integer score;
    private String nick;
    private String message;
    private LocalTime sendDate;

    public Message() {

    }

    public Message(Boolean connection, String id) {
        type = Type.CONNECTION;
        this.connection = connection;
        this.id = id;
    }

    public Message(Result result) {
        type = Type.RESULT;
        this.result = result;
    }

    public Message(String nick, String message, LocalTime sendDate) {
        this.nick = nick;
        this.message = message;
        this.sendDate = sendDate;
    }

    //=================================
    //=     Getters & Setters         =
    //=================================

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isConnected() {
        return connection;
    }

    public void setConnection(Boolean connection) {
        this.connection = connection;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public PlayerChoice getChoice() {
        return choice;
    }

    public void setChoice(PlayerChoice choice) {
        this.choice = choice;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalTime getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalTime sendDate) {
        this.sendDate = sendDate;
    }

    //************************************


    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", id='" + id + '\'' +
                ", choice=" + choice +
                ", result=" + result +
                ", score=" + score +
                ", nick='" + nick + '\'' +
                ", message='" + message + '\'' +
                ", sendDate=" + sendDate +
                '}';
    }
}
