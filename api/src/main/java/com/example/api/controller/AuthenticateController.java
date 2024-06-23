/**
 * Controller class for handling authentication-related endpoints.
 */
package com.example.api.controller;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.DTO.AuthenticateDTO.LoginRequestDTO;
import com.example.api.DTO.AuthenticateDTO.RefreshRequestDTO;
import com.example.api.DTO.AuthenticateDTO.RegisterRequestDTO;
import com.example.api.DTO.AuthenticateDTO.ResetPasswordRequestDTO;
import com.example.api.DTO.AuthenticateDTO.SendUserCodeDTO;
import com.example.api.DTO.AuthenticateDTO.TokenRequestDTO;
import com.example.api.config.SecretConfig;
import com.example.api.config.CustomHandler.CustomBadRequestHandler;
import com.example.api.model.database.User;
import com.example.api.model.enums.Role;
import com.example.api.model.enums.Type;
import com.example.api.service.AclService;
import com.example.api.service.AuthenticationService;
import com.example.api.service.MailService;
import com.example.api.service.MqttService;
import com.example.api.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller class for handling authentication-related endpoints.
 * 
 * @RestController Indicates that the class is a REST controller, capable of
 *                 handling HTTP requests and returning HTTP responses.
 *                 Routes:
 *                 - POST /register: Handles user registration.
 *                 - POST /login: Handles user login.
 *                 - POST /validate_accout: Handles sending a validation account
 *                 code.
 *                 - POST /validate: Handles validating the account with a code.
 *                 - POST /forget_password: Handles sending a reset password
 *                 code.
 *                 - POST /reset_password: Handles resetting the password with a
 *                 code.
 */
@RestController
public class AuthenticateController extends CustomBadRequestHandler {

    @Autowired
    SecretConfig secretConfig;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    @Autowired
    AclService aclService;

    @Autowired
    MqttService mqttService;

    /**
     * Sends a validation account mail to the user.
     *
     * @param token the validation token
     * @param user  the user
     * @return a map containing the email service response
     */
    private HashMap<String, Object> sendValidateAccountMail(String token, User user) {
        try {
            mailService.sendSimpleMessage(user.getEmail(), "Validate your account",
                    secretConfig.getMAIL_BODY_HOSTNAME() + "/validate?token=" + token);
            return new HashMap<>() {
                {
                    put("message", "ok");
                }
            };
        } catch (MailException exception) {
            return new HashMap<>() {
                {
                    put("error", "mail service unavailable");
                }
            };
        }
    }

    /**
     * Sends a forget password mail to the user.
     *
     * @param token the forget password token
     * @param user  the user
     * @return a map containing the email service response
     */
    private HashMap<String, Object> sendForgetPasswordMail(String token, User user) {
        try {
            mailService.sendSimpleMessage(user.getEmail(), "Forget password",
                    secretConfig.getMAIL_BODY_HOSTNAME() + "/forget_password?token=" + token);
            return new HashMap<>() {
                {
                    put("message", "ok");
                }
            };
        } catch (MailException exception) {
            return new HashMap<>() {
                {
                    put("error", "mail service unavailable");
                }
            };
        }
    }

    /**
     * Sends a user code based on email and type.
     *
     * @param email the user's email
     * @param type  the type of code to be sent
     * @return a ResponseEntity containing the response
     */
    private ResponseEntity<HashMap<String, Object>> sendUserCode(String email, Type type) {
        Optional<User> searchUser = userService.findByEmail(email);
        if (!searchUser.isPresent())
            return new ResponseEntity<>(new HashMap<>() {
                {
                    put("error", "user not found");
                }
            }, HttpStatus.BAD_REQUEST);

        User user = searchUser.get();
        HashMap<String, Object> codeToken = authenticationService.createValidateAccoutCodeByUser(user, type);
        if (codeToken.containsKey("error"))
            return new ResponseEntity<>(codeToken, HttpStatus.BAD_REQUEST);

        HashMap<String, Object> emailServiceResponse;
        if (type == Type.FORGET_PASSWORD) {
            emailServiceResponse = sendForgetPasswordMail((String) codeToken.get("token"), user);
        } else {
            emailServiceResponse = sendValidateAccountMail((String) codeToken.get("token"), user);
        }
        return new ResponseEntity<>(emailServiceResponse,
                emailServiceResponse.containsKey("error") ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.OK);
    }

    /**
     * Handles user registration.
     *
     * @param user   the user to be registered
     * @param result the BindingResult containing validation errors
     * @return a ResponseEntity containing the response
     */
    @PostMapping("/register")
    public ResponseEntity<HashMap<String, Object>> register(@RequestBody @Valid RegisterRequestDTO userRequest,
            BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setImage(userRequest.getImage());
        user.setPassword(userRequest.getPassword());
        user.setUsername(userRequest.getUsername());
        user.setRole(userRequest.getRole());

        HashMap<String, Object> response = authenticationService.register(user);
        if (!response.containsKey("access_token"))
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        if (user.getRole() != Role.REGISTER)
            return new ResponseEntity<>(response, HttpStatus.OK);

        HashMap<String, Object> codeToken = authenticationService.createValidateAccoutCodeByUser(user,
                Type.VALIDATE_ACCOUNT);
        if (codeToken.containsKey("error"))
            return new ResponseEntity<>(codeToken, HttpStatus.BAD_REQUEST);

        HashMap<String, Object> emailServiceResponse = sendValidateAccountMail((String) codeToken.get("token"), user);
        return new ResponseEntity<>(emailServiceResponse,
                emailServiceResponse.containsKey("error") ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.OK);
    }

    /**
     * Handles user login.
     *
     * @param request the login request
     * @param result  the BindingResult containing validation errors
     * @return a ResponseEntity containing the response
     */
    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody @Valid LoginRequestDTO request,
            BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);
        HashMap<String, Object> response = authenticationService.authenticate(request);
        return new ResponseEntity<>(response,
                response.containsKey("access_token") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * Retrieves the MQTT password for the authenticated user.
     *
     * @param user The authenticated user.
     * @return ResponseEntity<HashMap<String, Object>> A response entity containing
     *         the MQTT password.
     */
    @GetMapping("/login")
    public ResponseEntity<HashMap<String, Object>> getMqttPassword(@AuthenticationPrincipal User user) {
        HashMap<String, Object> cacheResponse = aclService.clearAndFillAclCacheForUser(user);
        if (cacheResponse.containsKey("error"))
            return new ResponseEntity<>(cacheResponse, HttpStatus.SERVICE_UNAVAILABLE);

        HashMap<String, Object> response = mqttService.createOrUpdateTopicForUser(user);
        if (cacheResponse.containsKey("error"))
            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles sending a validation account code.
     *
     * @param request the SendUserCodeDTO containing the user's email
     * @param result  the BindingResult containing validation errors
     * @return a ResponseEntity containing the response
     */
    @PostMapping("/validate_accout")
    public ResponseEntity<HashMap<String, Object>> sendvalidateAccountCode(@RequestBody @Valid SendUserCodeDTO request,
            BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);
        return sendUserCode(request.getMail(), Type.VALIDATE_ACCOUNT);
    }

    /**
     * Handles validating the account with a code.
     *
     * @param token  the TokenRequestDTO containing the validation token
     * @param result the BindingResult containing validation errors
     * @return a ResponseEntity containing the response
     */
    @PostMapping("/validate")
    public ResponseEntity<HashMap<String, Object>> validateAccountwithCode(@RequestBody @Valid TokenRequestDTO token,
            BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);
        HashMap<String, Object> authResult = authenticationService.readValidateAccoutCode(token.getToken());
        return new ResponseEntity<>(authResult,
                authResult.containsKey("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    /**
     * Handles sending a reset password code.
     *
     * @param request the SendUserCodeDTO containing the user's email
     * @param result  the BindingResult containing validation errors
     * @return a ResponseEntity containing the response
     */
    @PostMapping("/forget_password")
    public ResponseEntity<HashMap<String, Object>> sendResetPasswordCode(@RequestBody @Valid SendUserCodeDTO request,
            BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);
        return sendUserCode(request.getMail(), Type.FORGET_PASSWORD);
    }

    /**
     * Handles resetting the password with a code.
     *
     * @param request the ResetPasswordRequestDTO containing the reset password
     *                token and new password
     * @param result  the BindingResult containing validation errors
     * @return a ResponseEntity containing the response
     */
    @PostMapping("/reset_password")
    public ResponseEntity<HashMap<String, Object>> resetPasswordCode(
            @RequestBody @Valid ResetPasswordRequestDTO request, BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);
        HashMap<String, Object> authResult = authenticationService.readForgetPasswordCode(request.getToken(),
                request.getPassword());
        return new ResponseEntity<>(authResult,
                authResult.containsKey("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<HashMap<String, Object>> refresh(@RequestBody @Valid RefreshRequestDTO request,
            BindingResult result) {
        if (result.hasErrors())
            return handleBadRequest(result);
        HashMap<String, Object> authResult = authenticationService.refresh(request.getRefresh());
        return new ResponseEntity<>(authResult,
                authResult.containsKey("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

}
