<template>
    <div>

    </div>
</template>

<script>
import WebSocketService from '@/utils/websocket'

export default {
    data() {
        return {
            id: null,
            ws: new WebSocketService(`/ws/123`),
        }
    },
    created() {
        this.initWebsocket()
        this.initEven()
    },
    methods: {
        // websocket初始化方法
        initWebsocket() {
            this.ws.connect()
        },
        close() {
            this.ws.close()
        },
        initEven() {
            // 使用箭头函数保持 this 上下文
            document.addEventListener('keydown', (event) => {
                this.ws.send(event.key);
            });
        }

    },
    beforeDestroy() {
        this.ws.close()
    }
}
</script>

<style scoped></style>