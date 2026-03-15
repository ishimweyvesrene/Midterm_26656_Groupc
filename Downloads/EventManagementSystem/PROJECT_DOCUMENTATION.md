# Event Management System - Complete Documentation

## Table of Contents

1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Data Model & Entity Relationships](#data-model--entity-relationships)
4. [Database Schema](#database-schema)
5. [Data Flow](#data-flow)
6. [Workflow & Process Logic](#workflow--process-logic)
7. [Key Features & Implementations](#key-features--implementations)
8. [API Endpoints](#api-endpoints)
9. [Example Scenarios](#example-scenarios)

---

## Project Overview

The **Event Management System** is a Spring Boot REST API application that manages events, users, categories, and locations. It demonstrates enterprise-level database design with complex relationships including one-to-many, many-to-many, one-to-one, and self-referencing hierarchical relationships.

**Purpose:** Track and manage events, attendees, and venue information with a hierarchical location system.

**Key Stakeholders:**

- Event Organizers (create and manage events)
- Users/Attendees (register for and attend events)
- Administrators (manage categories and locations)

---

## Technology Stack

| Component  | Technology      | Version                   |
| ---------- | --------------- | ------------------------- |
| Language   | Java            | 17+                       |
| Framework  | Spring Boot     | 3.x                       |
| ORM        | Hibernate JPA   | Jakarta Persistence       |
| Database   | MySQL           | 8.0+                      |
| Build Tool | Maven           | 3.6+                      |
| Other      | Spring Data JPA | Included with Spring Boot |

**Key Dependencies:**

- Spring Web (REST API)
- Spring Data JPA (Database abstraction)
- Hibernate (ORM)
- MySQL Driver (Database connectivity)

---

## Data Model & Entity Relationships

### Entity Overview

```
┌─────────────────────────────────────────────────────────────┐
│                       ENTITIES MAP                          │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  Category (1) ─────────→ (Many) Event                        │
│                             │                                │
│                             │ (Many-to-Many)                 │
│                             │                                │
│  Location (1) ←─────────→ (Many) Event (as Venue)           │
│       ▲                                                       │
│       │ (Self-Referencing)                                   │
│   Location (Parent)                                          │
│                                                               │
│  User (Many) ←─────────→ (Many) Event (Attendees)           │
│       │                                                       │
│       │ (Many-to-One)                                        │
│       │                                                       │
│   Location (Residence)                                       │
│       │                                                       │
│       │ (One-to-One)                                         │
│       │                                                       │
│   UserProfile (1)                                            │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

### Detailed Entity Descriptions

#### 1. **Location Entity**

- **Purpose:** Represents geographical areas in a hierarchical structure
- **Attributes:**
  - `id` (Long): Primary key, auto-generated
  - `name` (String): Location name (e.g., "Johannesburg")
  - `code` (String): Unique location code (e.g., "JHB"")
  - `type` (ELocationType Enum): PROVINCE, CITY, or VENUE
  - `parent` (Location): Reference to parent location (self-referencing for hierarchy)
  - `createdAt` (LocalDateTime): Timestamp of creation

- **Hierarchy Example:**

  ```
  South Africa (Country)
  ├── Gauteng (PROVINCE)
  │   ├── Johannesburg (CITY)
  │   │   └── FNB Stadium (VENUE)
  │   └── Pretoria (CITY)
  └── KwaZulu-Natal (PROVINCE)
      └── Durban (CITY)
  ```

- **Relationship:** Self-referencing Many-to-One (parent_id)

---

#### 2. **Category Entity**

- **Purpose:** Classifies events by type
- **Attributes:**
  - `id` (Long): Primary key
  - `name` (String): Unique category name (e.g., "Music", "Technology")
  - `description` (String): Details about the category
  - `events` (Set<Event>): Collection of events in this category

- **Relationship:** One-to-Many with Event (cascade delete orphans)

---

#### 3. **User Entity**

- **Purpose:** Represents system users and event attendees
- **Attributes:**
  - `id` (Long): Primary key
  - `name` (String): User's full name
  - `email` (String): Unique email address
  - `location` (Location): Where the user resides (Many-to-One)
  - `profile` (UserProfile): Associated user profile (One-to-One)
  - `events` (Set<Event>): Events the user attends (Many-to-Many, inverse side)

- **Relationships:**
  - Many-to-One with Location
  - One-to-One with UserProfile (cascade)
  - Many-to-Many with Event (through event_attendees join table)

---

#### 4. **UserProfile Entity**

- **Purpose:** Stores additional user information
- **Attributes:**
  - `id` (Long): Primary key
  - `bio` (String): User biography
  - `phoneNumber` (String): Contact number
  - `user` (User): Reference back to User

- **Relationship:** One-to-One with User (inverse/mappedBy side)

---

#### 5. **Event Entity**

- **Purpose:** Represents events that can be attended
- **Attributes:**
  - `id` (Long): Primary key
  - `title` (String): Event title
  - `description` (String): Event details
  - `eventDate` (LocalDateTime): When the event occurs
  - `category` (Category): Event classification (Many-to-One)
  - `venue` (Location): Where the event is held (Many-to-One)
  - `attendees` (Set<User>): Users attending the event (Many-to-Many)

- **Relationships:**
  - Many-to-One with Category
  - Many-to-One with Location (as venue)
  - Many-to-Many with User (owning side)

---

## Database Schema

### Tables Created by Hibernate

```sql
-- Hierarchical locations
CREATE TABLE location (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL UNIQUE,
    location_type VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    created_at DATETIME,
    FOREIGN KEY (parent_id) REFERENCES location(id)
);

-- Event categories
CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES category(id)
);

-- User profiles (one-to-one with users)
CREATE TABLE user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bio VARCHAR(255),
    phone_number VARCHAR(255)
);

-- Users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    location_id BIGINT,
    profile_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (location_id) REFERENCES location(id),
    FOREIGN KEY (profile_id) REFERENCES user_profile(id)
);

-- Events table
CREATE TABLE event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    event_date DATETIME,
    category_id BIGINT,
    venue_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES category(id),
    FOREIGN KEY (venue_id) REFERENCES location(id)
);

-- Many-to-Many junction table for event attendees
CREATE TABLE event_attendees (
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES event(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Data Flow

### 1. **Initialization Data Flow** (Application Startup)

```
Application Start
    ↓
DemonstrationRunner (CommandLineRunner) Executes
    ↓
├─→ Create Location Hierarchy
│   ├─ Save Provinces: Gauteng, KwaZulu-Natal
│   ├─ Save Cities: Johannesburg (→Gauteng), Durban (→KZN)
│   └─ Save Venues: FNB Stadium (→Johannesburg)
│       ↓ (All saved to DATABASE via LocationRepository)
│
├─→ Create Categories
│   ├─ Music: "Music Festivals and Concerts"
│   └─ Technology: "Tech Conferences and Hackathons"
│       ↓ (Saved to DATABASE via CategoryRepository)
│
├─→ Create Users with Profiles
│   ├─ Alice Smith
│   │   ├─ Email: alice@example.com
│   │   ├─ Location: Gauteng
│   │   └─ Profile: "Tech Enthusiast", "555-0101"
│   ├─ Bob Jones
│   │   ├─ Email: bob@example.com
│   │   ├─ Location: KwaZulu-Natal
│   │   └─ Profile: "Music Lover", "555-0102"
│   └─ Charlie Brown
│       ├─ Email: charlie@example.com
│       ├─ Location: Gauteng
│       └─ Profile: "Developer", "555-0103"
│       ↓ (All saved to DATABASE via UserRepository)
│
├─→ Create Events with Relationships
│   ├─ Summer Fest (Concert)
│   │   ├─ Category: Music
│   │   ├─ Venue: FNB Stadium
│   │   └─ Attendees: Bob, Alice
│   └─ Tech Innovate (Hackathon)
│       ├─ Category: Technology
│       ├─ Venue: FNB Stadium
│       └─ Attendees: Alice, Charlie
│       ↓ (All saved to DATABASE via EventRepository)
│
└─→ System Ready to Accept API Requests
    ↓
API Server Listening on http://localhost:8082
```

### 2. **Query Data Flow** (User Making Requests)

```
HTTP Request (GET /api/users)
    ↓
UserController.getAllUsers()
    ↓
Call UserRepository.findAll(Pageable)
    ↓
Spring Data JPA generates SQL query
    ↓
Execute against MySQL Database
    ↓
Retrieved Results (Paginated)
    ↓
Convert to JSON
    ↓
HTTP Response (200 OK)
```

### 3. **Relationship Navigation Flow**

```
Query: Get an Event with all details
    ↓
EventRepository.findById(1)
    ↓
Hibernate Fetches: event table row
    ↓
Lazy/Eager Load category
    ↓ (Cascade: FetchType.LAZY by default)
Lazy/Eager Load venue (Location)
    ↓
Lazy/Eager Load attendees (Set<User>)
    ↓
Lazy Load each User's:
    ├─ profile (UserProfile)
    └─ location (Location)
    ↓
Complete Event Object with All Relationships
```

---

## Workflow & Process Logic

### 1. **System Initialization Workflow**

**When:** Application starts

**Process:**

1. Spring Boot starts application
2. Spring Data JPA initializes repositories
3. Hibernate creates/validates schema
4. DemonstrationRunner.run() executes automatically (@Component with CommandLineRunner)
5. Transactional context established (@Transactional)
6. Sample data created and persisted

**Key Logic in DemonstrationRunner:**

```
Step 1: Create and save Locations (Hierarchical)
- Gauteng Province (parent=null)
- KZN Province (parent=null)
- Johannesburg City (parent=Gauteng)
- Durban City (parent=KZN)
- FNB Stadium Venue (parent=Johannesburg)

Step 2: Create and save Categories
- Music category
- Technology category

Step 3: Create and save Users with Profiles
FOR EACH user:
  - Create User object
  - Create UserProfile object
  - Set location reference
  - Set profile reference
  - Save user (cascades profile save)

Step 4: Create and save Events with attendees
FOR EACH event:
  - Create Event object
  - Set category
  - Set venue
  - Add attendees (bidirectional)
  - Save event

Step 5: Execute demonstration queries
- Test email existence check
- Test filtering by province
- Test pagination and sorting
```

### 2. **Event Registration Workflow**

**Scenario:** User wants to attend an event

```
User Sends: POST /api/events/{eventId}/attendees/{userId}
    ↓
EventController receives request
    ↓
Fetch Event by ID
    ↓
Fetch User by ID
    ↓
Add User to Event.attendees (Set<User>)
    ↓
Hibernate tracks change (Set is dirty)
    ↓
Save Event (cascades to event_attendees join table)
    ↓
INSERT into event_attendees (event_id, user_id) VALUES (?, ?)
    ↓
Response: 200 OK (User registered for event)
```

### 3. **User Search by Location Workflow**

**Query Used:**

```java
@Query("SELECT u FROM User u WHERE u.location.code = :code OR u.location.name = :name")
List<User> findUsersByProvinceCodeOrName(@Param("code") String code, @Param("name") String name);
```

**Execution:**

1. Input: code="GP", name="Gauteng"
2. Generate SQL: SELECT u FROM users u JOIN location l WHERE l.code='GP' OR l.name='Gauteng'
3. Execute on database
4. Fetch ALL matching users with their relationships
5. Return List<User> with location and profile data

---

## Key Features & Implementations

### Feature 1: Hierarchical Location Structure (Self-Referencing)

- **Implementation:** Location.parent field references another Location
- **Use Case:** Support Province → City → Venue hierarchy
- **Query Example:**
  ```java
  // Find all cities under Gauteng province
  List<Location> cities = locationRepository.findByParent(gauteng);
  ```

### Feature 2: One-to-One User Profile

- **Implementation:** User.profile with @OneToOne(cascade=CascadeType.ALL)
- **Behavior:** Saving a User automatically saves its Profile
- **Use Case:** Store additional user metadata separately

### Feature 3: Many-to-Many Event Attendees

- **Implementation:** @ManyToMany with @JoinTable("event_attendees")
- **Join Table:** Contains event_id and user_id columns
- **Use Case:** Track which users attend which events (many users per event, many events per user)

### Feature 4: Pagination & Sorting

- **Implementation:** JpaRepository.findAll(Pageable)
- **Usage:**

  ```java
  Pageable pageable = PageRequest.of(0, 10, Sort.by("name").descending());
  Page<User> page = userRepository.findAll(pageable);

  // Returns:
  // - getTotalElements(): Total count
  // - getTotalPages(): Number of pages
  // - getContent(): Current page data (10 items)
  ```

### Feature 5: Custom Query with JPQL

- **Implementation:** @Query with @Param annotations
- **Advantage:** Type-safe, more readable than native SQL
- **Example:**
  ```java
  @Query("SELECT u FROM User u WHERE u.location.code = :code OR u.location.name = :name")
  List<User> findUsersByProvinceCodeOrName(@Param("code") String code, @Param("name") String name);
  ```

### Feature 6: Existence Checking

- **Implementation:** existsByEmail() method
- **Generated Query:** SELECT 1 FROM users WHERE email = ? LIMIT 1
- **Use Case:** Validate email uniqueness before creating user

---

## API Endpoints

### User Endpoints

```
GET    /api/users                          - Get all users (with pagination)
GET    /api/users/{id}                     - Get user by ID
POST   /api/users                          - Create new user
PUT    /api/users/{id}                     - Update user
DELETE /api/users/{id}                     - Delete user
GET    /api/users/province/{code|name}    - Get users by province (custom)
```

### Event Endpoints

```
GET    /api/events                         - Get all events (with pagination)
GET    /api/events/{id}                    - Get event by ID
POST   /api/events                         - Create new event
PUT    /api/events/{id}                    - Update event
DELETE /api/events/{id}                    - Delete event
POST   /api/events/{eventId}/attendees/{userId} - Add attendee to event
```

### Category Endpoints

```
GET    /api/categories                     - Get all categories
GET    /api/categories/{id}                - Get category by ID
POST   /api/categories                     - Create new category
PUT    /api/categories/{id}                - Update category
DELETE /api/categories/{id}                - Delete category
```

### Location Endpoints

```
GET    /api/locations                      - Get all locations
GET    /api/locations/{id}                 - Get location by ID
POST   /api/locations                      - Create new location
PUT    /api/locations/{id}                 - Update location
DELETE /api/locations/{id}                 - Delete location
GET    /api/locations/{id}/children        - Get child locations (custom)
```

---

## Example Scenarios

### Scenario 1: Full Event Registration Flow

**Goal:** User Alice wants to attend the "Tech Innovate" hackathon

**Steps:**

1. **Application Already Has:**
   - Alice (User, ID=1)
   - Tech Innovate Event (ID=2)

2. **Request:**

   ```
   POST /api/events/2/attendees/1
   ```

3. **Controller Logic:**

   ```java
   Event event = eventRepository.findById(2).get();  // Tech Innovate
   User user = userRepository.findById(1).get();     // Alice
   event.addAttendee(user);                           // Add to Set
   eventRepository.save(event);                       // Persist
   ```

4. **Database Changes:**

   ```sql
   INSERT INTO event_attendees (event_id, user_id) VALUES (2, 1);
   ```

5. **Result:**
   ```json
   {
     "id": 2,
     "title": "Tech Innovate",
     "description": "Annual Hackathon",
     "category": { "id": 2, "name": "Technology" },
     "venue": { "id": 5, "name": "FNB Stadium" },
     "attendees": [
       { "id": 1, "name": "Alice Smith", "email": "alice@example.com" },
       { "id": 3, "name": "Charlie Brown", "email": "charlie@example.com" }
     ]
   }
   ```

---

### Scenario 2: Paginated Search with Sorting

**Goal:** Get all users, sorted by name descending, page 0, 2 per page

**Request:**

```
GET /api/users?page=0&size=2&sortBy=name&direction=DESC
```

**Database Query Generated:**

```sql
SELECT * FROM users ORDER BY name DESC LIMIT 2 OFFSET 0;
SELECT COUNT(*) FROM users;
```

**Response:**

```json
{
  "content": [
    { "id": 3, "name": "Charlie Brown", "email": "charlie@example.com" },
    { "id": 2, "name": "Bob Jones", "email": "bob@example.com" }
  ],
  "totalElements": 3,
  "totalPages": 2,
  "currentPage": 0,
  "size": 2,
  "hasNext": true
}
```

---

### Scenario 3: Hierarchical Location Navigation

**Goal:** Find all venues under Johannesburg city

**Data Structure:**

```
Gauteng (ID=1)
├─ Johannesburg (ID=3, parent=1)
│  └─ FNB Stadium (ID=5, parent=3)
└─ Pretoria (ID=4, parent=1)

KwaZulu-Natal (ID=2)
└─ Durban (ID=6, parent=2)
   └─ Durban Convention Center (ID=7, parent=6)
```

**Query:**

```java
List<Location> johannesburgVenues = locationRepository.findByParent(
    locationRepository.findById(3).get()  // Johannesburg
);
```

**Result:**

```
[FNB Stadium]
```

---

### Scenario 4: Complex Query - Users by Province

**Goal:** Find all users in Gauteng or KwaZulu-Natal

**Request:**

```
GET /api/users/search/province?code=GP&name=Gauteng
```

**JPQL Query:**

```jpql
SELECT u FROM User u
WHERE u.location.code = 'GP'
   OR u.location.name = 'Gauteng'
```

**SQL Generated:**

```sql
SELECT u.* FROM users u
INNER JOIN location l ON u.location_id = l.id
WHERE l.code = 'GP' OR l.name = 'Gauteng';
```

**Result:**

```
- Alice Smith (alice@example.com)
- Charlie Brown (charlie@example.com)
```

---

## Summary: Complete Data Flow Example

### Creating an Event with Attendees

```
1. USER ACTION
   Client sends: POST /api/events
   {
     "title": "Summer Music Festival",
     "description": "Outdoor music festival",
     "eventDate": "2026-06-15T18:00:00",
     "categoryId": 1,
     "venueId": 5
   }

2. CONTROLLER LAYER
   EventController receives request
   → Extracts JSON data
   → Creates Event object
   → Sets category: categoryRepository.findById(1)
   → Sets venue: locationRepository.findById(5)

3. SERVICE/REPOSITORY LAYER
   eventRepository.save(event)
   → Spring Data JPA creates SQL INSERT
   → Hibernate manages the transaction

4. DATABASE LAYER
   INSERT INTO event (title, description, event_date, category_id, venue_id)
   VALUES ('Summer Music Festival', 'Outdoor...', '2026-06-15...', 1, 5)
   → MySQL executes, returns generated ID = 10

5. RESPONSE LAYER
   Return HTTP 201 Created with:
   {
     "id": 10,
     "title": "Summer Music Festival",
     "category": {...},
     "venue": {...},
     "attendees": []
   }

6. CLIENT RECEIVES
   New event created successfully with ID 10
   Can now add attendees: POST /api/events/10/attendees/1 (add user 1)
```

---

## Key Takeaways

1. **Spring Data JPA** abstracts database operations - you work with objects, not SQL
2. **Hibernate** handles all relationship cascading and lazy/eager loading
3. **Repositories** are the gateway between your code and the database
4. **Transactions** ensure data consistency across multiple operations
5. **JPQL** allows type-safe queries without writing raw SQL
6. **Pagination** makes handling large datasets efficient
7. **Relationships** are defined in entity classes, not in SQL schemas
8. **Cascading** operations (save, delete) propagate through relationships
