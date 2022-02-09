package de.metallist.backend;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReasonCodes {
    RC_ALLG_ERFOLG("RC_G_00", "Aktion erfolgreich"),
    RC_ANLEGEN_ERFOLG("RC_A_00", "Vertrag erfolgreich angelegt"),
    RC_ANLEGEN_FEHLER("RC_A_10", "Vertrag anlegen fehlerhaft"),
    RC_LOESCHEN_ERFOLG("RC_L_00", "Vertrag erfolgreich gelöscht"),
    RC_LOESCHEN_FEHLEND("RC_L_10", "zu löschender Vertrag nicht vorhanden"),
    RC_LOESCHEN_FEHLER("RC_L_20", "allgemeiner Fehler beim Löschen"),
    RC_UPDATE_ERFOLG("RC_U_00", "Vertrag erfolgreich aktualisiert"),
    RC_UPDATE_FEHLER("RC_U_10", "Vertrag aktualisieren fehlerhaft"),
    RC_ALLG_FEHLER("RC_G_99", "Aktion nicht erfolgreich");

    @Getter
    private final String codenumber;
    @Getter
    private final String description;

}
