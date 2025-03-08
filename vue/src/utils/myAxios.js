// 导入 axios 库，用于发送 HTTP 请求  
import axios from 'axios';
  
// 创建一个 axios 实例，用于后续的 HTTP 请求  
const axiosInstance = axios.create({  
  // 设置请求的基准 URL，后续请求都会基于这个 URL  
  baseURL: process.env.VUE_APP_URL,  
  // 设置请求超时时间，单位为毫秒  
  timeout: 30000  
});  

export default axiosInstance;