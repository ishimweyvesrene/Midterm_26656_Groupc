package com.example.EventManagementSystem.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.EventManagementSystem.model.*;
import com.example.EventManagementSystem.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DemonstrationRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRepository eventRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("====== STARTING DEMONSTRATION ======");

        // 2. Implementation of saving Location (Hierarchical data)
        Location gauteng = new Location(null, "Gauteng", "GP", ELocationType.PROVINCE, null, LocalDateTime.now());
        locationRepository.save(gauteng);

        Location kzn = new Location(null, "KwaZulu-Natal", "KZN", ELocationType.PROVINCE, null, LocalDateTime.now());
        locationRepository.save(kzn);

        Location joburg = new Location(null, "Johannesburg", "JHB", ELocationType.CITY, gauteng, LocalDateTime.now());
        locationRepository.save(joburg);

        Location durban = new Location(null, "Durban", "DBN", ELocationType.CITY, kzn, LocalDateTime.now());
        locationRepository.save(durban);

        Location stadium = new Location(null, "FNB Stadium", "FNB", ELocationType.VENUE, joburg, LocalDateTime.now());
        locationRepository.save(stadium);

        System.out.println("--- Locations Saved successfully. Hierarchical relationships (Self-Referencing ManyToOne) created.");

        // Create Categories (OneToMany to Events)
        Category music = new Category("Music", "Music Festivals and Concerts");
        Category tech = new Category("Technology", "Tech Conferences and Hackathons");
        categoryRepository.saveAll(List.of(music, tech));

        // Create Users & Profiles (One-to-One and Many-to-One to Location)
        User alice = new User("Alice Smith", "alice@example.com");
        alice.setLocation(gauteng); // User resides in Gauteng
        alice.setProfile(new UserProfile("Tech Enthusiast", "555-0101"));

        User bob = new User("Bob Jones", "bob@example.com");
        bob.setLocation(kzn); // User resides in KZN
        bob.setProfile(new UserProfile("Music Lover", "555-0102"));

        User charlie = new User("Charlie Brown", "charlie@example.com");
        charlie.setLocation(gauteng); // User resides in Gauteng
        charlie.setProfile(new UserProfile("Developer", "555-0103"));

        userRepository.saveAll(List.of(alice, bob, charlie));

        // Create Event (Many-to-One to Category, Many-to-One to Venue)
        Event concert = new Event("Summer Fest", "Annual Music Festival", LocalDateTime.now().plusDays(30));
        concert.setCategory(music);
        concert.setVenue(stadium);
        
        Event hackathon = new Event("Tech Innovate", "Annual Hackathon", LocalDateTime.now().plusDays(60));
        hackathon.setCategory(tech);
        hackathon.setVenue(stadium);

        // 4. Implementation of Many-to-Many relationship (Users attending Events)
        concert.addAttendee(bob);
        concert.addAttendee(alice);
        hackathon.addAttendee(alice);
        hackathon.addAttendee(charlie);

        eventRepository.saveAll(List.of(concert, hackathon));
        System.out.println("--- Entities populated successfully.");

        // 7. Implementation of existBy() method.
        System.out.println("\n--- Testing existsByEmail() ---");
        boolean exists = userRepository.existsByEmail("alice@example.com");
        boolean notExists = userRepository.existsByEmail("nobody@example.com");
        System.out.println("Does alice@example.com exist? " + exists);
        System.out.println("Does nobody@example.com exist? " + notExists);

        // 8. Retrieve all users from a given province using province code OR province name.
        System.out.println("\n--- Testing Retrieval by Province (GP or Gauteng) ---");
        List<User> gautengUsers = userRepository.findUsersByProvinceCodeOrName("GP", "Gauteng");
        System.out.println("Users in Gauteng:");
        for(User u : gautengUsers) {
            System.out.println(" - " + u.getName() + " (" + u.getEmail() + ")");
        }

        // 3. Implementation of Sorting & Pagination functionality.
        System.out.println("\n--- Testing Pagination and Sorting ---");
        // Sort by name descending, page 0, size 2
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").descending());
        Page<User> sortedPage = userRepository.findAll(pageable);
        
        System.out.println("Total Users: " + sortedPage.getTotalElements());
        System.out.println("Total Pages: " + sortedPage.getTotalPages());
        System.out.println("Users on Document Page 0 (Sorted Desc by Name):");
        for(User u : sortedPage.getContent()) {
             System.out.println(" - " + u.getName());
        }

        System.out.println("====== DEMONSTRATION COMPLETE ======");
    }
}
