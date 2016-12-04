package com.azamat.websocket;

import javax.json.Json;

/**
 * Класс реализующий методы отправки JSON сообщений.
 */
public final class PlayerHandler {
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
}
