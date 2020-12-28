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
    static DataDirectory<UserHome> userHomeDataDirectory;
    static String[] usernames = new String[]{
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

        userHomeDataDirectory = new DataDirectory<UserHome>("userhomes", "home") {
            @Override
            public UserHome mapFromFileConfig(FileConfig fileConfig) {
                String homeId = fileConfig.getString("homeId");
                String userId = fileConfig.getString("userId");
                String homeName = fileConfig.getString("homeName");
                String location = fileConfig.getString("location");
                return new UserHome(homeId, userId, homeName, location);
            }

            @Override
            public FileConfig saveInFileConfig(UserHome configObject, FileConfig fileConfig) {
                fileConfig.set("homeId", configObject.getHomeId());
                fileConfig.set("userId", configObject.getUserId());
                fileConfig.set("homeName", configObject.getHomeName());
                fileConfig.set("location", configObject.getLocation());
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
        for (String username : usernames) {
            String password = getPassword() + "";
            if (!userDataDirectory.existsAny(Filter.equals("username", username))) {
                String userId = UUID.randomUUID().toString();
                if (!userDataDirectory.existsAny(Filter.equals("userId", userId))) {
                    userDataDirectory.save(new User(userId, username, password));
                }
            }
        }
        System.out.println("Took: " + ((System.currentTimeMillis() - start)) + "ms");
    }

    @Test
    public void testB() {
        String homeName = "base";
        String location = "1,2,3,4";

        String playerName = "Koboo"; //args[0]
        Filter.EqualsFilter filter = Filter.equals("username", playerName);



        if(userDataDirectory.existsAny(filter)) {
            User user = userDataDirectory.findAny(filter);
            userDataDirectory.save(user);
        }

        if(userDataDirectory.existsAny(Filter.equals("username", "Koboo"), Filter.equals("rank", "Admin"))) {
            User user = userDataDirectory.findAny(Filter.equals("username", "Koboo"), Filter.equals("rank", "Admin"));
        }
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

    public static class UserHome implements DataFile {

        String homeId;
        String userId;
        String homeName;
        String location;

        public UserHome(String homeId, String userId, String homeName, String location) {
            this.homeId = homeId;
            this.userId = userId;
            this.homeName = homeName;
            this.location = location;
        }

        public String getHomeId() {
            return homeId;
        }

        public String getUserId() {
            return userId;
        }

        public String getHomeName() {
            return homeName;
        }

        public String getLocation() {
            return location;
        }

        @Override
        public String getFileIdentifier() {
            return getHomeId();
        }
    }

}
