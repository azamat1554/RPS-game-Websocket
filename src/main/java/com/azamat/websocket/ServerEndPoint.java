package com.azamat.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.StringReader;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

//Создается новый экземпляр класса при каждом соединении
@ApplicationScoped
@ServerEndpoint(value = "/game/{room}")
public class ServerEndPoint {
    //хранит игроков, которые ожидают подключения соперника
    private static final Map<String, Player> idlePlayers = new ConcurrentHashMap<>();

    private String roomId;
    private Player player;

    private static final Logger logger = Logger.getLogger(ServerEndPoint.class.getName());

    @OnOpen
    public void onOpen(Session session, @PathParam("room") String roomId) {
        logger.log(Level.INFO, "поиск @OnOpen" + roomId);
        session.setMaxIdleTimeout(300_000);

        try {
            //если только зашел на сайт и нет id, или если зашел под неизвестным uuid,
            // тогда сгенерировать новый uuid и создать новую комнату
            if (!idlePlayers.containsKey(roomId) || idlePlayers.get(roomId).isConnected()) {
                this.roomId = roomId = Long.toHexString(UUID.randomUUID().getMostSignificantBits());
                player = new Player(roomId, session);
                idlePlayers.put(player.getId(), player);

                PlayerHandler.sendIdMessage(player);
            } else {
                this.roomId = roomId;
                player = new Player(roomId, session);
                player.setOpponent(idlePlayers.get(roomId));
                idlePlayers.get(roomId).setOpponent(player);

                //отвечать нужно обоим пользователям, чтобы они знали, что соединение установленно
                PlayerHandler.sendConnectionMessage(player, null);
                if (player.isConnected())
                    PlayerHandler.sendConnectionMessage(player.getOpponent(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void close() {
        logger.log(Level.SEVERE, "@OnClose" + roomId);

        if (player.isConnected()) {
            idlePlayers.put(roomId, player.getOpponent());
            //удаляет ссылку на себя у оппонента
            player.getOpponent().setOpponent(null);
            PlayerHandler.sendConnectionMessage(player.getOpponent(), "Your opponent disconnected.");
        } else {
            idlePlayers.remove(roomId);
            //PlayerHandler.sendConnectionMessage(player, "You were inactive for too long.");
        }

    }

    @OnError
    public void onError(Throwable error) {
        logger.log(Level.SEVERE, "@OnError", error);
    }

    @OnMessage
    public void onMessage(String message) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            switch (Type.valueOf(jsonMessage.getString("type"))) {
                case MESSAGE:
                    PlayerHandler.sendChatMessage(player.getOpponent(), message);
                    //PlayerHandler.sendChatMessage(player.getOpponent(), jsonMessage.getString("message"));
                    break;
                case RESULT:
                    player.setChoice(PlayerChoice.valueOf(jsonMessage.getString("choice")));

                    if (player.getChoice() != null & player.getOpponent().getChoice() != null)
                        play();
            }
        }
    }

    private void play() {
        if (player.getChoice() == player.getOpponent().getChoice()) {
            PlayerHandler.sendResultMessage(player, Result.DRAW, player.getChoice());
            PlayerHandler.sendResultMessage(player.getOpponent(), Result.DRAW, player.getChoice());
        } else {
            Player winner = getWinner(player, player.getOpponent());

            PlayerHandler.sendResultMessage(winner, Result.WIN, winner.getOpponent().getChoice());
            PlayerHandler.sendResultMessage(winner.getOpponent(), Result.LOSE, winner.getChoice());
        }

        player.setChoice(null);
        player.getOpponent().setChoice(null);
    }

    private Player getWinner(Player player1, Player player2) {
        PlayerChoice choiceP1 = player1.getChoice();
        PlayerChoice choiceP2 = player2.getChoice();

        if ((choiceP1 == PlayerChoice.ROCK & choiceP2 == PlayerChoice.SCISSORS) ||
                (choiceP1 == PlayerChoice.PAPER & choiceP2 == PlayerChoice.ROCK) ||
                (choiceP1 == PlayerChoice.SCISSORS & choiceP2 == PlayerChoice.PAPER)) {
            return player1;
        } else {
            return player2;
        }
    }
}
