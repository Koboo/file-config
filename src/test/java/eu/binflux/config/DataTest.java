package eu.binflux.config;


import eu.binflux.config.data.DataDirectory;
import eu.binflux.config.data.DataFile;
import eu.binflux.config.data.Filter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class DataTest {

    static DataDirectory<User> userDataDirectory;

    @BeforeClass
    public static void before() {
        userDataDirectory = new DataDirectory<User>("user", "user") {
            @Override
            public User mapFromFileConfig(FileConfig fileConfig) {
                String userId = fileConfig.getString("userId");
                String username = fileConfig.getString("username");
                String password = fileConfig.getString("password");
                return new User(userId, username, password);
            }

            @Override
            public FileConfig saveInFileConfig(User configObject, FileConfig fileConfig) {
                fileConfig.set("userId", configObject.getUserId());
                fileConfig.set("username", configObject.getUsername());
                fileConfig.set("password", configObject.getPassword());
                return fileConfig;
            }
        };
    }

    @AfterClass
    public static void after() {

    }

    @Test
    public void testA() {
        System.out.println("Test A data-directory");
        final long start = System.currentTimeMillis();
        String[] usernames = new String[]{
                "Koboo",
                "Rizon",
                "Scope",
                "Pawii",
                "HeyHo",
                "Elia",
                "John",
                "Thomas",
                "Lukas",
                "Tom",
                "Vincent",
                "Jan",
                "Justin",
                "Daniela",
                "Marie",
                "Anna-Lena",
                "Hans Peter",
                "Execute",
                "Hopsi",
                "Kari",
                "Finn",
                "Nutzer",
        };
        for (String username : usernames) {
            String password = getPassword() + "";
            if (!userDataDirectory.filterEquals(Filter.equals("username", username))) {
                String userId = UUID.randomUUID().toString();
                if (!userDataDirectory.filterEquals(Filter.equals("userId", userId))) {
                    userDataDirectory.saveIdentifier(new User(userId, username, password));
                }
            } else {
                User user = userDataDirectory.findEquals(Filter.equals("username", username));
                List<User> userList = userDataDirectory.collectEquals(Filter.equals("username", username));
                System.out.println("Exists: " + user.getUserId() + "/" + user.getUserId());
            }
        }
        System.out.println("Took: " + ((System.currentTimeMillis() - start)) + "ms");
    }

    public static int getPassword() {
        int pw = new Random().nextInt(8999992);
        if (pw == 0)
            pw = getPassword();
        return pw;
    }

    public static class User implements DataFile {

        String userId;
        String username;
        String password;

        public User(String userId, String username, String password) {
            this.userId = userId;
            this.username = username;
            this.password = password;
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String getFileIdentifier() {
            return getUserId();
        }
    }

}
