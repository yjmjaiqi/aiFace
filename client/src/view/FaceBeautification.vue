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
        程度值[0,100]<br>
        美白程度&nbsp;&nbsp;&nbsp;<el-input-number v-model="whitening" :min="0" :max="100"/><br>
        磨皮程度&nbsp;&nbsp;&nbsp;<el-input-number v-model="smoothing" :min="0" :max="100"/><br>
        瘦脸程度&nbsp;&nbsp;&nbsp;<el-input-number v-model="thinface" :min="0" :max="100"/><br>
        笑脸程度&nbsp;&nbsp;&nbsp;<el-input-number v-model="shrink_face" :min="0" :max="100"/><br>
        大眼程度&nbsp;&nbsp;&nbsp;<el-input-number v-model="enlarge_eye" :min="0" :max="100"/><br>
        去眉毛程度<el-input-number v-model="remove_eyebrow" :min="0" :max="100"/><br>
        滤镜名称
        <el-select v-model="value" placeholder="Select">
          <el-option
              @click="q"
              v-for="item in options"
              :key="item.value"
              :value="item.value"
              :disabled="item.disabled"
          />
        </el-select><br>
        <el-button @click="sure">确定</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FaceBeautification",
  data(){
    return{
      dialogVisible:false,
      dialogImageUrl:'',
      imgUrl:'',
      whitening:'0',
      smoothing:'0',
      thinface:'0',
      shrink_face:'0',
      enlarge_eye:'0',
      remove_eyebrow:'0',
      file:'',
      value: '',
      options: [
        { value: 'black_white-黑白' },
        { value: 'calm-平静' },
        { value: 'sunny-晴天' },
        { value: 'trip-旅程' },
        { value: 'beautify-美肤' },
        { value: 'wangjiawei-王家卫' },
        { value: 'cutie-唯美' },
        { value: 'macaron-可人儿' },
        { value: 'new_york-纽约' },
        { value: 'sakura-樱花' },
        { value: '17_years_old-十七岁' },
        { value: 'clight-柔光灯' },
        { value: 'tea_time-下午茶' },
        { value: 'whiten-亮肤' },
        { value: 'chaplin-卓别林' },
        { value: 'flowers-花香' },
        { value: 'memory-回忆' },
        { value: 'ice_lady-冰美人' },
        { value: 'paris-巴黎' },
        { value: 'times-时光' },
        { value: 'lomo-LOMO' },
        { value: 'old_times-旧时光' },
        { value: 'spring-早春' },
        { value: 'story-故事' },
        { value: 'abao-阿宝色' },
        { value: 'wlight-补光灯' },
        { value: 'warm-暖暖' },
        { value: 'glitter-绚烂' },
        { value: 'lavender-薰衣草' },
        { value: 'chanel-香奈儿' },
        { value: 'prague-布拉格' },
        { value: 'old_dream-旧梦' },
        { value: 'blossom-桃花' },
        { value: 'pink-粉黛' },
        { value: 'jiang_nan-江南' },
      ],
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
        fd.append('thinface', this.thinface);
        fd.append('shrink_face', this.shrink_face);
        fd.append('enlarge_eye', this.enlarge_eye);
        fd.append('remove_eyebrow', this.remove_eyebrow);
        fd.append('filter_type', this.value.split("-")[0]);
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