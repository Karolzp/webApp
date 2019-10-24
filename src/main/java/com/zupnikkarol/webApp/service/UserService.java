package com.zupnikkarol.webApp.service;

import com.zupnikkarol.webApp.model.CSVUsersFileReader;
import com.zupnikkarol.webApp.model.User;
import com.zupnikkarol.webApp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        CSVUsersFileReader csvUsersFileReader = new CSVUsersFileReader();
        List<User> users = csvUsersFileReader.getListOfUsersFromFile("users.csv");
        addUsersToDatabase(users);
    }

    private void addUsersToDatabase(List<User> users) {
        for (User user : users) {
            if (user.getPhoneNumber() != null && userRepository.existsUserByPhoneNumber(user.getPhoneNumber())) {
                log.info("User not saved - {} - There is user with the same phone number.", user.toString());
            } else {
                try {
                    userRepository.save(user);
                    log.info("User saved - {}", user.toString());
                } catch (DataAccessException e) {
                    log.info("incorrect user details for user: {}", user.toString());
                }
            }
        }
    }

    public ResponseEntity<Long> getNumberOfUsers() {
        return new ResponseEntity<>(userRepository.count(), HttpStatus.OK);
    }

    public Page<User> getUsersSortedByAge(int page) {
        return userRepository.findAll(PageRequest.of(page, 5, Sort.by("birthDate")));
    }

    public ResponseEntity<User> getOldestUserWithPhoneNumber() {
        List<User> user = userRepository.findTop1ByPhoneNumberIsNotNullOrderByBirthDate();
        if (user.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.get(0), HttpStatus.OK);
    }

    public ResponseEntity<String> deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> deleteUsers(List<Integer> listOfIds) {
        for (Integer id : listOfIds) {
            if (id < 0) {
                return new ResponseEntity<>("The id must be greater than 0", HttpStatus.BAD_REQUEST);
            }
            if (!userRepository.existsById(id.longValue())) {
                log.info("Users with Ids: {} not deleted. One of them with id: {} not exists", listOfIds, id);
                return new ResponseEntity<>("user with id " + id + " not found", HttpStatus.NOT_FOUND);
            }
        }

        for (Integer id : listOfIds) {
            userRepository.deleteById(id.longValue());
        }
        log.info("Users with Ids: {} deleted.", listOfIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Collection<User>> getUserByLastName(String lastName) {
        if (lastName.length() < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<User> users = userRepository.findAllByLastName(lastName);
        if (users.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }
}
