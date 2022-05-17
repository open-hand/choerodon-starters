package io.choerodon.onlyoffice.service.impl;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import io.choerodon.onlyoffice.service.OnlyOfficeFileHandler;
import io.choerodon.onlyoffice.service.OnlyOfficeService;
import io.choerodon.onlyoffice.utils.KeyDecryptHelper;
import io.choerodon.onlyoffice.vo.DocumentEditCallback;

/**
 * Created by wangxiang on 2022/5/6
 */
@Service
public class OnlyOfficeServiceImpl implements OnlyOfficeService {

    @Autowired(required = false)
    private OnlyOfficeFileHandler onlyOfficeFileHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(OnlyOfficeServiceImpl.class);

    @Override
    public JSONObject saveFile(JSONObject obj) throws IOException {
        //1.文档加载时会调用这个接口，此时status = 1，我们给onlyoffice的服务返回{"error":"0"}的信息，文档才能正常的打开
        //这样onlyoffice会认为回调接口是没问题的，这样就可以在线编辑文档了，否则的话会弹出窗口说明
        // The document could not be saved. Please check connection settings or contact your administrator.
        DocumentEditCallback documentEditCallback = JSONObject.toJavaObject(obj, DocumentEditCallback.class);
        if (documentEditCallback.getStatus() != null
                && (documentEditCallback.getStatus().equals(1)
                || documentEditCallback.getStatus().equals(4))) {

            return getNOErrorJson("0");
        }
        //2.当我们关闭编辑窗口后，十秒钟左右onlyoffice会将它存储的我们的编辑后的文件，，此时status = 2，通过request发给我们，我们需要做的就是接收到文件然后回写该文件。
        // 当状态值仅等于2或3时，存在链路。
        //当点了活动保存时，status=6
        else if (documentEditCallback.getStatus() != null && (documentEditCallback.getStatus().equals(2)
                || documentEditCallback.getStatus().equals(3)
                || documentEditCallback.getStatus().equals(6))) {
            LOGGER.info("====文档编辑完成，现在开始保存编辑后的文档，其下载地址为:{}" + documentEditCallback.getUrl());
            if (StringUtils.isEmpty(documentEditCallback.getTitle()) || StringUtils.isEmpty(documentEditCallback.getUrl())) {
                return getNOErrorJson("0");
            }
            URL url = new URL(documentEditCallback.getUrl());
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            InputStream stream = connection.getInputStream();
            //此处获取到的流即是onlyoffice服务下的文件流。
            MultipartFile multipartFile = getMultipartFile(stream, documentEditCallback.getTitle());
            //业务系统根据规则处理文件
            onlyOfficeFileHandler.fileProcess(multipartFile, documentEditCallback);
            connection.disconnect();
            return getNOErrorJson("0");

        } else {
            return getNOErrorJson("0");
        }
    }

    private JSONObject getNOErrorJson(String s) {
        JSONObject re = new JSONObject();
        re.put("error", s);
        return re;
    }

    public MultipartFile getMultipartFile(InputStream inputStream, String fileName) {
        FileItem fileItem = createFileItem(inputStream, fileName);
        //CommonsMultipartFile是feign对multipartFile的封装，但是要FileItem类对象
        return new CommonsMultipartFile(fileItem);
    }

    /**
     * FileItem类对象创建
     *
     * @param inputStream inputStream
     * @param fileName    fileName
     * @return FileItem
     */
    public FileItem createFileItem(InputStream inputStream, String fileName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "file";
        FileItem item = factory.createItem(textFieldName, MediaType.MULTIPART_FORM_DATA_VALUE, true, fileName);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        OutputStream os = null;
        //使用输出流输出输入流的字节
        try {
            os = item.getOutputStream();
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        } catch (IOException e) {
            LOGGER.error("Stream copy exception", e);
            throw new IllegalArgumentException("文件上传失败");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LOGGER.error("Stream close exception", e);

                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Stream close exception", e);
                }
            }
        }

        return item;
    }

}

