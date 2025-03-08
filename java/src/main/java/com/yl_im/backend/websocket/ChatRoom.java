package com.yl_im.backend.websocket;

import com.alibaba.fastjson2.JSON;
import com.yl_im.backend.constant.MessageTypeConstant;
import com.yl_im.backend.pojo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint("/ChatRoom/{userName}/{roomName}")
public class ChatRoom {
    private static final CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();
    private static final ConcurrentHashMap<String, CopyOnWriteArraySet<Session>> SESSION_POOL = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<Message>> MESSAGE_POOL = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userName") String userName, @PathParam(value = "roomName") String roomName) {
        try {
            session.getUserProperties().put("user", userName);
            session.getUserProperties().put("room", roomName);
            SESSIONS.add(session);
            SESSION_POOL.computeIfAbsent(roomName, k -> new CopyOnWriteArraySet<>()).add(session);
            log.info("id: {} joined room: {}, number of people in the room: {}, total number of connections: {}", userName, roomName, getOnlineCount(roomName), SESSIONS.size());
            sendAllMessage(roomName,
                    JSON.toJSONString(
                            new Message(roomName, userName, null, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null, MessageTypeConstant.MESSAGE_TYPE_USER_JOIN)
                    )
            );
        } catch (Exception e) {
            log.error("Error occurred when opening WebSocket connection", e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            String roomName = (String) session.getUserProperties().get("room");
            String userName = (String) session.getUserProperties().get("user");
            if (userName != null) {
                SESSION_POOL.computeIfPresent(roomName, (k, v) -> {
                    v.remove(session);
                    return v.isEmpty() ? null : v;
                });
            }
            SESSIONS.remove(session);
            sendAllMessage(roomName,
                    JSON.toJSONString(
                            new Message(roomName, userName, null, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null, MessageTypeConstant.MESSAGE_TYPE_USER_QUIT)
                    )
            );
            log.info("Connection with id: {} closed, total number of connections: {}", userName, SESSIONS.size());
        } catch (Exception e) {
            log.error("Error occurred when closing WebSocket connection", e);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {

        Message mes = JSON.parseObject(message, Message.class);
        String roomName = mes.getRoomName();
        String userName = mes.getUserName();

        if (userName != null) {
            sendAllMessage(roomName, message);
            CopyOnWriteArrayList<Message> messages = MESSAGE_POOL.computeIfAbsent(roomName, k -> new CopyOnWriteArrayList<>());
            synchronized (messages) {
                if (messages.size() >= 10) {
                    messages.remove(messages.iterator().next()); // Remove the earliest message
                }
                messages.add(mes);
            }

            log.info("room:{}, id: {}, message: {}", roomName, userName, message);
        } else {
            log.info("[WebSocket message] Received message from unknown user: {}", message);
        }
    }

    /**
     * This method broadcasts a message to all sessions in the same room.
     *
     * @param roomName Room name
     * @param message  Message content
     */
    public void sendAllMessage(String roomName, String message) {
        CopyOnWriteArraySet<Session> sessions = SESSION_POOL.get(roomName);
        for (Session session : sessions) {
            try {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                log.error("Error occurred while broadcasting message", e);
            }
        }
    }

    /**
     * Send a message to a specific session by user name.
     *
     * @param userName User name
     * @param message  Message content
     */
    public void sendMessageTo(String userName, String message) {
        try {
            for (CopyOnWriteArraySet<Session> sessions : SESSION_POOL.values()) {
                for (Session session : sessions) {
                    if (userName.equals(session.getUserProperties().get("user"))) {
                        if (session.isOpen()) {
                            session.getAsyncRemote().sendText(message);
                            return; // Exit the method after sending the message
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while sending message", e);
        }
    }

    /**
     * Get the number of online users in the room.
     *
     * @param roomName Room name
     * @return Number of online users
     */
    public Integer getOnlineCount(String roomName) {
        Set<Session> sessions = SESSION_POOL.get(roomName);
        if (sessions != null) {
            return sessions.size();
        } else {
            return 0;
        }
    }

    /**
     * Get chat history for a specific room.
     *
     * @param roomName Room name
     * @return Chat history as JSON string
     */
    public String getChatHistory(String roomName) {
        CopyOnWriteArrayList<Message> messages = MESSAGE_POOL.get(roomName);
        if (messages != null) {
            return JSON.toJSONString(messages);
        } else {
            return JSON.toJSONString(new CopyOnWriteArraySet<Message>());
        }
    }
}
