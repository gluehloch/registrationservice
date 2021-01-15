package de.awtools.registration.mail;

import de.awtools.registration.HttpConst;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailController {

    private final SendMailService sendMailService;

    public MailController(SendMailService sendMailService) {
        this.sendMailService = sendMailService;
    }

    @ApiOperation(value = "submit", notes = "Starts the registration process")
    @CrossOrigin
    @PostMapping(path = "/submit", headers = { HttpConst.HEADER }, produces = HttpConst.JSON_UTF_8)
    public ResponseEntity<Boolean> register() {
        final String from = "mail@andre-winkler.de";
        final String recipient = "gluehloch@googlemail.com";
        final String subject = "Subject";
        final String messageText = "Das ist ein Test.";

        sendMailService.sendMail(from, recipient, subject, messageText);

        return ResponseEntity.ok(Boolean.TRUE);
    }

}
