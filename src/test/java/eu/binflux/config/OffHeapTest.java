package eu.binflux.config;


import eu.binflux.config.collection.OffHeapMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OffHeapTest {

    static OffHeapMap<OffHeapObject> config;

    @BeforeClass
    public static void before() {
        config = new OffHeapMap<>("offheap_test", true);
    }

    @AfterClass
    public static void after() {

    }

    @Test
    public void testA() {
        System.out.println("Test A list-setting");
        config.put("test1", new OffHeapObject("Ich bin sooo cool und du nicht :P", -1));
    }

    @Test
    public void testB() {
        System.out.println("Test B list-updating");
        System.out.println("Contains: " + (config.containsKey("test1")));
    }

    @Test
    public void testC() {
        System.out.println("Test C list-updating");
        System.out.println("get: " + (config.get("test1").getString()));
    }


}
