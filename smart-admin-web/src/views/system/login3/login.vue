<!--
  * 登录
-->
<template>
  <div class="login-container">
    <div class="box-item desc">
      <div class="welcome">
        <p>欢迎登录 </p>
        <p class="sub-welcome">基于Web应用的数据加解密系统</p>
      </div>
      <img class="welcome-img" :src="leftBg2" />
    </div>
    <div class="box-item login">
      <img class="login-qr" :src="loginQR" />
      <div class="login-title">账号登录</div>
      <a-form ref="formRef" class="login-form" :model="loginForm" :rules="rules">
        <a-form-item name="loginName">
          <a-input v-model:value.trim="loginForm.loginName" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item name="password">
          <a-input-password
            v-model:value="loginForm.password"
            autocomplete="on"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码：至少三种字符，最小 8 位"
          />
        </a-form-item>
        <a-form-item name="captchaCode">
          <a-input class="captcha-input" v-model:value.trim="loginForm.captchaCode" placeholder="请输入验证码" />
          <img class="captcha-img" :src="captchaBase64Image" @click="getCaptcha" />
        </a-form-item>
        <a-form-item>
          <a-checkbox v-model:checked="rememberPwd">记住密码</a-checkbox>
          <span> ( 账号：admin, 密码：123456)</span>
        </a-form-item>
        <a-form-item>
          <div class="btn" @click="onLogin">登录</div>
        </a-form-item>
      </a-form>
      <div class="more">
        <div class="title-box">
          <p class="line"></p>
          <p class="title">其他方式登录</p>
          <p class="line"></p>
        </div>
        <div class="login-type">
          <img :src="wechatIcon" />
          <img :src="aliIcon" />
          <img :src="douyinIcon" />
          <img :src="qqIcon" />
          <img :src="weiboIcon" />
          <img :src="feishuIcon" />
          <img :src="googleIcon" />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
  import { message } from 'ant-design-vue';
  import { onMounted, onUnmounted, reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { loginApi } from '/@/api/system/login-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { LOGIN_DEVICE_ENUM } from '/@/constants/system/login-device-const';
  import { useUserStore } from '/@/store/modules/system/user';
  import loginQR from '/@/assets/images/login/login-qr.png';
  import leftBg2 from '/@/assets/images/login/left-bg2.png';
  import wechatIcon from '/@/assets/images/login/wechat-icon.png';
  import aliIcon from '/@/assets/images/login/ali-icon.png';
  import douyinIcon from '/@/assets/images/login/douyin-icon.png';
  import qqIcon from '/@/assets/images/login/qq-icon.png';
  import weiboIcon from '/@/assets/images/login/weibo-icon.png';
  import feishuIcon from '/@/assets/images/login/feishu-icon.png';
  import googleIcon from '/@/assets/images/login/google-icon.png';

  import { buildRoutes } from '/@/router/index';
  import { smartSentry } from '/@/lib/smart-sentry';
  import {currentAlgorithm, currentKeys, encryptData} from '/@/lib/encrypt';
  import { localSave } from '/@/utils/local-util.js';
  import LocalStorageKeyConst from '/@/constants/local-storage-key-const.js';
  import { refreshKeys} from '/@/lib/encrypt';
  import { smartAxios } from '/@/lib/axios'; // 正确方式
  //--------------------- 登录表单 ---------------------------------

  const loginForm = reactive({
    loginName: 'admin',
    password: '123456',
    captchaCode: '',
    captchaUuid: '',
    loginDevice: LOGIN_DEVICE_ENUM.PC.value,
  });
  const rules = {
    loginName: [{ required: true, message: '用户名不能为空' }],
    password: [{ required: true, message: '密码不能为空' }],
    captchaCode: [{ required: true, message: '验证码不能为空' }],
  };

  const showPassword = ref(false);
  const router = useRouter();
  const formRef = ref();
  const rememberPwd = ref(false);

  onMounted(() => {
    document.onkeyup = (e) => {
      if (e.keyCode == 13) {
        onLogin();
      }
    };
  });

  onUnmounted(() => {
    document.onkeyup = null;
  });

  //登录
  async function onLogin() {
    formRef.value.validate().then(async () => {
      try {
        SmartLoading.show();
        //获取最新密钥
        await refreshKeys(smartAxios);


        // 加密密码
        const encryptedPassword = encryptData(loginForm.password);
        if (!encryptedPassword) {
          message.error('密码加密失败');
          return;
        }
        // 构建登录参数
        const loginParams = {
          ...loginForm,
          password: encryptedPassword
        };

        // 发送登录请求
        const res = await loginApi.login(loginParams);

        // 停止验证码刷新
        stopRefrestCaptchaInterval();

        // 保存Token到本地
        localSave(LocalStorageKeyConst.USER_TOKEN, res.data.token || '');



        // ------------------ 第三步：后续操作 ------------------
        message.success('登录成功');

        // 更新用户信息到pinia
        useUserStore().setUserLoginInfo(res.data);

        // 构建系统的路由
        buildRoutes();

        // 路由跳转
        router.push('/home');
      } catch (e) {
        if (e.data && e.data.code !== 0) {
          loginForm.captchaCode = '';
          getCaptcha();
        }
        message.error(`登录失败：${e.message}`);
        smartSentry.captureError(e);
      } finally {
        SmartLoading.hide();
      }
    });
  }

  //--------------------- 验证码 ---------------------------------

  const captchaBase64Image = ref('');
  async function getCaptcha() {
    try {
      let captchaResult = await loginApi.getCaptcha();
      captchaBase64Image.value = captchaResult.data.captchaBase64Image;
      loginForm.captchaUuid = captchaResult.data.captchaUuid;
      beginRefrestCaptchaInterval(captchaResult.data.expireSeconds);
    } catch (e) {
      console.log(e);
    }
  }

  let refrestCaptchaInterval = null;
  function beginRefrestCaptchaInterval(expireSeconds) {
    if (refrestCaptchaInterval === null) {
      refrestCaptchaInterval = setInterval(getCaptcha, (expireSeconds - 5) * 1000);
    }
  }

  function stopRefrestCaptchaInterval() {
    if (refrestCaptchaInterval != null) {
      clearInterval(refrestCaptchaInterval);
      refrestCaptchaInterval = null;
    }
  }

  onMounted(getCaptcha);
</script>
<style lang="less" scoped>
  @import './login.less';
</style>
