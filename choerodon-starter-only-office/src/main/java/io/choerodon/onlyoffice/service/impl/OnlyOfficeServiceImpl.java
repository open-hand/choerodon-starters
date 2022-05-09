package io.choerodon.onlyoffice.service.impl;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.choerodon.onlyoffice.service.OnlyOfficeService;
import io.choerodon.onlyoffice.vo.DocumentEditCallback;

/**
 * Created by wangxiang on 2022/5/6
 */
@Service
public class OnlyOfficeServiceImpl implements OnlyOfficeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnlyOfficeServiceImpl.class);

    @Override
    public JSONObject saveFile(JSONObject obj) throws IOException {
        //1.文档加载时会调用这个接口，此时status = 1，我们给onlyoffice的服务返回{"error":"0"}的信息，文档才能正常的打开
        //这样onlyoffice会认为回调接口是没问题的，这样就可以在线编辑文档了，否则的话会弹出窗口说明
        // The document could not be saved. Please check connection settings or contact your administrator.
        DocumentEditCallback documentEditCallback = JSONObject.toJavaObject(obj, DocumentEditCallback.class);
        if (documentEditCallback.getStatus() != null && documentEditCallback.getStatus().equals(1)) {

            JSONObject re = new JSONObject();
            re.put("error", "0");
            return re;
        }
        //2.当我们关闭编辑窗口后，十秒钟左右onlyoffice会将它存储的我们的编辑后的文件，，此时status = 2，通过request发给我们，我们需要做的就是接收到文件然后回写该文件。
        // 当状态值仅等于2或3时，存在链路。
        else if (documentEditCallback.getStatus() != null && (documentEditCallback.getStatus().equals(2) || documentEditCallback.getStatus().equals(3))) {
            //保存到文件服务器
            //将文件跟新到数据库
            LOGGER.info("====文档编辑完成，现在开始保存编辑后的文档，其下载地址为:{}" + documentEditCallback.getUrl());
            JSONObject re = new JSONObject();
            re.put("error", "0");
            return re;

        } else {
            JSONObject re = new JSONObject();
            re.put("error", "save file error");
            return re;
        }

//        Integer status = (Integer) obj.get("status");
//        //关闭后保存
//        if (status == 2 || status == 3) {
//            /*
//             * 当我们关闭编辑窗口后，十秒钟左右onlyoffice会将它存储的我们的编辑后的文件，
//             * 此时status = 2，通过request发给我们，我们需要做的就是接收到文件然后回写该文件。
//             * */
//            /*
//             * 定义要与文档存储服务保存的编辑文档的链接。
//             * 当状态值仅等于2或3时，存在链路。
//             * */
//            String downloadUri = (String) obj.get("url");
//            LOGGER.info("====文档编辑完成，现在开始保存编辑后的文档，其下载地址为:{}" + downloadUri);
//            //解析得出文件名
//            //String fileName = downloadUri.substring(downloadUri.lastIndexOf('/')+1);
////            String fileName = request.getParameter("fileName");
//            String fileName = (String) obj.get("title");
//
//            URL url = new URL(downloadUri);
//            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
//            InputStream stream = connection.getInputStream();
//            //通过文件流下载文件
////            //更换为实际的路径,保存硬盘中
////            File savedFile = new File("E:\\onlyoffice\\" + DateTools.getFormatDate("yyyy-MM-dd") + "\\" + fileName);
////            try (FileOutputStream out = new FileOutputStream(savedFile)) {
////                int read;
////                final byte[] bytes = new byte[1024];
////                while ((read = stream.read(bytes)) != -1) {
////                    out.write(bytes, 0, read);
////                }
////                out.flush();
////            }
//            connection.disconnect();
//        }
////        //手动保存时
////        if (status == 6) {
////            /*
////             * 当我们关闭编辑窗口后，十秒钟左右onlyoffice会将它存储的我们的编辑后的文件，
////             * 此时status = 2，通过request发给我们，我们需要做的就是接收到文件然后回写该文件。
////             * */
////            /*
////             * 定义要与文档存储服务保存的编辑文档的链接。
////             * 当状态值仅等于2或3时，存在链路。
////             * */
////            String downloadUri = (String) obj.get("url");
////            System.out.println("====文档编辑完成，现在开始保存编辑后的文档，其下载地址为:" + downloadUri);
////            //解析得出文件名
////            //String fileName = downloadUri.substring(downloadUri.lastIndexOf('/')+1);
////            String fileId = (String) obj.get("key");
////            File_Entity fe = fileService.getFileById(fileId);
////            String fileName = fe.getFileName();
////            String uuidFileName = fe.getUuidFileName();
////            String filePath = fe.getFilePath();
////            System.out.println("====下载的文件名:" + fileName);
////
////            URL url = new URL(downloadUri);
////            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
////            InputStream stream = connection.getInputStream();
////            //保存到原路径中
////            File savedFile = new File(BigFileUploadUtil.getBasePath() + "/" + filePath);
////            //另存为,保存硬盘中,原文件不变
////            //File savedFile = new File("E:\\onlyoffice\\"+DateTools.getFormatDate("yyyy-MM-dd")+"\\"+fileName);
////            if (!savedFile.getParentFile().exists()) {
////                savedFile.getParentFile().mkdirs();
////            }
////            try (FileOutputStream out = new FileOutputStream(savedFile)) {
////                int read;
////                final byte[] bytes = new byte[1024];
////                while ((read = stream.read(bytes)) != -1) {
////                    out.write(bytes, 0, read);
////                }
////                out.flush();
////            }
////            connection.disconnect();
//        }
    }

}

