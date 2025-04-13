<template>
  <a-card title="更新加密密钥">
    <a-alert v-if="activeAlgorithms.length > 0" :message="`请更新系统当前启用的算法：${activeAlgorithms.join(', ')}`" type="info" show-icon />
    <br>
    <a-form :model="form" @finish="handleSubmit">
      <a-form-item label="算法类型" name="algorithm" :rules="[{ required: true }]">
        <a-select v-model:value="form.algorithm">
          <a-select-option value="AES">AES</a-select-option>
          <a-select-option value="SM4">SM4</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="新密钥" name="key" :rules="[{ required: true }]">
        <a-input-group compact>
          <a-input
              v-model:value="form.key"
              :placeholder="form.algorithm === 'SM4' ? '32位十六进制密钥' : '16/24/32位密钥'"
              style="width: calc(100% - 100px)"
          />
          <a-button @click="generateKey" style="width: 100px">生成密钥</a-button>
        </a-input-group>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">立即更新</a-button>
      </a-form-item>
    </a-form>
  </a-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { postRequest } from '/@/lib/axios';
import CryptoJS from 'crypto-js';

const form = reactive({
  algorithm: undefined,
  key: ''
});

const activeAlgorithms = ref([]);

// 从后端获取激活的算法（POST请求）
const refreshActiveAlgorithms = async () => {
  try {
    const res = await postRequest('/api/encryption/activeAlgorithms');
    if (res.data && Array.isArray(res.data)) {
      activeAlgorithms.value = res.data;
    }
  } catch (e) {
    console.error('获取激活算法失败:', e);
  }
};

const generateKey = () => {
  if (!form.algorithm) {
    message.error('请先选择算法类型');
    return;
  }

  try {
    if (form.algorithm === 'AES') {
      // 生成符合规范的AES密钥（16/24/32位）
      const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      const keyLength = 32; // AES-256
      form.key = Array.from(
          { length: keyLength },
          () => charset.charAt(Math.floor(Math.random() * charset.length)))
              .join('');
    } else if (form.algorithm === 'SM4') {
      // 生成符合国密标准的SM4密钥（32位HEX）
      const sm4KeyBytes = CryptoJS.lib.WordArray.random(16);
      form.key = CryptoJS.enc.Hex.stringify(sm4KeyBytes);
    }
  } catch (error) {
    message.error('密钥生成失败');
    console.error('密钥生成错误:', error);
  }
};

const handleSubmit = async () => {
  try {
    const res = await postRequest('/api/encryption/updateKey', {
      algorithmType: form.algorithm,
      newKey: form.key
    });

    if (res.code === 0 && res.ok) {
      message.success('更新成功');
      await refreshActiveAlgorithms();
    } else {
      message.error(res.msg);
    }
  } catch (e) {
    message.error(e.response?.data?.msg || '更新失败');
  }
};

onMounted(() => {
  refreshActiveAlgorithms();
});
</script>