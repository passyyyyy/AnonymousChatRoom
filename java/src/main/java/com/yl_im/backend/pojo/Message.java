package com.yl_im.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String roomName;
    private String userName;
    private String customName;
    private String time;
    private String message;
    private String type;
}
