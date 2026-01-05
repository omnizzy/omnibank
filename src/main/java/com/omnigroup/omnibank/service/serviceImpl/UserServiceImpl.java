package com.omnigroup.omnibank.service.serviceImpl;

import com.omnigroup.omnibank.dto.*;
import com.omnigroup.omnibank.entity.User;
import com.omnigroup.omnibank.repository.UserRepository;
import com.omnigroup.omnibank.service.EmailService;
import com.omnigroup.omnibank.service.TransactionService;
import com.omnigroup.omnibank.service.UserService;
import com.omnigroup.omnibank.utils.AccountUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Data
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PasswordEncoder passwordEncoder;

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
                .password(userRequest.getPassword())
                .phoneNumber(passwordEncoder.encode(userRequest.getPhoneNumber()))
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
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXITS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXITS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getOtherName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        //check if account exists
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return AccountUtils.ACCOUNT_NOT_EXITS_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getOtherName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXITS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXITS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        //Save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto );

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getOtherName() + " " + userToCredit.getLastName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if account exists
        //check if amount to withdraw is not less than account balance
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXITS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXITS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        if (!(userToDebit.getAccountBalance().compareTo(request.getAmount()) >= 0)) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUND_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_FUND_MESSAGE)
                    .accountInfo(null)
                    .build();

        }
        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            //Save transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();
            transactionService.saveTransaction(transactionDto );

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getOtherName() + " " + userToDebit.getLastName())
                            .accountBalance(userToDebit.getAccountBalance() )
                            .build())
                    .build();
        }

    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        // get the account to debit(check if account exists)
        //check if the amount to debit is not more than balance
        //debit the account
        //get the account to credit
        //credit the account
        boolean isSourceAccountExists = userRepository.existsByAccountNumber(request.getSourceAccountNumber());
        boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXITS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXITS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User soureAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (!(soureAccountUser.getAccountBalance().compareTo(request.getAmount()) >= 0)) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUND_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_FUND_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        soureAccountUser.setAccountBalance(soureAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUserName = soureAccountUser.getFirstName() + " " + soureAccountUser.getOtherName() + " " + soureAccountUser.getLastName();
        userRepository.save(soureAccountUser);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT!")
                .recipient(soureAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + "has been debited from your account! Your current balance is " + soureAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        //String recipientUserName = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getOtherName() + " " + destinationAccountUser.getLastName();
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT!")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + "has been credited to your account from " + sourceUserName +" Your current balance is " + destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        //Save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(soureAccountUser.getAccountNumber())
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto );

        //Save transaction
        TransactionDto transactionDto1 = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto1 );

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();

    }

}
