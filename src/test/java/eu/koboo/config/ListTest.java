package eu.koboo.config;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ListTest {

    static FileConfig config;

    @BeforeClass
    public static void before() {
        config = Config.of("test.cfg");
        System.out.println("== " + config.getFileName());
    }

    @AfterClass
    public static void after() {

    }

    @Test
    public void testA() {
        System.out.println("== Test A list set");
        List<String> testList = Arrays.asList("ABC dwdw", "dtwfrvgw", "Odzuh", "dzgb3u2", "tgd2sd", "Does it update successfully?");
        config.setList("test", testList, "Very beautiful comment");
        List<String> getter = config.getList("test");
        getter.forEach(System.out::println);
    }

    @Test
    public void testB() {
        System.out.println("== Test B list types");
        List<? extends Number> testList = Arrays.asList(1, 2, 3.5, 5.333, 0.2);
        config.setList("numberList", testList, "Does this even work?");
        List<? extends Number> getter = config.getList("numberList");
        getter.forEach(System.out::println);
    }

    @Test
    public void testC() {
        System.out.println("Test C list update");
        List<String> testList = Arrays.asList("Does it update successfully?", "Check that folks", "No time needed", "one class File Configuration");
        config.setList("test", testList, "This comment is the beautifullest");
        List<String> getter = config.getList("test");
        getter.forEach(System.out::println);
    }

}
