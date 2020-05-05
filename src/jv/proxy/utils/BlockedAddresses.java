package jv.proxy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class BlockedAddresses {

    private static String blockedPathString = "blocked.txt";

    private static class BlockedAddress {
        public String path = "/";
        public String host = "";
    }

    private static final Vector<BlockedAddress> blocked = new Vector<>(0);

    public static void update() {
        try {
            Scanner scanner = new Scanner(new File(blockedPathString));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                BlockedAddress address = new BlockedAddress();
                if (line.contains("/")) {
                    int dashPos = line.indexOf('/');
                    address.host = line.substring(0, dashPos);
                    address.path = line.substring(dashPos, line.length());
                } else {
                    address.host = line;
                }
                blocked.add(address);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void printBlockedAddresses() {
        for (int i = 0; i < blocked.size(); i++) {
            System.out.println(blocked.elementAt(i).host + blocked.elementAt(i).path);
        }
    }

    public static void setFilePath(String path) {
        blockedPathString = path;
    }

    public static boolean findBlocked(String host, String path) {
        for (BlockedAddress address : blocked) {
            if (address.host.equals(host) && address.path.equals(path)) {
                return true;
            }
        }
        return false;
    }

}
