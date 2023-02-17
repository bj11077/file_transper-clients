package com.ex.mtp.filetr;

import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileTradeServiceTest {

    @Autowired
    FileTradeService service;


    @Test
    void serviceTest() throws IOException, ParseException {
        service.fileDownload();
    }
}