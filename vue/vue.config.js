const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // 前端启动端口号
  devServer:{
    port:80
    
  }
})
