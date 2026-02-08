package net.engineeringdigest.clinicmgm.service;


import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService implements SmsService{

    private static final Logger log = LoggerFactory.getLogger(TwilioSmsService.class);

    @Value("${twilio.phone-number}")
    private String fromNumber;


    @Value("${sms.enabled:true}")
    private boolean smsEnabled;

    @Override
    public void sendSms(String to, String message) {
        log.info(" SMS to {} : {}", to, message);
        System.out.println("📩 SMS to " + to + " : " + message);



        if (!smsEnabled) {
            log.info(" SMS disabled. Skipping Twilio send.");
            return;
        }


        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromNumber),
                message
        ).create();
    }
}

