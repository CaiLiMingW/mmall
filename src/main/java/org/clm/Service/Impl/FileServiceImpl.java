package org.clm.Service.Impl;

import com.google.common.collect.Lists;
import org.clm.Service.IFileService;
import org.clm.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file,String path){
        //获取文件名
        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg
        String uploadFileName = UUID.randomUUID().toString()+"."+fileName;
        logger.info("==========正在上传==========:\n{}\n" +
                "==========本地上传的路径==========\n{}\n" +
                "==========完整文件名==========\n{}\n",fileName,path,uploadFileName);

        File fileDir = new File(path);
        //检查上传的文件夹是否存在
        if(!fileDir.exists()){
            //赋予写入权限
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //创建上传文件对象
        File targetFile = new File(path,uploadFileName);
        try {
            logger.info("==========文件正在上传============");
            file.transferTo(targetFile);
            logger.info("==========文件上传成功============");
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            logger.info("==========文件名==========\n{}\n",targetFile.getName());
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //A:abc.jpg
        //B:abc.jpg
        return targetFile.getName();
    }



}
