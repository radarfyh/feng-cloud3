package work.metanet.feng.admin.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.services.s3.model.S3Object;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.entity.SysFile;
import work.metanet.feng.admin.mapper.SysFileMapper;
import work.metanet.feng.admin.service.SysFileService;
import work.metanet.feng.admin.util.ImageUtil;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.file.core.FileProperties;
import work.metanet.feng.common.file.core.FileTemplate;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理表(SysFile)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {

    @Autowired
    private FileProperties fileProperties;

    @Autowired
    private FileTemplate fileTemplate;

    /**
     * 默认不带文件夹上传文件
     *
     * @param file 资源
     * @return
     */
    @Override
    public R uploadFile(MultipartFile file) {
        String fileName = IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(file.getOriginalFilename());
        Map<String, String> resultMap = new HashMap<>(4);
        resultMap.put("bucketName", fileProperties.getBucketName());
        resultMap.put("fileName", fileName);
        // 判断上传文件是否为空
        if (null == file || 0 == file.getSize()) {
            return R.failed("上传文件不能为空");
        }
        // 拿到文件后缀名，例如：png
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (!isPictures(suffix) && !isFileType(suffix)) {
            return R.failed("不支持该格式的文件上传");
        }
        try {
            //判断是否是图片,判断是否超过了 1M,超过则考虑压缩
            MultipartFile multipartFile = isPictureSize(file, fileName);
            // 开始上传
            fileTemplate.putObject(fileProperties.getBucketName(), fileName, multipartFile.getInputStream());
            // 文件管理数据记录,收集管理追踪文件
            String original = CharsetUtil.convert(file.getOriginalFilename(), CharsetUtil.CHARSET_ISO_8859_1, CharsetUtil.CHARSET_UTF_8);
            fileLog(multipartFile, original);
            //绝对路径改为相对路径，通过前端路径拼接
            String fileUrl = fileProperties.getBucketName() + "/" + fileName;
            resultMap.put("url", fileUrl);
        } catch (Exception e) {
            log.error("上传失败", e);
            return R.failed(e.getLocalizedMessage());
        }
        return R.ok(resultMap);
    }

    /**
     * 自定义文件夹上传文件
     *
     * @param mulFile 资源
     * @param dir     文件存放目录
     * @return
     */
    @Override
    public R uploadFile(MultipartFile mulFile, String dir) {
        String fileName = IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(mulFile.getOriginalFilename());
        Map<String, String> resultMap = new HashMap<>(4);
        resultMap.put("bucketName", fileProperties.getBucketName());
        resultMap.put("fileName", fileName);
        String key = "/" + dir + "/" + fileName;//文件上传添加文件夹
        // 判断上传文件是否为空
        if (null == mulFile || 0 == mulFile.getSize()) {
            return R.failed("上传文件不能为空");
        }
        // 拿到文件后缀名，例如：png
        String suffix = mulFile.getOriginalFilename().substring(mulFile.getOriginalFilename().lastIndexOf(".") + 1);
        if (!isPictures(suffix) && !isFileType(suffix)) {
            return R.failed("不支持该格式的文件上传");
        }
        try {
            //判断是否是图片,判断是否超过了 100K，超过 则考虑压缩
            MultipartFile multipartFile = isPictureSize(mulFile, fileName);
            // 开始上传
            fileTemplate.putObject(fileProperties.getBucketName(), key, multipartFile.getInputStream());
            // 文件管理数据记录,收集管理追踪文件
            String original = CharsetUtil.convert(mulFile.getOriginalFilename(), CharsetUtil.CHARSET_ISO_8859_1, CharsetUtil.CHARSET_UTF_8);
            fileLog(multipartFile, original);
            //绝对路径改为相对路径，通过前端路径拼接
            String fileUrl = fileProperties.getBucketName() + "/" + fileName;
            resultMap.put("url", fileUrl);
        } catch (Exception e) {
            log.error("上传失败", e);
            return R.failed(e.getLocalizedMessage());
        }
        return R.ok(resultMap);
    }

    /**
     * 读取文件
     *
     * @param bucket
     * @param fileName
     * @param response
     */
    @Override
    public void getFile(String bucket, String fileName, HttpServletResponse response) {
        try (S3Object s3Object = fileTemplate.getObject(bucket, fileName)) {
            response.setContentType("application/octet-stream; charset=UTF-8");
            IoUtil.copy(s3Object.getObjectContent(), response.getOutputStream());
        } catch (Exception e) {
            log.error("文件读取异常: {}", e.getLocalizedMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param id
     * @return
     */
    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFile(Long id) {
        SysFile file = this.getById(id);
        fileTemplate.removeObject(fileProperties.getBucketName(), file.getFileName());
        return this.removeById(id);
    }

    @Override
    public R originalUpload(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String ossFileName = IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(fileName);
        Map<String, String> resultMap = new HashMap<>(4);
        resultMap.put("bucketName", fileProperties.getBucketName());
        resultMap.put("fileName", fileName);
        // 判断上传文件是否为空
        if (null == file || 0 == file.getSize()) {
            return R.failed("上传文件不能为空");
        }
        // 拿到文件后缀名，例如：png
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (!isFileType(suffix)) {
            return R.failed("不支持该格式的文件上传");
        }
        //文件大小限制，超过50M报错
        if ((1024 * 1024 * 50) < file.getSize()) {
            return R.failed("该文件过大，最大不超过50M");
        }
        try {
            // 开始上传
            fileTemplate.putObject(fileProperties.getBucketName(), ossFileName, file.getInputStream());
            // 文件管理数据记录,收集管理追踪文件
            String original = CharsetUtil.convert(file.getOriginalFilename(), CharsetUtil.CHARSET_ISO_8859_1, CharsetUtil.CHARSET_UTF_8);
            fileLog(file, original);
            //绝对路径改为相对路径，通过前端路径拼接
            String fileUrl = fileProperties.getBucketName() + "/" + fileName;
            resultMap.put("url", fileUrl);
        } catch (Exception e) {
            log.error("上传失败", e);
            return R.failed(e.getLocalizedMessage());
        }
        return R.ok(resultMap);
    }

    @Override
    public R bucketNameUpload(MultipartFile file, String bucketName) {
        String fileName = IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(file.getOriginalFilename());
        fileProperties.setBucketName(bucketName);
        Map<String, String> resultMap = new HashMap<>(4);
        resultMap.put("bucketName", bucketName);
        resultMap.put("fileName", fileName);
        // 判断上传文件是否为空
        if (null == file || 0 == file.getSize()) {
            return R.failed("上传文件不能为空");
        }
        // 拿到文件后缀名，例如：png
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (!isPictures(suffix)) {
            return R.failed("不支持该格式的文件上传");
        }
        try {
            //判断是否是图片,判断是否超过了 1M
            MultipartFile multipartFile = isPictureSize(file, fileName);
            // 开始上传
            fileTemplate.putObject(bucketName, fileName, multipartFile.getInputStream());
            // 文件管理数据记录,收集管理追踪文件
            String original = CharsetUtil.convert(file.getOriginalFilename(), CharsetUtil.CHARSET_ISO_8859_1, CharsetUtil.CHARSET_UTF_8);
            fileLog(multipartFile, original);
            //绝对路径改为相对路径，通过前端路径拼接
            String fileUrl = bucketName + "/" + fileName;
            resultMap.put("url", fileUrl);
        } catch (Exception e) {
            log.error("上传失败", e);
            return R.failed(e.getLocalizedMessage());
        }
        return R.ok(resultMap);
    }

    /**
     * 文件管理数据记录,收集管理追踪文件
     *
     * @param file     上传文件格式
     * @param original 原文件名
     */
    private void fileLog(MultipartFile file, String original) {
        SysFile sysFile = new SysFile();
        // 新文件名
        String fileName = CharsetUtil.convert(file.getOriginalFilename(), CharsetUtil.CHARSET_ISO_8859_1, CharsetUtil.CHARSET_UTF_8);
        sysFile.setFileName(fileName);
        sysFile.setOriginal(original);
        sysFile.setFileSize(file.getSize());
        sysFile.setType(FileUtil.extName(original));
        sysFile.setBucketName(fileProperties.getBucketName());
        sysFile.setCreateBy(SecurityUtils.getUser().getUsername());
        this.save(sysFile);
    }

    /**
     * 判断是否是图片
     * 判断是否超过了 200K
     */
    public MultipartFile isPictureSize(MultipartFile file, String fileName) throws Exception {
        // 拿到文件后缀名，例如：png
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if ("gif".equals(suffix)) {
            //gif格式的动图无法压缩
            return file;
        }
        if (isPicture(suffix) && (1024 * 1024) < file.getSize()) {
            FileInputStream input = null;
            try {
                File multFile = ImageUtil.multipartFileToFile(file);
                File newFile = new File(fileName);
//                byte[] bytes = PicUtils.compressPicForScale(file.getBytes(), 1024);

                ImageUtil.resizePng(multFile, newFile);
                // 获取输入流
                input = new FileInputStream(newFile);
                // 转为 MultipartFile
                file = new MockMultipartFile("file", newFile.getName(), "text/plain", input);
                // 上传成功后删除本地临时文件
                input.close();
                multFile.delete();
                newFile.delete();

            } catch (IOException e) {
                input.close();
                throw new IOException(e);
            }
        }
        // 不需要压缩，直接上传
        return file;
    }

    /**
     * 判断文件是否为图片
     */
    public boolean isPictures(String imgName) {
        boolean flag = false;
        //转换小写
        imgName = imgName.toLowerCase();
        if (StrUtil.isBlank(imgName)) {
            return false;
        }
        String[] arr = {"bmp", "dib", "gif", "jfif", "jpe", "jpeg", "jpg", "png", "tif", "tiff", "ico", "pdf", "mp3"};
        for (String item : arr) {
            if (item.equals(imgName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断文件是否为图片
     */
    public boolean isPicture(String imgName) {
        boolean flag = false;
        //转换小写
        imgName = imgName.toLowerCase();
        if (StrUtil.isBlank(imgName)) {
            return false;
        }
        String[] arr = {"bmp", "dib", "gif", "jfif", "jpe", "jpeg", "jpg", "png", "tif", "tiff", "ico"};
        for (String item : arr) {
            if (item.equals(imgName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断文件是否为pdf或word或ppt
     */
    public boolean isFileType(String fileType) {
        boolean flag = false;
        //转换小写
        fileType = fileType.toLowerCase();
        if (StrUtil.isBlank(fileType)) {
            return false;
        }
        String[] arr = {"md","pdf", "doc", "docx", "txt", "xls", "xlsx", "ppt", "pptx"};
        for (String item : arr) {
            if (item.equals(fileType)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}