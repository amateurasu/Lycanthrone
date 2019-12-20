package org.baeldung.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.baeldung.captcha.ICaptchaService;
import org.baeldung.persistence.model.User;
import org.baeldung.registration.OnRegistrationCompleteEvent;
import org.baeldung.service.IUserService;
import org.baeldung.web.dto.UserDto;
import org.baeldung.web.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Controller
public class RegistrationCaptchaController {
    private final IUserService userService;
    private final ICaptchaService captchaService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public RegistrationCaptchaController(
        IUserService userService, ICaptchaService captchaService, ApplicationEventPublisher eventPublisher
    ) {
        super();
        this.userService = userService;
        this.captchaService = captchaService;
        this.eventPublisher = eventPublisher;
    }

    // Registration

    @RequestMapping(value = "/user/registrationCaptcha", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse captchaRegisterUserAccount(
        @Valid final UserDto accountDto, final HttpServletRequest request
    ) {

        final String response = request.getParameter("g-recaptcha-response");
        captchaService.processResponse(response);

        log.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(
            new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
