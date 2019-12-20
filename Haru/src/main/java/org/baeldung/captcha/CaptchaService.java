package org.baeldung.captcha;

import lombok.extern.slf4j.Slf4j;
import org.baeldung.web.error.ReCaptchaInvalidException;
import org.baeldung.web.error.ReCaptchaUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Slf4j
@Service("captchaService")
public class CaptchaService implements ICaptchaService {
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    private final HttpServletRequest request;
    private final RestOperations restTemplate;
    private final CaptchaSettings captchaSettings;
    private final ReCaptchaAttemptService reCaptchaAttemptService;

    @Autowired
    public CaptchaService(
        HttpServletRequest request, CaptchaSettings captchaSettings,
        ReCaptchaAttemptService reCaptchaAttemptService, RestOperations restTemplate
    ) {
        this.request = request;
        this.captchaSettings = captchaSettings;
        this.reCaptchaAttemptService = reCaptchaAttemptService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void processResponse(final String response) {
        log.debug("Attempting to validate response {}", response);

        if (reCaptchaAttemptService.isBlocked(getClientIP())) {
            throw new ReCaptchaInvalidException("Client exceeded maximum number of failed attempts");
        }

        if (!responseSanityCheck(response)) {
            throw new ReCaptchaInvalidException("Response contains invalid characters");
        }

        final URI verifyUri = URI.create(
            String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                getReCaptchaSecret(), response, getClientIP()));

        try {
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
            log.debug("Google's response: {} ", googleResponse);

            if (!googleResponse.isSuccess()) {
                if (googleResponse.hasClientError()) {
                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
                throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
            }
        } catch (RestClientException e) {
            throw new ReCaptchaUnavailableException("Registration is unavailable. Please try again later.", e);
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }

    private boolean responseSanityCheck(final String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    @Override
    public String getReCaptchaSite() {
        return captchaSettings.getSite();
    }

    @Override
    public String getReCaptchaSecret() {
        return captchaSettings.getSecret();
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
