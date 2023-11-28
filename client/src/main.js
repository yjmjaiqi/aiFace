import { createApp } from 'vue'
import App from './App.vue'
import router from "./router/router"
import axios from 'axios';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// import ECharts from 'vue-echarts';
// import 'echarts'

const app = createApp(App)

app.config.globalProperties.$axios=axios
// axios.defaults.headers.post['Content-Type']='application/json;charset=UTF-8'///'multipart/form-data'//
axios.defaults.headers.get['Content-Type']='text/plain;charset=UTF-8'///'multipart/form-data'//
axios.defaults.headers.put['Content-Type']='application/json;charset=UTF-8'///'multipart/form-data'//
// // 添加请求拦截器
axios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    // 判断是否存在token,如果存在将每个页面header添加token
    if (sessionStorage.getItem("token")) {
        console.log("ok")
        config.headers['token'] = sessionStorage.getItem("token");
    }
    return config

}, function (error) {
    console.log(error)
    return Promise.reject(error)
})
app.use(ElementPlus)
app.use(router)
// app.component('ECharts', ECharts)
// app.use(store)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')