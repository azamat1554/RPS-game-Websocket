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

//// TODO: Решить где будет считаться score, в любом случае нужно сохранять в базу данных

//Создается новый экземпляр класса при каждом соединении
@ApplicationScoped
@ServerEndpoint(
        value = "/game/{room}"
//        encoders = MessageEncoder.class,
//        decoders = MessageDecoder.class
)
public class ServerEndPoint {
    //хранит игроков, которые ожидают подключения соперника
    private static final Map<String, Player> idlePlayers = new ConcurrentHashMap<>();

    private String roomId;
    private Player player;

    private static final Logger logger = Logger.getLogger(ServerEndPoint.class.getName());

    @OnOpen
    public void onOpen(Session session, @PathParam("room") String roomId) {
        logger.log(Level.INFO, "поиск @OnOpen" + roomId);

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
            idlePlayers.put(roomId, player.getOpponent());
            //удаляет ссылку на себя у оппонента
            player.getOpponent().setOpponent(null);
        } else {
            idlePlayers.remove(roomId);
        }

        PlayerHandler.sendConnectionMessage(player.getOpponent());
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
                    PlayerHandler.sendChatMessage(player.getOpponent(), message);
                    //PlayerHandler.sendChatMessage(player.getOpponent(), jsonMessage.getString("message"));
                    break;
                case RESULT:
                    player.setChoice(jsonMessage.getString("choice"));

                    //// TODO: 12/28/16 Возможно, первая проверка лишняя
                    if (player.getChoice() != null & player.getOpponent().getChoice() != null)
                        play();
            }
        }
    }

    private void play() {
        PlayerChoice choice = PlayerChoice.valueOf(player.getChoice());
        PlayerChoice opponentChoice = PlayerChoice.valueOf(player.getOpponent().getChoice());

        if (choice == opponentChoice) {
            PlayerHandler.sendResultMessage(player, Result.DRAW, opponentChoice);
            PlayerHandler.sendResultMessage(player.getOpponent(), Result.DRAW, choice);

            //// TODO: 12/6/16 Можно вынести в отдельный метод
        } else if ((choice == PlayerChoice.ROCK & opponentChoice == PlayerChoice.SCISSORS) ||
                (choice == PlayerChoice.PAPER & opponentChoice == PlayerChoice.ROCK) ||
                (choice == PlayerChoice.SCISSORS & opponentChoice == PlayerChoice.PAPER)) {
            player.incrementScore();
            PlayerHandler.sendResultMessage(player, Result.WIN, opponentChoice);
            PlayerHandler.sendResultMessage(player.getOpponent(), Result.LOSE, choice);
        } else {
            player.getOpponent().incrementScore();
            PlayerHandler.sendResultMessage(player, Result.LOSE, opponentChoice);
            PlayerHandler.sendResultMessage(player.getOpponent(), Result.WIN, choice);
        }

        player.setChoice(null);
        player.getOpponent().setChoice(null);
    }
}
