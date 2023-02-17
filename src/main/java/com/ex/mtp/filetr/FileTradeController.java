package com.ex.mtp.filetr;

import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileTradeController {


    private final FileTradeService fileTradeService;

    @GetMapping("/download")
    public String fileDownload() throws IOException, ParseException {
        fileTradeService.fileDownload();
        return "ok";
    }

    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file")MultipartFile file) throws IOException {
        fileTradeService.fileUpload(file);
        return "ok";
    }
}
