package com.api_orcafacil.service;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api_orcafacil.exception.BusinessException;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class AnexoService {

    private final S3Client s3Client;
    private final String bucket;
    private final String publicBaseUrl;
    private final boolean enabled;

    public AnexoService(
            @Value("${app.s3.bucket}") String bucket,
            @Value("${app.s3.endpoint}") String endpoint,
            @Value("${app.s3.region}") String region,
            @Value("${app.s3.access-key:}") String accessKey,
            @Value("${app.s3.secret-key:}") String secretKey) {
        this.bucket = bucket;
        this.publicBaseUrl = endpoint.replace("https://", "https://" + bucket + ".")
                .replace("http://", "http://" + bucket + ".");
        if (accessKey == null || accessKey.isBlank() || secretKey == null || secretKey.isBlank()) {
            this.s3Client = null;
            this.enabled = false;
        } else {
            this.enabled = true;
            this.s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();
        }
    }

    public String uploadFotoPerfil(Long idUsuario, MultipartFile file) throws IOException {
        validarS3();
        String key = "perfil/" + idUsuario + "/" + file.getOriginalFilename();
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build(), RequestBody.fromBytes(file.getBytes()));
        return publicBaseUrl + "/" + key;
    }

    public void removerFotoPerfil(String fotoUrl) {
        if (!enabled || fotoUrl == null || fotoUrl.isBlank()) {
            return;
        }
        String key = extrairChave(fotoUrl);
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }

    private void validarS3() {
        if (!enabled) {
            throw new BusinessException("Armazenamento S3 nao configurado");
        }
    }

    private String extrairChave(String url) {
        int idx = url.indexOf(bucket);
        if (idx >= 0) {
            return url.substring(url.indexOf('/', idx + bucket.length()) + 1);
        }
        return url.substring(url.lastIndexOf('/') > url.indexOf("://") ? url.indexOf('/', url.indexOf("://") + 3) : 0);
    }
}
