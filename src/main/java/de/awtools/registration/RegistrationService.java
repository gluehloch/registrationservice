package de.awtools.registration;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.awtools.registration.RegistrationValidation.ValidationCode;
import de.awtools.registration.password.PasswordEncoderWrapper;

/**
 * Register and confirm a new user.
 * 
 * @author winkler
 */
@Service
public class RegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    private PasswordEncoderWrapper passwordEncoder;
    
    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private TimeService timeService;

    /**
     * Starts the registration process.
     * 
     * @param nickname
     *            nickname
     * @param email
     *            the email address
     * @param password
     *            password
     * @param name
     *            real name
     * @param firstname
     *            real firstname
     * @param application
     *            the application to register for
     * @return RegistrationValidation
     */
    @Transactional
    public RegistrationValidation registerNewUserAccount(String nickname,
            String email, String password, String name, String firstname,
            String application) {

        Application app = applicationRepository.findByName(application);
        if (app == null) {
            LOG.info("Unknown application: [%s]", application);
            return new RegistrationValidation(nickname, ValidationCode.ILLEGAL_ARGUMENTS);
        }

        Registration registrationDefined = registrationRepository.findByNickname(nickname);
        if (registrationDefined != null) {
            LOG.info("Nickname already defined: [%s]", nickname);
            return new RegistrationValidation(nickname, ValidationCode.ILLEGAL_ARGUMENTS);
        }

        LocalDateTime now = timeService.now();

        Registration registration = new Registration();
        registration.setNickname(nickname);
        registration.setFirstname(firstname);
        registration.setName(name);
        registration.setPassword(new Password(passwordEncoder.encode(password)));
        registration.setEmail(new Email(email));
        registration.setCreated(now);
        UUID token = UUID.randomUUID();
        registration.setToken(new Token(token.toString()));
        registration.setApplication(application);
        registration.setConfirmed(false);

        registrationRepository.save(registration);

        return new RegistrationValidation(nickname, ValidationCode.OK);
    }

    @Transactional
    public RegistrationValidation confirmAccount(String token) {
        // TODO Auto-generated method stub
        return null;
    }

    @Transactional
    public RegistrationValidation validate(String nickname, String email,
            String applicationName) {

        Application application = applicationRepository.findByName(applicationName);
        if (application == null) {
            return new RegistrationValidation(nickname, ValidationCode.ILLEGAL_ARGUMENTS);
        }

        Registration registrationDefined = null; 
        
        registrationDefined = registrationRepository.findByNickname(nickname);
        if (registrationDefined != null) {
            return new RegistrationValidation(nickname, ValidationCode.KNOWN_NICKNAME);
        }
        
        registrationDefined = registrationRepository.findByEmail(email);
        if (registrationDefined != null) {
            return new RegistrationValidation(nickname, ValidationCode.KNOWN_MAILADDRESS);
        }
        
        return new RegistrationValidation(nickname, ValidationCode.OK);
    }

}
