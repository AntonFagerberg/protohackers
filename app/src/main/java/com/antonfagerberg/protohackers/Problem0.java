
package com.antonfagerberg.protohackers;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class Problem0 {

    void run() {
        int port = 6666;

        try (var executorService = Executors.newVirtualThreadPerTaskExecutor();
             var serverSocket = new ServerSocket(port)) {
            System.out.printf("Server started on port %s", port);

            while (true) {
                var socket = serverSocket.accept();

                executorService.submit(() -> {
                    System.out.printf("Started up %s%n", Thread.currentThread());
                    try {
                        var inputStream = socket.getInputStream();
                        var outputStream = socket.getOutputStream();
                        int read;

                        while ((read = inputStream.read()) != -1) {
                            System.out.printf("Read %s on %s%n", read, Thread.currentThread());
                            outputStream.write(read);
                        }

                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Problem0().run();
    }

}
