package utils.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class ApiClientExample {

    private static final String BASE_URL = "https://my-json-server.typicode.com/ghidy77/java_examples/";

    public static void main(String[] args) throws IOException {

        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", ContentType.APPLICATION_JSON.toString());

        // List all /products/
        HttpResponse response = get("products");
        printResponse(response);

        // List only the entry from /shoppers/ with id 3
        response = get("shoppers/3");
        printResponse(response);

        // Filter shoppers, by phone
        response = get("shoppers?phone=0788667788");
        printResponse(response);

        // Create a new entry in products
        response = post("products", headers, "{ \"name\" : \"American Cookies\" }");
        printResponse(response);

        // Update an existing entry in products
        response = put("products/1", headers, "{ \"id\": 1, \"name\": \"Clatite umplute\" }");
        printResponse(response);

        // Delete product with id 1
        response = delete("products/1");
        printResponse(response);

    }

    public static HttpResponse get(String url) throws IOException {
        HttpGet getRequest = new HttpGet(BASE_URL + url);
        return HttpClients.custom().build().execute(getRequest);

    }

    public static HttpResponse post(String url, Map<String, String> headers, String body) throws IOException {

        HttpPost postRequest = new HttpPost(BASE_URL + url);
        postRequest.setEntity(new StringEntity(body));

        if (!headers.isEmpty()) {
            for (String key : headers.keySet()) {
                postRequest.addHeader(key, headers.get(key));
            }
        }
        return HttpClients.custom().build().execute(postRequest);

    }

    public static HttpResponse put(String url, Map<String, String> headers, String body) throws IOException {

        HttpPut putRequest = new HttpPut(BASE_URL + url);
        putRequest.setEntity(new StringEntity(body));

        if (!headers.isEmpty()) {
            for (String key : headers.keySet()) {
                putRequest.addHeader(key, headers.get(key));
            }
        }
        return HttpClients.custom().build().execute(putRequest);

    }

    public static HttpResponse delete(String url) throws IOException {

        HttpDelete deleteRequest = new HttpDelete(BASE_URL + url);
        return HttpClients.custom().build().execute(deleteRequest);

    }

    public static void printResponse(HttpResponse response) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        System.out.println("Status code is: " + response.getStatusLine().getStatusCode());
        System.out.println("Request message is: " + response.getStatusLine().getReasonPhrase());

        response.getEntity().writeTo(outstream);
        System.out.println("Response Body: \n " + outstream.toString());
    }

}
