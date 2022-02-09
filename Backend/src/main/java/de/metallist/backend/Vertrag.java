package de.metallist.backend;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;
import java.util.Optional;


/**
 * Model, welches die Verträge mit ihren wichtigsten Eigenschaften repräsentiert
 *
 * @author mhenke
 * @version 0.1
 *
 */
@Entity
@Slf4j
public class Vertrag {
    @Id
    @Getter @Setter
    private int id;

    @Getter @Setter
    private String typ;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private float kosten;

    /** gibt die Anzahl der Zahlungen pro Jahr an (1, 2, 4, 12) */
    @Getter @Setter
    private int turnus;

    @Getter @Setter
    private String beschreibung;

    @Getter @Setter
    private String dokumentpfad;


    /**
     * legt einen neuen Vertrag an
     * @param typ          Vertragsart (Kategorie)
     * @param name         Bezeichner
     * @param kosten       Kosten je Zahlung
     * @param turnus       Turnus (siehe {@link #turnus})
     * @param beschreibung kurze Inhaltsbeschreibung
     * @param dokumentpfad Ablageort für Dokumente
     */
    public void neuerVertrag(String typ, String name, String kosten, String turnus, String beschreibung, String dokumentpfad) {
        this.typ = typ;
        this.name = name;
        this.kosten = Float.parseFloat(kosten);
        this.turnus = Integer.parseInt(turnus);
        this.beschreibung = beschreibung;
        this.dokumentpfad = dokumentpfad;
    }

    /**
     * löscht einen gegebenen Vertrag
     * @param vertragsRepo hält die Operationen für die SQL-DB
     * @param id           ID des Vertrages (primary key)
     * @param name         Bezeichner zur Kontrolle
     * @return             Wahrheitswert, der über Erfolg/Misserfolg informiert
     * @throws NullPointerException wird geworfen, wenn Vertrag nicht vorhanden
     */
    public boolean deleteVertrag(Repository vertragsRepo, String id, String name) throws NullPointerException{
        Optional<Vertrag> vertrag = vertragsRepo.findById(Integer.parseInt(id));
        if (vertrag.isEmpty() || !Objects.equals(vertrag.get().getName(), name)) throw new NullPointerException();
        if (vertrag.get().getName().equals(name)) {
            vertragsRepo.delete(vertrag.get());
            return true;
        } else {
            return false;
        }
    }

    /**
     * sucht nach einem bestimmten Vertrag im Repo
     * @param vertragsRepo hält die Operationen für die SQL-DB
     * @param id           ID des Vertrages (primary key)
     * @return             Vertragsobjekt oder null
     */
    public Vertrag getVertrag(Repository vertragsRepo, String id) {
        Optional<Vertrag> vertrag = vertragsRepo.findById(Integer.parseInt(id));
        if (vertrag.isEmpty()) return null;
        else return vertrag.get();
    }

    /**
     * ändert die gegebenen Parameter des Vertrages
     * @param key   Name des Attributes, welches geändert werden soll
     * @param value neuer Wert
     * @return      Vertragsobjekt oder null
     */
    public Vertrag changeVertrag(String key, String value) {
        switch (key) {
            case "typ": {
                this.typ = value;
                break;
            }
            case "name": {
                this.name = value;
                break;
            }
            case "kosten": {
                this.kosten = Float.parseFloat(value);
                break;
            }
            case "turnus": {
                this.turnus = Integer.parseInt(value);
                break;
            }
            case "beschreibung": {
                this.beschreibung = value;
                break;
            }
            case "dokumentpfad": {
                this.dokumentpfad = value;
                break;
            }
            default: return null;
        }
        return this;
    }
}

