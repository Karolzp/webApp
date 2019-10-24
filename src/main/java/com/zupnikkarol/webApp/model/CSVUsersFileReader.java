package com.zupnikkarol.webApp.model;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVUsersFileReader implements UsersFileReader {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private List<User> users = new ArrayList<>();
    private List<String[]> stringUsers = new ArrayList<>();

    /** Method getListOfUsersFromFile() upload csv file. File must be placed in resource folder.
        csv header should be like "first_name;last_name;birth_date;phone_no"
        @param fileName full file name
        @return list of users
*/
    @Override
    public List<User> getListOfUsersFromFile(String fileName) {
        stringUsers = readUsersFile(fileName);
        stringUsers = validateReadFile(stringUsers);
        users = parseReadFileToArrayOfUsers(stringUsers);
        log.info("{} file with user details read", fileName);

        return users;
    }

    private List<User> parseReadFileToArrayOfUsers(List<String[]> listOfUserArrays) {
        List<User> listOfUsers = new ArrayList<>();
        for (String[] userDetail : listOfUserArrays) {
            User user = new User();
            user.setFirstName(userDetail[header.FIRSTNAME.id()].toLowerCase());
            user.setLastName(userDetail[header.LASTNAME.id()].toLowerCase());

            String[] arrOfStr = userDetail[header.BIRTHDATE.id()].split("\\.");
            int year = Integer.parseInt(arrOfStr[header.FIRSTNAME.id()]);
            int month = Integer.parseInt(arrOfStr[header.LASTNAME.id()]);
            int day = Integer.parseInt(arrOfStr[header.BIRTHDATE.id()]);
            user.setBirthDate(LocalDate.of(year, month, day));
            if (userDetail[header.PHONENUMBER.id()] != null) {
                user.setPhoneNumber(Integer.valueOf(userDetail[header.PHONENUMBER.id()]));
            }
            listOfUsers.add(user);

        }
        return listOfUsers;
    }

    private List<String[]> validateReadFile(List<String[]> listOfUserArrays) {
        removeSpacesFromReadFile();
        List<String[]> temporaryStringUsers = new ArrayList<>();
        for (String[] userDetail : listOfUserArrays) {
            if (userDetail.length >= 3) {
                if (isWord(userDetail[header.FIRSTNAME.id()]) && isWord(userDetail[header.LASTNAME.id()]) && isDate(userDetail[header.BIRTHDATE.id()])) {
                    if (userDetail.length >= 4 && isPhoneNumber(userDetail[header.PHONENUMBER.id()])) {
                        temporaryStringUsers.add(new String[]{userDetail[header.FIRSTNAME.id()], userDetail[header.LASTNAME.id()], userDetail[header.BIRTHDATE.id()], userDetail[header.PHONENUMBER.id()]});
                    } else {
                        temporaryStringUsers.add(new String[]{userDetail[header.FIRSTNAME.id()], userDetail[header.LASTNAME.id()], userDetail[header.BIRTHDATE.id()], null});
                    }
                }
            }
        }

        return new ArrayList<>(temporaryStringUsers);
    }

    private boolean isWord(String word) {
        return word.matches("[a-zA-ZąćęłńóśźżĄĘŁŃÓŚŹŻ]+");
    }

    private boolean isDate(String word) {
        return word.matches("[0-9]{4}.[0-9]{1,2}.[0-9]{1,2}");
    }

    private boolean isPhoneNumber(String word) {
        return word.matches("[0-9]{9}");
    }

    private void removeSpacesFromReadFile() {
        for (String[] row : stringUsers) {
            for (int n = 0; n < row.length; n++) {
                String newString = row[n].trim();
                row[n] = newString;
            }
        }
    }

    private List<String[]> readUsersFile(String fileName) {

        List<String[]> rows = new ArrayList<>();
        Resource resource = new ClassPathResource(fileName);

        try {
            File file = resource.getFile();

            CSVReader csvReader = new CSVReaderBuilder(new FileReader(file)).
                    withCSVParser(new CSVParserBuilder().withSeparator(';').build()).
                    withSkipLines(1).build();

            rows = csvReader.readAll();
        } catch (Exception e) {
            log.warn("{} file with user details not read", fileName);
            e.printStackTrace();
        }
        return rows;
    }

    // Headers from csv file and theirs columns id
    private enum header {
        FIRSTNAME(0),
        LASTNAME(1),
        BIRTHDATE(2),
        PHONENUMBER(3);

        private final int id;

        header(int number) {
            this.id = number;
        }

        public int id() {
            return id;
        }
    }
}
