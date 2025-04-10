import CryptoJS from 'crypto-js';
import CryptoSM from 'sm-crypto';
import JSEncrypt from 'jsencrypt';

export let currentAlgorithm = 'SM4';
//let currentAlgorithm = 'AES';
let rsaPrivateKey = null; // 保存当前会话的RSA私钥
export let currentKeys = {
  AES: '',
  SM4: ''
};
localStorage.setItem('name', '张三')
console.log('测试1'); //
export const refreshKeys = async (axiosInstance) => {
  try {

    //生成RSA密钥对
    const encryptor = new JSEncrypt({ default_key_size: 2048 });
    encryptor.getKey();
    const publicKey = encryptor.getPublicKey();
    rsaPrivateKey = encryptor.getPrivateKey();

    //公钥加密
    const response = await axiosInstance.post('/api/encryption/currentKey', {
      algorithm: currentAlgorithm,
      publicKey: publicKey,
      rsaPrivateKey:rsaPrivateKey//测试使用，实际使用中请删除
    });
    console.log('公钥加密后的密钥:', response.data); // 加密后的密钥

    localStorage.setItem('encryptedKey', response.data);//存储公钥加密后的密钥

    console.log('输出私钥',rsaPrivateKey);//测试使用，实际使用中请删除

    //私钥解密
    encryptor.setPrivateKey(rsaPrivateKey);
    const decryptedKey = encryptor.decrypt(response.data);

    console.log('私钥解密后的密钥:', decryptedKey); // 新增日志

    currentKeys[currentAlgorithm] = decryptedKey;//将解密得到的密钥赋给currentKeys中的生效算法

    //localStorage.setItem('encryptionKey', JSON.stringify(currentKeys[currentAlgorithm]));//存储生效算法的密钥
  } catch (e) {
    console.error('密钥获取失败:', e);
    throw e;
  }
};



function object2string(data) {
  if (typeof data === 'object') {
    return JSON.stringify(data);
  }
  return String(data).replace(/^['"]|['"]$/g, '');
}




const Encryptors = {
  AES: {
    encryptData(data) {
      // if (!currentKeys.AES) {
      //   throw new Error("AES密钥未初始化，请先调用refreshKeys获取密钥");
      // }
      const key = CryptoJS.enc.Utf8.parse(currentKeys[currentAlgorithm]);
      const utf8Data = CryptoJS.enc.Utf8.parse(object2string(data));
      const encrypted = CryptoJS.AES.encrypt(utf8Data, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
      });
      return encrypted.toString();
    },
    decryptData(data) {
      const key = CryptoJS.enc.Utf8.parse(currentKeys[currentAlgorithm]);
      const decrypted = CryptoJS.AES.decrypt(data, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
      });
      return decrypted.toString(CryptoJS.enc.Utf8);
    }
  },
  SM4: {
    encryptData(data) {
      if (!currentKeys[currentAlgorithm]) {
        throw new Error("SM4密钥未初始化，请先调用refreshKeys获取密钥");
      }
      console.log('SM4日志解密得到的密钥:', currentKeys[currentAlgorithm] ); // 新增日志
      const encryptData = CryptoSM.sm4.encrypt(object2string(data), currentKeys[currentAlgorithm]);
      return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(encryptData));
    },
    decryptData(data) {
      const words = CryptoJS.enc.Base64.parse(data);
      const decode64Str = CryptoJS.enc.Utf8.stringify(words);
      return CryptoSM.sm4.decrypt(decode64Str, currentKeys[currentAlgorithm]);
    }
  }
};

export const encryptData = (data) => Encryptors[currentAlgorithm]?.encryptData(data) || null;
export const decryptData = (data) => Encryptors[currentAlgorithm]?.decryptData(data) || null;




