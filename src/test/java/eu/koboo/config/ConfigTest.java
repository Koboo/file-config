package eu.koboo.config;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        System.out.println("Test A list set");
        List<String> testList = Arrays.asList("ABC dwdw", "dtwfrvgw", "Odzuh", "dzgb3u2", "tgd2sd", "Does it update successfully?");
        config.setList("test", testList, "Very beautiful comment");
        List<String> getter = config.getList("test");
        getter.forEach(System.out::println);
    }

    @Test
    public void testB() {
        System.out.println("Test B list update");
        List<String> testList = Arrays.asList("Does it update successfully?", "Check that folks", "No time needed", "one class File Configuration");
        config.setList("test", testList, "This comment is the beautifullest");
        List<String> getter = config.getList("test");
        getter.forEach(System.out::println);
    }

    @Test
    public void testC() {
        System.out.println("Test C type change");
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
        System.out.println("Test E remove");
        config.remove("testBytes");
    }

    @Test
    public void testF() {
        System.out.println("Test F type parsing");
        config.set("koboo", "Koboooooo");
        String text = config.get("koboo");
        System.out.println(text);
        config.set("testInnnt", 1234567);
        int testInnnt = config.get("testInnnt");
        System.out.println(testInnnt);
    }

    @Test
    public void testG() {
        System.out.println("Test G key-value");
        for(Map.Entry<String, Object> entry : config.allKeyValues().entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    @Test
    public void testH() {
        System.out.println("Test H list types");
        List<? extends Number> testList = Arrays.asList(1, 2, 3.5, 5.333, 0.2);
        config.setList("numberList", testList, "Does this even work?");
        List<? extends Number> getter = config.getList("numberList");
        getter.forEach(System.out::println);
    }

    @Test
    public void testI() {
        System.out.println("Test I Resources");
        FileConfig cfg = FileConfig.fromResource("test_rsc.cfg");
        cfg.allKeys().forEach(key -> System.out.println(cfg.get(key) + ""));
    }

}
