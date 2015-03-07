package server;

public class ConsoleHandler {

    public ConsoleHandler() {
    }

    public void print(String line) {
        System.out.print("\r> " + line + "\n> ");
    }

    public void blank() {
        System.out.print("\r> ");
    }
}
