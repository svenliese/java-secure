package de.sl.secure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static de.sl.secure.Constants.*;

public class Editor implements KeyListener {

    private SecureFile file;

    private JEditorPane editor;

    public Editor(SecureFile file) {
        this.file = file;
    }

    public boolean editFile() {
        final String[] options = {OPTION_SAVE, OPTION_NEW_PASS, OPTION_CANCEL};

        editor = new JEditorPane();
        editor.addKeyListener(this);
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

    private String getSearchText() {
        final String[] options = {OPTION_OK, OPTION_CANCEL};

        JTextField input = new JTextField();

        final int what = JOptionPane.showOptionDialog(
                editor,
                input,
                "search for",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                input
        );

        if(what<0 || options[what].equals(OPTION_CANCEL)) {
            return null;
        }

        return input.getText();
    }

    private void goTo(String searchFor) {
        final int pos = editor.getText().indexOf(searchFor);
        if(pos>=0) {
            editor.setCaretPosition(pos);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==70 && e.isControlDown()) {
            final String searchFor = getSearchText();
            if(searchFor!=null && !searchFor.isEmpty()) {
                goTo(searchFor);
            }
            editor.requestFocus();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // not used
    }
}
