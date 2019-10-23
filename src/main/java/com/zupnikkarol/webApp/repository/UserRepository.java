package com.zupnikkarol.webApp.repository;

import com.zupnikkarol.webApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findTop1ByPhoneNumberIsNotNullOrderByBirthDate();

    List<User> findAllByLastName(String lastName);

    boolean existsUserByPhoneNumber(Integer phoneNumber);
}
