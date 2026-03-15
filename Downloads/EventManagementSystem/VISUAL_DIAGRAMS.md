# Event Management System - Visual Diagrams & Quick Reference

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                     HTTP CLIENT (Browser/API Tools)                │
│                                                                      │
│  GET   /api/users          POST   /api/events                       │
│  GET   /api/events         DELETE /api/categories                   │
│  POST  /api/locations      PUT    /api/users/{id}                   │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ JSON Requests/Responses
                               ▼
╔═════════════════════════════════════════════════════════════════════╗
║                    SPRING BOOT APPLICATION                         ║
╠═════════════════════════════════════════════════════════════════════╣
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │              REST CONTROLLERS LAYER                          │   │
│  │  ─────────────────────────────────────────────────────────  │   │
│  │  • UserController        → Handles /api/users requests      │   │
│  │  • EventController       → Handles /api/events requests     │   │
│  │  • CategoryController    → Handles /api/categories requests │   │
│  │  • LocationController    → Handles /api/locations requests  │   │
│  └────────────────────┬─────────────────────────────────────────┘   │
│                       │                                              │
│                       ▼                                              │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │            DATA ACCESS LAYER (Repositories)                 │   │
│  │  ─────────────────────────────────────────────────────────  │   │
│  │  • UserRepository extends JpaRepository<User, Long>         │   │
│  │  • EventRepository extends JpaRepository<Event, Long>       │   │
│  │  • CategoryRepository extends JpaRepository<Category, Long> │   │
│  │  • LocationRepository extends JpaRepository<Location, Long> │   │
│  │                                                              │   │
│  │  Spring Data JPA Auto-generates SQL from method names       │   │
│  │  Example: findById(), findAll(), findByEmail(), etc.        │   │
│  └────────────────────┬─────────────────────────────────────────┘   │
│                       │                                              │
│                       ▼                                              │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │          ENTITY/MODEL LAYER (JPA Entities)                  │   │
│  │  ─────────────────────────────────────────────────────────  │   │
│  │  • User          (with @Entity, @Table, relationships)      │   │
│  │  • Event         (with @Entity, @Table, relationships)      │   │
│  │  • Category      (with @Entity, @Table, relationships)      │   │
│  │  • Location      (with @Entity, @Table, relationships)      │   │
│  │  • UserProfile   (with @Entity, @Table, relationships)      │   │
│  └────────────────────┬─────────────────────────────────────────┘   │
│                       │                                              │
│                       ▼                                              │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │              HIBERNATE ORM LAYER                             │   │
│  │  ─────────────────────────────────────────────────────────  │   │
│  │  • Converts Java objects to SQL statements                  │   │
│  │  • Handles relationship cascading                           │   │
│  │  • Manages lazy/eager loading of related objects            │   │
│  │  • Transaction management (@Transactional)                  │   │
│  │  • Query generation from JPQL/method names                  │   │
│  └────────────────────┬─────────────────────────────────────────┘   │
│                       │                                              │
│                       ▼                                              │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │       JDBC DRIVER & CONNECTION POOL                         │   │
│  │  ─────────────────────────────────────────────────────────  │   │
│  │  MySQL JDBC Driver → Manages database connections           │   │
│  └────────────────────┬─────────────────────────────────────────┘   │
└───────────────────────┼──────────────────────────────────────────────┘
                        │ SQL Queries
                        ▼
         ╔═════════════════════════════════════════════════╗
         ║         MYSQL DATABASE (Port 3306)             ║
         ║                                                 ║
         ║  • users table                                  ║
         ║  • user_profile table                           ║
         ║  • event table                                  ║
         ║  • category table                               ║
         ║  • location table                               ║
         ║  • event_attendees table (join table)           ║
         ║                                                 ║
         ║  Stores all application data persistently       ║
         ╚═════════════════════════════════════════════════╝
```

---

## Entity Relationship Diagram (ERD)

```
                    ┌──────────────────┐
                    │   CATEGORY       │
                    ├──────────────────┤
                    │ id (PK)          │
                    │ name (UNIQUE)    │
                    │ description      │
                    └────────┬─────────┘
                             │
                        (1 to Many)
                             │
                    ┌────────▼──────────┐
                    │      EVENT        │
                    ├───────────────────┤
                    │ id (PK)           │
                    │ title             │◄──────┐
                    │ description       │       │
                    │ eventDate         │   (1 to Many)
                    │ category_id (FK)  │       │
                    │ venue_id (FK)     │       │
                    └────────┬──────────┘       │
                             │                 │
                        (Many to Many)    ┌────┴───────────────┐
                             │            │    LOCATION        │
                    ┌────────▼──────────┐ ├───────────────────┤
                    │ event_attendees   │ │ id (PK)           │
                    │ (Join Table)      │ │ name              │
                    ├───────────────────┤ │ code (UNIQUE)     │
                    │ event_id (FK)     │ │ type              │
                    │ user_id (FK)      │ │ parent_id (FK)    │◄─────┐
                    └────────┬──────────┘ │ createdAt         │      │
                             │            └────────┬──────────┘      │
                        (Many to Many)            │                  │
                             │            (Self-Referencing)         │
                    ┌────────▼──────────┐         │                  │
                    │       USER        │         │ (Many to 1)      │
                    ├───────────────────┤         │                  │
                    │ id (PK)           │◄────────┼──────────────────┘
                    │ name              │         │
                    │ email (UNIQUE)    │    (Many to 1)
                    │ location_id (FK)  │◄────────┘
                    │ profile_id (FK)   │
                    │ (UNIQUE)          │
                    └────────┬──────────┘
                             │
                        (1 to 1)
                             │
                    ┌────────▼──────────┐
                    │  USER_PROFILE     │
                    ├───────────────────┤
                    │ id (PK)           │
                    │ bio               │
                    │ phoneNumber       │
                    └───────────────────┘

Legend:
─────►  Foreign Key relationship
(1 to Many) = one record in source has many in target
(Many to Many) = many records in source can relate to many in target
(Many to 1) = many records point to one record
```

---

## Request-Response Lifecycle

```
┌─────────────────────────────────────────────────────────────────────┐
│                    CLIENT SENDS REQUEST                             │
│                  GET /api/users?page=0&size=10                      │
└────────────────────────────────┬──────────────────────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ Spring Dispatcher      │
                    │ Servlet                │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ Request mapped to      │
                    │ UserController         │
                    │ .getAllUsers()         │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ Create Pageable        │
                    │ PageRequest.of(0, 10)  │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ Call Repository        │
                    │ userRepository         │
                    │ .findAll(pageable)     │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ Hibernate generates    │
                    │ SQL query:             │
                    │ SELECT * FROM users    │
                    │ ORDER BY id ASC        │
                    │ LIMIT 10 OFFSET 0      │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ JDBC executes query    │
                    │ Sends to MySQL         │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ MySQL returns data     │
                    │ 10 user records        │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ Hibernate maps         │
                    │ ResultSet to User      │
                    │ objects (lazy load     │
                    │ relationships)         │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ Controller creates     │
                    │ Page<User> response    │
                    │ with metadata:         │
                    │ totalElements: 25      │
                    │ totalPages: 3          │
                    │ content: [users...]    │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │ Jackson serializes     │
                    │ to JSON format         │
                    └────────────┬───────────┘
                                 │
                                 ▼
┌────────────────────────────────────────────────────────────────────┐
│                    CLIENT RECEIVES RESPONSE                        │
│  HTTP 200 OK                                                       │
│  {                                                                 │
│    "content": [                                                    │
│      {"id": 1, "name": "Alice", "email": "alice@example.com", ...}│
│      {"id": 2, "name": "Bob", "email": "bob@example.com", ...}    │
│      ...                                                           │
│    ],                                                              │
│    "totalElements": 25,                                            │
│    "totalPages": 3,                                                │
│    "currentPage": 0,                                               │
│    "hasNext": true                                                 │
│  }                                                                 │
└────────────────────────────────────────────────────────────────────┘
```

---

## Data Persistence During Operations

```
SAVE OPERATION
═════════════════════════════════════════════════════════════════════

User user = new User("Alice", "alice@example.com");        [Memory]
user.setLocation(gauteng);                                  [Memory]
userRepository.save(user);
                         │
                         ▼
        Spring Data JPA checks if object is NEW or EXISTING
                         │
                    (Is NEW)
                         │
                         ▼
        Hibernate generates: INSERT INTO users (...)
                         │
                         ▼
        JDBC Driver sends SQL to MySQL
                         │
                         ▼
        MySQL executes INSERT, generates ID = 1
                         │
                         ▼
        Hibernate sets user.id = 1 (in memory)
                         │
                         ▼
        User object now MANAGED (tracked by Hibernate)
                         │
                         ▼
        Return user (now persistent with ID)


DELETE OPERATION
═════════════════════════════════════════════════════════════════════

userRepository.delete(user);
                    │
                    ▼
        Hibernate checks if object is MANAGED
                    │
                    ▼
        If MANAGED: Mark for deletion (Dirty flag set)
                    │
                    ▼
        On transaction commit, Hibernate generates:
        DELETE FROM users WHERE id = ?
                    │
                    ▼
        JDBC executes DELETE
                    │
                    ▼
        MySQL removes record from database
                    │
                    ▼
        Hibernate detaches object from session
```

---

## Transaction Management Flow

```
@Transactional method called
        │
        ▼
    BEGIN TRANSACTION
        │
        ▼
    ┌─── Open database connection
    │    Set autocommit = FALSE
    │
    ├─── Execute first query (INSERT user)
    │    Mark change in transaction log
    │
    ├─── Execute second query (INSERT profile)
    │    Mark change in transaction log
    │
    ├─── Execute third query (INSERT location)
    │    Mark change in transaction log
    │
    └─── Check for errors...
             │
             ├─ NO ERRORS:
             │  │
             │  ▼
             │  COMMIT TRANSACTION
             │  │
             │  ├─── Write all changes to database
             │  ├─── Release locks
             │  └─── Close connection
             │  │
             │  ▼
             │  Success returned to caller
             │
             └─ ERROR OCCURS:
                │
                ▼
                ROLLBACK TRANSACTION
                │
                ├─── Undo all pending changes
                ├─── Release locks
                └─── Close connection
                │
                ▼
                Exception propagated to caller
```

---

## Cascade Operations

```
Event Deletion with Cascade
═════════════════════════════════════════════════════════════════════

eventRepository.deleteById(1);
        │
        ▼
    Fetch Event with ID=1
    (Also loads category, venue, attendees due to relationships)
        │
        ▼
    Check Event.category relationship
    CascadeType = NONE (default)
    → Category is NOT deleted
        │
        ▼
    Check Event.attendees relationship
    CascadeType = NONE (not configured)
    → event_attendees records ARE deleted (join table cleanup)
    → User objects are NOT deleted
        │
        ▼
    DELETE FROM event WHERE id=1
        │
        ▼
    DELETE FROM event_attendees WHERE event_id=1
        │
        ▼
    Result:
    ✓ Event deleted
    ✗ Category still exists
    ✗ Venue/Location still exists
    ✗ Users still exist (only attendance link removed)


User Deletion (No Cascade on Profile)
═════════════════════════════════════════════════════════════════════

userRepository.deleteById(1);
        │
        ▼
    Fetch User with ID=1
    (Also loads profile due to @OneToOne(cascade=CascadeType.ALL))
        │
        ▼
    Check User.profile relationship
    CascadeType = ALL (includes DELETE)
    → ALSO delete UserProfile with ID=X
        │
        ▼
    DELETE FROM user_profile WHERE id=X
        │
        ▼
    DELETE FROM users WHERE id=1
        │
        ▼
    Result:
    ✓ User deleted
    ✓ UserProfile deleted (cascade)
    ✗ Location still exists
    ✗ Events still exist (user removed from attendees)
```

---

## Lazy vs Eager Loading

```
LAZY LOADING (Default Behavior)
═════════════════════════════════════════════════════════════════════

User user = userRepository.findById(1).get();
        │
        ▼
    SELECT * FROM users WHERE id=1;   [IMMEDIATE]
    ✓ User object created with ID=1
    ✗ profile NOT loaded yet
    ✗ location NOT loaded yet
    ✗ events NOT loaded yet
        │
        ▼
    String email = user.getEmail();    [IMMEDIATE]
    ✓ Returns "alice@example.com"
        │
        ▼
    UserProfile profile = user.getProfile();
        │
        ▼
    This is FIRST access to profile!
        │
        ▼
    SELECT * FROM user_profile WHERE id=X;   [LAZY LOAD TRIGGERED]
    ✓ Profile loaded from database
        │
        ▼
    Subsequent access: user.getProfile()     [CACHED IN MEMORY]
    ✓ No new database query


EAGER LOADING (If Configured)
═════════════════════════════════════════════════════════════════════

@ManyToOne(fetch = FetchType.EAGER)
private Location location;

User user = userRepository.findById(1).get();
        │
        ▼
    SELECT u.*, l.* FROM users u
    LEFT JOIN location l ON u.location_id = l.id
    WHERE u.id = 1;   [SINGLE QUERY]

    ✓ User object created
    ✓ Location already loaded with first query
    ✗ profile still lazy
    ✗ events still lazy
        │
        ▼
    Location location = user.getLocation();  [IMMEDIATE]
    ✓ No new database query (already loaded)
```

---

## Query Execution Examples

```
GENERATED QUERY EXAMPLE 1: findByEmail()
═════════════════════════════════════════════════════════════════════

Java Code:
  userRepository.findByEmail("alice@example.com")

Hibernate automatically generates:
  SELECT u FROM User u WHERE u.email = ?

SQL executed:
  SELECT * FROM users WHERE email = 'alice@example.com' LIMIT 1;

Returns:
  Optional<User>


GENERATED QUERY EXAMPLE 2: existsByEmail()
═════════════════════════════════════════════════════════════════════

Java Code:
  userRepository.existsByEmail("alice@example.com")

Hibernate automatically generates:
  SELECT 1 FROM User u WHERE u.email = ? LIMIT 1

SQL executed:
  SELECT 1 FROM users WHERE email = 'alice@example.com' LIMIT 1;

Returns:
  boolean (true/false)


CUSTOM JPQL QUERY EXAMPLE 3
═════════════════════════════════════════════════════════════════════

Java Code:
  @Query("SELECT u FROM User u WHERE u.location.code = :code OR u.location.name = :name")
  List<User> findUsersByProvinceCodeOrName(@Param("code") String code, @Param("name") String name)

Called with:
  findUsersByProvinceCodeOrName("GP", "Gauteng")

Hibernate converts to SQL:
  SELECT u.* FROM users u
  INNER JOIN location l ON u.location_id = l.id
  WHERE l.code = 'GP' OR l.name = 'Gauteng';

Returns:
  List<User> with location and profile data loaded
```

---

## Relationship Matrix

```
┌─────────────┬──────────┬──────────┬──────────────┬──────────┐
│ Source      │ Target   │ Type     │ Join Table   │ Cascade  │
├─────────────┼──────────┼──────────┼──────────────┼──────────┤
│ User        │ Location │ Many-1   │ No (FK)      │ No       │
│ User        │ Profile  │ One-1    │ No (FK)      │ YES(ALL) │
│ User        │ Event    │ Many-Many│ event_attend │ No       │
├─────────────┼──────────┼──────────┼──────────────┼──────────┤
│ Event       │ Category │ Many-1   │ No (FK)      │ No       │
│ Event       │ Location │ Many-1   │ No (FK)      │ No       │
│ Event       │ User     │ Many-Many│ event_attend │ No       │
├─────────────┼──────────┼──────────┼──────────────┼──────────┤
│ Category    │ Event    │ One-Many │ No (mapped)  │ YES(ALL) │
├─────────────┼──────────┼──────────┼──────────────┼──────────┤
│ Location    │ Location │ Many-1   │ No (FK)      │ No       │
│ Location    │ Event    │ One-Many │ No (mapped)  │ No       │
│ Location    │ User     │ One-Many │ No (mapped)  │ No       │
├─────────────┼──────────┼──────────┼──────────────┼──────────┤
│ UserProfile │ User     │ One-1    │ No (mapped)  │ No       │
└─────────────┴──────────┴──────────┴──────────────┴──────────┘

Cascade Types:
- NO       = Changes don't propagate
- ALL      = Includes PERSIST, MERGE, REMOVE, REFRESH, DETACH
- PERSIST  = Save related objects on parent save
- REMOVE   = Delete related objects on parent delete
```

---

## Quick Reference: Common Operations

```
CRUD OPERATIONS
═════════════════════════════════════════════════════════════════════

CREATE:
  User user = new User("Alice", "alice@example.com");
  userRepository.save(user);

READ (single):
  User user = userRepository.findById(1).get();
  User user = userRepository.findByEmail("alice@example.com").get();

READ (multiple):
  List<User> users = userRepository.findAll();
  Page<User> page = userRepository.findAll(pageable);

UPDATE:
  User user = userRepository.findById(1).get();
  user.setName("Alice Updated");
  userRepository.save(user);  // Save again to persist changes

DELETE (single):
  userRepository.deleteById(1);

DELETE (multiple):
  userRepository.deleteAll(userList);


PAGINATION OPERATIONS
═════════════════════════════════════════════════════════════════════

Create Pageable:
  Pageable p = PageRequest.of(0, 10);  // Page 0, 10 items
  Pageable p = PageRequest.of(1, 20, Sort.by("name"));  // With sort
  Pageable p = PageRequest.of(0, 10, Sort.by("name").descending());

Get Page info:
  page.getContent()      → List<T> (items on current page)
  page.getTotalElements()→ long (total items in database)
  page.getTotalPages()   → int (total number of pages)
  page.hasNext()         → boolean (more pages available)
  page.hasPrevious()     → boolean (previous page available)
  page.getPageNumber()   → int (current page index)


RELATIONSHIP OPERATIONS
═════════════════════════════════════════════════════════════════════

Add to Many-to-Many:
  Event event = eventRepository.findById(1).get();
  User user = userRepository.findById(1).get();
  event.addAttendee(user);  // Add to Set<User> attendees
  eventRepository.save(event);

Remove from Many-to-Many:
  event.removeAttendee(user);
  eventRepository.save(event);

Navigate relationship:
  User user = userRepository.findById(1).get();
  Location loc = user.getLocation();  // Auto-loaded
  UserProfile profile = user.getProfile();  // Lazy-loaded
```

---

## Performance Tips

```
✓ DO's
──────────────────────────────────────────────────────────────
✓ Use pagination for large datasets (@RequestParam Pageable)
✓ Use custom @Query with JPQL for complex queries
✓ Use exists() instead of findAll() to check if records exist
✓ Eagerly load if you know you'll access the relationship
✓ Use transactions for multi-step operations (@Transactional)
✓ Use partial projections for large result sets
✓ Use batch operations for bulk inserts/updates


✗ DON'Ts
──────────────────────────────────────────────────────────────
✗ Don't use findAll() without pagination (loads entire table!)
✗ Don't eagerly load relationships you won't use
✗ Don't fetch in a loop (N+1 query problem)
✗ Don't expose entities directly in REST endpoints (use DTOs)
✗ Don't forget @Transactional on methods that modify data
✗ Don't load large collections without lazy initialization
✗ Don't chain multiple left joins without need
```

---

## Common Error & Solutions

```
ERROR 1: LazyInitializationException
──────────────────────────────────────
Message: could not initialize proxy – no Session

Cause: Accessing lazy-loaded relationship after session closed

Solution 1: Use @Transactional on controller method
  @GetMapping("/{id}")
  @Transactional  // ← Add this
  public ResponseEntity<User> getUser(@PathVariable Long id) { ... }

Solution 2: Change to eager loading
  @ManyToOne(fetch = FetchType.EAGER)
  private Location location;

Solution 3: Access relationships within transaction
  @Transactional
  public void process() {
    User user = repository.findById(1).get();
    user.getProfile().getBio();  // ← Safe, in transaction
  }


ERROR 2: Detached Entity Exception
───────────────────────────────────
Message: could not initialize proxy – no Session

Cause: Trying to save/update entity that's no longer managed

Solution 1: Use merge() instead of save()
  entityManager.merge(detachedUser);

Solution 2: Re-fetch from database
  User managed = repository.findById(user.getId()).get();
  managed.setName("New Name");
  repository.save(managed);


ERROR 3: Constraint Violation
──────────────────────────────
Message: Duplicate entry for key 'email'

Cause: Unique constraint violation

Solution: Check before creating
  if (!userRepository.existsByEmail(email)) {
    User user = new User(name, email);
    repository.save(user);
  }


ERROR 4: N+1 Query Problem
──────────────────────────
Symptom: 1 query for users + 1 query PER user for location

Cause: Loading relationship in loop triggers query per item

Solution: Use JOIN FETCH in JPQL
  @Query("SELECT u FROM User u JOIN FETCH u.location")
  List<User> findAllWithLocation();
```

---

## Database State After Demo Run

```
┌────────────────────────────────────────────────────────┐
│              DATABASE TABLES POPULATED                 │
├────────────────────────────────────────────────────────┤
│                                                         │
│ LOCATION TABLE (5 records)                             │
│ ┌──┬──────────────┬─────┬──────┬───────┐               │
│ │id│name          │code │type  │parent │               │
│ ├──┼──────────────┼─────┼──────┼───────┤               │
│ │1 │Gauteng       │GP   │Prov. │null   │               │
│ │2 │KwaZulu-Natal │KZN  │Prov. │null   │               │
│ │3 │Johannesburg  │JHB  │City  │1      │               │
│ │4 │Durban        │DBN  │City  │2      │               │
│ │5 │FNB Stadium   │FNB  │Venue │3      │               │
│ └──┴──────────────┴─────┴──────┴───────┘               │
│                                                         │
│ CATEGORY TABLE (2 records)                             │
│ ┌──┬──────────────┬─────────────────────────────┐      │
│ │id│name          │description                 │      │
│ ├──┼──────────────┼─────────────────────────────┤      │
│ │1 │Music         │Music Festivals and Concerts│      │
│ │2 │Technology   │Tech Conferences and Hackath│      │
│ └──┴──────────────┴─────────────────────────────┘      │
│                                                         │
│ USER_PROFILE TABLE (3 records)                         │
│ ┌──┬──────────────────┬───────────┐                   │
│ │id│bio               │phone      │                   │
│ ├──┼──────────────────┼───────────┤                   │
│ │1 │Tech Enthusiast   │555-0101   │                   │
│ │2 │Music Lover       │555-0102   │                   │
│ │3 │Developer         │555-0103   │                   │
│ └──┴──────────────────┴───────────┘                   │
│                                                         │
│ USERS TABLE (3 records)                                │
│ ┌──┬───────────────┬──────────────────┬─────┬────┐    │
│ │id│name           │email             │loc. │pro.│    │
│ ├──┼───────────────┼──────────────────┼─────┼────┤    │
│ │1 │Alice Smith    │alice@example.com │1    │1   │    │
│ │2 │Bob Jones      │bob@example.com   │2    │2   │    │
│ │3 │Charlie Brown  │charlie@example.. │1    │3   │    │
│ └──┴───────────────┴──────────────────┴─────┴────┘    │
│                                                         │
│ EVENT TABLE (2 records)                                │
│ ┌──┬────────────────┬──────────┬───┬────┐             │
│ │id│title           │category. │ven│date│             │
│ ├──┼────────────────┼──────────┼───┼────┤             │
│ │1 │Summer Fest     │1 (Music) │5  │+30d│             │
│ │2 │Tech Innovate   │2 (Tech)  │5  │+60d│             │
│ └──┴────────────────┴──────────┴───┴────┘             │
│                                                         │
│ EVENT_ATTENDEES TABLE (4 records)                      │
│ ┌──────────┬────────┐                                  │
│ │event_id  │user_id │                                  │
│ ├──────────┼────────┤                                  │
│ │1 (Fest)  │2 (Bob) │                                  │
│ │1 (Fest)  │1 (Alc) │                                  │
│ │2 (Hack)  │1 (Alc) │                                  │
│ │2 (Hack)  │3 (Chl) │                                  │
│ └──────────┴────────┘                                  │
│                                                         │
└────────────────────────────────────────────────────────┘
```

---

## Summary

This Event Management System demonstrates:

- **One-to-One relationships** (User ↔ UserProfile)
- **One-to-Many relationships** (Category → Events, Location → Users)
- **Many-to-Many relationships** (Event ↔ Users via event_attendees)
- **Self-referencing relationships** (Location hierarchy with parent)
- **Cascade operations** (Delete parent affects related children)
- **Pagination & sorting** (Efficient data retrieval)
- **Custom JPQL queries** (Complex filtering)
- **Transaction management** (Atomicity of operations)
- **Spring Data JPA** (Automatic repository implementations)
- **Hibernate ORM** (Object-relational mapping)
