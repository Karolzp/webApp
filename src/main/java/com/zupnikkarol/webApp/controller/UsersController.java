package com.zupnikkarol.webApp.controller;

import com.zupnikkarol.webApp.model.User;
import com.zupnikkarol.webApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
public class UsersController {

    private UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users/count")
    public ResponseEntity<Long> getNumberOfUsers() {
        return userService.getNumberOfUsers();
    }

    @GetMapping(value = "users/sorted-by-age", params = {"page"})
    public Page<User> getUsersSortedByAge(@RequestParam("page") int page) {
        return userService.getUsersSortedByAge(page);
    }

    @GetMapping("users/oldest-with-phone-number")
    public ResponseEntity<User> getOldestUserWithPhoneNumber() {
        return userService.getOldestUserWithPhoneNumber();
    }

    @DeleteMapping("users/{listOfIds}")
    public ResponseEntity<String> deleteUsers(@PathVariable List<Integer> listOfIds) {
        return userService.deleteUsers(listOfIds);
    }

    @GetMapping("users/{lastName}")
    public ResponseEntity<Collection<User>> getUserByLastName(@PathVariable("lastName") String lastName) {
        return userService.getUserByLastName(lastName);
    }
}
