package com.azamat.websocket;

import javax.ejb.Local;
import javax.json.Json;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by azamat on 11/30/16.
 */
public final class PlayerHandler {
//    private String id;
//    private String nick;
//    //выбор игрока (камень, ножныцы или бумага)
//    private String choice;
//    //счет за несколько игр
//    private Integer score = 0;
//
//    private Session session;
//    private PlayerHandler opponent;
//
//    public PlayerHandler() {
//    }
//
//    public PlayerHandler(String id, Session session) {
//        this.id = id;
//        this.session = session;
//    }

    //=========================================
    //=               Methods                 =
    //=========================================

    public static void sendIdMessage(Player player) {
        try {
            String cStr = Json.createObjectBuilder()
                    .add("type", Type.ID.toString())
                    .add("id", player.getId()).build().toString();

            if (player.isActive())
                player.getSender().sendText(cStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendConnectionMessage(Player player) {
        try {
            String message = Json.createObjectBuilder()
                    .add("type", Type.CONNECTION.toString())
                    .add("connection", player.isConnected()).build().toString();

            if (player.isActive())
                player.getSender().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendResultMessage(Player player, Result result, PlayerChoice choice) {
        try {
            String message = Json.createObjectBuilder()
                    .add("type", Type.RESULT.toString())
                    .add("result", result.toString())
                    .add("choice", choice.toString())
                    .add("score", player.getScore()).build().toString();

            if (player.isActive())
                player.getSender().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendChatMessage(Player player, String msg) {
        try {
            String message = Json.createObjectBuilder()
                    .add("type", Type.MESSAGE.toString())
                    .add("nick", player.getNick())
//                    .add("sendDate", player.getSendDate.toString())
                    .add("message", msg).build().toString();

            if (player.isActive())
                player.getSender().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void play(Player player) {
        PlayerChoice choice = PlayerChoice.valueOf(player.getChoice());
        PlayerChoice opponentChoice = PlayerChoice.valueOf(player.getOpponent().getChoice());

        if (choice == opponentChoice) {
//            sendResult(roomId, Result.DRAW, Result.DRAW);
            sendResultMessage(player, Result.DRAW, opponentChoice);
            sendResultMessage(player.getOpponent(), Result.DRAW, choice);
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
    }

//    public Player decodeMessage() {
//
//    }
}
