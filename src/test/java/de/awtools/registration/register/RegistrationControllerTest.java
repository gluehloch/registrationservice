package de.awtools.registration.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.awtools.registration.config.PersistenceJPAConfig;
import de.awtools.registration.user.ApplicationEntity;
import de.awtools.registration.user.ApplicationRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class })
@ComponentScan("de.awtools.registration")
@WebAppConfiguration
public class RegistrationControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new RegistrationController(registrationService))
                .build();
    }

    @Test
    public void emptyRegistration() throws Exception {
        RegistrationJson registration = new RegistrationJson();

        String requestJson = toString(registration);

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("validationCodes.*", Matchers.hasSize(7)))
                .andExpect(jsonPath("validationCodes.*", Matchers.containsInAnyOrder("UNKNOWN_APPLICATION",
                        "MISSING_ACCEPT_EMAIL", "MISSING_ACCEPT_COOKIE", "NICKNAME_IS_EMPTY", "PASSWORD_TOO_SHORT",
                        "EMAIL_IS_EMPTY", "FIRSTNAME_IS_EMPTY")));
    }

    /**
     * Die Annotation {@code @Transactional} sorgt dafuer, dass die angelegten Testdaten nach Testausfuehrung zurueck
     * gerollt werden.
     * 
     * @throws Exception
     *             ...
     */
    @Test
    @Transactional
    public void validRegistration() throws Exception {
        setupDatabase();
        RegistrationJson registration = new RegistrationJson();
        registration.setApplicationName("application");

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(registration)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("validationCodes.*", Matchers.hasSize(6)))
                .andExpect(jsonPath("validationCodes.*",
                        Matchers.containsInAnyOrder("MISSING_ACCEPT_EMAIL", "MISSING_ACCEPT_COOKIE",
                                "NICKNAME_IS_EMPTY", "PASSWORD_TOO_SHORT", "EMAIL_IS_EMPTY", "FIRSTNAME_IS_EMPTY")));

        registration.setAcceptCookie(true);
        registration.setAcceptMail(true);
        registration.setNickname("Frosch");
        registration.setFirstname("Andre");
        registration.setName("Winkler");
        registration.setPassword("secret-password");
        registration.setEmail("test@test.de");

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(registration)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("validationCodes", Matchers.hasSize(1)))
                .andExpect(jsonPath("validationCodes", Matchers.contains("OK")));
        
        RegistrationEntity registrationEntity = registrationRepository.findByNickname("Frosch")
                .orElseThrow(() -> new IllegalArgumentException());
        assertThat(registrationEntity.getNickname()).isEqualTo("Frosch");
        assertThat(registrationEntity.getApplication()).isEqualTo("application");
    }

    /**
     * Die Annotation {@code @Transactional} sorgt dafuer, dass die angelegten Testdaten nach Testausfuehrung zurueck
     * gerollt werden.
     * 
     * @throws Exception
     *             ...
     */
    @Test
    @Transactional
    public void invalidEmailRegistration() throws Exception {
        setupDatabase();
        RegistrationJson registration = new RegistrationJson();
        registration.setApplicationName("application");

        registration.setAcceptCookie(true);
        registration.setAcceptMail(true);
        registration.setNickname("Frosch");
        registration.setFirstname("Andre");
        registration.setName("Winkler");
        registration.setPassword("secret-password");
        registration.setEmail("test(at)test.de");

        mockMvc.perform(post("/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toString(registration)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("validationCodes", Matchers.hasSize(1)))
                .andExpect(jsonPath("validationCodes", Matchers.contains("EMAIL_IS_NOT_VALID")));
    }
    
    private String toString(RegistrationJson registration) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(registration);
        return requestJson;
    }

    private void setupDatabase() {
        ApplicationEntity application = new ApplicationEntity();
        application.setName("application");
        application.setDescription("Application Description");
        applicationRepository.save(application);
    }

}
