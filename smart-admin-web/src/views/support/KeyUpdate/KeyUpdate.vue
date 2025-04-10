<template>
  <a-card title="更新加密密钥">
    <a-form :model="form" @finish="handleSubmit">
      <a-form-item label="算法类型" name="algorithm" :rules="[{ required: true }]">
        <a-select v-model:value="form.algorithm">
          <a-select-option value="AES">AES</a-select-option>
          <a-select-option value="SM4">SM4</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="新密钥" name="key" :rules="[{ required: true }]">
        <a-input v-model:value="form.key" placeholder="输入32位密钥" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">立即更新</a-button>
      </a-form-item>
    </a-form>
  </a-card>
</template>

<script setup>
import { reactive } from 'vue';
import { message } from 'ant-design-vue';
import { postRequest } from '/@/lib/axios';

const form = reactive({
  algorithm: undefined,
  key: ''
});



const handleSubmit = async () => {
  try {
    const res = await postRequest('/api/encryption/updateKey', {
      algorithmType: form.algorithm,
      newKey: form.key
    }, {
      headers: {
        'Content-Type': 'application/json' // 明确指定JSON格式
      }
    });
    if (res.code === 1) {
      message.success('更新成功');
    } else {
      message.error(res.msg);
    }
  } catch (e) {
    message.error(e.response?.data?.msg || '更新失败');
  }
};







</script>