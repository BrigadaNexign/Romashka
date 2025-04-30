package rom.brt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rom.brt.entity.User;
import rom.brt.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUser(String msisdn) {
        User user = userRepository.findByMsisdn(msisdn);
        if (user == null) {
            User emptyUser = new User();
            emptyUser.setMsisdn(msisdn);
            return emptyUser;
        }
        return user;
    }
}
