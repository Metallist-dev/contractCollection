package de.metallist.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.Optional;

import static de.metallist.backend.ReasonCodes.RC_ANLEGEN_ERFOLG;
import static de.metallist.backend.ReasonCodes.RC_LOESCHEN_FEHLEND;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Controller, der sich um die Behandlung von Requests der REST-API kümmert
 *
 * @author mhenke
 * @version 0.1
 */
@Controller
@Slf4j
@RequestMapping(path = "/demo")
public class MainController {
    private final Repository vertragsRepo;

    @Autowired
    public MainController(Repository vertragsRepo) {
        this.vertragsRepo = vertragsRepo;
    }

    /**
     * legt einen neuen Vertrag an
     * @param typ          Vertragsart (Kategorie)
     * @param name         verständlicher Name
     * @param kosten       Kosten je Zahlung
     * @param turnus       Turnus (siehe {@link de.metallist.backend.Vertrag#turnus})
     * @param beschreibung kurze Inhaltsbeschreibung
     * @param dokumentpfad Ablageort für Dokumente
     * @return Meldung über Erfolg oder Misserfolg
     */
    @SuppressWarnings("JavadocReference")
    @PostMapping(path = "/add")
    public ResponseEntity<Vertrag> neuerVertrag(
            @RequestParam String typ,
            @RequestParam String name,
            @RequestParam String kosten,
            @RequestParam String turnus,
            @RequestParam String beschreibung,
            @RequestParam String dokumentpfad
            ) {
        try {
            Vertrag vertrag = new Vertrag();
            vertrag.neuerVertrag(
                    decode(typ), decode(name), decode(kosten), decode(turnus),
                    decode(beschreibung), decode(dokumentpfad)
            );

            int maxID = 1;
            for (Vertrag v : vertragsRepo.findAll()) {
                if (v.getId() >= maxID) maxID = v.getId() + 1;
            }
            vertrag.setId(maxID);

            vertragsRepo.save(vertrag);

            return ResponseEntity.ok(vertrag);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(NOT_FOUND).body(new Vertrag());
        }
    }

    /**
     * löscht einen bestimmten Vertrag
     * @param id   primary key des Vertrages
     * @param name Bezeichner zur Kontrolle
     * @return     Meldung über Erfolg/Misserfolg
     */
    @PostMapping(path = "/delete")
    public ResponseEntity<String> vertragLoeschen(@RequestParam String id, @RequestParam String name) {
        try {
            log.info("Versuche einen Vertrag mit der ID " + id + " zu löschen.");
            if (new Vertrag().deleteVertrag(vertragsRepo, id, name)) {
                log.info("Löschen erfolgreich.");
                return ResponseEntity.ok("erfolgreich");
            }
            else {
                log.error("Beim Löschen des Vertrages mit der ID " + id + " ist etwas schief gelaufen.");
                return ResponseEntity.badRequest().body("Irgendwas ist schief gelaufen.");
            }

        } catch (NullPointerException npe) {
            String message = RC_LOESCHEN_FEHLEND + ": Vertrag mit der ID " + id + " und dem Namen " +  name + " ist nicht vorhanden.";
            System.out.println(message);
            return ResponseEntity.unprocessableEntity().body("Vertrag mit der ID " + id + " und dem Namen " +  name + " ist nicht vorhanden.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Fehler: " + e.getMessage());
        }
    }

    /**
     * liefert alle Verträge zurück
     * @return ResponseBody mit allen Verträgen
     */
    @GetMapping(path = "/all")
    public ResponseEntity<Iterable<Vertrag>> getAlleVertraege() {
        log.info("GET-Request auf alle Verträge.");
        return ResponseEntity.ok(vertragsRepo.findAll());
    }

    /**
     * Liefert einen bestimmten Vertrag oder null zurück
     * @param id   Vertragsid (primary key)
     * @return     Vertrag oder null
     */
    @GetMapping(path = "/get")
    public ResponseEntity<Vertrag> getEinVertrag(@RequestParam String id) { return ResponseEntity.ok(new Vertrag().getVertrag(vertragsRepo, id)); }

    @PatchMapping(path = "/change/{id}")
    public ResponseEntity<Vertrag> changeVertrag(@PathVariable int id, @RequestParam String key, @RequestParam String value) {
        Optional<Vertrag> vertragsKandidat = vertragsRepo.findById(id);
        if (vertragsKandidat.isEmpty()) return null;
        Vertrag vertrag = vertragsKandidat.get();

        Vertrag neuerVertrag = vertrag.changeVertrag(key, value);
        //if (neuerVertrag==null) return "Es gab einen Fehler bei Änderung des Key " + key + "\n";
        //if (neuerVertrag==null) return ResponseEntity.status(CONFLICT).body("Es gab einen Fehler bei Änderung des Key " + key + "\n");
        if (neuerVertrag==null) return ResponseEntity.status(CONFLICT).body(new Vertrag());
        vertragsRepo.save(vertrag);
        //return ResponseEntity.ok("Der Vertrag " + id + " wurde im Wert " + key + " geändert. Neuer Wert: " + value + "\n");
        return ResponseEntity.ok(vertrag);
    }


    /// Helferfunktionen
    private String decode(String input) {
        return URLDecoder.decode(input, UTF_8);
    }
}
