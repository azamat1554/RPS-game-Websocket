package com.azamat.websocket;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

/**
 * Created by azamat on 11/28/16.
 */

public class MessageUtilities {

    public static URI uriFromJson(String message) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            URI uri = new URI();
            uri.setUri(jsonMessage.getString("senderId"));

            return uri;
        }
    }

    public static String uriToJson(URI uri) {
        return Json.createObjectBuilder()
                .add("type", "uri")
                .add("uri", uri.getUri()).build().toString();
    }


    public static Player gameFromJson(String message) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            Player player = new Player();
            player.setNick(jsonMessage.getString("nick"));
            player.setChoice(jsonMessage.getString("step"));

            return player;
        }
    }

    public static String gameToJson(Player player) {
        return Json.createObjectBuilder()
                .add("type", "player")
                .add("nick", player.getNick())
                .add("step", player.getChoice()).build().toString();
    }
}
