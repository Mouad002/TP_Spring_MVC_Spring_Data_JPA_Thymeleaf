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
- f





