package de.metallist.backend.utilities;

import de.metallist.backend.Contract;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    public void test_00_AddContract() {
        Contract newContract = new Contract(
                1000, "living", "rent", 250, 12,
                "98741", "123650", "2022-07-01",
                1, 8, "rent for flat",
                "/home/user/example"
        );

        assertTrue(session.addContract(newContract));
    }

    @Test
    public void test_01_GetSingleContract() {
        assertEquals(session.getSingleContract(1000).getContractNr(), "123650");
        assertNull(session.getSingleContract(999));
    }

    @Test
    public void test_02_RemoveContractById() {
        assertTrue(session.removeContract(1000));
        assertFalse(session.removeContract(999));
    }

    @Test
    public void test_03_RemoveContract() {
        this.test_00_AddContract();
        Contract contract = session.getSingleContract(1000);

        assertTrue(session.removeContract(contract));
    }

    @Test
    public void test_04_UpdateContract() {
        this.test_00_AddContract();

        assertEquals(
                session.updateContract(1000, "contractNr", "159753").getContractNr(),
                "159753"
        );
    }

    @Test
    public void test_05_writeFileTest() {
        this.test_00_AddContract();

        String filepath = new File("src/test/resources/testExportedFile.txt").getAbsolutePath();
        String password = "123superSecret!";
        Path path = Path.of(filepath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignore) {}

        boolean success = session.writeFile(filepath, password);

        assertTrue(success);
        assertTrue(Files.exists(path));
    }

    @Test
    public void test_06_loadFileTest() throws IOException {
        String filepath = new File("src/test/resources/testExportedFile.txt").getAbsolutePath();
        String correctContent = Files.readString(Paths.get(filepath));

        String shortContent = correctContent.split(":")[3];

        String corruptedFile = new File("src/test/resources/testCorruptedFile.txt").getAbsolutePath();
        String corruptedContent = Files.readString(Paths.get(corruptedFile));

        ArrayList<Contract> contracts = new ArrayList<>();

        // short content
        session.removeAllContracts();
        try {
            contracts = (ArrayList<Contract>) session.loadFile(shortContent, "123superSecret!");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Syntax error"));
        }
        assertEquals(contracts.size(), 0);

        // wrong password
        try {
            contracts = (ArrayList<Contract>) session.loadFile(correctContent, "0123superSecret!");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Wrong password.");
        }
        assertEquals(contracts.size(), 0);

        // corrupted file
        try {
            contracts = (ArrayList<Contract>) session.loadFile(corruptedContent, "123superSecret!");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Couldn't read data.");
        }
        assertEquals(contracts.size(), 0);

        // all fine
        session.removeAllContracts();
        contracts = (ArrayList<Contract>) session.loadFile(correctContent, "123superSecret!");
        assertFalse(contracts.isEmpty());
    }

    @Test
    public void test_07_GetContracts() {
        assertEquals(session.getContracts().get(1).getContractNr(), "123650");
    }

    @Test
    public void test_08_PrepareShutdown() {
        String filepath = new File("src/test/resources/testExport.txt").getAbsolutePath();

        Preferences preferences = Preferences.userNodeForPackage(de.metallist.backend.utilities.SessionUtil.class);

        String savepath = preferences.get("Filepath", "");
        preferences.put("Filepath", filepath);

        assertTrue(session.prepareShutdown());

        preferences.put("Filepath", savepath);
    }
}