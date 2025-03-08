package com.voting.backend.config;

import com.voting.backend.model.Poll;
import com.voting.backend.model.PollOption;
import com.voting.backend.model.User;
import com.voting.backend.repository.PollOptionRepository;
import com.voting.backend.repository.PollRepository;
import com.voting.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(
            UserRepository userRepository,
            PollRepository pollRepository,
            PollOptionRepository pollOptionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create a test user if none exists
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("USER");
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Create some sample polls
            createSamplePoll(user, "Favorite Programming Language", 
                    "What's your favorite programming language?",
                    new String[]{"Java", "Python", "JavaScript", "C#", "Go"});
            
            createSamplePoll(user, "Best Framework", 
                    "What's the best web framework?",
                    new String[]{"Spring Boot", "Django", "React", "Angular", "Vue"});
            
            createSamplePoll(user, "Preferred Database", 
                    "What's your preferred database?",
                    new String[]{"MySQL", "PostgreSQL", "MongoDB", "SQLite", "Oracle"});
            
            System.out.println("Sample data initialized successfully!");
        }
    }
    
    private void createSamplePoll(User user, String title, String description, String[] options) {
        Poll poll = new Poll();
        poll.setTitle(title);
        poll.setDescription(description);
        poll.setCreatedBy(user);
        poll.setCreatedAt(LocalDateTime.now());
        poll.setExpiresAt(LocalDateTime.now().plusDays(7));
        poll.setActive(true);
        poll.setMultipleChoiceAllowed(false);
        poll.setAnonymousVotingAllowed(true);
        
        Poll savedPoll = pollRepository.save(poll);
        
        for (String optionText : options) {
            PollOption option = new PollOption();
            option.setText(optionText);
            option.setPoll(savedPoll);
            option.setVoteCount(0);
            pollOptionRepository.save(option);
        }
    }
} 