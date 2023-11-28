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
    }
  },
  methods:{
    async uploadSuccess(file){
      let fd = new FormData();
      fd.append('image', file.file);
      // 二次封装的 axios
      await this.$axios.post("http://localhost:8081/face/addFace",fd,{
        headers: {'Content-Type': 'multipart/form-data'}
      }).then(res => {
        console.log(res.data)
        if(res.data.code === 200){
          this.$message.success(res.data.data);
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