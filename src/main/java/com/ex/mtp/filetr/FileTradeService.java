package com.ex.mtp.filetr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Bytes;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileTradeService {

    @Value("${window.filePath}")
    private String filePath;

    @Value("${client.server.path}")
    private String serverPath;


    /**
     * 업로드
     * 1. MultipartFile을 받아서 file로 변환
     * 2. file을 다시 HttpClient로 보내야되는 서버로 보냄
     **/
    public void fileUpload(MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());

        // 포스트요청 주소를 만듬
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost requestPost = new HttpPost(serverPath+"/upload-response");

        // multipartFile을 까서 file로 변환한걸 다시 바디에 담음
        FileBody fileBody = new FileBody(multiPartToFile2(file));
        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
        multipartEntity.addPart("file",fileBody);
        requestPost.setEntity(multipartEntity.build());
        HttpResponse response = client.execute(requestPost);

        System.out.println(response.getCode());
        System.out.println("완");
    }

    public File multiPartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }

    // 이게덜복잡함
    public File multiPartToFile2(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }


    /**
     * Mul2로 요청해서 파일가져와서 다운로드
     *
     **/
    public void fileDownload() throws IOException, ParseException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet requestGet = new HttpGet(serverPath+"/download-response");
        CloseableHttpResponse response = client.execute(requestGet);
        String JSON = EntityUtils.toString(response.getEntity());
        System.out.println(JSON);

        ObjectMapper mapper = new ObjectMapper();
        HashMap<String,Object> hashMap = mapper.readValue(JSON, HashMap.class);
        TradeDto dto = convertMapToDto(hashMap,mapper);

        System.out.println(hashMap.get("file").getClass());
        System.out.println(dto.getFile());

//        JsonNode obj = mapper.readTree(JSON);
//        JsonNode jsonNode = obj.get("file");
//        System.out.println(jsonNode.toString());

    }

    //test only
    public  void decodeFile(String encodedfilecontent, String decodedfile) throws IOException {

        decodedfile = "C:\\ss\\sample.zip";
        byte[] decoded = Base64.getDecoder().decode(encodedfilecontent);
        Files.write(Paths.get(decodedfile),decoded);
    }

    public TradeDto convertMapToDto(HashMap<String,Object> map,ObjectMapper mapper){
        if(!map.get("file").getClass().getName().contains("String")){
            List<Byte> file = (List<Byte>) map.get("file");
            String base64String = Base64.getEncoder().encodeToString(Bytes.toArray(file));
            map.put("file",base64String);
        }
        return mapper.convertValue(map,TradeDto.class);
    }
}

