package de.metallist.backend.utilities;

import de.metallist.backend.Contract;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class SessionUtilTest {
    private SessionUtil session;

    @BeforeClass
    public void beforeClass() {
        this.session = spy(new SessionUtil());
    }

    @Test
    public void test_00_Startup() {
        Preferences preferences = session.getPreferences();
        preferences.remove("Filepath");

        session.startup();
        preferences = session.getPreferences();

        assertNotNull(preferences.get("Filepath", null));
    }

    @Test
    public void test_01_AddContract() {
        Contract newContract = new Contract(
                1000, "living", "rent", 250, 12,
                "98741", "123650", "2022-07-01",
                1, 8, "rent for flat",
                "/home/user/example"
        );

        assertTrue(session.addContract(newContract));
    }

    @Test
    public void test_02_GetSingleContract() {
        assertEquals(session.getSingleContract(1000).getContractNr(), "123650");
        assertNull(session.getSingleContract(999));
    }

    @Test
    public void test_03_RemoveContractById() {
        assertTrue(session.removeContract(1000));
        assertFalse(session.removeContract(999));
    }

    @Test
    public void test_04_RemoveContract() {
        this.test_01_AddContract();
        Contract contract = session.getSingleContract(1000);

        assertTrue(session.removeContract(contract));
    }

    @Test
    public void test_05_UpdateContract() {
        this.test_01_AddContract();

        assertEquals(
                session.updateContract(1000, "contractNr", "159753").getContractNr(),
                "159753"
        );
    }

    @Test
    public void test_06_ImportContracts() {
        String filepath = "/home/mhenke/Programmierung/ContractCollection/Backend/src/test/resources/testImport.json";
        String falsepath = "/home/mhenke/Programmierung/ContractCollection/Backend/src/test/testImport.json";

        session.removeAllContracts();
        assertTrue(session.importContracts(falsepath).isEmpty());

        session.removeAllContracts();
        assertEquals(
                session.importContracts(filepath).get(1).getContractNr(),
                "14789"
        );
    }

    @Test
    public void test_07_Export() {
        String filepath = "/home/mhenke/Programmierung/ContractCollection/Backend/src/test/resources/testExport.json";
        Path path = Path.of(filepath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignore) {}

        session.export(filepath);

        assertTrue(Files.exists(path));
    }

    @Test
    public void test_08_GetContracts() {
        assertEquals(session.getContracts().get(1).getContractNr(), "14789");
    }

    @Test
    public void test_09_PrepareShutdown() {
        String filepath = "/home/mhenke/Programmierung/ContractCollection/Backend/src/test/resources/testExport.json";

        Preferences preferences = Preferences.userNodeForPackage(de.metallist.backend.utilities.SessionUtil.class);

        String savepath = preferences.get("Filepath", "");
        preferences.put("Filepath", filepath);

        assertTrue(session.prepareShutdown());

        preferences.put("Filepath", savepath);
    }
}