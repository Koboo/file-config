package eu.binflux.config;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ConfigTest {

    static FileConfig config;

    @BeforeClass
    public static void before() {
        config = FileConfig.newConfig("test.cfg", dConfig -> {
            dConfig.init("default", 2);
            dConfig.init("adv", "dzndwkplö");
            dConfig.init("Gotten", "sh");
            dConfig.init("Lorem ipsum", "dolor sit amet");
        });
        System.out.println(config.getFileName());
    }

    @AfterClass
    public static void after() {

    }

    @Test
    public void testA() {
        System.out.println("Test A list-setting");
        List<String> testList = Arrays.asList("ABC dwdw", "dtwfrvgw", "Odzuh", "dzgb3u2", "tgd2sd", "Does it update successfully?");
        config.setList("test", testList, "Very beautiful comment");
        List<String> getter = config.getList("test");
        getter.forEach(System.out::println);
    }

    @Test
    public void testB() {
        System.out.println("Test B list-updating");

        List<String> testList = Arrays.asList("Does it update successfully?", "Check that folks", "No time needed", "one class File Configuration");

        config.setList("test", testList, "This comment is the beautifullest");

        List<String> getter = config.getList("test");

        getter.forEach(System.out::println);
    }

    @Test
    public void testC() {
        System.out.println("Test C type-changing");
        String value = config.getString("Lorem ipsum");
        System.out.println("String: " + value);
        config.set("Lorem ipsum", 5000.55, "Can u read this?");
        double valueD = config.getDouble("Lorem ipsum");
        config.set("Lorem ipsum", "dolor sit amet, polus nor emiter", "Not a comment sry mate");
        System.out.println("Double: " + valueD);
    }

    @Test
    public void testD() {
        System.out.println("Test D byte-array");
        String someText = "I'm so cool, i'm using my own FileConfig";
        byte[] someBytes = someText.getBytes();
        config.set("testBytes", someBytes);

        byte[] testBytes = config.getByteArray("testBytes");
        System.out.println(new String(testBytes));
    }

    @Test
    public void testE() {
        System.out.println("Test E unset");
        config.unset("testBytes");
    }

}
