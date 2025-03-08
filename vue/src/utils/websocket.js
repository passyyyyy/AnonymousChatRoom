
/**
 * WebSocket 服务封装类，提供事件驱动的消息通信机制。
 * 支持自定义事件类型，采用发布-订阅模式管理消息监听。
 */
export default class WebSocketService {
    /**
     * 创建一个新的 WebSocketService 实例。
     * @param {string} url - WebSocket 服务器的 URL。
     */
    constructor(url) {
        this.url = process.env.VUE_APP_URL + url; // WebSocket 服务器的 URL
        this.ws = null; // WebSocket 实例
        // 事件监听器集合：{ eventName: [callback1, callback2] }
        this.listeners = {}; // 事件监听器对象
    }

    /**
     * 连接到 WebSocket 服务器。
     */
    connect() {
        // 如果当前没有 WebSocket 实例或者 WebSocket 实例已关闭，则创建一个新的 WebSocket 实例
        if (!this.ws || this.ws.readyState === WebSocket.CLOSED) {
            this.ws = new WebSocket(this.url);

            // WebSocket 连接成功时的回调
            this.ws.onopen = () => {
                
            };

            // 收到消息时的回调
            this.ws.onmessage = (event) => {
                const data = JSON.parse(event.data);
                this.emit(data.type, data);
            };

            // 发生错误时的回调
            this.ws.onerror = (error) => {
                alert('WebSocket连接出错',error);
            };

            // 连接关闭时的回调
            this.ws.onclose = () => {
                console.log('WebSocket连接关闭');
            };
        }
    }

    /**
     * 发送消息到 WebSocket 服务器。
     * @param {Object} message - 要发送的消息对象。
     */
    send(message) {
        // 如果 WebSocket 实例存在且状态为 OPEN，则发送消息
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message)); // 将消息对象转换为 JSON 字符串并发送
        } else {
            console.error('WebSocket is not open'); // 如果 WebSocket 未打开，则输出错误信息
        }
    }

    /**
     * 关闭 WebSocket 连接。
     */
    close() {
        // 如果 WebSocket 实例存在，则关闭连接
        if (this.ws) {
            this.ws.close();
        }
    }

    /**
     * 注册事件监听器。
     * @param {string} event - 事件名称。
     * @param {Function} callback - 回调函数。
     */
    on(event, callback) {
        if (!this.listeners[event]) {
            this.listeners[event] = [];
        }
        this.listeners[event].push(callback);
    }

    /**
     * 触发事件。
     * @param {string} event - 事件名称。
     * @param {*} data - 数据。
     */
    emit(event, data) {
        if (this.listeners[event]) {
            this.listeners[event].forEach((callback) => callback(data));
        }
    }
}