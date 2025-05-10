package rom.brt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rom.brt.dto.Subscriber;
import rom.brt.entity.User;
import rom.brt.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUser(String msisdn) {
        Optional<User> user = userRepository.findByMsisdn(msisdn);
        if (user.isEmpty()) {
            User emptyUser = new User();
            emptyUser.setUserId(-1);
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
}
