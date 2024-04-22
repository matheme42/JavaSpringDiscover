package com.example.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.api.DTO.AuthenticateDTO.LoginRequestDTO;
import com.example.api.model.database.Code;
import com.example.api.model.database.Token;
import com.example.api.model.database.User;
import com.example.api.model.enums.Role;
import com.example.api.model.enums.Type;
import com.example.api.repository.CodeRepository;
import com.example.api.repository.TokenRepository;
import com.example.api.repository.UserRepository;

import io.jsonwebtoken.MalformedJwtException;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CodeRepository codeRepository;

    /**
     * Saves the user code in the database.
     *
     * @param jwt  the JWT token
     * @param user the user associated with the code
     * @param type the type of code (e.g., FORGET_PASSWORD or VALIDATE_ACCOUNT)
     */
    private void saveUserCode(String jwt, User user, Type type) {
        Code code = new Code();
        code.setToken(jwt);
        code.setValid(true);
        code.setUser(user);
        code.setType(type);
        codeRepository.save(code);
    }

    /**
     * Revokes all codes of the given type associated with the user.
     *
     * @param user the user whose codes are to be revoked
     * @param type the type of codes to revoke
     */
    private void revokeAllCodeByUserAndType(User user, Type type) {
        List<Code> validCodeListByUser = codeRepository.findAllCodeByUserAndType(user.getId(), Type.VALIDATE_ACCOUNT);
        if (validCodeListByUser.isEmpty()) return;

        validCodeListByUser.forEach(t -> {
            t.setValid(false);
        });
        codeRepository.saveAll(validCodeListByUser);
    }

    /**
     * Saves the user token in the database.
     *
     * @param jwt  the JWT token
     * @param user the user associated with the token
     */
    private void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    /**
     * Revokes all tokens associated with the user.
     *
     * @param user the user whose tokens are to be revoked
     */
    private void revokeAllTokenByUser(User user) {
        List<Token> validTokenListByUser = tokenRepository.findAllTokenByUser(user.getId());
        if (validTokenListByUser.isEmpty()) return;

        validTokenListByUser.forEach(t -> {
            t.setLoggedOut(true);
        });
        tokenRepository.saveAll(validTokenListByUser);
    }

    /**
     * Generates an authentication token for the given user.
     * Revokes all previous tokens associated with the user.
     *
     * @param user the user for whom the token is generated
     * @return the generated authentication token
     */
    private String generateAuthToken(User user) {
        String jwt = jwtService.generateAuthToken(user);
        revokeAllTokenByUser(user);

        // save the generated token
        saveUserToken(jwt, user);
        return jwt;
    }

    /**
     * Registers a new user.
     *
     * @param user the user to be registered
     * @return a HashMap containing the token if registration is successful, or an error message otherwise
     */
    public HashMap<String, Object> register(User user) {
        Optional<User> searchUser = userRepository.findByUsername(user.getUsername());
        if (searchUser.isPresent()) return new HashMap<>() {{
            put("error", "username already used");
        }};
        searchUser = userRepository.findByEmail(user.getEmail());
        if (searchUser.isPresent()) return new HashMap<>() {{
            put("error", "email address already used");
        }};

        user.setUsername(user.getUsername());
        user.setEmail(user.getEmail());
        user.setImage(user.getImage());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());
        user = userRepository.save(user);

        if (user.getRole() == Role.REGISTER) return new HashMap<>() {{put("token", "");}};

        String jwt = generateAuthToken(user);
        return new HashMap<>() {{put("token", jwt);}};
    }

    /**
     * Authenticates a user.
     *
     * @param request the login request containing username and password
     * @return a HashMap containing the authentication token if authentication is successful, or an error message otherwise
     */
    public HashMap<String, Object> authenticate(LoginRequestDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Optional<User> searchUser = userRepository.findByUsername(request.getUsername());
        if (!searchUser.isPresent()) return new HashMap<>() {{
            put("error", "user not found");
        }};

        User user = searchUser.get();
        String jwt = generateAuthToken(user);
        return new HashMap<>() {{put("token", jwt);}};
    }

    /**
     * Creates a validation code for a user.
     *
     * @param user the user for whom the code is generated
     * @param type the type of code (e.g., FORGET_PASSWORD or VALIDATE_ACCOUNT)
     * @return a HashMap containing the validation code if creation is successful, or an error message otherwise
     */
    public HashMap<String, Object> createValidateAccoutCodeByUser(User user, Type type) {
        if (user.getRole() != Role.REGISTER && type == Type.VALIDATE_ACCOUNT) return new HashMap<>() {{put("error", "user already validate");}};
        revokeAllCodeByUserAndType(user, type);
        String jwtCode = jwtService.generateCodeToken(user);
        saveUserCode(jwtCode, user, type);
        return new HashMap<>() {{put("token", jwtCode);}};
    }

    /**
     * Extracts the code and user from a token.
     *
     * @param token the token to extract from
     * @return a HashMap containing the extracted code and user, or an error message if extraction fails
     */
    private HashMap<String, Object> extractCodeAndUserFromToken(String token) {
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (MalformedJwtException exception) {
            return new HashMap<>() {{put("error", "malformed token");}};
        }

        Optional<Code> searchCode = codeRepository.findByToken(token);
        if (!searchCode.isPresent()) {
            return new HashMap<>() {{put("error", "token not found");}};
        }

        final Code code = searchCode.get();
        final User user = code.getUser();

        if (!username.equals(user.getUsername()) || !code.isValid()) {
            return new HashMap<>() {{put("error", "invalid token");}};
        }

        return new HashMap<>() {{put("code", code); put("user", user);}};
    }

    /**
     * Reads a validation account code.
     *
     * @param token the token containing the code
     * @return a HashMap containing the authentication token if successful, or an error message otherwise
     */
    public HashMap<String, Object> readValidateAccoutCode(String token) {

        HashMap<String, Object> extractResult = extractCodeAndUserFromToken(token);
        if (extractResult.containsKey("error")) return extractResult;
        Code code = (Code) extractResult.get("code");
        User user = (User) extractResult.get("user");

        user.setRole(Role.USER);
        user = userRepository.save(user);
        code.setValid(false);
        codeRepository.save(code);
        String jwt = generateAuthToken(user);
        return new HashMap<>() {{put("token", jwt);}};
    }

    /**
     * Reads a forget password code and updates the user's password.
     *
     * @param token    the token containing the code
     * @param password the new password
     * @return a HashMap containing the authentication token if successful, or an error message otherwise
     */
    public HashMap<String, Object> readForgetPasswordCode(String token, String password) {
        HashMap<String, Object> extractResult = extractCodeAndUserFromToken(token);
        if (extractResult.containsKey("error")) return extractResult;
        Code code = (Code) extractResult.get("code");
        User user = (User) extractResult.get("user");

        user.setPassword(passwordEncoder.encode(password));
        user = userRepository.save(user);
        code.setValid(false);
        codeRepository.save(code);
        revokeAllTokenByUser(user);
        String jwt = generateAuthToken(user);
        return new HashMap<>() {{put("token", jwt);}};
    }
}
