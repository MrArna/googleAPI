package configuration; /**
 * Created by Marco on 12/09/16.
 */
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.PubsubScopes;
import com.google.common.base.Preconditions;
import service.MailService;
import wrapper.RetryHttpInitializerWrapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Create a Pubsub client using portable credentials.
 */
public class PortableConfiguration {

    // Default factory method.
    public static Pubsub createPubsubClient() throws IOException {
        return createPubsubClient(Utils.getDefaultTransport(),
                Utils.getDefaultJsonFactory());
    }

    // A factory method that allows you to use your own HttpTransport
    // and JsonFactory.
    public static Pubsub createPubsubClient(HttpTransport httpTransport,
                                            JsonFactory jsonFactory) throws IOException {


        InputStream in = MailService.class.getResourceAsStream("/app_credential.json");
        Preconditions.checkNotNull(httpTransport);
        Preconditions.checkNotNull(jsonFactory);
        GoogleCredential credential = GoogleCredential.fromStream(in,httpTransport,jsonFactory);
        // In some cases, you need to add the scope explicitly.
        if (credential.createScopedRequired()) {
            credential = credential.createScoped(PubsubScopes.all());
        }
        // Please use custom HttpRequestInitializer for automatic
        // retry upon failures.  We provide a simple reference
        // implementation in the "Retry Handling" section.
        HttpRequestInitializer initializer =
                new RetryHttpInitializerWrapper(credential);
        return new Pubsub.Builder(httpTransport, jsonFactory, initializer)
                .build();
    }
}
