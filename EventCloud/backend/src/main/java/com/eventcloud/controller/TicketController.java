package com.eventcloud.controller;
import com.eventcloud.service.CurrentUserService;
import com.eventcloud.repository.RegistrationRepository;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
@RestController @RequestMapping("/api/tickets")
public class TicketController {
 private final RegistrationRepository regs;private final CurrentUserService current;
 public TicketController(RegistrationRepository r,CurrentUserService c){regs=r;current=c;}
 @GetMapping(value="/{registrationId}/qr",produces=MediaType.IMAGE_PNG_VALUE)
 public ResponseEntity<byte[]> qr(@PathVariable UUID registrationId)throws Exception{
  var user=current.require();var reg=regs.findById(registrationId).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
  if(!reg.getUser().getId().equals(user.getId())&&user.getRole()!=com.eventcloud.domain.Role.ADMIN&&!reg.getEvent().getOrganizer().getId().equals(user.getId()))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
  var matrix=new QRCodeWriter().encode(reg.getTicketCode(),BarcodeFormat.QR_CODE,320,320);var out=new ByteArrayOutputStream();MatrixToImageWriter.writeToStream(matrix,"PNG",out);
  return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(out.toByteArray());
 }
}
