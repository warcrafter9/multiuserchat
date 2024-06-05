package chat.client;

import connection.TCPConnection;
import connection.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Клиентское приложение
 */
public class ClientApp extends JFrame implements ActionListener, TCPConnectionListener {
    private final JTextArea log = new JTextArea();
    private final JTextField nickname = new JTextField();
    private final JTextField fieldInput = new JTextField();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy - hh:mm || ");
    private final ZonedDateTime zonedDateTime = ZonedDateTime.now();

    private TCPConnection tcpConnection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ClientApp clientApp = new ClientApp("Чат");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Создает окно клиента и устанавливает соединение с сервером
     *
     * @param name имя окна
     * @throws IOException если не удается установить соединение с сервером
     */
    private ClientApp(String name) throws IOException {
        super(name);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        log.setEditable(false);
        log.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(log);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        fieldInput.addActionListener(this);
        add(scrollPane, BorderLayout.CENTER);
        add(nickname, BorderLayout.NORTH);
        add(fieldInput, BorderLayout.SOUTH);
        setVisible(true);

        tcpConnection = new TCPConnection(this, new Socket("127.0.0.1", 8080));
    }

    /**
     * Обработчик событий для поля ввода. Отправляет сообщение на сервер.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldInput.getText();
        if (message.isEmpty()) {//при вводе пустой строки
        } else {
            fieldInput.setText(null);
            tcpConnection.sendMessage(nickname.getText() + ": " + message);
        }
    }

    /**
     * Выводит сообщение в текстовое поле логов.
     *
     * @param message сообщение для вывода.
     */
    private void printMsg(String message) {
        SwingUtilities.invokeLater(() -> {
            log.append(FORMATTER.format(zonedDateTime) + message + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });

    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connected");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String message) {
        printMsg(message);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Disconnected");
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Соединение разорвано.", "Информация", JOptionPane.INFORMATION_MESSAGE);
            tcpConnection.disconnect();
        });
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        printMsg("Исключение: " + exception);
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Произошла ошибка: " + exception.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE));
    }
}
