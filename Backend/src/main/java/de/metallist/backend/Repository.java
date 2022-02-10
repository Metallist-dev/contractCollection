package de.metallist.backend;

import org.springframework.data.repository.CrudRepository;

/**
 * Interface, which holds the operations for the sql-database
 *
 * @author Metallist-dev
 * @version 0.1
 */
public interface Repository extends CrudRepository<Contract, Integer> {

}
