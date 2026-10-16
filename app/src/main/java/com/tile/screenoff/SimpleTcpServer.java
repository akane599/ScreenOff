package com.tile.screenoff;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SimpleTcpServer {
    private static final int SOCKET_TIMEOUT_MS = 10_000;
    private static final int BUFFER_SIZE = 1024;

    public interface TcpConnectionListener {
        void onRequest(HttpRequest request, Connection connection);
        void onRequestTooLarge(Connection connection);
    }

    public static class Connection {
        private final Socket socket;
        private final BufferedOutputStream out;

        Connection(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new BufferedOutputStream(socket.getOutputStream());
        }

        public synchronized void output(byte[] data) throws IOException {
            out.write(data);
            out.flush();
        }
    }

    private final TcpConnectionListener listener;
    private final Set<Socket> connections = Collections.synchronizedSet(new HashSet<Socket>());
    private ServerSocket serverSocket;

    public SimpleTcpServer(TcpConnectionListener listener, int port) {
        this.listener = listener;
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            serverSocket = null;
        }
    }

    public void start() {
        if (serverSocket == null || serverSocket.isClosed()) {
            return;
        }

        new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(SOCKET_TIMEOUT_MS);
                    connections.add(socket);
                    new Thread(() -> handle(socket)).start();
                } catch (SocketException e) {
                    if (!serverSocket.isClosed()) {
                        continue;
                    }
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    private void handle(Socket socket) {
        try {
            Connection connection = new Connection(socket);
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            HttpRequestParser parser = new HttpRequestParser();
            byte[] buffer = new byte[BUFFER_SIZE];
            while (!parser.isComplete() && !parser.isTooLarge()) {
                int size = in.read(buffer);
                if (size < 0) {
                    return;
                }
                byte[] chunk = new byte[size];
                System.arraycopy(buffer, 0, chunk, 0, size);
                parser.add(chunk);
            }
            if (parser.isTooLarge()) {
                listener.onRequestTooLarge(connection);
            } else {
                HttpRequest request = parser.parse();
                if (request != null) {
                    listener.onRequest(request, connection);
                }
            }
        } catch (IOException ignored) {
        } finally {
            connections.remove(socket);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void stop() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
        synchronized (connections) {
            for (Socket socket : connections) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
            connections.clear();
        }
    }
}
