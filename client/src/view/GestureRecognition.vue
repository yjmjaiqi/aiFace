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
  <div>手势识别图：<a href="https://console.faceplusplus.com.cn/documents/10065685">https://console.faceplusplus.com.cn/documents/10065685</a></div>
  <div class="demo-image">
    <div class="block">
      <span class="demonstration">{{ fit }}</span>
      <el-image style="width: 400px; height: 400px;margin-left: 400px" :src="imgUrl" fit="contain" />
    </div>
  </div>
</template>

<script>
export default {
  name: "GestureRecognition",
  data(){
    return{
      dialogVisible:false,
      dialogImageUrl:'',
      imgUrl:'',
    }
  },
  methods:{
    async uploadSuccess(file){
      let fd = new FormData();
      fd.append('image', file.file);
      // 二次封装的 axios
      await this.$axios.post("http://localhost:8081/face/gesture",fd,{
        headers: {'Content-Type': 'multipart/form-data'}
      }).then(res => {
        console.log(res.data)
        if(res.data.code === 200){
          this.$message.success("手势识别成功");
          this.imgUrl = res.data.data
          console.log(res.data.data)
        }else if(res.data.code === 404){
          this.$message.error(res.data.data)
        } else {
          this.$message.error("未检测到手势")
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