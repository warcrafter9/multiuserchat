package chat.blacklistfilter;

import java.util.function.Predicate;

public interface BlackListFilter {
    /**
     * From the given list of comments removes ones
     * that contain words from the black list.
     *
     * @param message list of comments; every comment
     *                is a sequence of words, separated
     *                by spaces, punctuation or line breaks
     */
    default String filterMessage(String message, Predicate<StringBuilder> filter) {
        String filteredMessage = null;
        StringBuilder sb = new StringBuilder(message);
        if (filter.test(sb)) {
            filteredMessage = String.valueOf(sb);
        }
        return filteredMessage;
    }
}

