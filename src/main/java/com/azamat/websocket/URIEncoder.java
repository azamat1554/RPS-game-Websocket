package com.azamat.websocket;

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by azamat on 11/30/16.
 */
public class URIEncoder implements Encoder.Text<Message>{
    @Override
    public String encode(Message object) throws EncodeException {
        return Json.createObjectBuilder()
                .add("senderId", object.getNick())
                .add("sendDate", object.getSendDate().toString())
                .add("message", object.getMessage()).build().toString();
    }

    @Override
    public void init(EndpointConfig config) {}

    @Override
    public void destroy() {}
}
