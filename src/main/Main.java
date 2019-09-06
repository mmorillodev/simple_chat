package main;

import utils.ScannerUtils;
import sockets.*;

import java.io.IOException;

public class Main {

    private static final String OPTIONS;

    static {
        OPTIONS = "[S]erver or [C]lient? ";
    }

    public static void main(String[] args) {
        char opt = new ScannerUtils()
                .getChar(
                        OPTIONS,
                        c -> c == 'S' ||
                                c == 'C' ||
                                c == 's' ||
                                c == 'c'
                );

        if(opt == 'S' || opt == 's') {
            new Server().init();
        }
        else {
            try {
                new Client().init();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
