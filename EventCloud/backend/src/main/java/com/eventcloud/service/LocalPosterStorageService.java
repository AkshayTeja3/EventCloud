package com.eventcloud.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.util.UUID;
@Service
@ConditionalOnProperty(name="app.storage-mode", havingValue="local", matchIfMissing=true)
public class LocalPosterStorageService implements PosterStorageService {
 private final Path dir;
 public LocalPosterStorageService(@Value("${app.upload-dir}") String uploadDir){this.dir=Paths.get(uploadDir).toAbsolutePath().normalize();}
 @Override public String store(MultipartFile file,String contentType)throws Exception{
  Files.createDirectories(dir);String ext=contentType.equals("image/png")?".png":contentType.equals("image/webp")?".webp":".jpg";
  String name=UUID.randomUUID()+ext;Path target=dir.resolve(name).normalize();
  if(!target.startsWith(dir))throw new IllegalArgumentException("Invalid filename");
  file.transferTo(target);return "/uploads/"+name;
 }
}
