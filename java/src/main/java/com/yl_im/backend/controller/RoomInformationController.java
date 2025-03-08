package com.yl_im.backend.controller;

import com.yl_im.backend.pojo.result.Result;
import com.yl_im.backend.websocket.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/RoomInformation")
public class RoomInformationController {

    @Autowired
    private ChatRoom chatRoom;

    @GetMapping("/getOnlineCount")
    public Result<Integer> getOnlineCount(String roomName) {
        return Result.success(chatRoom.getOnlineCount(roomName));
    }

    @GetMapping("/getChatHistory")
    public Result<String> getChatHistory(String roomName) {
        return Result.success(chatRoom.getChatHistory(roomName));
    }
}
