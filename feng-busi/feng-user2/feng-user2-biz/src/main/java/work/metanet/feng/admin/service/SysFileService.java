package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysFile;
import work.metanet.feng.common.core.util.R;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件管理表(SysFile)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysFileService extends IService<SysFile> {

    /**
     * 上传文件
     *
     * @param file 资源
     * @return
     */
    R uploadFile(MultipartFile file);

    /**
     * 上传文件
     *
     * @param file 资源
     * @param dir  文件存放目录
     * @return
     */
    R uploadFile(MultipartFile file, String dir) throws IOException;

    /**
     * 读取文件
     *
     * @param bucket   桶名称
     * @param fileName 文件名称
     * @param response 输出流
     */
    void getFile(String bucket, String fileName, HttpServletResponse response);

    /**
     * 删除文件
     *
     * @param id
     * @return
     */
    Boolean deleteFile(Long id);

    /**
     * 原文件名上传
     *
     * @param file
     * @return
     */
    R originalUpload(MultipartFile file);

    /**
     * 指定桶文件上传
     * @param file
     * @param bucketName
     * @return
     */
    R bucketNameUpload(MultipartFile file, String bucketName);
}