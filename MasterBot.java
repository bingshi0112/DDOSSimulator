//package com.company;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by bing on 3/4/17.
 */

public class MasterBot {

    static Map<String, String> connectionMap = new HashMap<>();
    static List<String> slaveList = new ArrayList<>();
    static int counter = 0;
    static private ServerSocket socket = null;

    public static void main(String[] args) throws IOException {

        if (args.length == 2) {
            if (args[0].equals("-p")) {
                try {
                    openPort(Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    System.out.println("Please input a number!");
                    System.out.print(">");
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                System.out.println("Wrong command, should start with \"-p\"!");
                System.out.print(">");
                System.exit(1);
            }
        } else {
            System.out.println("Wrong number of arguments!");
            System.out.print(">");
            System.exit(1);
        }

        Thread inputThread = new Thread(() -> {
            Scanner scan = new Scanner(System.in);
            while (true) {
                System.out.print(">");
                String input;
                input = scan.nextLine();
                if (input.contains("list")) {
                    listAll();
                } else if (input.contains("disconnect")) {
                    disconnect(input);
                } else if (input.contains("connect")) {
                    connect(input);
                } else if (input.startsWith("ipscan")) {
                    ipscan(input);
                } else if (input.startsWith("geoipscan")) {
                    geoipscan(input);
                } else if (input.startsWith("tcpportscan")) {
                    tcpportscan(input);
                } else {
                    System.out.print("Invalid command! ");
                }
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    Thread.currentThread().interrupt();
//                }
            }

        });
        inputThread.start();
    }

    static void openPort(int port) {
        try {
            socket = new ServerSocket(port, 0, InetAddress.getByName(null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread serverThread = new Thread(() -> {
            while (true) {
                try {
                    ClientWorker worker = new ClientWorker(socket.accept(), connectionMap);
                    Thread thread = new Thread(worker);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String masterDate = sdf.format(new Date());
                    thread.setName("Slave" + (++counter) + "~~" + socket.getInetAddress().getHostName() + "~~" + socket.getInetAddress() + " " + socket.getLocalPort() + " " + masterDate);
                    thread.start();
                    System.out.println("Connect to slave \"" + socket.getInetAddress().getHostName() + "\"!");
                    System.out.print(">");
                    slaveList.add(socket.getInetAddress().getHostName());
                } catch (IOException e) {
                    System.out.println("Fail to accept");
                    System.out.print(">");
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });
        serverThread.start();
    }

    static Thread[] listAll() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        String[] slaves;

        for (Thread thread : threadArray) {
            if (thread.getName().contains("Slave")) {
                slaves = thread.getName().split("~~");
                for (String element : slaveList) {
                    if(element.equals(slaves[1]))
                        System.out.println(slaves[1] + " " + slaves[2]);
                }
            }
        }
        System.out.print(">");
        return threadArray;
    }

    static void connect(String input) {
        String[] inputs = input.split(" ");
        int count = inputs.length;
        String slave = inputs[1];

        if (count == 4) {
            try {
                Integer.parseInt(inputs[3]);
                connectionMap.put(slave, input);
            } catch (NumberFormatException e) {
                System.out.print("Please input a number to indicate which host port to connect! \n>");
                e.printStackTrace();
            }
        } else if (count == 5) {
            try {
                Integer.parseInt(inputs[4]);
                connectionMap.put(slave, input);
            } catch (NumberFormatException e) {
                if (inputs[4].equals("keepalive") || inputs[4].startsWith("/"))
                    connectionMap.put(slave, input);
                else
                    System.out.print("Last command is incorrect! Please enter \"keepalive\" or a path start with \"/\"\n>");
            }
        } else if (count == 6) {
            int i = 0;
            try {
                Integer.parseInt(inputs[3]);
                i++;
            } catch (NumberFormatException e) {
                System.out.print("Please input a number as fourth argument to indicate which host port to connect! \n>");
            }
            try {
                Integer.parseInt(inputs[4]);
                i++;
            } catch (NumberFormatException e) {
                System.out.print("Please input a number as fifth argument to indicate number of connections you would like to setup! \n>");
            }
            if ((inputs[5].equals("keepalive") || inputs[5].startsWith("/")) && i == 2)
                connectionMap.put(slave, input);
            else
                System.out.print("Last command is incorrect! Please enter \"keepalive\" or a path start with \"/\"\n>");
        } else {
            System.out.println("Wrong number of commands!");
            System.out.print(">");
        }
    }

    static void disconnect(String input) {
        String[] inputs = input.split(" ");
        int count = inputs.length;
        String slave = inputs[1];

        connectionMap.put(slave, input);
        if (count == 3 || count == 4) {
            connectionMap.put(slave, input);
        } else {
            System.out.println("Invalid input!");
            System.out.print(">");
        }
    }

    static void ipscan(String input) {
        String[] inputs = input.split(" ");
        int count = inputs.length;
        if (count == 3) {
            if (inputs[2].contains("-")) {
                String[] ipRange = inputs[2].split("-");
                if (ipRange.length == 2) {
                    String[] ipTuplesBeg = ipRange[0].split("\\.");
                    String[] ipTuplesEnd = ipRange[1].split("\\.");
                    if (ipTuplesBeg.length == 4 && ipTuplesEnd.length == 4) {
                        try {
                            Integer.parseInt(ipTuplesBeg[0]);
                            Integer.parseInt(ipTuplesEnd[0]);
                            Integer.parseInt(ipTuplesBeg[1]);
                            Integer.parseInt(ipTuplesEnd[1]);
                            Integer.parseInt(ipTuplesBeg[2]);
                            Integer.parseInt(ipTuplesEnd[2]);
                            Integer.parseInt(ipTuplesBeg[3]);
                            Integer.parseInt(ipTuplesEnd[3]);
                            if (inputs[1].equals("all")) {
                                for (String element : slaveList) {
                                    connectionMap.put(element, input);
                                }
                            } else {
                                String slave = inputs[1];
                                connectionMap.put(slave, input);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Each tuple segment should be an integer!");
                            System.out.print(">");
                        }
                    } else {
                        System.out.println("Ip should have four tuples!");
                        System.out.print(">");
                    }
                } else {
                    System.out.println("Port range should only contain two segments!");
                    System.out.print(">");
                }
            } else {
                System.out.println("Port range should be separated by \"-\"!");
                System.out.print(">");
            }
        } else {
            System.out.println("Wrong numbers of command!");
            System.out.print(">");
        }
    }

    static void geoipscan(String input) {
        String[] inputs = input.split(" ");
        int count = inputs.length;
        if (count == 3) {
            if (inputs[2].contains("-")) {
                String[] ipRange = inputs[2].split("-");
                if (ipRange.length == 2) {
                    String[] ipTuplesBeg = ipRange[0].split("\\.");
                    String[] ipTuplesEnd = ipRange[1].split("\\.");
                    if (ipTuplesBeg.length == 4 && ipTuplesEnd.length == 4) {
                        try {
                            Integer.parseInt(ipTuplesBeg[0]);
                            Integer.parseInt(ipTuplesEnd[0]);
                            Integer.parseInt(ipTuplesBeg[1]);
                            Integer.parseInt(ipTuplesEnd[1]);
                            Integer.parseInt(ipTuplesBeg[2]);
                            Integer.parseInt(ipTuplesEnd[2]);
                            Integer.parseInt(ipTuplesBeg[3]);
                            Integer.parseInt(ipTuplesEnd[3]);
                            if (inputs[1].equals("all")) {
                                for (String element : slaveList) {
                                    connectionMap.put(element, input);
                                }
                            } else {
                                String slave = inputs[1];
                                connectionMap.put(slave, input);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Each tuple segment should be an integer!");
                            System.out.print(">");
                        }
                    } else {
                        System.out.println("Ip should have four tuples!");
                        System.out.print(">");
                    }
                } else {
                    System.out.println("Port range should only contain two segments!");
                    System.out.print(">");
                }
            } else {
                System.out.println("Port range should be separated by \"-\"!");
                System.out.print(">");
            }
        } else {
            System.out.println("Wrong numbers of command!");
            System.out.print(">");
        }
    }

    static void tcpportscan(String input) {
        String[] inputs = input.split(" ");
        int count = inputs.length;

        if (count == 4) {
            if (inputs[3].contains("-")) {
                String[] portRange = inputs[3].split("-");
                if (portRange.length == 2) {
                    try {
                        int portLower = Integer.parseInt(portRange[0]);
                        int portHigher = Integer.parseInt(portRange[1]);
                        if (portLower >= 0 && portHigher <= 65535) {
                            if (inputs[1].equals("all")) {
                                for (String element : slaveList) {
                                    connectionMap.put(element, input);
                                }
                            } else {
                                String slave = inputs[1];
                                connectionMap.put(slave, input);
                            }
                        } else {
                            System.out.println("Port number should range between 0 and 65535!");
                            System.out.print(">");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Each tuple segment should be an integer!");
                        System.out.print(">");
                    }
                } else {
                    System.out.println("Port range should only contain two segments!");
                    System.out.print(">");
                }
            } else {
                System.out.println("Port range should be separated by \"-\"!");
                System.out.print(">");
            }
        } else {
            System.out.println("Wrong numbers of command!");
            System.out.print(">");
        }
    }

    static class ClientWorker implements Runnable {
        private Socket socket;
        Map<String, String> connectMap;

        ClientWorker(Socket socket, Map<String, String> map) {
            this.socket = socket;
            this.connectMap = map;
        }

        @Override
        public void run() {
            try {
                socket.setKeepAlive(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String slaveName = Thread.currentThread().getName();
                String[] slaveNames = slaveName.split("~~");
                final String slave = slaveNames[1];

                Thread outStreamThread = new Thread(() -> {
                    while (true) {
                        if (connectMap.containsKey(slave)) {
                            out.println(connectMap.get(slave));
                            System.out.println("Sent to Slave \"" + slave + "\": \"" + connectMap.get(slave) + "\"");
                            System.out.print(">");
                            connectMap.remove(slave);
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                });
                outStreamThread.start();

                Thread inStreamThread = new Thread(() -> {
                    try {
                        String slaveResult;
                        while ((slaveResult = in.readLine()) != null) {
                            System.out.println(slaveResult);
                            System.out.print(">");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Thread.currentThread().interrupt();
                            }
                        }
//                        Thread.currentThread().stop();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                inStreamThread.start();

//                if (in.readLine() == null) {
//                    Thread.currentThread().stop();
//                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                System.out.println("fail I/O");
                System.out.print(">");
            }
        }
    }
}


