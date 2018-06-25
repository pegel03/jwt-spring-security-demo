package org.zerhusen.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.Person;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PersonRestService {
    private List<Person> persons;

    PersonRestService() {
        persons = new ArrayList<>();
        persons.add(new Person("Hello", "World"));
        persons.add(new Person("Foo", "Bar"));
    }

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(path = "/persons", method = RequestMethod.GET)
    public List<Person> getPersons() {
        List resultList = em.createQuery(
            "SELECT c FROM Authority c")
//            .setParameter("custName", name)
            .setMaxResults(10)
            .getResultList();
        resultList.forEach(x -> System.out.println("pgl results: " + x.toString()));
        return persons;
    }

    @RequestMapping(path = "/persons/{name}", method = RequestMethod.GET)
    public Person getPerson(@PathVariable("name") String name) {
        return persons.stream()
                .filter(person -> name.equalsIgnoreCase(person.getName()))
                .findAny().orElse(null);
    }
}
