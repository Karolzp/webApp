package com.zupnikkarol.webApp.model;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UsersFileReader {

    List<User> getListOfUsersFromFile(String fileName);

}
