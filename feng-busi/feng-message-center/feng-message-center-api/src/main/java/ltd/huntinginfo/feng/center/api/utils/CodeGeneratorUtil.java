package ltd.huntinginfo.feng.center.api.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import cn.hutool.core.lang.UUID;
import cn.hutool.extra.qrcode.QrCodeUtil;

public class CodeGeneratorUtil {
    
    private static final int MAX_UUID_LENGTH = 30;

    // 设备码生成（ZB前缀）
    static public String DeviceCodeGenerator(Integer length) {
        validateLength(length);
        return "ZB" + getRandomUUIDSubstring(length - 2);
    }
    
    // 区域码生成（QY前缀）
    static public String ZoneCodeGenerator(Integer length) {
        validateLength(length);
        return "QY" + getRandomUUIDSubstring(length - 2);
    }
    
    // 位置码生成（CS前缀）
    static public String PlaceCodeGenerator(Integer length) {
        validateLength(length);
        return "CS" + getRandomUUIDSubstring(length - 2);
    }
    
    // 生成条形码（默认尺寸）
    static public String BarcodeGenerator(String code) throws IOException {
        return BarcodeGenerator(code, 300, 100);
    }
    
    // 生成条形码（自定义尺寸，返回Base64）
    static public String BarcodeGenerator(String code, int width, int height) throws IOException {
        validateCodeAndSize(code, width, height);
        
        Code128Writer writer = new Code128Writer();
        BitMatrix matrix = writer.encode(code, BarcodeFormat.CODE_128, width, height);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }
    
    // 生成二维码（返回Base64）
    static public String QrcodeGenerator(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("编码内容不能为空");
        }
        byte[] qrCodeBytes = QrCodeUtil.generatePng(code, 300, 300);
        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }
    
    //--- 私有方法 ---//
    static private void validateLength(Integer length) {
        if (length == null || length <= 2 || length > MAX_UUID_LENGTH) {
            throw new IllegalArgumentException("长度必须为5-" + MAX_UUID_LENGTH + "之间的整数");
        }
    }
    
    static private String getRandomUUIDSubstring(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }
    
    static private void validateCodeAndSize(String code, int width, int height) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("编码内容不能为空");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("宽度和高度必须为正整数");
        }
    }
}