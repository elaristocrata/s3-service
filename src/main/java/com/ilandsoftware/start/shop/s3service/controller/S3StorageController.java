package com.ilandsoftware.start.shop.s3service.controller;

import com.ilandsoftware.start.shop.s3service.dto.UpLoadFileResponse;
import com.ilandsoftware.start.shop.s3service.service.S3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("storage-service/api/v1")
public class S3StorageController {
    @Autowired
    private S3StorageService s3StorageService;

    @PostMapping("/upload")
    public ResponseEntity<UpLoadFileResponse> uploadFile(@RequestParam(value = "file") MultipartFile multipartFile) {
        return new ResponseEntity<>(s3StorageService.upLoadFile(multipartFile), HttpStatus.CREATED);

    }

    @GetMapping("/download/{file_name}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable(value = "file_name") String fileName) {
        byte[] data = s3StorageService.downLoadFile(fileName);
        ByteArrayResource byteArrayResource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(byteArrayResource);
    }
}
