package com.azamat.websocket;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by azamat on 11/28/16.
 */
//// TODO: 12/3/16 Сделать roomId глобальной переменной, в классе Room внедрить Player
//чтобы избавиться от session.

//Создается новый экземпляр класса при каждом соединении
@ServerEndpoint(
        value = "/game/{room}",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class
)
public class ServerEndPoint {
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    private static final Logger logger = Logger.getLogger(ServerEndPoint.class.getName());

    @OnOpen
    public void onOpen(Session session, @PathParam("room") String roomId) {
        logger.log(Level.INFO, "поиск @OnOpen" + roomId);

        //если только зашел на сайт и нет uuid, или если зашел под неизвестным uuid,
        // тогда сгенерировать новый uuid и создать новую комнату
        if (!rooms.containsKey(roomId) || (rooms.get(roomId).size() == 2)) {
            //// TODO: 12/3/16 возможно имеет смысл сделать roomId глобальной переменной
            roomId = Long.toHexString(UUID.randomUUID().getMostSignificantBits());
            rooms.put(roomId, new Room(roomId, session));

            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendObject(new Message(roomId));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (rooms.get(roomId).size() < 2) {

            rooms.get(roomId).addPlayer(roomId, session);
        }
    }

    @OnClose
    public void close(Session session) {
        logger.log(Level.SEVERE, "@OnClose");

        String roomId = (String) session.getUserProperties().get("roomId");

        if (rooms.get(roomId).size() == 1)
            rooms.remove(roomId);
        else if (rooms.get(roomId).size() == 2)
            rooms.get(roomId).remove(session);
    }

    @OnError
    public void onError(Throwable error) {
        logger.log(Level.SEVERE, "@OnError", error);
    }

    @OnMessage
    public void onMessage(Message message, Session session) {
//        logger.log(Level.SEVERE, "@OnMessage");
//        logger.log(Level.SEVERE, message.toString());

        String roomId = (String) session.getUserProperties().get("roomId");
        if (message.getType() == Type.MESSAGE) {
            sendMessage(session, message);
        } else {
            session.getUserProperties().put("choice", message.getChoice());

            if (rooms.get(roomId).Player_1().getUserProperties().get("choice") != null
                    & rooms.get(roomId).Player_2().getUserProperties().get("choice") != null) {

                play(roomId);
            }
        }

    }

    private void play(String roomId) {
        PlayerChoice player1, player2;

        player1 = (PlayerChoice) rooms.get(roomId).Player_1().getUserProperties().get("choice");
        player2 = (PlayerChoice) rooms.get(roomId).Player_2().getUserProperties().get("choice");

        if (player1 == player2) {
            sendResult(roomId, Result.DRAW, Result.DRAW);
        } else if ((player1 == PlayerChoice.ROCK & player2 == PlayerChoice.SCISSORS) ||
                (player1 == PlayerChoice.PAPER & player2 == PlayerChoice.ROCK) ||
                (player1 == PlayerChoice.SCISSORS & player2 == PlayerChoice.PAPER)) {
            sendResult(roomId, Result.WIN, Result.LOSE);
        } else {

            sendResult(roomId, Result.LOSE, Result.WIN);
        }

        rooms.get(roomId).Player_1().getUserProperties().put("choice", null);
        rooms.get(roomId).Player_2().getUserProperties().put("choice", null);
    }

    private void sendResult(String roomId, Result player1, Result player2) {
        try {
            rooms.get(roomId).Player_1().getBasicRemote().sendObject(createMessage(rooms.get(roomId).Player_1(), player1));
            rooms.get(roomId).Player_2().getBasicRemote().sendObject(createMessage(rooms.get(roomId).Player_2(), player2));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(Session player, Result result) {
        //// TODO: 12/3/16 Впринципе, можно сохранить ссылку на оппонента в Session (Player)
        String roomId = (String) player.getUserProperties().get("roomId");
        Session anotherPlayer = rooms.get(roomId).getAnotherPlayer(player);

        Message message = new Message(result);
        message.setChoice((PlayerChoice) anotherPlayer.getUserProperties().get("choice"));

        if (result == Result.WIN) {
            int score = (int) player.getUserProperties().get("score");
            player.getUserProperties().put("score", ++score);
        }
        message.setScore((int) player.getUserProperties().get("score"));

        logger.log(Level.INFO, "сообщение " + message);
        return message;
    }

    private void sendMessage(Session sender, Message message) {
        String roomId = (String) sender.getUserProperties().get("roomId");
        logger.log(Level.INFO, "сообщение " + message);
        try {
            rooms.get(roomId).getAnotherPlayer(sender).getBasicRemote().sendObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
