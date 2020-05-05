public class ServerPrinter {

    public static void print(long threadID, String line) {
        System.out.println("[SERVER] > THREAD_ID:" + String.valueOf(threadID) + " > " + line);
    }
}
