package eu.binflux.config;


import eu.binflux.config.collection.ExpiringMap;
import eu.binflux.config.collection.ExpiringOffHeapMap;
import eu.binflux.config.collection.OffHeapMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ExpiringOffHeapTest {

    static ExpiringMap<OffHeapObject> config;

    @BeforeClass
    public static void before() {
        config = new ExpiringMap<>(500, TimeUnit.MILLISECONDS, 250);
    }

    @AfterClass
    public static void after() {

    }

    @Test
    public void testA() throws InterruptedException {
        System.out.println("Test A list-setting");
        config.put("test1", new OffHeapObject("Ich bin sooo cool und du nicht :P", -1));
        Thread.sleep(3500);
    }

    @Test
    public void testB() {
        System.out.println("Test B list-updating");
        System.out.println("Contains: " + (config.containsKey("test1")));
    }

    @Test
    public void testC() throws InterruptedException {
        System.out.println("Test C list-updating");
        config.put("test1", new OffHeapObject("Ich bin sooo cool und du nicht :P", -1));
        System.out.println("Contains: " + (config.containsKey("test1")));
        Thread.sleep(750);
        System.out.println("Contains: " + (config.containsKey("test1")));
    }


}
