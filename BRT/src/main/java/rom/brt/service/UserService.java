package rom.brt.service;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rom.brt.dto.*;
import rom.brt.entity.User;
import rom.brt.entity.UserParams;
import rom.brt.exception.UserNotFoundException;
import rom.brt.repository.UserParameterRepository;
import rom.brt.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserParameterRepository userParameterRepository;

    public User findUser(String msisdn) {
        Optional<User> user = userRepository.findByMsisdn(msisdn);
        if (user.isEmpty()) {
            User emptyUser = new User();
            emptyUser.setUserId(-1L);
            emptyUser.setMsisdn(msisdn);
            return emptyUser;
        }
        return user.get();
    }

    public Subscriber createServicedSubscriberFromRecord(User user) {
        try{
            return Subscriber.fromServicedUser(
                    user.getUserId(),
                    user.getMsisdn(),
                    user.getTariffId(),
                    user.getUserParams().getMinutes(),
                    user.getUserParams().getPaymentDay()
            );
        } catch (Exception e) {
            logger.error("Error creating serviced Subscriber instance: {}", e.getLocalizedMessage());
            return null;
        }
    }

    public Subscriber createForeignSubscriberFromRecord(User user) {
        try {
            return Subscriber.fromForeignUser(user.getMsisdn());
        } catch (Exception e) {
            logger.error("Error creating foreign Subscriber instance: {}", e.getLocalizedMessage());
            return null;
        }
    }

    @Transactional
    public UserResponse getUserByMsisdn(String msisdn) throws UserNotFoundException {
        User user = userRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new UserNotFoundException(msisdn));
        logSuccess();
        return mapToUserResponse(user);
    }

    @Transactional
    public void topUpBalance(String msisdn, BalanceUpdate dto) throws UserNotFoundException {
        User user = userRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new UserNotFoundException(msisdn));
        user.setBalance(BigDecimal.valueOf(user.getBalance().doubleValue() + dto.amount()));
        userRepository.save(user);
        logSuccess();
    }

    @Transactional
    public void createUser(UserUpdateRequest request) {
        logger.info("Creating user for request: {}", request);
        User user = userRepository.findByMsisdn(request.msisdn())
                .map(existing -> {
                    existing.setUserName(request.name());
                    existing.setTariffId(request.tariffId());
                    existing.setBalance(BigDecimal.valueOf(request.balance()));
                    return existing;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .userName(request.name())
                            .msisdn(request.msisdn())
                            .tariffId(request.tariffId())
                            .balance(BigDecimal.valueOf(request.balance() != null ? request.balance() : 100.0))
                            .registrationDate(LocalDateTime.now())
                            .build();

                    UserParams params = UserParams.builder()
                            .user(newUser)  // Устанавливаем связь
                            .minutes(request.minutes() != null ? request.minutes() : 0)
                            .paymentDay(request.paymentDay())
                            .build();

                    newUser.setUserParams(params);
                    return userRepository.save(newUser);
                });

        if (user.getUserParams() == null) {
            UserParams params = UserParams.builder()
                    .user(user)
                    .minutes(request.minutes() != null ? request.minutes() : 0)
                    .paymentDay(request.paymentDay())
                    .build();
            user.setUserParams(params);
            userParameterRepository.save(params);  // Явное сохранение
        } else {
            UserParams params = user.getUserParams();
            params.setMinutes(request.minutes() != null ? request.minutes() : 0);
            params.setPaymentDay(request.paymentDay());
            userParameterRepository.save(params);// Явное сохранение
        }
    }

    @Transactional
    public void changeUserTariff(String msisdn, ChangeTariffRequest request) throws UserNotFoundException {
        User user = userRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new UserNotFoundException(msisdn));
        user.setTariffId(request.tariffId());
        userRepository.save(user);
        logSuccess();
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUserName(),
                user.getTariffId(),
                user.getMsisdn(),
                user.getBalance(),
                user.getRegistrationDate()
        );
    }

    private void logSuccess() {
        logger.info("Successful UserService operation");
    }
}
