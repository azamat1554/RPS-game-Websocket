package com.azamat.websocket;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by azamat on 11/30/16.
 */
public class Message {
    //тип пакета, определяет содержимое объекта (сообщение, результаты, идентификатор)
    private Type type;
    private String roomId;
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

    public Message(String roomId) {
        type = Type.ID;
        this.roomId = roomId;
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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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
                ", roomId='" + roomId + '\'' +
                ", choice=" + choice +
                ", result=" + result +
                ", score=" + score +
                ", nick='" + nick + '\'' +
                ", message='" + message + '\'' +
                ", sendDate=" + sendDate +
                '}';
    }
}
