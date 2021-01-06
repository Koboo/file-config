package eu.binflux.config;

import java.io.Serializable;

public class OffHeapObject implements Serializable {

    String string;
    int integer;

    public OffHeapObject(String string, int integer) {
        this.string = string;
        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public int getInteger() {
        return integer;
    }
}
