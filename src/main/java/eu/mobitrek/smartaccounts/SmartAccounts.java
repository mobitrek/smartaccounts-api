package eu.mobitrek.smartaccounts;

import eu.mobitrek.security.HmacSha1Signature;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SmartAccounts {
    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String DEFAULT_URL = "https://sa.smartaccounts.eu/api";
    private String url;
    private String apikey;
    private String secret;
    private String clientId;
//    private SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
    private HttpClient cli = HttpClients.createDefault();

    public SmartAccounts(String apikey, String secret, String clientId, String url){
        this.apikey = apikey;
        this.secret = secret;
        this.clientId = clientId;
        this.url = url;
    }

    public SmartAccounts(String apikey, String secret, String clientId){
        this(apikey, secret, clientId, DEFAULT_URL);
    }

    public HttpResponse execPost(String cmd, String json) throws Exception{
        URIBuilder ub = uriBuilder(cmd);
        ub.addParameter("clientId", clientId);
        sign(ub, json);
        HttpPost post = new HttpPost(ub.build());
        post.setEntity(new StringEntity(json));
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("clientId", "f2016c93-d642-49da-97e2-25ec4573c140"));
//        post.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse resp = cli.execute(post);
        return resp;
    }

    public HttpResponse exec(String cmd, Map<String,String> params) throws Exception{
        URIBuilder ub = uriBuilder(cmd);

        if (params != null){
            for (String k : params.keySet()){
                ub.addParameter(k, params.get(k));
            }
        }

        sign(ub);
        URI uri = ub.build();
        log.info("uri {}", uri.toString());
        HttpGet get = new HttpGet(uri);

        HttpResponse resp = cli.execute(get);
//        log.info(resp.toString());
//        log.info(EntityUtils.toString(resp.getEntity()));
        return resp;
    }

    public HttpResponse execPurchasesalesClientsGet(Map<String, String> params) throws Exception{
        return exec("/purchasesales/clients:get", params);
    }

    public HttpResponse execPurchasesalesClientInvoicesAdd(String json) throws Exception{
        return execPost("/purchasesales/clientinvoices:add", json);
    }

    private URIBuilder uriBuilder(String cmd) throws URISyntaxException {
        return uriBuilder(cmd, null);
    }

    private URIBuilder uriBuilder(String cmd, String json) throws URISyntaxException {
        String temp = url + cmd;
        if (json != null){
            temp += json;
        }

        URIBuilder ub = new URIBuilder(temp);
        ub.addParameter("apikey", apikey)
                .addParameter("timestamp", getEstonianDate().format(dtf));
        return ub;
    }

    static ZonedDateTime getEstonianDate(){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime z = ZonedDateTime.now(ZoneId.of("Europe/Tallinn"));
        log.info("now UTC: {}", now.toString());
        log.info("now Tallinn: {}", z);
        return z;
    }

    private void sign(URIBuilder ub, String body) throws URISyntaxException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String query = ub.build().getQuery();
        log.info("query {}, body {}", query, body);
        if (body != null) query += body;
        String signature = HmacSha1Signature.calculateRFC2104HMAC(query, secret);
        ub.addParameter("signature", signature);
    }

    private void sign(URIBuilder ub) throws URISyntaxException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        sign(ub, null);
    }
}
