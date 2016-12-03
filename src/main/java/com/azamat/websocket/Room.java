package com.azamat.websocket;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

public class Room {
//    private final String roomId;
    private final List<Session> players = new ArrayList<>();
//    private final Map<String, List<Session>> rooms = new ConcurrentHashMap<>();


//    public Room(String uuid) {
//        this.uuid = uuid;
//    }

    public Room(String roomId, Session session) {
        addPlayer(roomId, session);
    }

//    public boolean notExist(String uuid) {
//        return !rooms.containsKey(uuid);
//    }

//    public String addRoom() {
//        String uuid = UUID.randomUUID().toString();
//        rooms.put(uuid, new ArrayList<>());
//        return uuid;
//    }

    //если пользователь не добавился тогда, вернуть false
    public boolean addPlayer(String roomId, Session session) {
        if (players.size() == 2)
            return false;

//        session.getUserProperties().put("playerId", players.size() + 1);
        session.getUserProperties().put("roomId", roomId);
        session.getUserProperties().put("score", 0);
        players.add(session);
//        rooms.get(uuid).add(session);

        return true;
    }

    public Session getAnotherPlayer(Session session) {
        if (session.equals(players.get(0)))
            return players.get(1);
        else
            return players.get(0);
    }

    public List<Session> getPlayers() {
        return players;
    }

    public Session Player_1() {
        return players.get(0);
    }

    public Session Player_2() {
        return players.get(1);
    }

//    private Session findWinner() {
//        PlayerChoice player1, player2;
//        player1 = PlayerChoice.valueOf((String) players.get(0).getUserProperties().get("choice"));
//        player2 = PlayerChoice.valueOf((String) players.get(1).getUserProperties().get("choice"));
//
//        if (player1 == player2)
//            return Result.DRAW;
//    }

    public int size() {
        return players.size();
    }

    public void remove(Session session) {
        players.remove(session);
    }
}
