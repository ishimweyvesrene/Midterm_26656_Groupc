package com.example.EventManagementSystem.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Requirement 3: Many-to-Many relationship with Event_Attendees join table
// Requirement 4: Many-to-One relationships with Category and Location (Venue)
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private LocalDateTime eventDate;

    // Many-to-One relationship to Category
    // Multiple Events can belong to One Category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Many-to-One relationship to Location (Venue)
    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Location venue;

    // Many-to-Many relationship to User
    // Join table to manage the relationship
    @ManyToMany
    @JoinTable(name = "event_attendees", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> attendees = new HashSet<>();

    public Event() {
    }

    public Event(String title, String description, LocalDateTime eventDate) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Location getVenue() {
        return venue;
    }

    public void setVenue(Location venue) {
        this.venue = venue;
    }

    public Set<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<User> attendees) {
        this.attendees = attendees;
    }

    public void addAttendee(User user) {
        this.attendees.add(user);
        user.getEvents().add(this);
    }

    public void removeAttendee(User user) {
        this.attendees.remove(user);
        user.getEvents().remove(this);
    }
}
