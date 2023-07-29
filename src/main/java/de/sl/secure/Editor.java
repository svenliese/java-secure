package de.sl.secure;

import javax.swing.*;
import java.awt.*;

import static de.sl.secure.Constants.*;

public class Editor {

    private SecureFile file;

    public Editor(SecureFile file) {
        this.file = file;
    }

    public boolean editFile() {
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

        if(what<0 || options[what].equals(OPTION_CANCEL)) {
            return true;
        }

        final String content = editor.getText();

        if(options[what].equals(OPTION_NEW_PASS) || !file.isEncrypted()) {
            final char[] pass = Pass.getConfirmedPass();
            if (pass == NO_PASS) {
                return false;
            }
            file = new SecureFile(file.getFile(), new String(pass));
        }

        file.saveContent(content);

        return true;
    }
}
