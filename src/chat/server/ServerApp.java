package chat.server;

import chat.blacklistfilter.MessageFilter;
import connection.TCPConnection;
import connection.TCPConnectionListener;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class ServerApp implements TCPConnectionListener {

    private final MessageFilter messageFilter;
    private static TreeSet<String> blackList = new TreeSet<>(comparatorBlackList());
    private static List<TCPConnection> listConnection = new ArrayList<>();

    public static List<TCPConnection> getListThread() {
        return listConnection;
    }

    public static void main(String[] args) {
        fillBlackList();
        new ServerApp();
    }

    /**
     * В конструкторе мы создаем фильтр сообщений и ожидаем соединения
     */
    public ServerApp() {
        messageFilter = new MessageFilter();

        try (ServerSocket servSocket = new ServerSocket(8080)) {
            while (true) {
                new TCPConnection(this, servSocket.accept());

            }
        } catch (IOException e) {
            throw new RuntimeException("Произошла ошибка при подключении клиента:" + e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        System.out.println("User connected: " + tcpConnection.getSocket().getInetAddress());
        listConnection.add(tcpConnection);
        sendAllConnections("User connected: " + tcpConnection.getSocket().getInetAddress());
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String message) {
        sendAllConnections(messageFilter.filterMessage(message, messageFilter.filterMasker(blackList)));
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        listConnection.remove(tcpConnection);
        sendAllConnections(String.format("Пользователь -  %s:%s отвалился", tcpConnection.getSocket().getInetAddress(),
                tcpConnection.getSocket().getPort()));
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception exception) {
        System.out.println("Исключение: " + tcpConnection.getSocket().getInetAddress() + " " + exception);
    }

    /**
     * Отправляет сообщение всем текущим соединениям.
     *
     * @param message сообщение для отправки.
     */
    private void sendAllConnections(String message) {
        for (TCPConnection connection : listConnection) {
            connection.sendMessage(message);
        }
    }

    /**
     * Возвращает компаратор для черного списка, который сравнивает строки без учета регистра
     *
     * @return компаратор строк
     */

    private static Comparator<String> comparatorBlackList() {
        return (s1, s2) -> {
            if (s1 == null) {
                return -1;
            }
            if (s1.equals(s2)) {
                return 0;
            } else if (s2 == null) {
                return 1;
            }
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        };
    }

    private static void fillBlackList() {
        try (FileReader fileReader = new FileReader("src\\chat\\blacklistfilter\\blackList.txt");
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while (bufferedReader.ready()) {
                blackList.addAll(Arrays.asList(bufferedReader.readLine().split(",")));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Проверьте наличие и путь файла блеклиста.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
