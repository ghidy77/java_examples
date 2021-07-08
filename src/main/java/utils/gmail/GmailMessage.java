package utils.gmail;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/** Gmail Message (Email) object */
public class GmailMessage {
    private Message message;
    private static final Logger LOG = LogManager.getLogger(GmailMessage.class);
    private static final int MILISECONDS = 1000;

    /** Constructor initialises with Message
     *
     * @param message an Message instance returned by Gmail Client list().execute() methods */
    public GmailMessage(Message message) {
        this.message = message;
    }

    /** get the content of an email as plain text
     *
     * @return Content of the Message (email) as plain text */
    public String getContent() {
        return getContent(false);
    }

    /** get the content of an email as html / text
     *
     * @param htmlFormat set true for the content to be returned in HTML format
     * @param charset can be "Windows-1252" or UTF-8 or any other valid Charset
     * @return Content of the Message (email) in the required format: html or text
     * @throws UnsupportedEncodingException if Charset is not supported */
    public String getContent(boolean htmlFormat, String charset) {
        StringBuilder stringBuilder = new StringBuilder();
        String format;
        if (htmlFormat) {
            format = "text/html";
        } else {
            format = "text/plain";
        }

        // Payload should not be null to parse the Message
        if (message.getPayload() != null) {
            // First search for Parts in order to build the message
            if (message.getPayload().getParts() != null) {
                for (MessagePart messagePart : message.getPayload().getParts()) {
                    // search for the text format html or plain
                    if (messagePart.getMimeType().equals(format)) {
                        stringBuilder.append(messagePart.getBody().getData());
                    }
                }
                // If Message doesn't have Parts, fallback on Body of the message, if present
            } else if (message.getPayload().getBody() != null) {
                stringBuilder.append(message.getPayload().getBody().getData());
            }
        }

        byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
        try {
            return new String(bodyBytes, charset);
        } catch (UnsupportedEncodingException e) {
            LOG.debug(e);
        }
        return "";
    }

    /** get content of the email encoded by default in UTF-8
     *
     * @param htmlFormat set true for the content to be returned in HTML format
     * @return Content of the Message (email) in the required format: html or text */
    public String getContent(boolean htmlFormat) {
        return getContent(htmlFormat, "UTF-8");
    }

    /** get estimated size in bytes of the message.
     *
     * @return Estimated size in bytes of the message. */
    public int getSize() {
        return message.getSizeEstimate().intValue();
    }

    /** a short part of the message text.
     *
     * @return A short part of the message text. */
    public String getPreview() {
        return message.getSnippet();
    }

    /** get Date of the message. For normal SMTP-received email, this represents the time the message was originally
     * accepted by Google, which is more reliable than the Date header.
     *
     * @param pattern Pattern of date time formatter. e.g.: "yyyy-MM-dd hh:mm:ss"
     * @return String of Date time in the chosen pattern */
    public String getDate(String pattern) {
        return LocalDateTime.ofEpochSecond(message.getInternalDate() / MILISECONDS, 0, ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(pattern));
    }

    /** get the Message id
     *
     * @return id of Message */
    public String getId() {
        return message.getId();
    }

    /** get the value of a Header. example: From, To, Subject, Content-Type, Delivered-To
     *
     * @param header the name of the header. example: From, To, Subject, Content-Type, Delivered-To
     * @return the value of the header */
    public String getHeader(String header) {
        Optional<MessagePartHeader> headerMessage = message.getPayload().getHeaders().stream()
                .filter(messageHeader -> header.equals(messageHeader.getName())).findFirst();
        if (headerMessage.isPresent()) {
            return headerMessage.get().getValue();
        }
        return "";
    }

    /** Returns a pretty-printed serialized JSON string representation or toString() if getFactory() is null. */
    @Override
    public String toString() {
        try {
            return message.toPrettyString();
        } catch (IOException e) {
            LOG.debug(e);
        }
        return "";
    }
}
