package main;

import utils.*;
import sockets.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        char opt = new ScannerUtils()
                .getChar(
                        StaticResources.MENU_OPTIONS,
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
