package de.sl.secure;

import java.io.*;

public class SecureFile {

    private final File file;
    private final String pass;

    SecureFile(File file, String pass) {
        this.file = file;
        this.pass = pass;
    }

    SecureFile(File file) {
        this(file, null);
    }

    static String readFromFile(File file) {
        try(FileInputStream is = new FileInputStream(file)) {
            final byte[] data = is.readAllBytes();
            return new String(data);
        } catch(IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static void saveToFile(File file, String content) {
        try(FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            writer.flush();
        } catch(IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public boolean isEncrypted() {
        return pass!=null;
    }

    public File getFile() {
        return file;
    }

    public String readContent() {
        String content = readFromFile(file);

        if(isEncrypted()) {
            try {
                final AES aes = new AES(pass);
                content = aes.decrypt(content);
            } catch(Exception ex) {
                throw new IllegalStateException(ex);
            }
        }

        return content;
    }

    public void saveContent(String content) {
        String contentToSave = content;

        if(isEncrypted()) {
            try {
                final AES aes = new AES(pass);
                contentToSave = aes.encrypt(content);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }

        saveToFile(file, contentToSave);
    }
}
