package org.clm.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by geely
 */
public interface IFileService {

    String upload(MultipartFile file, String path);

}
