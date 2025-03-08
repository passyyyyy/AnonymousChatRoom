<template>
    <div class="chat-container">
        <header class="chat-header">
            <h1>💬 Chat Room <span class="online-count">👥 {{ onlineCount }}人在线</span> </h1>
            <div class="room-info">
                <span class="user-tag">👤 {{ customName ? customName : userName }}<i class="el-icon-info"
                        style="margin-left: 10px;" @click="handleChangPrice()"></i></span>
                <span class="room-tag">#️⃣ {{ roomName }}</span>
            </div>
        </header>

        <div class="message-container">
            <ul class="message-list">
                <li v-for="(message, index) in messages" :key="index"
                    :class="[message.isMe ? 'my-message' : message.isSystem ? 'system-message' : 'other-message']">
                    <div class="message-header" v-if="!message.isMe && !message.isSystem">
                        {{ message.customName ? message.customName : message.userName }}
                    </div>
                    <div class="message-content">
                        <span class="message-text">{{ message.content }}</span>
                        <span class="message-time" v-if="!message.isSystem">{{ message.timestamp }}</span>
                    </div>
                </li>
            </ul>
        </div>

        <div class="input-area">
            <input v-model="newMessage" placeholder="输入消息..." @keyup.enter="sendMessage" class="message-input" />
            <button @click="sendMessage" class="send-button">
                <span class="send-icon">✈️</span>
            </button>
        </div>
    </div>
</template>

<script>
import WebSocketService from '@/utils/websocket';
import axios from '@/utils/myAxios';

export default {
    data() {
        return {
            ws: null,
            userName: '',
            roomName: '',
            customName: '',
            messages: [],
            newMessage: '',
            onlineCount: 0,
            pollInterval: null
        };
    },
    created() {
        this.init();
        this.wsInit();
        this.startPolling();
        this.getChatHistory();
    },
    beforeDestroy() {
        this.stopPolling();
        if (this.ws) this.ws.close();
    },
    methods: {
        init() {
            this.userName = this.$route.params.user;
            this.roomName = this.$route.params.room;
        },
        wsInit() {
            if (this.userName && this.roomName) {
                this.ws = new WebSocketService(`/ChatRoom/${this.userName}/${this.roomName}`);
                this.ws.connect();
                this.setupWebSocketHandlers();
            }
        },
        setupWebSocketHandlers() {
            // 聊天消息处理
            this.ws.on('chat_message', (data) => {
                if (data.userName !== this.userName) {
                    this.addMessage({
                        customName: data.customName,
                        userName: data.userName,
                        content: data.message,
                        isMe: false
                    });
                }
            });

            // 用户加入处理
            this.ws.on('user_join', (data) => {
                this.addSystemMessage(`${data.userName} 加入了房间`);
                this.fetchOnlineCount();
            });

            // 用户退出处理
            this.ws.on('user_quit', (data) => {
                this.addSystemMessage(`${data.userName} 离开了房间`);
                this.fetchOnlineCount();
            });

        },
        async fetchOnlineCount() {
            try {
                await axios.get('/RoomInformation/getOnlineCount', {
                    params: { roomName: this.roomName }
                }).then(response => {
                    this.onlineCount = response.data.data;
                });
            } catch (error) {
                console.error('获取在线人数失败:', error);
            }
        },
        async getChatHistory() {
            try {
                await axios.get('/RoomInformation/getChatHistory', {
                    params: { roomName: this.roomName }
                }).then(response => {
                    const ChatHistory = JSON.parse(response.data.data)
                    ChatHistory.forEach(message => {
                        this.messages.push({
                            userName: message.userName,
                            customName: message.customName,
                            content: message.message,
                            isMe: message.userName == this.userName ? true : false,
                            timestamp: message.time? message.time : 'Old History'
                        });
                    });
                    this.scrollToBottom();
                });

            } catch (error) {
                console.error('获取在线人数失败:', error);
            }
        },
        handleChangPrice() {
            this.$prompt('请输入名称', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({ value }) => {
                this.customName = value;
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消输入'
                });
            });
        },
        startPolling() {
            this.fetchOnlineCount();
            this.pollInterval = setInterval(this.fetchOnlineCount, 10000);
        },
        stopPolling() {
            clearInterval(this.pollInterval);
        },
        addMessage(message) {
            this.messages.push({
                ...message,
                timestamp: new Date().toLocaleTimeString()
            });
            this.scrollToBottom();
        },
        addSystemMessage(content) {
            this.messages.push({
                content,
                isSystem: true,
            });
            this.scrollToBottom();
        },
        sendMessage() {
            if (this.newMessage.trim()) {
                const payload = {
                    type: 'chat_message',
                    customName: this.customName,
                    roomName: this.roomName,
                    userName: this.userName,
                    message: this.newMessage,
                    time: new Date().toLocaleString(),
                };

                this.ws.send(payload);
                this.addMessage({
                    userName: 'You',
                    content: this.newMessage,
                    isMe: true
                });
                this.newMessage = '';
            }
        },
        scrollToBottom() {
            this.$nextTick(() => {
                const container = this.$el.querySelector('.message-container');
                container.scrollTop = container.scrollHeight;
            });
        }
    }
};
</script>

<style scoped>
.chat-container {
    max-width: 800px;
    margin: 20px auto;
    background: #f5f5f5;
    border-radius: 15px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    height: 90vh;
    display: flex;
    flex-direction: column;
}

.chat-header {
    background: #2c3e50;
    color: white;
    padding: 20px;
    border-radius: 15px 15px 0 0;
}

.chat-header h1 {
    margin: 0;
    font-size: 1.8em;
}

.room-info {
    margin-top: 10px;
    display: flex;
    gap: 15px;
    font-size: 0.9em;
    opacity: 0.9;
    align-items: center;
}

.online-count {

    font-size: 16px;
    color: #27ae60;
    font-weight: 500;
    margin-left: auto;
}

.message-container {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    background: #fafafa;
}

.message-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.message-list li {
    max-width: 70%;
    padding: 12px 16px;
    border-radius: 15px;
    margin: 8px 0;
}

.message-header {
    font-size: 0.75em;
    color: #666;
    margin-bottom: 4px;
}

.message-content {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
}

.message-text {
    flex: 1;
    word-break: break-word;
}

.message-time {
    font-size: 0.75em;
    color: rgba(255, 255, 255, 0.7);
    margin-left: 10px;
    flex-shrink: 0;
}

.my-message {
    background: #3498db;
    color: white;
    align-self: flex-end;
    border-bottom-right-radius: 5px;
}

.my-message .message-time {
    color: rgba(255, 255, 255, 0.7);
}

.other-message {
    background: #ffffff;
    color: #333;
    align-self: flex-start;
    border-bottom-left-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.other-message .message-time {
    color: rgba(0, 0, 0, 0.5);
}

.system-message {
    align-self: center;
    background: #e8e8e8;
    color: #666;
    max-width: 80%;
    text-align: center;
    font-size: 0.9em;
    padding: 8px 16px;
}

.input-area {
    display: flex;
    padding: 20px;
    background: white;
    border-top: 1px solid #eee;
    border-radius: 0 0 15px 15px;
}

.message-input {
    flex: 1;
    padding: 12px 20px;
    border: 2px solid #eee;
    border-radius: 30px;
    font-size: 1em;
    outline: none;
    transition: border-color 0.3s;
}

.message-input:focus {
    border-color: #3498db;
}

.send-button {
    background: #3498db;
    border: none;
    color: white;
    padding: 12px 20px;
    margin-left: 15px;
    border-radius: 30px;
    cursor: pointer;
    transition: background 0.3s;
    display: flex;
    align-items: center;
}

.send-button:hover {
    background: #2980b9;
}

.send-icon {
    font-size: 1.2em;
    line-height: 1;
}

@media (max-width: 768px) {
    .chat-container {
        margin: 0;
        height: 100vh;
        border-radius: 0;
    }

    .room-info {
        flex-wrap: wrap;
        gap: 10px;
    }

    .online-count {
        margin-left: 0;
        width: 100%;
        text-align: right;
    }

    .message-list li {
        max-width: 85%;
        padding: 10px 14px;
    }
}
</style>