package com.azamat.websocket;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by azamat on 11/28/16.
 */
public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(String message) throws DecodeException {
        Logger.getLogger(MessageDecoder.class.getName()).log(Level.SEVERE, "поиск: " + message);

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            Message msg = new Message();
            switch (Type.valueOf(jsonMessage.getString("type"))) {
                case MESSAGE:
                    msg.setType(Type.MESSAGE);
                    msg.setNick(jsonMessage.getString("nick"));
                    msg.setSendDate(LocalTime.now());
                    msg.setMessage(jsonMessage.getString("message"));;
                    break;
                case RESULT:
                    msg.setType(Type.RESULT);
                    msg.setChoice(PlayerChoice.valueOf(jsonMessage.getString("choice")));
            }

            return msg;
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }
}
