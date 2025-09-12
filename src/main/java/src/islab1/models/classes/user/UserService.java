package src.islab1.models.classes.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
