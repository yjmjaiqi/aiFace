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
    <span>皮肤分析结果如下</span>
    <hr>
    <el-input
        v-model="desc"
        :rows="20"
        type="textarea"
        placeholder="Please input"
        readonly
        style="width: 500px"
    />
  </div>
  <el-image style="width: 400px; height: 400px;margin-left: 550px;margin-top: -500pc" :src="imgUrl" fit="contain" />
</template>

<script>
export default {
  name: "SkinAnalysis",
  data(){
    return{
      dialogVisible:false,
      dialogImageUrl:'',
      imgUrl:'',
      desc:'',
    }
  },
  methods:{
    async uploadSuccess(file){
      let fd = new FormData();
      fd.append('image', file.file);
      // 二次封装的 axios
      await this.$axios.post("http://localhost:8081/face/skinAnalysis",fd,{
        headers: {'Content-Type': 'multipart/form-data'}
      }).then(res => {
        console.log(res.data)
        if(res.data.code === 200){
          this.$message.success("皮肤分析完成");
          this.desc = "左眼双眼皮检测结果："+res.data.data.left_eyelids+"\r\n"+"右眼双眼皮检测结果："+res.data.data.right_eyelids
          +"\r\n"+"眼袋检测结果："+res.data.data.eye_pouch+"\r\n"+"黑眼圈检测结果："+res.data.data.dark_circle+"\r\n"+"抬头纹检测结果："
          +res.data.data.forehead_wrinkle+"\r\n"+"鱼尾纹检测结果："+res.data.data.crows_feet+"\r\n"+"眼部细纹检测结果："
              +res.data.data.eye_finelines+"\r\n"+"眉间纹检测结果："+res.data.data.glabella_wrinkle+"\r\n"+"法令纹检测结果："
              +res.data.data.nasolabial_fold+"\r\n"+"肤质检测结果："+res.data.data.skin_type+"\r\n"+"前额毛孔检测结果："
              +res.data.data.pores_forehead+"\r\n"+"左脸颊毛孔检测结果："+res.data.data.pores_left_cheek+"\r\n"+"右脸颊毛孔检测结果："
              +res.data.data.pores_right_cheek+"\r\n"+"下巴毛孔检测结果："+res.data.data.pores_jaw+"\r\n"+"黑头检测结果："
              +res.data.data.blackhead+"\r\n"+"痘痘检测结果："+res.data.data.acne+"\r\n"+"痣检测结果："
              +res.data.data.mole+"\r\n"+"斑点检测结果："+res.data.data.skin_spot
          this.imgUrl = res.data.data.imgUrl
          console.log(res.data.data)
        }else if(res.data.code === 404){
          this.$message.error(res.data.data)
        } else {
          this.$message.error("皮肤分析失败")
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