package eu.binflux.config;

import java.io.Serializable;

@Deprecated
public interface ConfigObject extends Serializable {

    String getFileIdentifier();
}
