package com.ilandsoftware.start.shop.s3service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.ilandsoftware.start.shop.s3service.crosscutting.constants.Constants;
import com.ilandsoftware.start.shop.s3service.dto.UpLoadFileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.ilandsoftware.start.shop.s3service.crosscutting.constants.Constants.ERROR_CREATING_CONTENT;
import static com.ilandsoftware.start.shop.s3service.crosscutting.constants.Constants.ERROR_CREATING_FILE;

@Slf4j
@Service
public class S3StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;
    @Value("${application.bucket.url}")
    private String url;
    @Autowired
    private AmazonS3 s3Client;

    public UpLoadFileResponse upLoadFile(MultipartFile file) {
        File fileObject = convertMultipartFileToFile(file);
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObject));
        fileObject.delete();
        return UpLoadFileResponse.builder().url(url+fileName).build();
    }

    public byte[] downLoadFile(String filename) {
        S3Object s3Object = s3Client.getObject(bucketName, filename);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        System.out.println(s3Object.getObjectMetadata());
        try {
            byte[] content = IOUtils.toByteArray(s3ObjectInputStream);
            return content;
        } catch (IOException ioException) {
            log.error(ERROR_CREATING_CONTENT , ioException);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_CREATING_CONTENT);
        }
    }
    private File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException ioException) {
            log.error(ERROR_CREATING_FILE, ioException);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_CREATING_FILE);
        }

        return convertedFile;
    }
}
