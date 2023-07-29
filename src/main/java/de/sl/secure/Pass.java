package de.sl.secure;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import static de.sl.secure.Constants.*;

public class Pass {

    private Pass() {}

    public static char[] getPassForDecryption(String title) {

        final JPasswordField passwordField = new JPasswordField();
        passwordField.addAncestorListener(
                new AncestorListener() {
                    @Override
                    public void ancestorRemoved( final AncestorEvent event ) {
                        // not used
                    }
                    @Override
                    public void ancestorMoved( final AncestorEvent event ) {
                        // not used
                    }
                    @Override
                    public void ancestorAdded( final AncestorEvent event ) {
                        passwordField.requestFocusInWindow();
                    }
                }
        );

        final String[] options = {OPTION_OK, OPTION_CANCEL};
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

        if(what<0 || options[what].equals(OPTION_CANCEL)) {
            return NO_PASS;
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
        return what>=0 && options[what].equals(OPTION_RETRY);
    }

    public static char[] getConfirmedPass() {

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

        return NO_PASS;
    }
}
