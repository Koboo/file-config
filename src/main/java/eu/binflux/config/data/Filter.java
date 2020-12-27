package eu.binflux.config.data;

public class Filter {

    public static EqualsFilter equals(String key, Object value) {
        return new EqualsFilter(key, value);
    }

    public static ExistsFilter exists(String key) {
        return new ExistsFilter(key);
    }

    public static class EqualsFilter {

        private final String key;
        private final Object value;

        private EqualsFilter(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

    }

    public static class ExistsFilter {

        private final String key;

        private ExistsFilter(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

    }

}