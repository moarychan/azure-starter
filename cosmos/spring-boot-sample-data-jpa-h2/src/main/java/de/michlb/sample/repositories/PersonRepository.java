package de.michlb.sample.repositories;

import de.michlb.sample.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by mbart on 28.02.2016.
 */
public interface PersonRepository extends JpaRepository<Person, Long>{

//    List<Person> findByNameIgnoreCase(String name);
//
//    List<Person> findByNameAndNicknameAllIgnoreCase(String name, String nickname);
}
