package com.azamat.websocket;

import javax.websocket.Session;

/**
 * Created by azamat on 11/30/16.
 */
public class Player {
    private String nick;
    //выбор игрока (камень, ножныцы или бумага)
    private String choice;
    //счет за несколько игр
    private Integer score = 0;

    private Session session;

    public Player() {
    }

    public Player(Session session) {
        this.session = session;
    }

    public int incrementAndGetScore() {
        return score++;
    }

    public int getScore() {
        return score;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (nick != null ? !nick.equals(player.nick) : player.nick != null) return false;
        return choice != null ? choice.equals(player.choice) : player.choice == null;

    }

    @Override
    public int hashCode() {
        int result = nick != null ? nick.hashCode() : 0;
        result = 31 * result + (choice != null ? choice.hashCode() : 0);
        return result;
    }
}
