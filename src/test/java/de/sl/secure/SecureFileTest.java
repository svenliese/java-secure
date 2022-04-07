package de.sl.secure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class SecureFileTest {

    void deleteTmpFile(File tmpFile) {
        if(tmpFile!=null) {
            Assertions.assertTrue(tmpFile.delete());
        }
    }

    File createTmpFile() {
        try {
            return File.createTempFile("test", ".tmp");
        } catch (IOException ex) {
            Assertions.fail(ex);
        }
        return null;
    }

    @Test
    void shouldWriteReadNormal() {
        final File tmpFile = createTmpFile();

        try {
            final SecureFile file = new SecureFile(tmpFile);
            Assertions.assertFalse(file.isEncrypted());

            final String content = "eins\n\nzwei\n";
            file.saveContent(content);

            final String fromFile = file.readContent();
            Assertions.assertEquals(content, fromFile);
        } finally {
            deleteTmpFile(tmpFile);
        }
    }

    @Test
    void shouldWriteReadEncrypted() {
        final File tmpFile = createTmpFile();

        try {
            final SecureFile file = new SecureFile(tmpFile, "mypass");
            Assertions.assertTrue(file.isEncrypted());

            final String content = "eins\n\nzwei\n";
            file.saveContent(content);

            final String fromFile = file.readContent();
            Assertions.assertEquals(content, fromFile);
        } finally {
            deleteTmpFile(tmpFile);
        }
    }
}
