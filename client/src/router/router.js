import { createRouter, createWebHashHistory } from "vue-router";

const routes = [

    {
        path:'/', // 登录
        component:()=>import("../view/SignIn")
    },
    {
        path:'/signIn', // 登录
        component:()=>import("../view/SignIn")
    },
    {
        path:'/signUp', // 注册
        component:()=>import("../view/SignUp")
    },
    {
        path:'/home', // 首页
        component:()=>import("../view/HomePage"),
        children:[{
            path:'/faceDetect', // 人脸检测
            component:()=>import("../view/FaceDetect")
        },{
            path:'/faceCompare', // 人脸比对
            component:()=>import("../view/FaceCompare")
        },{
            path:'/faceBeauty', // 人脸美颜
            component:()=>import("../view/FaceBeauty")
        },{
            path:'/faceBeautification', // 人脸美化
            component:()=>import("../view/FaceBeautification")
        },{
            path:'/faceAdd', // 人脸添加
            component:()=>import("../view/FaceAdd")
        },{
            path:'/faceSearch', // 人脸搜索
            component:()=>import("../view/FaceSearch")
        },{
            path:'/skinAnalysis', // 皮肤分析
            component:()=>import("../view/SkinAnalysis")
        },{
            path:'/gestureRecognition', // 手势识别
            component:()=>import("../view/GestureRecognition")
        },{
            path:'/characterRecognition', // 文字识别
            component:()=>import("../view/CharacterRecognition")
        },]
    },
]
const router = createRouter({
    history: createWebHashHistory(),
    routes,
    strict: true, // applies to all routes
})
export default router;