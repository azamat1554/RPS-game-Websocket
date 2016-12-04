package com.azamat.websocket;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by azamat on 11/28/16.
 */
public class MessageEncoder implements Encoder.Text<Message>{
    @Override
    public String encode(Message message) throws EncodeException {
        JsonObject jsonObject = Json.createObjectBuilder().build();

        switch (message.getType()) {
            case MESSAGE:
                return Json.createObjectBuilder()
                        .add("type", Type.MESSAGE.toString())
                        .add("nick", message.getNick())
                        .add("sendDate", message.getSendDate().toString())
                        .add("message", message.getMessage()).build().toString();

            case RESULT:
                return Json.createObjectBuilder()
                        .add("type", Type.RESULT.toString())
                        .add("result", message.getResult().toString())
                        .add("choice", message.getChoice().toString())
                        .add("score", message.getScore()).build().toString();
            case CONNECTION:
                return Json.createObjectBuilder()
                        .add("type", Type.CONNECTION.toString())
                        .add("connection", message.isConnected())
                        .add("id", message.getId()).build().toString();
        }

        return null;
    }

    @Override
    public void init(EndpointConfig config) {}

    @Override
    public void destroy() {}
}
