# TP Spring MVC Spring Data JPA Thymeleaf
## project initialization
- first we need to initialize the project with four dependencies which are :
  - h2 database
  - spring web
  - lombok
  - spring data jpa
- first we will use h2 database to test our app, but later we will migrate to mysql with maria-db dialect as we add more features.
## Patient entity
- we are working on patient database so we will create a Patient entity, using jpa annotation for orm, and lombok annotations to generate the getters and setters and constructors.
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Patient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private Date dateNaissance;
    private boolean malade;
    private int score;
}
```
## Patient Repository
- after creating the entity class we need to create repository class that will allow us to make use of spring data functionality that simplify object mapping.
- the repository comes with a bunch of method that helps us managing our model.
- additionly we are creating a method to search for the patient by their names `nom`, we will use two methods, one is the native method of spring data and the second using query annotation.
```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    public Page<Patient> findByNomContains(String nom, Pageable pageable);
    @Query("select p from Patient p where p.nom like :x")
    public Page<Patient> search(@Param("x") String nom, Pageable pageable);
}
```
## Patient Controller
- now it's time to create the controller, that will be responsible for executing the requests of the client and redirecting to the corresponding pages.
- the pages will be created with thymeleaf template engine.
- here we have `@RequestParam` annotation that bind the request parameters with java arguments, like we did with page, size and keyword.
- `@GetMapping("/route")` annotation is telling us, the next method will be executed if the client searched for `route`.
- `Model` class is the wrapper that will hold the data from this class to the thymeleaf page.
- the methods of the controller simply return a string, that represent the name of the thymeleaf page in our project.
- In delete method we have returned `redirect:/index` which will create another get request that finally call `index`
```java
public class PatientController {
    private PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }
    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "5") int size,
                        @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        Page<Patient> patients = patientRepository.findByNomContains(keyword, PageRequest.of(page,size));
        model.addAttribute("patients", patients.getContent());
        model.addAttribute("pages", new int[patients.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "patients";
    }

    @GetMapping("/delete")
    public String delete(Model model, Long id, int page, String keyword) {
        patientRepository.deleteById(id);
        return "redirect:/index?page=" + page + "&keyword=" + keyword;
    }
}
```
## Additional features
- after creating the controller and the repository we are adding other functionalities like adding new patient, and modifying it, by simply creating new thymeleaf pages and other methods in the controller.
### Validation
- additionally we create a validation for patient data where we add or modify a patient by using `Spring boot starter validation`.
- this last one provide us with annotations that will be added to the `Entity` class and the `argument` of the controller method.
```java
@NotEmpty @Size(min = 4, max = 10)
private String nom;

@DecimalMin("100")
private int score;
```
```java
@PostMapping(path = "/editPatient")
public String editPatient(Model model,
                          @Valid Patient patient,
                          BindingResult bindingResult,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "") String keyword) {
    System.out.println(patient);
    if(bindingResult.hasErrors()) return "form-patients";
    patientRepository.save(patient);
    return "redirect:/index?page="+page+"&keyword="+keyword;
}
```
- the annotations `@NotEmpty`, `@Size` and `@DecimalMin` serve to set rules on variables in the form.
- the annotation `@Valid` serve as an activator of the rules or the contraints that we have set in the model.
- the variable `BindingResult` works always with the annotation `@Valid` and it holds the errors of the validation that were not respected.
### Template
- thymeleaf enable us to use a html page as a template.
- for example we can put a nav bar at a page and use it as template.
- this helps us in avoiding redundant code between pages.
- we can define a template by adding the following code in `<html>` tag
```html
<html lang="en"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://ultraq.net.nz/thymeleaf/layout"
>
```
- in our case we used `template1.html` as a template, we put the nav bar on it and a section that hold the other page.
```html
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  ... 
</nav>
<section layout:fragment="content1"></section>
```
- additionally when we call a page that particular page must mention the template
- we do that by simply adding an attribute to `html` tag which is `layout:decorate='name-of-template''` and adding the attribute `layout:fragment="content1"` to the wrapper of the page.
```html
<html lang="en"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://ultraq.net.nz/thymeleaf/layout"
      layout:decorate="template1"
>
```
```html
<body>
    <div layout:fragment="content1">
        ...
    </div>
</body>
```
## Security part
### SecurityConfig class
- we start by defining a class `SecurityConfig` that will enable us to define the configurations of spring security.
- we put `@Configuration` annotation before the class to allow spring to detect as a configuration class.
- we enable spring security by using the annotation `@EnableWebSecurity`, and it registers a SecurityFilterChain that processes incoming HTTP requests, and this annotation is necessary to allow spring security.
```java

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
}
```
- we have two method in our `SecurityConfig` class.
- the first method is `inMemoryUserDetailsManager` that creates and manages in-memory user authentication in your Spring Security configuration. It allows you to define users directly in the application without needing a database.
```java
@Bean
public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
  return new InMemoryUserDetailsManager(
          User.withUsername("user1").password(passwordEncoder.encode("1234")).roles("USER").build(),
          User.withUsername("user2").password(passwordEncoder.encode("1234")).roles("USER").build(),
          User.withUsername("admin").password(passwordEncoder.encode("1234")).roles("USER", "ADMIN").build()
  );
}
```
- the second is `securityFilterChain` method which is responsible for defining the security rules in your application. It creates and configures a `SecurityFilterChain` bean, which controls authentication, authorization, and session management in Spring Security.
- in requestMatchers we write the path with `/**` like `/user/**` which mean any path after `/user/` will hold the corresponding rules.
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
  return httpSecurity
          .formLogin(ar -> ar.loginPage("/login").permitAll())
          .rememberMe(rm -> rm.key("remember-me-key") // Enables remember-me feature
                  .tokenValiditySeconds(86400) // Token valid for 1 day (optional)
          )
          .exceptionHandling(ar -> ar.accessDeniedPage("/notAuthorized"))
          .authorizeHttpRequests(ar -> ar
                  .requestMatchers("/webjars/**", "/css/**", "/js/**", "/h2-console/**").permitAll()
                  .requestMatchers("/user/**").hasRole("USER")
                  .requestMatchers("/admin/**").hasRole("ADMIN")
                  .anyRequest().authenticated()
          )
          .build();
}
```
### Not authorized page
- after writing a rule in `securityFilterChain` method that indicate a redirection if there is an exception.
```java
httpSecurity.exceptionHandling(ar -> ar.accessDeniedPage("/notAuthorized"));
```
- we define a controller responsible for security routes where we define a method `notAuthorized()` for redirecting to the page `notAutorized.html`
```java
@Controller
public class SecurityController {
    @GetMapping("/notAuthorized")
    public String notAuthorized() {
        return "notAuthorized";
    }
}
```
### Login customization
- to customize the login page we create a page `login.html` and we configure it with the method `formLogin` of `HttpSecurity` object 
```java
httpSecurity.formLogin(ar -> ar.loginPage("/login").permitAll());
```
### Remember me functionality
- if we want the app to remember the login we can use remember me functionality. by using a form with `post` method to the route `login` and a checkbox with the name `remember-me` we can tell spring to activate remember me functionality.
```html
<form method="post" th:action="@{/login}">
    <input type="checkbox" name="remember-me"> Remember me
    <button type="submit">login</button>
</form>
```
### redirection from login
- if we want to always redirect to a page like home after login its done by `defaultSuccessUrl("route")`.
```java
httpSecurity
        .formLogin(ar -> ar.loginPage("/login").defaultSuccessUrl("/").permitAll())
```
### Save users and roles to the database
- the user and the roles weren't stocked with the previous method, it is crucial to save them in the database for flexible interactions.
- first we should disable the method that return `InMemoryUserDetailManager` class.
- second create a method that return `JdbcUserDetailsManager` class.
```java
@Bean
public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
  return new JdbcUserDetailsManager(dataSource);
}
```
- `DataSource` class is holding the same database configuration of `application.properties`.
- with `JdbcUserDetailsManager` we need to create the tables of users and roles, and we can find the structure in spring security library folder.
- then we create a file `schema.sql` in resources containing the script of creating the tables.
- but to make things work we need to change some properties in `application.properties`.
```properties
# we tell jpa to not regenerate the entities
# at the same time will look for a file in resources schema.sql and execute it.
spring.jpa.hibernate.ddl-auto=none

# If you're using data.sql or schema.sql along with JPA/Hibernate,
# setting this to true ensures that Hibernate creates the schema first before the scripts execute.
spring.jpa.defer-datasource-initialization=true

# each time we reload the project we execute the file schema.sql 
spring.sql.init.mode=always
```
- to seed the database we can use `data.sql` file that will be executed as `schema.sql`
- we can use `JdbcUserDetailsManager` class in a CommandLineRunner method, this gives us another way to create users.
```java
@Bean
CommandLineRunner commandLineRunner(JdbcUserDetailsManager jdbcUserDetailsManager) {
    return args -> {
        UserDetails ud1 = jdbcUserDetailsManager.loadUserByUsername("user11");
        if (ud1 == null)
            jdbcUserDetailsManager.createUser(
                    User.withUsername("user11").password(passwordEncoder.encode("1234")).roles("USER").build()
            );
    };
}
```
### User details service part
#### Custom user and role entities
- we start by adding our custom models `AppUser` & `AppRole`.
```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUser {
    @Id
    private String userId;
    @Column(unique = true)
    private String username;
    private String email;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<AppRole> roles;
}

```
```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AppRole {
    @Id
    private String role;
}
```
#### spring data repositories of user and role
- second we create two repositories or spring data interfaces to handle users and roles transactions.
```java
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);
}

public interface AppRoleRepository extends JpaRepository<AppRole, String> { 
    
}
```
#### AccountService
- for business layer we are creating one service to handle all account operations.
```java
public interface AccountService {
    AppUser addNewUser(String username, String password, String confirmPassword, String email);
    AppRole addNewRole(String role);
    void addRoleToUser(String username, String role);
    void removeRoleFromUser(String username, String role);
    AppUser loadUserByUsername(String username);
}
```
- and also we create an implementation of this interface `AccountServiceImpl`
```java
@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;
    ...
}
```
#### Execute at runtime
- to work with this new service we will comment the last `CommandLineRunner` method and create another one while injecting the service as an argument.
```java
@Bean
CommandLineRunner commandLineRunnerUserDetails(AccountService accountService) {
  return args -> {
    accountService.addNewRole("USER");
    accountService.addNewRole("admin");
    accountService.addNewUser("user1", "1234", "1234", "user1@gmail.com");
    accountService.addNewUser("user2", "1234", "1234", "user2@gmail.com");
    accountService.addNewUser("admin", "1234", "1234", "admin@gmail.com");

    accountService.addRoleToUser("user1", "USER");
    accountService.addRoleToUser("user2", "USER");
    accountService.addRoleToUser("admin", "USER");
    accountService.addRoleToUser("admin", "ADMIN");
  };
}
```
#### Configuration stuff
- we should tell hibernate to generate the database again as it was first, by changing the following property.
```properties
from
spring.jpa.hibernate.ddl-auto=none
to
spring.jpa.hibernate.ddl-auto=update
```
#### User details service
- now we are creating a custom user details service class.
```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private AccountService accountService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = accountService.loadUserByUsername(username);
        ...
    }
}
```
- after creating it we will tell spring to choose our custom `UserDetailsService` with a parameters in `SecurityFilterChain` of `SecurityConfig`
```java
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
            ...
            .exceptionHandling(ar -> ar.accessDeniedPage("/notAuthorized"))
            .userDetailsService(userDetailsServiceImpl)
            .authorizeHttpRequests(ar -> ar
            ...
}
```
- now when a client login spring will call the method `loadUserByUsername` of our custom class `UserDetailsServiceImpl`.




