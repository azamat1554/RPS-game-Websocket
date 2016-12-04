package com.azamat.websocket;

import com.azamat.websocket.encoders.MessageEncoder;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by azamat on 11/28/16.
 */
//// TODO: 12/3/16 Сделать roomId глобальной переменной, в классе Room внедрить PlayerHandler
//чтобы избавиться от session.

//Создается новый экземпляр класса при каждом соединении
@ApplicationScoped
@ServerEndpoint(
        value = "/game/{room}"
//        encoders = MessageEncoder.class,
//        decoders = MessageDecoder.class
)
public class ServerEndPoint {
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    //хранит игроков, которые ожидают подключения соперника
    private static final Map<String, Player> players = new ConcurrentHashMap<>();

    private String roomId;
    private Player player;

    private static final Logger logger = Logger.getLogger(ServerEndPoint.class.getName());

    @OnOpen
    public void onOpen(Session session, @PathParam("room") String roomId) {
        logger.log(Level.INFO, "поиск @OnOpen" + roomId);

//        this.session = session;
//        try {
//            //если только зашел на сайт и нет uuid, или если зашел под неизвестным uuid,
//            // тогда сгенерировать новый uuid и создать новую комнату
//            if (!rooms.containsKey(roomId) || (rooms.get(roomId).size() == 2)) {
//                roomId = Long.toHexString(UUID.randomUUID().getMostSignificantBits());
//                this.roomId = roomId;
//                rooms.put(roomId, new Room(roomId, session));
//
//                //отвечать нужно обоим пользователям, чтобы они знали, что соединение установленно
//                if (session.isOpen())
//                    session.getBasicRemote().sendObject(new Message(false, roomId));
//            } else if (rooms.get(roomId).size() < 2) {
//                this.roomId = roomId;
//                rooms.get(roomId).addPlayer(roomId, session);
//
//                if (session.isOpen())
//                    session.getBasicRemote().sendObject(new Message(true, roomId));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            //если только зашел на сайт и нет id, или если зашел под неизвестным uuid,
            // тогда сгенерировать новый uuid и создать новую комнату
            if (!players.containsKey(roomId) || players.get(roomId).isConnected()) {
                this.roomId = roomId = Long.toHexString(UUID.randomUUID().getMostSignificantBits());
                player = new Player(roomId, session);
                players.put(player.getId(), player);

                //отвечать нужно обоим пользователям, чтобы они знали, что соединение установленно
                PlayerHandler.sendIdMessage(player);
            } else {
                this.roomId = roomId;
                player = new Player(roomId, session);
                player.setOpponent(players.get(roomId));
                players.get(roomId).setOpponent(player);

                PlayerHandler.sendConnectionMessage(player);
                if (player.isConnected())
                    PlayerHandler.sendConnectionMessage(player.getOpponent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void close() {
        logger.log(Level.SEVERE, "@OnClose" + roomId);

        if (player.isConnected()) {
            players.put(roomId, player.getOpponent());
            //удаляет ссылку на себя у оппонента
            player.getOpponent().setOpponent(null);
        } else {
            players.remove(roomId);
        }

        PlayerHandler.sendConnectionMessage(player.getOpponent());

//        if (rooms.get(roomId).size() == 1)
//            rooms.remove(roomId);
//        else if (rooms.get(roomId).size() == 2)
//            rooms.get(roomId).remove(session);
    }

    @OnError
    public void onError(Throwable error) {
        logger.log(Level.SEVERE, "@OnError", error);
    }

    @OnMessage
    public void onMessage(String message) {
        logger.log(Level.SEVERE, "@OnMessage" + roomId);
//        logger.log(Level.SEVERE, message.toString());

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            switch (Type.valueOf(jsonMessage.getString("type"))) {
                case MESSAGE:
//                    msg.setType(Type.MESSAGE);
//                    msg.setNick(jsonMessage.getString("nick"));
//                    msg.setSendDate(LocalTime.now());
//                    msg.setMessage(jsonMessage.getString("message"));
                    PlayerHandler.sendChatMessage(player.getOpponent(), jsonMessage.getString("message"));
                    break;
                case RESULT:
//                    msg.setType(Type.RESULT);
//                    msg.setChoice(PlayerChoice.valueOf(jsonMessage.getString("choice")));
                    player.setChoice(jsonMessage.getString("choice"));

                    if (player.getChoice() != null & player.getOpponent().getChoice() != null)
                        play();
//                        PlayerHandler.play(player);
//                    PlayerHandler.sendResultMessage(player, jsonMessage.getString("result"), jsonMessage.getString("choice"));
//                    PlayerHandler.sendResultMessage(player, jsonMessage.getString("result"), jsonMessage.getString("choice"));
            }
        }

//        if (message.getType() == Type.MESSAGE) {
//            PlayerHandler.sendChatMessage();
//            sendMessage(session, message);
//        } else {
//            session.getUserProperties().put("choice", message.getChoice());
//
//            if (rooms.get(roomId).Player_1().getUserProperties().get("choice") != null
//                    & rooms.get(roomId).Player_2().getUserProperties().get("choice") != null) {
//
//                play(roomId);
//            }
//        }
    }

    private void play() {
        PlayerChoice choice = PlayerChoice.valueOf(player.getChoice());
        PlayerChoice opponentChoice = PlayerChoice.valueOf(player.getOpponent().getChoice());

        if (choice == opponentChoice) {
//            sendResult(roomId, Result.DRAW, Result.DRAW);
            PlayerHandler.sendResultMessage(player, Result.DRAW, opponentChoice);
            PlayerHandler.sendResultMessage(player.getOpponent(), Result.DRAW, choice);
        } else if ((choice == PlayerChoice.ROCK & opponentChoice == PlayerChoice.SCISSORS) ||
                (choice == PlayerChoice.PAPER & opponentChoice == PlayerChoice.ROCK) ||
                (choice == PlayerChoice.SCISSORS & opponentChoice == PlayerChoice.PAPER)) {
//            sendResult(roomId, Result.WIN, Result.LOSE);
            PlayerHandler.sendResultMessage(player, Result.WIN, opponentChoice);
            PlayerHandler.sendResultMessage(player.getOpponent(), Result.LOSE, choice);
        } else {
            PlayerHandler.sendResultMessage(player, Result.LOSE, opponentChoice);
            PlayerHandler.sendResultMessage(player.getOpponent(), Result.WIN, choice);
//            sendResult(roomId, Result.LOSE, Result.WIN);
        }

//        rooms.get(roomId).Player_1().getUserProperties().put("choice", null);
//        rooms.get(roomId).Player_2().getUserProperties().put("choice", null);

        player.setChoice(null);
        player.getOpponent().setChoice(null);
    }
}
