package connection;

import java.io.*;
import java.net.Socket;

/**
 * Класс, отвечающий за соединение
 */
public class TCPConnection {
    private final TCPConnectionListener eventListener;
    private final Socket socket;
    private final Thread threadCon;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    /**
     * Устанавливает соединение и запускает поток для чтения и отправки сообщений
     *
     * @param listener объект, реализующий интерфейс TCPConnectionListener для обработки событий
     * @param socket   сокет для соединения
     */
    public TCPConnection(TCPConnectionListener listener, Socket socket) throws IOException {
        this.eventListener = listener;
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.threadCon = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!threadCon.isInterrupted()) {
                        eventListener.onReceiveString(TCPConnection.this, reader.readLine());
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                    disconnect();
                }
            }
        });

        threadCon.start();
    }

    /**
     * Отправляет сообщение через соединение
     *
     * @param message сообщение для отправки
     */
    public synchronized void sendMessage(String message) {
        try {
            writer.write(message + "\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Закрывает соединение и освобождает ресурсы.
     */
    public synchronized void disconnect() {
        this.threadCon.interrupt();
        try {
            socket.close();
            reader.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "connection: " + TCPConnection.this.socket.getInetAddress();
    }

    public Socket getSocket() {
        return socket;
    }
}
