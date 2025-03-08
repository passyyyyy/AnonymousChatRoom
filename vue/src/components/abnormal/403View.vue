<template>
  <div
    style="font-family: 'Arial'; display: flex; height: 100vh; align-items: center; justify-content: center; background-color: beige;">
    <div
      style="background-color: white; width: 960px; height: 483.5px; border-radius: 50px; display: flex; align-items: center; justify-content: center; flex-direction: column;">
      <div style="margin-bottom: 50px;">
        <el-result title="403" subTitle="无权限访问">
          <template slot="icon">
            <el-image
              src="https://sso.ciic-cloud.com/static/images/error.png"></el-image>
          </template>
          <template slot="extra">
            <el-button type="primary" size="medium" @click="navigateTo('/')">返回</el-button>
          </template>
        </el-result>
      </div>
    </div>
  </div>
</template>


<script>
export default {
  methods: {
    navigateTo(path) {
      if (this.$route.path !== path) {
        this.$router.push(path);
      }
    },
  }
}
</script>

<style></style>