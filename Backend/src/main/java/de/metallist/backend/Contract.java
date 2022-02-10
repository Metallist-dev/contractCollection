package de.metallist.backend;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.Optional;


/**
 * Model, which represents the contract with their most important attributes
 *
 * @author Metallist-dev
 * @version 0.1
 *
 */
@Entity
@Slf4j
public class Contract {
    @Id
    @Getter @Setter
    private int id;

    @Getter @Setter
    private String category;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private float expenses;

    /** number of payments per year (1, 2, 4, 12) */
    @Getter @Setter
    private int cycle;

    @Getter @Setter
    private String customerNr;

    @Getter @Setter
    private String contractNr;

    /** start of the contract in format "YYYY-MM-DD" */
    @Getter @Setter
    private String startDate;

    /** length of a contractual period in months */
    @Getter @Setter
    private int contractPeriod;

    /** length of a period of notice in weeks */
    @Getter @Setter
    private int periodOfNotice;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String documentPath;


    /**
     * adds a new contract
     * @param category          category
     * @param name              readable name
     * @param expenses          expenses per payment
     * @param cycle             cycle (see {@link #cycle})
     * @param customerNr        ID of customer
     * @param contractNr        ID of contract
     * @param startDate         start of the contract in format "YYYY-MM-DD"
     * @param contractPeriod    length of a contractual period
     * @param periodOfNotice    length of a period of notice in weeks
     * @param description       short description of content
     * @param documentPath      path to folder with documents
     */
    public void newContract(
            String category, String name, String expenses, String cycle, String customerNr, String contractNr,
            String startDate, String contractPeriod, String periodOfNotice, String description, String documentPath
    ) {
        this.category = category;
        this.name = name;
        this.expenses = Float.parseFloat(expenses);
        this.cycle = Integer.parseInt(cycle);
        this.customerNr = customerNr;
        this.contractNr = contractNr;
        this.startDate = startDate;
        this.contractPeriod = Integer.parseInt(contractPeriod);
        this.periodOfNotice = Integer.parseInt(periodOfNotice);
        this.description = description;
        this.documentPath = documentPath;
    }

    /**
     * deletes a given contract
     * @param contractRepo holds the operations for the sql-database
     * @param id           ID of the contract (primary key)
     * @param name         name for second check
     * @return             boolean, which informs about success/failure
     * @throws NullPointerException if contract not available
     */
    public boolean deleteContract(Repository contractRepo, String id, String name) throws NullPointerException{
        Optional<Contract> vertrag = contractRepo.findById(Integer.parseInt(id));
        if (vertrag.isEmpty() || !Objects.equals(vertrag.get().getName(), name)) throw new NullPointerException();
        if (vertrag.get().getName().equals(name)) {
            contractRepo.delete(vertrag.get());
            return true;
        } else {
            return false;
        }
    }

    /**
     * searches for a given contract in the database
     * @param contractRepo holds the operations for the sql-database
     * @param id           ID of the contract (primary key)
     * @return             object of the contract or null
     */
    public Contract getContract(Repository contractRepo, String id) {
        Optional<Contract> vertrag = contractRepo.findById(Integer.parseInt(id));
        if (vertrag.isEmpty()) return null;
        else return vertrag.get();
    }

    /**
     * changes a given attribute of the contract
     * @param key   name of the attribute, which is supposed to be changed
     * @param value new value
     * @return      object of the contract or null
     */
    public Contract updateContract(String key, String value) {
        switch (key) {
            case "typ": {
                this.category = value;
                break;
            }
            case "name": {
                this.name = value;
                break;
            }
            case "kosten": {
                this.expenses = Float.parseFloat(value);
                break;
            }
            case "turnus": {
                this.cycle = Integer.parseInt(value);
                break;
            }
            case "beschreibung": {
                this.description = value;
                break;
            }
            case "dokumentpfad": {
                this.documentPath = value;
                break;
            }
            default: return null;
        }
        return this;
    }
}

