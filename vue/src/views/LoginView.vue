<template>
    <div>
        <!-- 移动端布局 -->
        <div v-if="isMobile" class="mobile-container">
            <div class="mobile-header">
                <h1 class="mobile-title">Anonymous Chat</h1>
            </div>
            <div class="mobile-form">
                <div class="form-section">
                    <h2 class="form-title">Join Room</h2>
                    <input class="mobile-input" v-model="roomName" placeholder="Room Name" />
                    <button class="mobile-primary-btn" @click="joinRoom">Join Room</button>
                </div>
            </div>
            <div class="mobile-footer">Unique ID: {{ id }}</div>
        </div>

        <!-- 桌面端布局 -->
        <div v-else class="desktop-container">
            <div class="desktop-header">
                <h1>Anonymous Chat</h1>
            </div>
            <div class="desktop-form">
                <h2>Join Room</h2>
                <input type="text" v-model="roomName" placeholder="Room Name" />
                <button @click="joinRoom">Join Room</button>
            </div>
            <div class="desktop-footer">Unique ID: {{ id }}</div>
        </div>
    </div>
</template>

<script>
import { v4 as uuid } from 'uuid';

export default {
    data() {
        return {
            id: '',
            showCreateRoom: true,
            username: '',
            roomName: '',
            isMobile: false
        };
    },
    created() {
        this.initId();
        this.detectMobile();
    },

    methods: {
        joinRoom() {
            this.navigator(this.username, this.roomName);
        },
        initId() {
            const id = uuid();
            this.id = id;
            this.username = id;
        },
        detectMobile() {
            this.isMobile = /Android|iPhone/i.test(navigator.userAgent);
        },
        navigator(user, room) {
            this.$router.push(`/chat/${user}/${room}`);
        }
    }
};
</script>

<style>
/* 移动端样式 */
.mobile-container {
    height: 100vh;
    background: #1a1a1a;
    display: flex;
    flex-direction: column;
    padding: 20px;
}

.mobile-header {
    padding: 30px 0;
    text-align: center;
}

.mobile-title {
    color: #fff;
    font-size: 28px;
    margin: 0;
}

.mobile-form {
    flex: 1;
    padding: 20px 0;
}

.form-section {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    padding: 25px;
    margin-bottom: 20px;
}

.form-title {
    color: #fff;
    font-size: 20px;
    margin-bottom: 20px;
}

.mobile-input {
    width: 100%;
    padding: 14px;
    margin-bottom: 15px;
    border: 1px solid #4a4a4a;
    border-radius: 8px;
    background: #2d2d2d;
    color: #fff;
}

.mobile-primary-btn {
    width: 100%;
    padding: 16px;
    background: #007AFF;
    color: white;
    border: none;
    border-radius: 8px;
    margin-top: 10px;
}

.mobile-footer {
    text-align: center;
    color: #666;
    padding: 15px;
    border-top: 1px solid #333;
}

/* 桌面端样式 */
.desktop-container {
    height: 100vh;
    background: rgba(0, 0, 0, 0.8);
    display: flex;
    flex-direction: column;
}

.desktop-header {
    padding: 40px;
    text-align: center;
}

.desktop-header h1 {
    color: white;
    font-size: 2.5rem;
}

.desktop-form {
    max-width: 400px;
    margin: 0 auto;
    padding: 20px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 8px;
}

.desktop-form h2 {
    color: white;
    margin-bottom: 20px;
}

.desktop-form input {
    width: 90%;
    padding: 12px;
    margin-bottom: 15px;
    border-radius: 6px;
    border: 1px solid #444;
    background: #333;
    color: white;
}

.desktop-form button {
    width: 100%;
    padding: 12px;
    background: #007AFF;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
}

.desktop-footer {
    margin-top: auto;
    text-align: center;
    padding: 20px;
    color: #666;
    border-top: 1px solid #333;
}
</style>