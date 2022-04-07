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

    static String readEncrypted(File file, String pass) {
        try(FileInputStream is = new FileInputStream(file)) {
            final AES aes = new AES(pass);
            final byte[] data = is.readAllBytes();
            return aes.decrypt(new String(data));
        } catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static String readNormal(File file) {
        try(FileInputStream is = new FileInputStream(file)) {
            final byte[] data = is.readAllBytes();
            return new String(data);
        } catch(IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static void saveEncrypted(File file, String content, String pass) {
        try(FileWriter writer = new FileWriter(file)) {
            final AES aes = new AES(pass);
            final String encoded = aes.encrypt(content);
            writer.write(encoded);
            writer.flush();
        } catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static void saveNormal(File file, String content) {
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
        if(isEncrypted()) {
            return readEncrypted(file, pass);
        } else {
            return readNormal(file);
        }
    }

    public void saveContent(String content) {
        if(isEncrypted()) {
            saveEncrypted(file, content, pass);
        } else {
            saveNormal(file, content);
        }
    }
}
