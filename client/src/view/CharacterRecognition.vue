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
    <span>文字识别结果如下</span>
    <hr>
    <el-input
        v-model="words"
        :rows="10"
        type="textarea"
        placeholder="Please input"
        readonly
    />
  </div>
</template>

<script>
export default {
  name: "CharacterRecognition",
  data(){
    return{
      dialogVisible:false,
      dialogImageUrl:'',
      words:'',
    }
  },
  methods:{
    async uploadSuccess(file){
      let fd = new FormData();
      fd.append('image', file.file);
      // 二次封装的 axios
      await this.$axios.post("http://localhost:8081/face/words",fd,{
        headers: {'Content-Type': 'multipart/form-data'}
      }).then(res => {
        console.log(res.data)
        if(res.data.code === 200){
          this.$message.success("文字识别成功");
          this.words = res.data.data
          console.log(res.data.data)
        }else if(res.data.code === 404){
          this.$message.error(res.data.data)
        } else {
          this.$message.error("文字识别失败")
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