package utils.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import utils.PropertyReader;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/** Gmail Client used to read email from Gmail */
public class GmailClient {
    private static final Logger LOG = LogManager.getLogger(GmailClient.class);
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final int LOCAL_SERVER_PORT = 8888;
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /** Generated token represents these scopes. If scopes are changed, delete the tokens/ folder to generate it
     * again */
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.MAIL_GOOGLE_COM);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String USER_EMAIL = PropertyReader.getProperty("gmail.user");
    private static final long MAX_RESULTS = 100L;
    private static final List<String> INBOX_LABEL = Arrays.asList("INBOX");
    private static final List<String> UNREAD_LABEL = Arrays.asList("UNREAD");
    private static final int MAXIMUM_WAIT_SECONDS = 1200;
    private static final int RETRY_AFTER_MILISECONDS = 20000;
    private NetHttpTransport httpTransport;
    private Gmail gmail;

    /** Constructor that initializes the Gmail Client
     *
     * @throws GeneralSecurityException if http transport cannot be instantiated
     * @throws IOException if credentials / token files are missing */
    public GmailClient() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        gmail = new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /** Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found. */
    private Credential getCredentials() throws IOException {
        // Load client secrets.
        InputStream in = GmailClient.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(LOCAL_SERVER_PORT).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /** get map (id: name) with gmail account's labels. e.g.: INBOX, PROMOTIONS, UPDATES
     *
     * @return Map of labels (id: name)
     * @throws IOException if client encounters an error */
    public Map<String, String> getGmailLabels() throws IOException {
        Map<String, String> labelsMap = new HashMap<>();
        ListLabelsResponse listResponse = gmail.users().labels().list(USER_EMAIL).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.isEmpty()) {
            throw new IllegalStateException("No Gmail labels found");
        }
        for (Label label : labels) {
            labelsMap.put(label.getId(), label.getName());
        }
        return labelsMap;
    }

    /** get Messages ids from Inbox, filtered by a gmail query
     *
     * @param query String used to filter the Messages listed, the same as in Gmail. For example,
     *              "from:someuser@example.com rfc822msgid:<somemsgid@example.com> is:unread"
     * @return List of Messages ids
     * @throws IOException if client encounters an error */
    public List<String> getFilteredMessageIds(String query) throws IOException {
        ListMessagesResponse response = gmail.users().messages().list(USER_EMAIL).setMaxResults(MAX_RESULTS).setQ(query)
                .execute();

        return extractIdsFromResponseList(response, query);
    }

    /** get all Messages Ids from Inbox
     *
     * @return List of all Messages ids
     * @throws IOException if client encounters an error */
    public List<String> getAllMessageIds() throws IOException {
        ListMessagesResponse response = gmail.users().messages().list(USER_EMAIL).setMaxResults(MAX_RESULTS)
                .setLabelIds(INBOX_LABEL).execute();
        return extractIdsFromResponseList(response, null);

    }

    /** helper method that parses the ListMessagesResponse and extracts the ids of the messages. You can use the
     * returned id to call getMessage(id) in order to instantiate a GmailMessage
     *
     * @param response this is the result of the list().execute() methods of gmail client
     * @param query String used to filter the Messages listed to search in the next pages
     * @return list of Messages ids
     * @throws IOException if client encounters an error */
    private List<String> extractIdsFromResponseList(ListMessagesResponse response, String query) throws IOException {
        List<Message> messages = new ArrayList<>();
        List<String> messagesIDs = new ArrayList<>();

        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();

                // if search filter was set, it must be sent again for the next pages
                if (query != null) {
                    response = gmail.users().messages().list(USER_EMAIL).setLabelIds(INBOX_LABEL).setQ(query)
                            .setPageToken(pageToken).execute();
                } else {
                    response = gmail.users().messages().list(USER_EMAIL).setLabelIds(INBOX_LABEL)
                            .setPageToken(pageToken).execute();
                }
            } else {
                break;
            }
        }

        for (Message message : messages) {
            messagesIDs.add(message.getId());
        }
        return messagesIDs;
    }

    /** get a Message object, searching by its ID
     *
     * @param messageId id of the Message object, can be found in getAllMessagesIDs();
     * @return GmailMessage object
     * @throws IOException if client encounters an error */
    public GmailMessage getMessage(String messageId) throws IOException {
        return new GmailMessage(gmail.users().messages().get(USER_EMAIL, messageId).execute());
    }

    /** wait for the number of emails in inbox to be more than a provided number
     *
     * @param number number of emails currently in inbox
     * @return true if new email was received
     * @throws  if the condition is false */
    public boolean waitForInboxEmailsToBeMoreThan(int number) {
        Awaitility.await().pollDelay(20,TimeUnit.SECONDS).atMost(20, TimeUnit.MINUTES).until(()-> {
            try {
                return this.getAllMessageIds().size() > number;
            } catch (IOException e) {
                LOG.debug(e);
            }
            return false;
            }
        );

        return true;
    }

    /** Get the last email message
     *
     * @return The {@link GmailMessage} email message
     * @throws IOException if connection is not successful */
    public GmailMessage getLastEmail() throws IOException {
        String lastEmailId = getAllMessageIds().get(0);
        return getMessage(lastEmailId);
    }

    /** Mark the message as read
     *
     * @param messageId The {@link String} message id
     * @throws IOException if connection is not successful */
    public void markAsRead (String messageId) throws IOException {
        ModifyMessageRequest modified =
                new ModifyMessageRequest()
                        .setAddLabelIds(INBOX_LABEL)
                        .setRemoveLabelIds(UNREAD_LABEL);

        gmail.users().messages().modify(USER_EMAIL, messageId, modified).execute();
    }
}
