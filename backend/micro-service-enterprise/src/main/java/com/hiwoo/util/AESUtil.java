package com.hiwoo.util;


import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

@SuppressWarnings("all")
public class AESUtil {

    public static final String KEY_ALGORITHM = "AES";
    public static final String algorithmStr = "AES/CBC/PKCS5Padding";
    public static final String aesKeyString = "MjIxMTc5ZThjMjdhYmUxMmNjYWUyNjIzMzNhODdhMjk=";
    public static final String ivString = "7CFjktOL2REF30KHbcsxsQ==";
    private static final String HASH_ALGORITHM = "SHA-256";

    public static final byte[] aesKey = Base64.getDecoder().decode(aesKeyString);
    public static final byte[] iv = Base64.getDecoder().decode(ivString);
    public static boolean initialized = false;

    /**
     * 加密方法
     *
     * @param originalContent 原始内容
     * @return 加密后的内容
     * @throws GeneralSecurityException 加密过程中的异常
     */
    public static String encrypt(String originalContent) throws GeneralSecurityException {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance(algorithmStr);
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey, KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
            byte[] encryptedBytes = cipher.doFinal(originalContent.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes); // 返回URL安全的Base64编码的字符串


        } catch (GeneralSecurityException e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }
    /**
     * Md5加密
     *
     * @param originalContent 原始内容
     * @return 加密后的内容
     */
    public static String md5(String originalContent) {

        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String digestHex = md5.digestHex(originalContent);
        return digestHex;

    }
    /**
     * 解密方法
     *
     * @param content 密文内容
     * @return 解密后的内容
     * @throws GeneralSecurityException 解密过程中的异常
     */
    public static String decrypt(String content) {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance(algorithmStr);
            Key sKeySpec = new SecretKeySpec(aesKey, KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, new IvParameterSpec(iv));

            byte[] contentBytes = Base64.getUrlDecoder().decode(content); // 使用URL安全的Base64解码
            byte[] decryptedBytes = cipher.doFinal(contentBytes);
            return new String(decryptedBytes);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }

    /**
     * 安全提供者初始化
     */
    public static void initialize() {
        if (initialized) return;

        try {
            Security.addProvider(new BouncyCastleProvider());
            initialized = true;
        } catch (SecurityException e) {
            throw new SecurityException("Bouncy Castle provider initialization failed", e);
        }
    }


    /**
     * 生成IV（初始化向量）的算法参数
     *
     * @param iv IV字节数组
     * @return 算法参数
     * @throws Exception 异常
     */
    public static String generateIV() throws Exception {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[16]; // IV 应为 16 字节（128 位）
            secureRandom.nextBytes(iv); // 生成随机的 IV
            AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
            params.init(new IvParameterSpec(iv));

            return Base64.getEncoder().encodeToString(iv); // 返回 Base64 编码的 IV 字符串
        } catch (Exception e) {
            throw new Exception("Failed to generate IV", e);
        }
    }

    /**
     * 生成指定长度的AES密钥
     *
     * @param keySize 密钥长度
     * @return 生成的AES密钥
     * @throws GeneralSecurityException 密钥生成过程中的异常
     */
    public static byte[] generateAESKey(int keySize) throws GeneralSecurityException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM);
            keyGen.init(keySize);
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralSecurityException("AES key generation failed", e);
        }
    }
    /**
     * 将十六进制字符串转换为字节数组
     * @param hexString
     * @return
     */
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
    /**
     * 将字节数组转换为十六进制字符串
     * @param bytes
     * @return
     */
    private static byte[] bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString().getBytes();
    }

}