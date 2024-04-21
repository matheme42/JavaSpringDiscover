package com.example.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.DTO.AuthenticateDTO.LoginRequestDTO;
import com.example.api.DTO.AuthenticateDTO.ResetPasswordRequestDTO;
import com.example.api.DTO.AuthenticateDTO.SendUserCodeDTO;
import com.example.api.DTO.AuthenticateDTO.TokenRequestDTO;
import com.example.api.config.SecretConfig;
import com.example.api.model.database.User;
import com.example.api.model.enums.Role;
import com.example.api.model.enums.Type;
import com.example.api.service.AuthenticationService;
import com.example.api.service.MailService;
import com.example.api.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthenticateController {

    @Autowired
    SecretConfig secretConfig;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    private ResponseEntity<HashMap<String, Object>>  handleBadRequest(BindingResult bindingResult) {
        List<String> result = new Vector<>();
        for (FieldError e : bindingResult.getFieldErrors()) {
            result.add("field: " + e.getField() + ", rejected value: " + e.getRejectedValue() + ", message: " + e.getDefaultMessage());
        }
        return new ResponseEntity<>(new HashMap<>() {{put("error" , result);}}, HttpStatus.BAD_REQUEST);
    }


    private HashMap<String, Object> sendValidateAccountMail(String token, User user) {
        try {  /// send validate accout email
            mailService.sendSimpleMessage(user.getEmail(), "Validate your accout", secretConfig.getMAIL_BODY_HOSTNAME() + "/validate?token=" + (String)token);
            return new HashMap<>() {{put("message", "ok");}};
        } catch(MailException exception) {
            return new HashMap<>() {{put("error", "mail service unavailable");}};
        }
    }

    private HashMap<String, Object> sendForgetPasswordMail(String token, User user) {
        try {  /// send validate accout email
            mailService.sendSimpleMessage(user.getEmail(), "Forget password", secretConfig.getMAIL_BODY_HOSTNAME() + "/forget_password?token=" + (String)token);
            return new HashMap<>() {{put("message", "ok");}};
        } catch(MailException exception) {
            return new HashMap<>() {{put("error", "mail service unavailable");}};
        }
    }


    @PostMapping("/register")
    public ResponseEntity<HashMap<String, Object>> register(@RequestBody @Valid User user, BindingResult result) {

        if (result.hasErrors()) return handleBadRequest(result);
        HashMap<String, Object> response = authenticationService.register(user);
        if (!response.containsKey("token")) return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        
        /// send validate accout email
        if (user.getRole() != Role.REGISTER) return new ResponseEntity<>(response, HttpStatus.OK);

        HashMap<String, Object> codeToken = authenticationService.createValidateAccoutCodeByUser(user, Type.VALIDATE_ACCOUNT);
        if (codeToken.containsKey("error")) return new ResponseEntity<>(codeToken, HttpStatus.BAD_REQUEST);

        HashMap<String, Object> emailServiceResponse = sendValidateAccountMail((String)codeToken.get("token"), user);
        return new ResponseEntity<>(emailServiceResponse, emailServiceResponse.containsKey("error") ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.OK);
    }
 
    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody @Valid LoginRequestDTO request, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        HashMap<String, Object> response = authenticationService.authenticate(request);
        return new ResponseEntity<>(response, response.containsKey("token") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    
    private ResponseEntity<HashMap<String, Object>> sendUserCode(String email, Type type) {
        Optional<User> searchUser = userService.findByEmail(email);
        if (!searchUser.isPresent()) return new ResponseEntity<>(new HashMap<>() {{
            put("error" , "user not found");
        }}, HttpStatus.BAD_REQUEST);

        User user = searchUser.get();
        HashMap<String, Object> codeToken = authenticationService.createValidateAccoutCodeByUser(user, type);
        if (codeToken.containsKey("error")) return new ResponseEntity<>(codeToken, HttpStatus.BAD_REQUEST);

        HashMap<String, Object> emailServiceResponse;
        if (type == Type.FORGET_PASSWORD) {
            emailServiceResponse = sendForgetPasswordMail((String)codeToken.get("token"), user);
        } else {
            emailServiceResponse = sendValidateAccountMail((String)codeToken.get("token"), user);
        }
        return new ResponseEntity<>(emailServiceResponse, emailServiceResponse.containsKey("error") ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.OK);
    }

    @PostMapping("/validate_accout")
    public ResponseEntity<HashMap<String, Object>> sendvalidateAccountCode(@RequestBody @Valid SendUserCodeDTO request, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        return sendUserCode(request.getMail(), Type.VALIDATE_ACCOUNT);
    }

    
    @PostMapping("/validate")
    public ResponseEntity<HashMap<String, Object>> validateAccountwithCode(@RequestBody @Valid TokenRequestDTO token, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        HashMap<String, Object> authResult = authenticationService.readValidateAccoutCode(token.getToken());
        return new ResponseEntity<>(authResult, authResult.containsKey("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }


    @PostMapping("/forget_password")
    public ResponseEntity<HashMap<String, Object>> sendResetPasswordCode(@RequestBody @Valid SendUserCodeDTO request, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        return sendUserCode(request.getMail(), Type.FORGET_PASSWORD);
    }

    @PostMapping("/reset_password")
    public ResponseEntity<HashMap<String, Object>> resetPasswordCode(@RequestBody @Valid ResetPasswordRequestDTO request, BindingResult result) {
        if (result.hasErrors()) return handleBadRequest(result);
        HashMap<String, Object> authResult = authenticationService.readForgetPasswordCode(request.getToken(), request.getPassword());
        return new ResponseEntity<>(authResult, authResult.containsKey("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }
}
