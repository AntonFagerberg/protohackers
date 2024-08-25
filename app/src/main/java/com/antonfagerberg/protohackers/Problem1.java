
package com.antonfagerberg.protohackers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class Problem1 {

    void run() {
        int port = 6666;
        var objectMapper = new ObjectMapper();
        try (var executorService = Executors.newVirtualThreadPerTaskExecutor();
             var serverSocket = new ServerSocket(port)) {

            while (true) {
                var socket = serverSocket.accept();

                executorService.submit(() -> {
                    try {
                        var bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        var bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                        var line = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            try {
                                var json = objectMapper.readTree(line);

                                if (json.has("method") &&
                                        json.get("method").asText().equals("isPrime") &&
                                        json.has("number") &&
                                        json.get("number").isNumber()) {

                                    var response = JsonNodeFactory.instance.objectNode()
                                            .put("method", "isPrime")
                                            .put("prime", json.get("number").isIntegralNumber() && isPrime(json.get("number").asInt()));

                                    bufferedWriter.append(response.toString()).append('\n').flush();
                                } else {
                                    bufferedWriter.append("{}\n").flush();
                                    socket.close();
                                }
                            } catch (JsonParseException e) {
                                bufferedWriter.append("{}\n").flush();
                                socket.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    public static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }

        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        new Problem1().run();
    }

}
