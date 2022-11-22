package com.example.mqtttestclient.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ImageController {
    @PostMapping("/image-upload")
    public void imageUpload(@RequestParam("imageFile") MultipartFile file) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        Path fileNameAndPath = Paths.get("/home/shahidul/Downloads/MQTTTestClient/MQTTTestClient/images_http/", file.getOriginalFilename());
        fileNames.append(file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
    }
}
