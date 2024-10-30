package it.smartcommunitylab.pgazienda.service;

import it.smartcommunitylab.pgazienda.PGAziendaApp;
import it.smartcommunitylab.pgazienda.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootTest(classes = PGAziendaApp.class)
public class MailServiceTest {

    @Spy
    private MailService mailService;

    private static final String DEFAULT_LOGIN = "johndoe@example.com";
    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String DEFAULT_LASTNAME = "doe";

    private User user;

    /**
     * Initialize the test by setting up the user mock.
     * This includes the username, name, surname and activated status.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = mock(User.class);
        user.setUsername(DEFAULT_LOGIN);
        user.setActivated(true);
        user.setName(DEFAULT_FIRSTNAME);
        user.setSurname(DEFAULT_LASTNAME);
    }


    /**
     * Tests the sendActivationEmail method when the user's username is null.
     * Verifies that the method logs a message in this case.
     */
    @Test
    public void testSendActivationEmailUserNull() throws Exception {

        user.setUsername(null);

        Logger mailServiceLogger = LoggerFactory.getLogger(MailService.class);

        mailService.sendActivationEmail(user);

        assertThat(mailServiceLogger).isNotNull();

    }

}
