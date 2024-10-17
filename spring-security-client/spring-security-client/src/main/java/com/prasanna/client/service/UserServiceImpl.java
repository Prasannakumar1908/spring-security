package com.prasanna.client.service;

import com.prasanna.client.entity.PasswordResetToken;
import com.prasanna.client.entity.User;
import com.prasanna.client.entity.VerificationToken;
import com.prasanna.client.model.UserModel;
import com.prasanna.client.repository.PasswordResetTokenRepository;
import com.prasanna.client.repository.UserRepository;
import com.prasanna.client.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRole("USER");
        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken == null) {
            return "Invalid verification token";
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        // Check if token is expired
        if (verificationToken.getExpirationTime().before(cal.getTime())) {
            verificationTokenRepository.delete(verificationToken);
            return "Expired verification token";
        }

        // Check if user is already verified
        if (user.isEnabled()) {
            return "User already verified";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "Verified";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;

    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token,user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if(passwordResetToken == null) {
            return "Invalid";
        }
        User user = passwordResetToken.getUser();
        Calendar cal = Calendar.getInstance();
        // Check if token is expired
        if (passwordResetToken.getExpirationTime().before(cal.getTime())) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "Expired ";
        }
        return "Valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword,user.getPassword());
    }
}
