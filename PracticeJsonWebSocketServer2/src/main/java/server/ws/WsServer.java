package server.ws;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@ServerEndpoint("/websocketendpoint")
public class WsServer {
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
    private static ConcurrentHashMap<String, HashSet<Session>> repo = new ConcurrentHashMap<>();
    private static HashSet<Session> toBeNotified = new HashSet<Session>();
    private static final Gson gson = new Gson();

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        synchronized (clients) {
            try {
                JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
                String messageType = jsonMessage.get("type").getAsString();
                String service = jsonMessage.get("service").getAsString();

                // Handle different message types
                switch (messageType) {
                    case "INIT":
                        handleInitMessage(service, session);
                        break;
                    case "MESSAGE":
                        broadcastMessage(jsonMessage, session, service);
                        break;
                    case "CLOSE":
                        handleCloseMessage(service, session);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorMessage(session, "Invalid message format");
            }
        }
    }

    private void handleInitMessage(String service, Session session) {
        HashSet<Session> theList = repo.computeIfAbsent(service, k -> new HashSet<>());
        theList.add(session);
        toBeNotified = theList;
        
        // Notify others in the same service about new connection
        JsonObject notification = new JsonObject();
        notification.addProperty("type", "NOTIFICATION");
        notification.addProperty("message", "New client joined " + service);
        broadcastToService(notification, session, service);
    }

    private void handleCloseMessage(String service, Session session) {
        HashSet<Session> theList = repo.get(service);
        if (theList != null) {
            theList.remove(session);
            if (theList.isEmpty()) {
                repo.remove(service);
            }
        }
    }

    private void broadcastMessage(JsonObject message, Session sender, String service) {
        HashSet<Session> recipients = repo.get(service);
        if (recipients != null) {
            broadcastToService(message, sender, service);
        }
    }

    private void broadcastToService(JsonObject message, Session sender, String service) {
        HashSet<Session> recipients = repo.get(service);
        if (recipients != null) {
            for (Session recipient : recipients) {
                if (!recipient.equals(sender)) {
                    try {
                        recipient.getBasicRemote().sendText(message.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            JsonObject error = new JsonObject();
            error.addProperty("type", "ERROR");
            error.addProperty("message", errorMessage);
            session.getBasicRemote().sendText(error.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        // Remove session from all services
        for (HashSet<Session> sessions : repo.values()) {
            sessions.remove(session);
        }
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}