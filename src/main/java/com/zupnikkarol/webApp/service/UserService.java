package com.zupnikkarol.webApp.service;

import com.zupnikkarol.webApp.model.CSVUsersFileReader;
import com.zupnikkarol.webApp.model.User;
import com.zupnikkarol.webApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
                System.out.println("Can't save " + user.getFirstName() + " " + user.getLastName() + ". There is user with the same phone number " + user.getPhoneNumber());
            } else {
                userRepository.save(user);
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
            if (!userRepository.existsById(id.longValue())) {
                return new ResponseEntity<>("user with id " + id + " not found", HttpStatus.NOT_FOUND);
            }
        }
        for (Integer id : listOfIds) {
            userRepository.deleteById(id.longValue());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Collection<User>> getUserByLastName(String lastName) {
        List<User> users = userRepository.findAllByLastName(lastName);
        if (users.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }
}
