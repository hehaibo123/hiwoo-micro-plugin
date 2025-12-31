package com.hiwoo.util;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA 工具类，支持密钥生成、加密、解密、签名和验签功能
 */
@SuppressWarnings("all")
public class RSAUtil {
    private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);
    private static final int KEYSIZE = 2048;
    private static final int MAX_BLOCK_SIZE = 117; // 统一块大小
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String DEFAULT_SEED = "$%^*%^()(ED47d784sde78";
    private static final String CODE_FORMAT_UTF8 = "UTF-8";
    public static final int MAX_ENCRYPT_BLOCK = 245; // 设置单次加密数据长度的限制
    public static final int MAX_DECRYPT_BLOCK = 256; // 设置单次解密数据长度的限制

    // 硬编码的公钥和私钥
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApZ6Lzam876wDMZbEGQpxXwrcKpovRvdA2AJtc1MoOYqriHLE5AszWfxiKDz2AYZx8a8+80ZMVYiYKPc+SEWQz/u/8AFe9FJxkc662Ue3DxHVNmszPLI0ju8jEGFHRNUB/urN1UKtARID7qhwBXyMDif2MUFJq8n8aEFh9FNNPXUu9A77P0ud15cwHGm+BD8w3JrvgdjmpcSlXEr1TZwKrvGKXV3//jzUDllyEsKFDxG7jWoIfTe3WD3eUtBKgHVCBnG3QwBeRldmfvfLBvoO/qycTSfbkDk76pQyjFeugGRE1n8GOUhsefLboMGgmVbHCavEcdZlB7A5VxTW6ydDywIDAQAB";
    public static final String PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQClnovNqbzvrAMxlsQZCnFfCtwqmi9G90DYAm1zUyg5iquIcsTkCzNZ/GIoPPYBhnHxrz7zRkxViJgo9z5IRZDP+7/wAV70UnGRzrrZR7cPEdU2azM8sjSO7yMQYUdE1QH+6s3VQq0BEgPuqHAFfIwOJ/YxQUmryfxoQWH0U009dS70Dvs/S53XlzAcab4EPzDcmu+B2OalxKVcSvVNnAqu8YpdXf/+PNQOWXISwoUPEbuNagh9N7dYPd5S0EqAdUIGcbdDAF5GV2Z+98sG+g7+rJxNJ9uQOTvqlDKMV66AZETWfwY5SGx58tugwaCZVscJq8Rx1mUHsDlXFNbrJ0PLAgMBAAECggEAUM3s+dfR1cHAASbE4E7HB4794k89t8sUuQ9eJUEyQ/jTPgx44aZRVJsdrPIpOI9G8psFBJ6MycKOMpaGra47PC5g9BFHKb87p98MLMyflNunghiFdByiJgaoveooYn3mrmATMvSIAhivEHbfyN1+W73FkTyAXC99VcRorVMjQVMsUwCl7S5+0QrCPh7tdUzLVfQpIP3Br5jmJZ/LMR0uWXHwMYd+QdbUsJ1j/uFzRHdGTmNtgGD11F6H5x/arRMnXtG9/vVL1f7oJ7NEh4ittgybR+RAU7vTREUEnzdqLhy+xWcqxxqfS0t0OKaqMjee83rhZm/+PTTY3SThyBBe8QKBgQDibt65SbMOgZeb3wFTtH1HnsZE66lNJNlr5IMAMDXxPHA1ZDVb7z7zQs6lKPCbLSaraMWqGbONh7MsTa/C0RhHoPK/Nbmk0ZJrw5VcMZ9/P+nFN04P8bXiedR5beQ+20PTaPmMt/qZgckiSo+PYkkljrrZY/Vkrye4/78FRbFPMwKBgQC7PtBRu6jH1O7T4/wR67F6KCE6wQtZkiCrFJmeZltZ7sKNxBJgjh9Q5SVCnoBmlq3ZW+lPuxfOFZW3w2XNPnHAMv/nNYeFTRGItcTLw3ts2ygf6g1izSoQSVkNQTrUJr2XPXv+aCEO1TOhr3VOFF0VYMMRO8aPLQKxNh6LWhKZCQKBgAja8yTl5/XCRa6X2xJt9ZROdkyNttJIhaQ9jI3+VK25gl3kLF4LDjgvPauh3T8EVgXQFCLh3G7FhtFNvHju+ncaqUOnEzqJ46MJRBcV7bnpQvgugIQJHObdgty42ntG0fE7or7JFyjXtYj8x36BBoFMgqwrx5YHb5NBlTPclOGXAoGAZJBh1ePRzDGCa27grMdHNHJlFmcjr2GaBgg0USxsF6rx9q29qPnru8i18EescmyuxjRk5Bt5t4dEN8PL4yWMdkttwUGwnVk8942YOk+s9FPoAuvzcIL7+m9KUqIb9GEyJg4xj/tg9dRvvIC9oceyDo8/dPwDslXcQkzsrq8bmfECgYBXraWizyXq4wLui3AOYLLGXg9HS0D9lr3nLPURXiyp/QwxFtqfEO+7sCY7xj5O8p2Y6gWqkPYYDrvcoo0HqHIbKuHRtpi1AFE9Dh/4nI8Eo/LZWUBDS3mtRzaPDj+6FPvdgtx13gilvwNOdhZing79+BTgLV5tm4Bi6xXixsirtQ==";

    /**
     * 生成密钥对：Base64 转码的字符串
     *
     * @return 包含公钥和私钥的Base64字符串Map
     */
    public static Map<String, String> initKeyBase64Str() {
        try {
            Map<String, String> map = new HashMap<>(2);
            Map<String, Key> keyMap = initKey();
            PublicKey publicKey = (PublicKey) keyMap.get("PublicKey");
            PrivateKey privateKey = (PrivateKey) keyMap.get("PrivateKey");
            map.put("PublicKey", new String(Base64.getEncoder().encode(publicKey.getEncoded())));
            map.put("PrivateKey", new String(Base64.getEncoder().encode(privateKey.getEncoded())));
            logger.info("生成密钥 = " + JSON.toJSONString(map));
            return map;
        } catch (Exception e) {
            logger.error("生成Base64格式的密钥对失败: {}", e.getMessage());
            throw new RuntimeException("生成Base64格式的密钥对失败", e);
        }
    }

    /**
     * 生成默认密钥
     *
     * @return 密钥对象
     */
    public static Map<String, Key> initKey() {
        return initKey(DEFAULT_SEED);
    }

    /**
     * 生成密钥对
     *
     * @param seed 种子
     * @return 密钥对象
     */
    public static Map<String, Key> initKey(String seed) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            secureRandom.setSeed(seed.getBytes());
            KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keygen.initialize(KEYSIZE, secureRandom);
            KeyPair keys = keygen.genKeyPair();
            PublicKey publicKey = keys.getPublic();
            PrivateKey privateKey = keys.getPrivate();
            Map<String, Key> map = new HashMap<>(2);
            map.put("PublicKey", publicKey);
            map.put("PrivateKey", privateKey);
            logger.info("生成密钥 = " + JSON.toJSONString(map));
            return map;
        } catch (Exception e) {
            logger.error("生成密钥对失败: {}", e.getMessage());
            throw new RuntimeException("生成密钥对失败", e);
        }
    }

    /**
     * 获取公钥对象
     *
     * @param pubKeyStr 公钥字符串
     * @return 公钥对象
     */
    public static PublicKey getPublicKey(String pubKeyStr) {
        try {
            byte[] publicKeys = Base64.getDecoder().decode(pubKeyStr);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeys);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            logger.info("传入的公钥为：【" + pubKeyStr + "】，转义后的公钥为：【" + publicKey + "】");
            return publicKey;
        } catch (Exception e) {
            logger.error("获取公钥失败: {}", e.getMessage());
            throw new RuntimeException("获取公钥失败", e);
        }
    }

    /**
     * 公钥加密
     *
     * @param str 待加密字符串
     * @return 加密后的Base64字符串
     */
    public static String encrypt(String str) {
        try {
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM)
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY)));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // 指定填充方式
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encryptedBytes = cipher.doFinal(str.getBytes(CODE_FORMAT_UTF8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            logger.error("公钥加密失败: {}", e.getMessage());
            throw new RuntimeException("公钥加密失败", e);
        }
    }



    /**
     * RSA 公钥加密（限制长度）
     *
     * @param str 待加密字符串
     * @return 加密后的Base64字符串
     */
    public static String encryptByPublicKey(String str) {
        try {
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM)
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY)));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // 指定填充方式
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] data = str.getBytes(CODE_FORMAT_UTF8);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int inputLength = data.length;
            int offset = 0;

            while (inputLength - offset > 0) {
                int encryptLength = Math.min(inputLength - offset, MAX_ENCRYPT_BLOCK);
                byte[] cache = cipher.doFinal(data, offset, encryptLength);
                outputStream.write(cache, 0, cache.length);
                offset += MAX_ENCRYPT_BLOCK;
            }

            byte[] encryptedBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            logger.error("公钥加密失败: {}", e.getMessage());
            throw new RuntimeException("公钥加密失败", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param str 待解密字符串
     * @return 解密后的字符串
     */
    public static String decrypt(String str) {
        try {
            byte[] inputByte = Base64.getDecoder().decode(str);
            byte[] decoded = Base64.getDecoder().decode(PRIVATE_KEY);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM)
                    .generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // 指定填充方式
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] decryptedBytes = cipher.doFinal(inputByte);
            return new String(decryptedBytes, CODE_FORMAT_UTF8);
        } catch (Exception e) {
            logger.error("私钥解密失败: {}", e.getMessage());
            throw new RuntimeException("私钥解密失败", e);
        }
    }
    /**
     * RSA 私钥解密（限制长度）
     *
     * @param encryStr 待解密字符串
     * @return 解密后的字符串
     */
    public static String decryptByPrivateKey(String encryStr) {
        try {
            byte[] inputByte = Base64.getDecoder().decode(encryStr);
            byte[] decoded = Base64.getDecoder().decode(PRIVATE_KEY);

            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM)
                    .generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // 指定填充方式
            cipher.init(Cipher.DECRYPT_MODE, priKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int inputLength = inputByte.length;
            int offset = 0;

            while (inputLength - offset > 0) {
                int decryptLength = Math.min(inputLength - offset, MAX_DECRYPT_BLOCK);
                byte[] cache = cipher.doFinal(inputByte, offset, decryptLength);
                outputStream.write(cache, 0, cache.length);
                offset += MAX_DECRYPT_BLOCK;
            }

            byte[] decryptedBytes = outputStream.toByteArray();
            return new String(decryptedBytes, CODE_FORMAT_UTF8);
        } catch (Exception e) {
            logger.error("私钥解密失败: {}", e.getMessage());
            throw new RuntimeException("私钥解密失败", e);
        }
    }

    /**
     * 加签：生成报文签名
     *
     * @param content    报文内容
     * @param privateKey 私钥字符串
     * @param encode     编码格式
     * @return 签名字符串
     */
    public static String doSign(String content, String privateKey, String encode) {
        try {
            String unsign = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
            byte[] privateKeys = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeys);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey psbcPrivateKey = keyFactory.generatePrivate(privateKeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(psbcPrivateKey);
            signature.update(unsign.getBytes(encode));
            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            logger.error("生成报文签名出现异常: {}", e.getMessage());
            throw new RuntimeException("生成报文签名出现异常", e);
        }
    }

    /**
     * 验证：验证签名信息
     *
     * @param content   签名报文
     * @param signed    签名信息
     * @param publicKey 公钥对象
     * @param encode    编码格式
     * @return 是否验证通过
     */
    public static boolean doCheck(String content, String signed, PublicKey publicKey, String encode) {
        try {
            String unsigned = Base64.getEncoder().encodeToString(content.getBytes(encode));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(unsigned.getBytes(encode));
            return signature.verify(Base64.getDecoder().decode(signed));
        } catch (Exception e) {
            logger.error("报文验证签名出现异常: {}", e.getMessage());
            throw new RuntimeException("报文验证签名出现异常", e);
        }
    }
}
