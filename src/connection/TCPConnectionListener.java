package connection;

/**
 * Интерфейс для прослушивания  каждого соединения
 * Реализация интерфейса нужна для обработки различных событий, происходящих у соединения
 */
public interface TCPConnectionListener {
    /**
     * Вызывается, когда соединение успешно установлено
     *
     * @param tcpConnection установленное соединение
     */
    void onConnectionReady(TCPConnection tcpConnection);

    /**
     * Вызывается, когда получено строковое сообщение из соединения
     *
     * @param tcpConnection соединение, из которого получено сообщение
     * @param message       сообщение, полученное из соединения
     */
    void onReceiveString(TCPConnection tcpConnection, String message);

    /**
     * Вызывается, когда соединение разорвано
     *
     * @param tcpConnection соединение, которое было разорвано
     */
    void onDisconnect(TCPConnection tcpConnection);

    /**
     * Вызывается, когда в соединении произошла ошибка.
     *
     * @param tcpConnection соединение, в котором произошла ошибка
     * @param exception  исключение, которое произошло
     */
    void onException(TCPConnection tcpConnection, Exception exception);


}
