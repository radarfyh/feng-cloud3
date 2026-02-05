package work.metanet.feng.common.core.util;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.core.config.TenantOfHeader;
import work.metanet.feng.common.core.constant.CommonConstants;

@Slf4j
public class JasyptUtil {
	
    /**
     * 加密租戶ID
     * @param tenantId
     * @return
     */
    public static String encryptTenant(TenantOfHeader tenant) {
        tenant.setTimestamp(System.currentTimeMillis());

        if (StrUtil.isBlank(tenant.getTenantId())) {
        	//tenant.setTenantId(CommonConstants.DEFAULT_TENANT_ID);
        }        
        String encryptedTenant = encrypt(JSONUtil.toJsonStr(tenant), PBE_ALGORITHMS_MD5_DES, PBE_GENERAL_PASSWORD);
        
        return encryptedTenant;
    }
    
    /**
     * PBE 算法
     */
    public static final String PBE_ALGORITHMS_MD5_DES = "PBEWithMD5AndDES";
    public static final String PBE_ALGORITHMS_MD5_TRIPLEDES = "PBEWITHMD5ANDTRIPLEDES";
    public static final String PBE_ALGORITHMS_SHA1_DESEDE = "PBEWITHSHA1ANDDESEDE";
    public static final String PBE_ALGORITHMS_SHA1_RC2_40 = "PBEWITHSHA1ANDRC2_40";
    public static final String PBE_GENERAL_PASSWORD = "Feng@2025";
 
    /**
     * Jasypt 加密
     *
     * @param encryptedStr 加密字符串
     * @param password     盐值
     * @return
     */
    public static String encrypt(String encryptedStr, String password) {
        return encrypt(encryptedStr, PBE_ALGORITHMS_MD5_DES, password);
    }
 
    /**
     * Jasypt 加密
     *
     * @param encryptedStr 加密字符串
     * @param algorithm    加密算法
     *                     PBE ALGORITHMS: [PBEWITHMD5ANDDES, PBEWITHMD5ANDTRIPLEDES, PBEWITHSHA1ANDDESEDE, PBEWITHSHA1ANDRC2_40]
     * @param password     盐值
     * @return
     */
    public static String encrypt(String encryptedStr, String algorithm, String password) {
        // StandardPBEStringEncryptor、StandardPBEBigDecimalEncryptor、StandardPBEBigIntegerEncryptor、StandardPBEByteEncryptor
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
 
        // 指定加密算法
        config.setAlgorithm(algorithm);
        // 加密盐值
        config.setPassword(password);
        //config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        encryptor.setConfig(config);
 
        // 加密
        return encryptor.encrypt(encryptedStr);
    }
 
    static {
        // 添加 BouncyCastle 提供者以支持 PKCS7 填充
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 解密前端加密的数据 (AES/ECB/PKCS7Padding)
     * @param encryptedValue 加密后的Base64字符串
     * @param key 密钥 (必须16/24/32字节长度)
     * @return 解密后的原始字符串
     * @throws Exception 解密失败时抛出异常
     */
    public static String decryptAES(String encryptedValue, String key) throws Exception {
    	long start = System.nanoTime();
    	
        // 1. 将密钥转换为字节数组（UTF8编码）
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        
        // 2. 创建密钥（ECB模式不需要IV）
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        
        // 3. 初始化Cipher（ECB模式，PKCS7填充）
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        // 4. Base64解码并解密
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedValue);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        // 5. 返回解密后的字符串（UTF8编码）
        String ret = new String(decryptedBytes, StandardCharsets.UTF_8);
        long cost = System.nanoTime() - start;
        log.info("解密耗时: {} ms", cost / 1_000_000);
        
        return ret;
    }
    
    /**
     * AES 加密 (AES/ECB/PKCS7Padding)
     * @param plainText 要加密的原始字符串
     * @param key 密钥 (必须16/24/32字节长度)
     * @return Base64编码的加密字符串
     * @throws Exception 加密失败时抛出异常
     */
    public static String encryptAES(String plainText, String key) throws Exception {
        // 1. 将密钥转换为字节数组（UTF8编码）
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        
        // 2. 创建密钥（ECB模式不需要IV）
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        
        // 3. 初始化Cipher（ECB模式，PKCS7填充）
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        // 4. 加密数据
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        
        // 5. 返回Base64编码的加密结果
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }    
    /**
     * Jasypt 解密
     *
     * @param decryptStr 解密字符串
     * @param password   盐值
     * @return
     */
    public static String decrypt(String decryptStr, String password) {
        return decrypt(decryptStr, PBE_ALGORITHMS_MD5_DES, password);
    }
 
    /**
     * Jasypt 解密
     *
     * @param decryptStr 解密字符串
     * @param algorithm  指定解密算法：解密算法要与加密算法一一对应
     *                   PBE ALGORITHMS: [PBEWITHMD5ANDDES, PBEWITHMD5ANDTRIPLEDES, PBEWITHSHA1ANDDESEDE, PBEWITHSHA1ANDRC2_40]
     * @param password   盐值
     * @return
     */
    public static String decrypt(String decryptStr, String algorithm, String password) {
        // StandardPBEStringEncryptor、StandardPBEBigDecimalEncryptor、StandardPBEBigIntegerEncryptor、StandardPBEByteEncryptor
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
 
        // 指定解密算法：解密算法要与加密算法一一对应
        config.setAlgorithm(algorithm);
        // 加密秘钥
        config.setPassword(password);
        //config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        encryptor.setConfig(config);
 
        // 解密
        return encryptor.decrypt(decryptStr);
    }
 
//    public static void main(String[] args) {
//        String encryptedStr = "I am the string to be encrypted";
//        String algorithm = PBE_ALGORITHMS_SHA1_RC2_40;
//        String password = "salt";
// 
//        String str = JasyptUtil.encrypt(encryptedStr, algorithm, password);
//        System.out.println("加密后的字符串：" + str);
//        System.out.println("解密后的字符串：" + JasyptUtil.decrypt(str, algorithm, password));
//    }
}
