package chat.blacklistfilter;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Класс, организующий фильтрацию слов по черному списку
 */
public class MessageFilter implements BlackListFilter {

    /**
     * Метод, осуществляющий маскировку нехороших слов
     *
     * @param blackList - множество слов, подвергаемых фильтрации
     * @return предикат фильтрации
     */
    public Predicate<StringBuilder> filterMasker(Set<String> blackList) {
        return message -> {
            String messageStr = message.toString();
            StringBuilder filteredMessage = new StringBuilder(messageStr);
            for (String word : messageStr.split("[;,\n  ]+")) {
                if (blackList.contains(word)) {
                    int startIndex = filteredMessage.indexOf(word);
                    int endIndex = startIndex + word.length();
                    filteredMessage.replace(startIndex, endIndex, createMask(word));
                }
            }
            message.replace(0, message.length(), filteredMessage.toString());
            return true;
        };
    }

    /**
     * Метод, осуществляющий маскировку нехороших слов.
     *
     * @param wordToMask - слово, которое необходимо замаскировать
     * @return зафильтрованное слово
     */
    private static String createMask(String wordToMask) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < wordToMask.length(); i++) {
            stringBuilder.append("*");
        }
        return stringBuilder.toString();
    }
}
