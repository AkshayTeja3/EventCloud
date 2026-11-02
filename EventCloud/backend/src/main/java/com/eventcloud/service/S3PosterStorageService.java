package com.eventcloud.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.UUID;
@Service
@ConditionalOnProperty(name="app.storage-mode", havingValue="s3")
public class S3PosterStorageService implements PosterStorageService {
 private final String bucket;
 private final String region;
 private final S3Client s3;
 public S3PosterStorageService(@Value("${app.s3.bucket}") String bucket,@Value("${app.s3.region}") String region){
  if(bucket==null||bucket.isBlank())throw new IllegalArgumentException("S3_BUCKET is required when STORAGE_MODE=s3");
  this.bucket=bucket;
  this.region=region;
  this.s3=S3Client.builder().region(Region.of(region)).credentialsProvider(DefaultCredentialsProvider.create()).build();
 }
 @Override
 public String store(MultipartFile file,String contentType)throws Exception{
  String ext=contentType.equals("image/png")?".png":contentType.equals("image/webp")?".webp":".jpg";
  String key="event-posters/"+UUID.randomUUID()+ext;
  s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),
   software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(),file.getSize()));
  return "https://"+bucket+".s3."+region+".amazonaws.com/"+key;
 }
}
