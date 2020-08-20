package com.reimu.controller.upload;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.reimu.base.response.HttpDataContainer;
import com.reimu.util.DateUtils;
import com.reimu.util.EmptyUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class UploaderController extends Upload {

    @Value("${project.staticFile.location}")
    private String SAVE_PATH;

    @Value("${project.webPath}")
    private String WEB_PATH;

    //@RequiresAuthentication
    @PostMapping(value = "/upload")
    public HttpDataContainer uploadFile(@RequestParam MultipartFile file,
                                        @RequestParam(required = false) String category,
                                        HttpServletRequest request) {
        String fileUrl;
        if (EmptyUtils.isEmpty(category) || !category.matches("^[A-Za-z0-9]+$")) category = "default/";  // 防止路径注入
        else category += "/";
        String dateStr = DateUtils.formatDate(new Date(), "yyyy_MM_dd") + "/";
        try {
            fileUrl = super.upload(file, SAVE_PATH + category + dateStr, false, request, false, false);
        } catch (Exception e) {
            return HttpDataContainer.create(-1, e.getMessage());
        }
        if (fileUrl == null) {
            return HttpDataContainer.create(-1, "请上传允许格式的文件");
        }
        fileUrl = fileUrl.replace(SAVE_PATH, WEB_PATH);
        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS, fileUrl,null);
    }


//
//    @ResponseBody
//    @RequiresAuthentication
//    @RequestMapping(value = "/upload/del", method = {RequestMethod.POST})
//    public HttpDataContainer uploadFile(@RequestParam Integer resid,
//                                        HttpServletRequest request) {
//        UploadResEnt file = uploadService.getFileEnt(resid);
//        if (file == null) {
//            return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS, false);
//        }
//        boolean result = this.removeFile(request, file.getUrl());
//        if (result) {
//            uploadService.delFileRecord(resid);
//        }
//        return HttpDataContainer.create(HttpDataContainer.STATUS_SUCCESS, result);
//    }
//
//
//    @RequestMapping(value = "/download/{resid}", method = {RequestMethod.GET})
//    @RequiresAuthentication
//    public void download(@PathVariable Integer resid,
//                            HttpServletRequest request,
//                            HttpServletResponse response) {
//        UploadResEnt fileEnt = uploadService.getFileEnt(resid);
//        if (fileEnt == null) {
//            ResponseHelper.createBody(response).createDataContainer(HttpDataContainer.create(-1, "发生内部错误，请联系管理员。")).printOut();
//        }
//        //得到要下载的文件
//        File file = new File(this.getRealRelativePath(request) + fileEnt.getUrl());
//        //如果文件不存在
//        if (!file.exists()) {
//            ResponseHelper.createBody(response).createDataContainer(HttpDataContainer.create(-1, "下载链接不存在，请联系管理员。")).printOut();
//            return;
//        }
//        //读取要下载的文件，保存到文件输入流
//        try (FileInputStream in = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
//            response.setHeader("content-disposition", "attachment;filename=" + new String((file.getName()).getBytes("UTF-8"), "iso-8859-1"));
//            response.setHeader("Content-Length", file.length() + "");
//            byte[] bytes = new byte[2048];
//            while (in.read(bytes) > 0) {
//                os.write(bytes);
//            }
//        } catch (IOException e) {
//            ResponseHelper.createBody(response).createDataContainer(HttpDataContainer.create(-1, "发生内部错误，请联系管理员。")).printOut();
//        }
//    }
}
