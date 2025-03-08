// router/index.js
import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    component: () => import("@/views/LoginView.vue"),
  },
  {
    path: "/chat/:user/:room",
    component: () => import("@/views/ChatView.vue"),
  },
  {
    path: "/404",
    component: () => import("@/components/abnormal/404View.vue"),
  },
  {
    path: "/403",
    component: () => import("@/components/abnormal/403View.vue"),
  },
  {
    path: "*", // 捕获所有未匹配的路由
    component: () => import("@/components/abnormal/404View.vue"),
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

// 添加全局前置守卫
router.beforeEach((to, from, next) => {
  // 如果目标路由是 /chat/:user/:room
  if (to.path.startsWith("/chat/")) {
    // 检查来源路由是否是 /
    if (from.path !== "/") {
      // 如果不是从 / 页面来的，强制跳转到 / 页面
      next("/");
    } else {
      // 如果是从 / 页面来的，继续跳转
      next();
    }
  } else {
    // 如果目标路由不是 /chat/:user/:room，继续跳转
    next();
  }
});

export default router;