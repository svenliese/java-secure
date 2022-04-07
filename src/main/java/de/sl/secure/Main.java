package de.sl.secure;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.prefs.Preferences;

public class Main {

    private static final String OPTION_OPEN = "open";
    private static final String OPTION_DECRYPT = "decrypt";
    private static final String OPTION_CANCEL = "cancel";
    private static final String OPTION_OK = "ok";
    private static final String OPTION_SAVE = "save";
    private static final String OPTION_RETRY = "retry";
    private static final String OPTION_NEW_PASS = "new pass";

    private static final String PREF_FILENAME = "filename";

    private static final String MSG_ENTER_PASS = "enter password";
    private static final String MSG_CONFIRM_PASS = "confirm password";

    private static SecureFile getFile() {
        final Preferences preferences = Preferences.userNodeForPackage(Main.class);
        final String fileName = preferences.get(PREF_FILENAME, null);

        final String[] options = {OPTION_OPEN, OPTION_DECRYPT, OPTION_CANCEL};

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setControlButtonsAreShown(false);
        if(fileName!=null) {
            final File file = new File(fileName);
            if(file.exists() && file.isFile()) {
                fileChooser.setSelectedFile(file);
            }
        }

        final int what = JOptionPane.showOptionDialog(
            null,
            fileChooser,
            "choose file",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        if(options[what].equals(OPTION_CANCEL)) {
            return null;
        }

        final File file = fileChooser.getSelectedFile();
        if(file==null || !file.isFile()) {
            return null;
        }

        preferences.put(PREF_FILENAME, file.getPath());

        if(options[what].equals(OPTION_OPEN)) {
            return new SecureFile(file);
        }

        char[] pass = getPassForDecryption(MSG_ENTER_PASS);
        if(pass.length==0) {
            return null;
        }

        return new SecureFile(file, new String(pass));
    }

    private static char[] getPassForDecryption(String title) {
        final String[] options = {OPTION_OK, OPTION_CANCEL};
        final JPasswordField passwordField = new JPasswordField();
        final int what = JOptionPane.showOptionDialog(
            null,
            passwordField,
            title,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
        if(options[what].equals(OPTION_CANCEL)) {
            return new char[0];
        }
        return passwordField.getPassword();
    }

    private static boolean passEqual(char[] first, char[] second) {
        return new String(first).equals(new String(second));
    }

    private static boolean retry() {
        final String[] options = {OPTION_RETRY, OPTION_CANCEL};
        final int what = JOptionPane.showOptionDialog(
            null,
            "passwords not equal !",
            "error",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            options[0]
        );
        return options[what].equals(OPTION_RETRY);
    }

    private static char[] getPassForEncryption() {

        char[] first;
        char[] second;
        do {
            first = getPassForDecryption(MSG_ENTER_PASS);
            if (first.length == 0) {
                return first;
            }

            second = getPassForDecryption(MSG_CONFIRM_PASS);
            if (second.length == 0) {
                return second;
            }

            if(passEqual(first, second)) {
                return first;
            }

        } while(retry());

        return new char[0];
    }

    private static boolean editFile(SecureFile file) {
        final String[] options = {OPTION_SAVE, OPTION_NEW_PASS, OPTION_CANCEL};
        final JEditorPane editor = new JEditorPane();
        editor.setText(file.readContent());
        final JScrollPane scrollPane = new JScrollPane(editor);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        final int what = JOptionPane.showOptionDialog(
            null,
            scrollPane,
            "edit content",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
        if(options[what].equals(OPTION_CANCEL)) {
            return true;
        }

        final String content = editor.getText();

        if(options[what].equals(OPTION_NEW_PASS) || !file.isEncrypted()) {
            final char[] pass = getPassForEncryption();
            if (pass.length == 0) {
                return false;
            }
            file = new SecureFile(file.getFile(), new String(pass));
        }

        file.saveContent(content);

        return true;
    }

    public static void main(String[] args) {
        SecureFile file = getFile();
        if(file!=null) {
            boolean canExit;
            do {
                canExit = editFile(file);
            } while(!canExit);
        }
    }
}
