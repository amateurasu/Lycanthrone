package edu.fpt.comp1640.utils.mail;

import lombok.extern.slf4j.Slf4j;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import java.util.Map;

/**
 * A SaslClientFactory that returns instances of OAuth2SaslClient.
 *
 * <p>Only the "XOAUTH2" mechanism is supported. The {@code callbackHandler} is
 * passed to the OAuth2SaslClient. Other parameters are ignored.
 */
@Slf4j
public class OAuth2SaslClientFactory implements SaslClientFactory {
    public static final String OAUTH_TOKEN_PROP = "mail.imaps.sasl.mechanisms.oauth2.oauthToken";

    public SaslClient createSaslClient(
        String[] mechanisms,
        String authorizationId,
        String protocol,
        String serverName,
        Map<String, ?> props,
        CallbackHandler callbackHandler
    ) {
        boolean matchedMechanism = false;
        for (String mechanism : mechanisms) {
            if ("XOAUTH2".equalsIgnoreCase(mechanism)) {
                matchedMechanism = true;
                break;
            }
        }
        if (!matchedMechanism) {
            log.info("Failed to match any mechanisms");
            return null;
        }
        return new OAuth2SaslClient((String) props.get(OAUTH_TOKEN_PROP),
            callbackHandler);
    }

    public String[] getMechanismNames(Map<String, ?> props) {
        return new String[]{"XOAUTH2"};
    }
}
