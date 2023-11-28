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
        美白程度<el-input-number v-model="whitening" :min="0" :max="100"/><br>
        磨皮程度<el-input-number v-model="smoothing" :min="0" :max="100"/><br>
        <el-button @click="sure">确定</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FaceBeauty",
  data(){
    return{
      dialogVisible:false,
      dialogImageUrl:'',
      imgUrl:'',
      whitening:'0',
      smoothing:'0',
      file:'',
    }
  },
  methods:{
    async uploadSuccess(file){
      this.file = file
    },
    async sure(){
      if(this.file !== ''){
        let fd = new FormData();
        fd.append('image', this.file.file);
        fd.append('whitening', this.whitening);
        fd.append('smoothing', this.smoothing);
        // 二次封装的 axios
        await this.$axios.post("http://localhost:8081/face/faceBeauty",fd,{
          headers: {'Content-Type': 'multipart/form-data'}
        }).then(res => {
          console.log(res.data)
          if(res.data.code === 200){
            this.$message.success("人脸美颜成功");
            this.imgUrl = res.data.data
            console.log(res.data.data)
          }else if(res.data.code === 404){
            this.$message.error(res.data.data)
          } else {
            this.$message.error("未检测到人脸")
          }})
      }else {
        this.$message.warning("请选择图片")
      }
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