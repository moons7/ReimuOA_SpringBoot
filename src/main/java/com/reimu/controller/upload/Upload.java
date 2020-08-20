package com.reimu.controller.upload;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import com.reimu.util.RandomUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Upload {
    private static final Logger log = LoggerFactory.getLogger(Upload.class);
    private long allowSize = 100L;// 允许文件大小
    private String fileName;
    private String[] fileNames;

    public String getAllowSuffix() {
        return "jpg,png,pdf";
    }

    public long getAllowSize() {
        return allowSize * 1024 * 1024;
    }

    public void setAllowSize(long allowSize) {
        this.allowSize = allowSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String[] getFileNames() {
        return fileNames;
    }

    public void setFileNames(String[] fileNames) {
        this.fileNames = fileNames;
    }


    protected String getFileNameNew() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return fmt.format(new Date()) + "_" + RandomUtils.getRandomNumbers(8);
    }


    protected String getBasePath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath();
    }


    protected String getRealRelativePath(HttpServletRequest request) {
        return request.getSession().getServletContext()
                .getRealPath("/");
    }


    public void uploads(MultipartFile[] files, String destDir,
                        HttpServletRequest request, boolean addHttpUrl) {
        String basePath = getBasePath(request);
        String realPath = getRealRelativePath(request);
        if (addHttpUrl) {
            realPath = basePath + realPath;
        }
        try {
            fileNames = new String[files.length];
            int index = 0;
            for (MultipartFile file : files) {
                fileNames[index++] = saveSingleFile(true,realPath, destDir, file);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("upload file occurred a error: {}", e.getMessage());
            }
        }
    }


    public String upload(MultipartFile file, String destDir, boolean use_context_path,
                         HttpServletRequest request, boolean addHttpUrl, boolean useOriginName) {
        String basePath = getBasePath(request);
        String realPath = getRealRelativePath(request);
        if (addHttpUrl) {
            realPath = basePath + realPath;
        }
        try {
            return useOriginName ? saveFileByOrginName(realPath, destDir, use_context_path, file) : saveSingleFile(use_context_path, realPath, destDir, file);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("upload file occurred a error: {}", e.getMessage());
            }
            return null;
        }
    }


    protected String saveSingleFile(boolean use_context_path, String realPath, String destDir, MultipartFile singleMultipartFile) throws Exception {
        String suffix = singleMultipartFile.getOriginalFilename().substring(
                singleMultipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        int length = getAllowSuffix().indexOf(suffix);
        if (length == -1) {
            throw new Exception("请上传允许格式的文件");
        }
        if (singleMultipartFile.getSize() > getAllowSize()) {
            throw new Exception("您上传的文件大小已经超出范围");
        }
        File destFile = new File(use_context_path ? realPath + destDir : destDir);
        if (!destFile.exists()) {
            destFile.mkdirs();
        }
        String fileNameNew = getFileNameNew() + "." + suffix;
        File f = new File(destFile.getAbsoluteFile() + File.separator + fileNameNew);
        try {
            singleMultipartFile.transferTo(f);
            f.createNewFile();
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("upload file occurred a error: {}", e.getMessage());
            }
        }
        return destDir + fileNameNew;
    }


    protected String saveFileByOrginName(String realPath, String destDir, boolean use_context_path, MultipartFile singleMultipartFile) throws Exception {
        String originalFilename = singleMultipartFile.getOriginalFilename();
        int pointPosition = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(pointPosition + 1);
        int length = getAllowSuffix().indexOf(suffix);
        if (length == -1) {
            throw new Exception("请上传允许格式的文件");
        }
        if (singleMultipartFile.getSize() > getAllowSize()) {
            throw new Exception("您上传的文件大小已经超出范围");
        }
        File destFile = new File(use_context_path ? realPath + destDir : destDir);
        if (!destFile.exists()) {
            destFile.mkdirs();
        }
        File f = new File(destFile.getAbsoluteFile() + File.separator + singleMultipartFile.getOriginalFilename());
        String filePath;
        //处理重名文件
        if (f.exists()) {
            String fileFront = originalFilename.substring(0, pointPosition);
            filePath = fileFront + "_" + RandomUtils.getRandomNumbers(8) + "." + suffix;
            f = new File(destFile.getAbsoluteFile() + File.separator + filePath);
            filePath = destDir + filePath;
        } else {
            if (!destDir.endsWith("/")) destDir += "/";
            filePath = destDir + originalFilename;
        }
        try {
            singleMultipartFile.transferTo(f);
            f.createNewFile();
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("upload file occurred a error: {}", e.getMessage());
            }
        }
        return filePath;
    }


    public List<String> upload(MultipartFile file[], String destDir,
                               HttpServletRequest request, boolean addHttpUrl) {
        String basePath = getBasePath(request);
        String realPath = getRealRelativePath(request);
        if (addHttpUrl) {
            realPath = basePath + realPath;
        }
        List<String> urlList = new ArrayList<>();
        try {
            for (MultipartFile singleFile : file) {
                saveSingleFile(true,realPath, destDir, singleFile);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("upload file occurred a error: {}", e.getMessage());
            }
        } finally {
            return urlList;
        }
    }


    public List<String> upload(List<MultipartFile> fileList, String destDir,
                               HttpServletRequest request, boolean addHttpUrl) {
        String basePath = getBasePath(request);
        String realPath = getRealRelativePath(request);
        if (addHttpUrl) {
            realPath = basePath + realPath;
        }
        List<String> urlList = new ArrayList<>();
        try {
            for (MultipartFile singleFile : fileList) {
                urlList.add(saveSingleFile(true,realPath, destDir, singleFile));
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("upload file occurred a error: {}", e.getMessage());
            }
        } finally {
            return urlList;
        }
    }


    public boolean removeFile(HttpServletRequest request, String url) {
        String realPath = getRealRelativePath(request);
        File file = new File(realPath + url);
        if (!file.exists() && !file.isDirectory()) {
            return false;
        }
        return file.delete();
    }

}