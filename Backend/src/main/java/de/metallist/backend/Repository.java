package de.metallist.backend;

import org.springframework.data.repository.CrudRepository;

/**
 * Interface, welches die Operationen für die SQL-Datenbank hält
 *
 * @author mhenke
 * @version 0.1
 */
public interface Repository extends CrudRepository<Vertrag, Integer> {

}
