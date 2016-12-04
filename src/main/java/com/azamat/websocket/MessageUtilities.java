//package com.azamat.websocket;
//
//import javax.json.Json;
//import javax.json.JsonObject;
//import javax.json.JsonReader;
//import java.io.StringReader;
//
///**
// * Created by azamat on 11/28/16.
// */
//
//public class MessageUtilities {
//
//    public static URI uriFromJson(String message) {
//        try (JsonReader reader = Json.createReader(new StringReader(message))) {
//            JsonObject jsonMessage = reader.readObject();
//
//            URI uri = new URI();
//            uri.setUri(jsonMessage.getString("senderId"));
//
//            return uri;
//        }
//    }
//
//    public static String uriToJson(URI uri) {
//        return Json.createObjectBuilder()
//                .add("type", "uri")
//                .add("uri", uri.getUri()).build().toString();
//    }
//
//
//    public static PlayerHandler gameFromJson(String message) {
//        try (JsonReader reader = Json.createReader(new StringReader(message))) {
//            JsonObject jsonMessage = reader.readObject();
//
//            PlayerHandler playerHandler = new PlayerHandler();
//            playerHandler.setNick(jsonMessage.getString("nick"));
//            playerHandler.setChoice(jsonMessage.getString("step"));
//
//            return playerHandler;
//        }
//    }
//
//    public static String gameToJson(PlayerHandler playerHandler) {
//        return Json.createObjectBuilder()
//                .add("type", "playerHandler")
//                .add("nick", playerHandler.getNick())
//                .add("step", playerHandler.getChoice()).build().toString();
//    }
//}
