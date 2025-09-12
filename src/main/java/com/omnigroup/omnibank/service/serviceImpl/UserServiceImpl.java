package com.omnigroup.omnibank.service.serviceImpl;

import com.omnigroup.omnibank.dto.*;
import com.omnigroup.omnibank.entity.User;
import com.omnigroup.omnibank.repository.UserRepository;
import com.omnigroup.omnibank.service.EmailService;
import com.omnigroup.omnibank.service.UserService;
import com.omnigroup.omnibank.utils.AccountUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Data
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXITS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXITS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject(savedUser.getFirstName() + "WELCOME TO OMNIBANK!")
                .messageBody("Congratulations! Your Account has been Successfully Created. \nYour Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName() + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getOtherName() + " " + savedUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if account exists
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        return null;
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        return null;
    }
}
