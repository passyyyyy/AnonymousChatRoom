<template>
  <div class="full-page">
    <router-view></router-view>
  </div>
</template>

<style>
/* 设置整个页面的填充和边距为0 */
html, body, .full-page {
  margin: 1px;
  margin-right: 1px;
  margin-bottom: -6px;
  overflow: hidden;
}

/* 设置router-view填充整个父元素 */
.router-view {
  width: 100%;
  height: 100%;
}
</style>
