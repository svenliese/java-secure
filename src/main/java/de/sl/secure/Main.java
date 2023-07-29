package de.sl.secure;

import javax.swing.*;
import java.io.*;
import java.util.prefs.Preferences;

import static de.sl.secure.Constants.*;

public class Main {

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

        if(what<0 || options[what].equals(OPTION_CANCEL)) {
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

        final char[] pass = Pass.getPassForDecryption(MSG_ENTER_PASS);
        if(pass==NO_PASS) {
            return null;
        }

        return new SecureFile(file, new String(pass));
    }

    static void showError(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        JTextArea textArea = new JTextArea(sw.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showOptionDialog(
            null,
            scrollPane,
            "error",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            new String[]{"ok"},
            "ok"
        );
    }

    public static void main(String[] args) {
        try {
            final SecureFile file = getFile();
            if (file != null) {
                boolean canExit;
                final Editor editor = new Editor(file);
                do {
                    canExit = editor.editFile();
                } while (!canExit);
            }
        } catch(Exception ex) {
            showError(ex);
        }
    }
}
