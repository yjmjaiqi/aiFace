<template>
  <div class="demo-image__error">
    <div class="block">
      <span class="demonstration">Default</span>
      <el-image :src="imgUrl1" fit="contain"/>
      <el-upload
          :limit="1"
          :http-request="uploadSuccess1"
      >
        <template #trigger>
          <el-button type="primary">select file</el-button>
        </template>
        <template #tip>
          <div class="el-upload__tip text-red">
            limit 1 file, new file will cover the old file
          </div>
        </template>
      </el-upload>
    </div>
    <div class="block">
      <span class="demonstration">Custom</span>
      <el-image :src="imgUrl2" fit="contain">
        <template #error>
          <div class="image-slot">
            <el-icon><icon-picture /></el-icon>
          </div>
        </template>
      </el-image>
      <el-upload
          :http-request="uploadSuccess2"
          :limit="1"
      >
        <template #trigger>
          <el-button type="primary">select file</el-button>
        </template>
        <template #tip>
          <div class="el-upload__tip text-red">
            limit 1 file, new file will cover the old file
          </div>
        </template>
      </el-upload>
    </div>
    <div>
      <el-button style="margin-left: 45%" @click="compare">人脸比对</el-button><br>
      <span style="margin-left: 45%;color: red">{{ res }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "FaceCompare",
  data(){
    return{
      imgUrl1:'',
      imgUrl2:'',
      face_token1:'',
      face_token2:'',
      res:'',
    }
  },
  methods:{
    async compare(){
      if(this.face_token1 !== '' && this.face_token2 !== ''){
        let fd = new FormData();
        fd.append('face_token1', this.face_token1);
        fd.append('face_token2', this.face_token2);
        await this.$axios.post("http://localhost:8081/face/compare",fd,{
          headers: {'Content-Type': 'multipart/form-data'}
        }).then(res => {
          console.log(res.data)
          if(res.data.code === 200){
            this.$message.success("人脸比对成功");
            this.res = res.data.data
          }else if(res.data.code === 404){
            this.$message.error(res.data.data)
          } else {
            this.$message.error("人脸比对失败")
          }
        })
      }else{
        this.$message.warning("需要两张图片进行比较")
      }
    },
    async uploadSuccess1(file){
      let fd = new FormData();
      fd.append('image', file.file);
      // 二次封装的 axios
      await this.$axios.post("http://localhost:8081/face/face_token1",fd,{
        headers: {'Content-Type': 'multipart/form-data'}
      }).then(res => {
        if(res.data.code === 200){
          console.log(res.data.data)
          this.$message.success("人脸检测成功");
          this.face_token1 = res.data.data.face_Token
          this.imgUrl1 = res.data.data.imgUrl
        }else if(res.data.code === 404){
          this.$message.error(res.data.data)
        } else {
          this.$message.error("未检测到人脸")
        }})
    },
    async uploadSuccess2(file){
      let fd = new FormData();
      fd.append('image', file.file);
      // 二次封装的 axios
      await this.$axios.post("http://localhost:8081/face/face_token2",fd,{
        headers: {'Content-Type': 'multipart/form-data'}
      }).then(res => {
        console.log(res.data)
        if(res.data.code === 200){
          this.$message.success("人脸检测成功");
          this.face_token2 = res.data.data.face_Token
          this.imgUrl2 = res.data.data.imgUrl
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
.demo-image__error .block {
  padding: 30px 0;
  text-align: center;
  border-right: solid 1px var(--el-border-color);
  display: inline-block;
  width: 49%;
  box-sizing: border-box;
  vertical-align: top;
}
.demo-image__error .demonstration {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 20px;
}
.demo-image__error .el-image {
  padding: 0 5px;
  max-width: 300px;
  max-height: 200px;
  width: 100%;
  height: 200px;
}

.demo-image__error .image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 30px;
}
.demo-image__error .image-slot .el-icon {
  font-size: 30px;
}
</style>