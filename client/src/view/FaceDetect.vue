<template>
  <el-upload
      :limit="1"
      list-type="picture-card"
      :http-request="uploadSuccess"
      :on-preview="handlePictureCardPreview"
  >
    <el-icon><Plus /></el-icon>
    上传图片
  </el-upload>
  <el-dialog v-model="dialogVisible">
    <img :style="{ width: '50%', height: 'auto' }" :src="dialogImageUrl" alt="Preview Image" />
  </el-dialog>
  <div class="demo-image">
    <div class="block">
      <span class="demonstration">{{ fit }}</span>
      <el-image style="width: 400px; height: 400px;margin-left: 400px" :src="imgUrl" fit="contain" />
      <div style="border:1px solid ;width:380px;height:400px;margin-top: -400px">
        <span>性别：{{ gender }}</span>
        <span>年龄：{{ age }}</span>
        <span>笑容：{{ smile }}</span>
        <hr>
        <p>情绪值 [0,100]</p>
        <span>愤怒：{{ anger }}</span>
        <span>厌恶：{{ disgust }}</span>
        <span>恐惧：{{ fear }}</span>
        <span>高兴：{{ happiness }}</span>
        <span>平静：{{ neutral }}</span>
        <span>伤心：{{ sadness }}</span>
        <span>惊讶：{{ surprise }}</span>
        <hr>
        <p>面部特征值 [0,100]</p>
        <span>健康：{{ health }}</span>
        <span>色斑：{{ stain }}</span>
        <span>青春痘：{{ acne }}</span>
        <span>黑眼圈：{{ dark_circle }}</span>
        <hr>
        <p>人脸姿势 [-180, 180]</p>
        <span>抬头：{{ pitch_angle }}</span>
        <span>旋转：{{ roll_angle }}</span>
        <span>摇头：{{ yaw_angle }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FaceDetect",
  data(){
    return{
      dialogVisible:false,
      dialogImageUrl:'',
      acne:'',
      age:'',
      anger:'',
      dark_circle:'',
      disgust:'',
      fear:'',
      gender: '',
      happiness:'',
      health:'',
      imgUrl:'',
      neutral:'',
      pitch_angle:'',
      roll_angle:'',
      sadness:'',
      smile:'',
      stain:'',
      surprise:'',
      yaw_angle:''
    }
  },
  methods:{
    async uploadSuccess(file){
      let fd = new FormData();
      fd.append('image', file.file);
      // 二次封装的 axios
      await this.$axios.post("http://192.168.34.246:8081/face/detect",fd,{
        headers: {'Content-Type': 'multipart/form-data'}
      }).then(res => {
        console.log(res.data)
        if(res.data.code === 200){
          this.$message.success("人脸检测成功");
          this.imgUrl = res.data.data.imgUrl
          this.acne = res.data.data.acne
          this.anger = res.data.data.anger
          this.age = res.data.data.age
          this.dark_circle = res.data.data.dark_circle
          this.disgust = res.data.data.disgust
          this.fear = res.data.data.fear
          this.gender = res.data.data.gender
          this.happiness = res.data.data.happiness
          this.health = res.data.data.health
          this.neutral = res.data.data.neutral
          this.imgUrl = res.data.data.imgUrl
          this.pitch_angle = res.data.data.pitch_angle
          this.roll_angle = res.data.data.roll_angle
          this.sadness = res.data.data.sadness
          this.smile = res.data.data.smile
          this.stain = res.data.data.stain
          this.surprise = res.data.data.surprise
          this.yaw_angle = res.data.data.yaw_angle
          console.log(res.data.data)
        }else if(res.data.code === 404){
          this.$message.error(res.data.data)
        } else {
          this.$message.error("未检测到人脸")
        }})
    },
    handlePictureCardPreview(file) {
      // 设置对话框中图片的 URL
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
  }
}
</script>

<style scoped>
.el-image {
  width: 100%;
  height: 100%;
}
</style>