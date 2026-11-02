package com.eventcloud.controller;
import com.eventcloud.domain.*;
import com.eventcloud.repository.EventRepository;
import com.eventcloud.service.CurrentUserService;
import com.eventcloud.service.PosterStorageService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@RestController @RequestMapping("/api/events")
public class PosterController {
 private final EventRepository events;private final CurrentUserService current;private final PosterStorageService storage;
 public PosterController(EventRepository e,CurrentUserService c,PosterStorageService storage){events=e;current=c;this.storage=storage;}
 @PostMapping(value="/{id}/poster",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) @Transactional
 public Map<String,String> upload(@PathVariable UUID id,@RequestParam("file") MultipartFile file)throws Exception{
  AppUser user=current.require();
  Event e=events.findByIdForUpdate(id).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
  if(user.getRole()!=Role.ADMIN&&!e.getOrganizer().getId().equals(user.getId()))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
  String type=file.getContentType();if(type==null||!List.of("image/jpeg","image/png","image/webp").contains(type))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Poster must be JPEG, PNG, or WebP");
  if(file.isEmpty()||file.getSize()>8*1024*1024)throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Poster must be between 1 byte and 8 MB");
  String url=storage.store(file,type);e.setPosterUrl(url);return Map.of("posterUrl",url);
 }
}
