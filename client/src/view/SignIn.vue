<template>
  <div>
    <!-- 摄像头画面展示 -->
    <center>
      <h1>基于人脸识别的登录注册系统</h1>
      <div>
        <video ref="video" autoplay></video>
        <button @click="openCamera">打开摄像头</button>
        <button @click="closeCamera">关闭摄像头</button>
        <button @click="takePicture('登录')" >登录</button>
        <button @click="takePicture('注册')" >注册</button>
      </div>
    </center>

  </div>
</template>

<script>
import router from "@/router/router";

export default {
  name: "SignIn",
  data() {
    return {
      isLogin: true,
      username: '',
      password: '',
      newUsername: '',
      newPassword: '',
      showCamera: false,
      mediaStream: null,
      state:''
    };
  },
  methods: {
    toggleForm() {
      this.isLogin = !this.isLogin;
    },
    login() {
      // 处理登录逻辑
      console.log('登录:', this.username, this.password);
    },
    register() {
      // 处理注册逻辑
      console.log('注册:', this.newUsername, this.newPassword);
    },
    takePicture(val) {
      this.showCamera = false; // 隐藏相机画面
      const video = this.$refs.video;
      const canvas = document.createElement('canvas');
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);
      const imgData = canvas.toDataURL('image/png');
      // 在这里可以将拍摄的照片数据(imgData)发送到服务器或进行其他操作
      console.log('拍摄的照片数据:', imgData);
      // let fd = new FormData();
      // fd.append('image', imgData);
      // console.log(fd)
      // 二次封装的 axios
      // alert(val)
      if(val === "登录"){
        this.$axios.post("http://localhost:8081/face/login",imgData,{
          headers: {
            'Content-Type': 'text/plain' // 修改为合适的Content-Type
          }}).then(res => {
          console.log(res.data)
          if(res.data.code === 200){
            this.$message.success(res.data.data);
            router.push("/home")
          }else if(res.data.code === 404){
            this.$message.error(res.data.data)
          }
        })
      }else if(val === "注册"){
        this.$axios.post("http://localhost:8081/face/register",imgData,{
          headers: {
            'Content-Type': 'text/plain' // 修改为合适的Content-Type
          }}).then(res => {
          console.log(res.data)
          if(res.data.code === 200){
            this.$message.success(res.data.data);
          }else if(res.data.code === 404){
            this.$message.error(res.data.data)
          }
        })
      }
    },
    async openCamera() {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ video: true });
        this.$refs.video.srcObject = stream;
        this.stream = stream;
      } catch (error) {
        console.error('无法访问摄像头:', error);
      }
      // try {
      //   const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      //   this.mediaStream = stream;
      //   this.showCamera = true;
      //   const video = this.$refs.video;
      //   video.srcObject = stream;
      // } catch (error) {
      //   console.error('无法打开摄像头:', error);
      // }
    },
    async closeCamera() {
      try {
        // await navigator.mediaDevices.getUserMedia({ video: false });
        this.mediaStream.getTracks().forEach(track => track.stop());
      } catch (error) {
        console.error('无法关闭摄像头:', error);
      }
    }
  },
  mounted() {
    // 在组件挂载时打开摄像头
    // this.openCamera();
  },
  // beforeDestroy() {
  //   // 在组件销毁前关闭摄像头
  //   if (this.mediaStream) {
  //     this.mediaStream.getTracks().forEach(track => track.stop());
  //   }
  // }
};
</script>

<style scoped>
 /*样式可以根据需要自行调整 */
video {
  width: 100%;
  max-width: 400px;
}
</style>
