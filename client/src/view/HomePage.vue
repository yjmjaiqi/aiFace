<template>
  <div class="layout">
    <el-container v-if="showMenu" class="container">
      <el-aside class="aside">
        <div class="head">
          <div>
            <span>人脸识别系统</span>
          </div>
        </div>
        <div class="line"/>
        <el-menu
            :default-openeds="defaultOpen"
            background-color="#222832"
            text-color="#fff"
            :router="true"
            :default-active='currentPath'
        >
          <el-submenu index="1">
            <el-menu-item-group>
              <el-menu-item index="/faceDetect">
                <el-icon>
                  <Avatar/>
                </el-icon>
                人脸检测
              </el-menu-item>
              <el-menu-item index="/faceCompare">
                <el-icon>
                  <User/>
                </el-icon>
                人脸比对
              </el-menu-item>
              <el-menu-item index="/faceBeauty">
                <el-icon>
                  <User/>
                </el-icon>
                人脸美颜
              </el-menu-item>
              <el-menu-item index="/faceBeautification">
                <el-icon>
                  <Cherry/>
                </el-icon>
                人脸美化
              </el-menu-item>
              <el-menu-item index="/faceAdd">
                <el-icon><CirclePlus /></el-icon>
                人脸添加
              </el-menu-item>
              <el-menu-item index="/faceSearch">
                <el-icon><Search /></el-icon>
                人脸搜索
              </el-menu-item>
              <el-menu-item index="/skinAnalysis">
                <el-icon :size='20'>
                  <Opportunity/>
                </el-icon>
                皮肤分析
              </el-menu-item>
              <el-menu-item index="/gestureRecognition">
                <el-icon><Pointer /></el-icon>
                手势识别
              </el-menu-item>
              <el-menu-item index="/characterRecognition">
                <el-icon><Reading /></el-icon>
                文字识别
              </el-menu-item>
            </el-menu-item-group>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-container class="content">
        <!--        <Header/>-->
        <div class="main">
          <router-view/>
        </div>
      </el-container>
    </el-container>
    <el-container v-else class="container">
      <router-view/>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "HomePage",
  data(){
    return{
      defaultOpen: ['1'],
      showMenu: true,
      currentPath: '/',
      count: {
        number: 1
      }
    }
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  background-color: #ffffff;
}

.container {
  height: 100vh;
}

.aside {
  width: 200px !important;
  background-color: #222832;
  overflow: hidden;
  overflow-y: auto;
  -ms-overflow-style: none;
  overflow: -moz-scrollbars-none;
}

.aside::-webkit-scrollbar {
  display: none;
}

.head {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
}

.head > div {
  display: flex;
  align-items: center;
}

.head img {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}

.head span {
  font-size: 20px;
  color: #ffffff;
}

.line {
  border-top: 1px solid hsla(0, 0%, 100%, .05);
  border-bottom: 1px solid rgba(0, 0, 0, .2);
}

.content {
  display: flex;
  flex-direction: column;
  max-height: 100vh;
  overflow: hidden;
}

.main {
  height: calc(100vh - 100px);
  overflow: auto;
  padding: 10px;
}
</style>
  <style>
  body {
    padding: 0;
    margin: 0;
    box-sizing: border-box;
  }

.el-menu {
  border-right: none !important;
}

.el-submenu {
  border-top: 1px solid hsla(0, 0%, 100%, .05);
  border-bottom: 1px solid rgba(0, 0, 0, .2);
}

.el-submenu:first-child {
  border-top: none;
}

.el-submenu [class^="el-icon-"] {
  vertical-align: -1px !important;
}

a {
  color: #409eff;
  text-decoration: none;
}

.el-pagination {
  text-align: center;
  margin-top: 20px;
}

.el-popper__arrow {
  display: none;
}
</style>