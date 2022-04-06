package de.sl.secure;

import java.io.*;

public class SecureFile {

    private final File file;

    private boolean encrypted;
    private String pass;

    SecureFile(File file, boolean encrypted) {
        this.file = file;
        this.encrypted = encrypted;
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
        return encrypted;
    }

    public void setPass(String pass) {
        this.pass = pass;
        this.encrypted = true;
    }

    public String readContent() {
        if(encrypted) {
            return readEncrypted(file, pass);
        } else {
            return readNormal(file);
        }
    }

    public void saveContent(String content) {
        if(encrypted) {
            saveEncrypted(file, content, pass);
        } else {
            saveNormal(file, content);
        }
    }
}
