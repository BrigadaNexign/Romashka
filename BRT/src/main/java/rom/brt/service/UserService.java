package rom.brt.service;

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

    /**
     * Находит пользователя по номеру телефона.
     * Возвращает пользователя с userId = -1 если не найден - не обслуживается.
     *
     * @param msisdn номер телефона для поиска
     * @return найденный пользователь или пустая запись с userId = -1
     */
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

    /**
     * Создает обслуживаемого абонента из данных пользователя.
     *
     * @param user сущность пользователя
     * @return DTO абонента или null при ошибке
     */
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

    /**
     * Пополняет баланс пользователя.
     *
     * @param msisdn номер телефона пользователя
     * @param dto DTO с суммой пополнения
     * @throws UserNotFoundException если пользователь не найден
     */
    @Transactional
    public void topUpBalance(String msisdn, BalanceUpdate dto) throws UserNotFoundException {
        User user = userRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new UserNotFoundException(msisdn));
        user.setBalance(BigDecimal.valueOf(user.getBalance().doubleValue() + dto.amount()));
        userRepository.save(user);
        logSuccess();
    }

    /**
     * Создает или обновляет пользователя.
     * При создании также инициализирует параметры пользователя.
     *
     * @param request DTO с данными пользователя
     */

    @Transactional
    public void createUser(UserUpdateRequest request) {
        logger.info("Creating user for request: {}", request);

        int minutes = request.minutes() != null ? request.minutes() : 0;

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
                            .user(newUser)
                            .minutes(minutes)
                            .paymentDay(request.paymentDay())
                            .build();

                    newUser.setUserParams(params);
                    return userRepository.save(newUser);
                });

        UserParams params;
        if (user.getUserParams() == null) {
            params = UserParams.builder()
                    .user(user)
                    .minutes(minutes)
                    .paymentDay(request.paymentDay())
                    .build();
            user.setUserParams(params);
        } else {
            params = user.getUserParams();
            params.setMinutes(minutes);
            params.setPaymentDay(request.paymentDay());
        }
        userParameterRepository.save(params);
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
