import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Collectors;

public class HttpClientRunner {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientRunner.class);

    public static void main(String[] args) {
        String responseFromClient = "";
        if (args.length != 4) {
            logger.info("usage: java HttpClientRunner.jar");
            return;
        }
        String targetURL = args[0];
        String urlParameters = args[1];
        String method = args[2];
        String type = args[3];
        logger.info("Using: ");
        logger.info("targetURL: {}", targetURL);
        logger.info("urlParameters: {}", urlParameters);
        logger.info("method: {}", method);
        logger.info("type: {}", type);

        switch (method) {
            case "1":
                responseFromClient = new HttpRunner().executeMethod(targetURL, urlParameters, type);
                break;
            case "2":
                responseFromClient = new HttpRunner2().executeMethod(targetURL, urlParameters, type);
                break;
            case "3":
                responseFromClient = new HttpRunner3().executeMethod(targetURL, urlParameters, type);
                break;
            case "4":
                responseFromClient = new HttpRunner4().executeMethod(targetURL, urlParameters, type);
                break;
            default:
                logger.info("nothing choosen");
        }
        logger.info("responseFromClient: {}", responseFromClient);
    }

}

class HttpRunner {
    public String executeMethod(String targetURL, String urlParameters, String methodType) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(methodType.toUpperCase(Locale.ROOT));
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

class HttpRunner2 {
    public String executeMethod(String targetURL, String urlParameters, String methodType) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(targetURL);
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                response.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}

class HttpRunner3 {
    public String executeMethod(String targetURL, String urlParameters, String methodType) {
        try {
            URL url = new URL(targetURL);
            InputStream inputStream = url.openStream();
            try {
                /* Now read the retrieved document from the stream. */
                String text = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                return text;
            } finally {
                inputStream.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

class HttpRunner4 {
    public String executeMethod(String targetURL, String urlParameters, String methodType) {

        //Instantiate an HttpClient
        HttpClient client = new HttpClient();

        //Instantiate a GET HTTP method
        PostMethod method = new PostMethod(targetURL);
        method.setRequestHeader("Content-type",
                "text/xml; charset=ISO-8859-1");

        //Define name-value pairs to set into the QueryString
        NameValuePair nvp1 = new NameValuePair("firstName", "fname");
        NameValuePair nvp2 = new NameValuePair("lastName", "lname");
        NameValuePair nvp3 = new NameValuePair("email", "email@email.com");

        method.setQueryString(new NameValuePair[]{nvp1, nvp2, nvp3});

        try {
            int statusCode = client.executeMethod(method);

            System.out.println("Status Code = " + statusCode);
            System.out.println("QueryString>>> " + method.getQueryString());
            System.out.println("Status Text>>>"
                    + HttpStatus.getStatusText(statusCode));

            //Get data as a String
            System.out.println(method.getResponseBodyAsString());

            //OR as a byte array
            byte[] res = method.getResponseBody();

            //write to file
            FileOutputStream fos = new FileOutputStream("donepage.html");
            fos.write(res);

            //release connection
            method.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
