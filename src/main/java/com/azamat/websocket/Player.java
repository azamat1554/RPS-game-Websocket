package com.azamat.websocket;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

/**
 * Created by azamat on 11/30/16.
 */
public class Player {
    private String id;
    private String nick;
    //выбор игрока (камень, ножныцы или бумага)
    private String choice;
    //счет за несколько игр
    private Integer score = 0;

    private Session session;
    private Player opponent;

    public Player() {
    }

    public Player(String id, Session session) {
        this.id = id;
        nick = session.getId();
        this.session = session;
    }

    //=========================================
    //=               Methods                 =
    //=========================================

    public boolean isConnected() {
        if (opponent != null && opponent.isActive())
            return true;
        return false;
    }

    public boolean isActive() {
        return session.isOpen();
    }

    public int incrementScore() {
        return score++;
    }


    //==========================================
    //=            Getter & Setter             =
    //==========================================


    public String getId() {
        return id;
    }

    public int incrementAndGetScore() {
        return score++;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public RemoteEndpoint.Basic getSender() {
        return session.getBasicRemote();
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

    //=============================================
    //=        equals, hashcode, toString         =
    //=============================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (id != null ? !id.equals(player.id) : player.id != null) return false;
        if (nick != null ? !nick.equals(player.nick) : player.nick != null) return false;
        if (choice != null ? !choice.equals(player.choice) : player.choice != null) return false;
        if (score != null ? !score.equals(player.score) : player.score != null) return false;
        if (session != null ? !session.equals(player.session) : player.session != null) return false;
        return opponent != null ? opponent.equals(player.opponent) : player.opponent == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (nick != null ? nick.hashCode() : 0);
        result = 31 * result + (choice != null ? choice.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (session != null ? session.hashCode() : 0);
        result = 31 * result + (opponent != null ? opponent.hashCode() : 0);
        return result;
    }
}

