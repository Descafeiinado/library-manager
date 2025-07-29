package br.edu.ifba.inf008.core.domain.interfaces;

/**
 * Interface representing an entity that has a name. This interface provides methods to get and set
 * the name of the entity.
 */
public interface Nameable {

    /**
     * Returns the name of the entity.
     *
     * @return the name of the entity
     */
    String getName();

    /**
     * Sets the name of the entity.
     *
     * @param name the name to set
     */
    void setName(String name);
}
