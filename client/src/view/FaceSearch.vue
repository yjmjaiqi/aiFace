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
  <div>
    <div style="border:1px solid ;width:580px;height:400px;">
      <span>置信度越高，且大于阈值1e-5，则认为两个人脸越可能是同一个人</span>
      <hr>
      <span>置信度 [0,100]：{{ confidence }}</span><br>
      <span>face_token：{{ face_token }}</span>
      <hr>
      <p>阈值 [0,100]</p>
      <span>thresholds_1e_3：{{ thresholds_1e_3 }}</span><br>
      <span>thresholds_1e_4：{{ thresholds_1e_4 }}</span><br>
      <span>thresholds_1e_5：{{ thresholds_1e_5 }}</span><br>
      <hr>
      <p><span>相似度</span> <span style="color:red">{{ isTrue }}</span></p>
    </div>
  </div>
</template>

<script>
export default {
  name: "FaceSearch",
  data(){
    return{
      dialogVisible:false,
      dialogImageUrl:'',
      confidence:'',
      face_token:'',
      thresholds_1e_3:'',
      thresholds_1e_4:'',
      thresholds_1e_5:'',
      isTrue:'',
    }
  },
  methods:{
    async uploadSuccess(file){
      let fd = new FormData();
      fd.append('image', file.file);
      // 二次封装的 axios
      await this.$axios.post("http://localhost:8081/face/search",fd,{
        headers: {'Content-Type': 'multipart/form-data'}
      }).then(res => {
        console.log(res.data)
        if(res.data.code === 200){
          this.$message.success("人脸搜索成功");
          this.confidence = res.data.data.confidence
          this.face_token = res.data.data.face_token
          this.thresholds_1e_3 = res.data.data.thresholds_1e_3
          this.thresholds_1e_4 = res.data.data.thresholds_1e_4
          this.thresholds_1e_5 = res.data.data.thresholds_1e_5
          if(res.data.data.ok === true){
            this.isTrue = "人脸相似度极高"
          }else if(res.data.data.ok === false){
            this.isTrue = "人脸相似度低"
          }
          console.log(res.data.data)
        }else if(res.data.code === 404){
          this.$message.error(res.data.data)
        } else {
          this.$message.error("人脸搜索失败")
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
</style>