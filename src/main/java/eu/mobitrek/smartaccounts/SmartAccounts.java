package eu.mobitrek.smartaccounts;

import eu.mobitrek.security.HmacSha1Signature;
import eu.mobitrek.smartaccounts.response.SaResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
public class SmartAccounts {
    public static final String DEFAULT_URL = "https://sa.smartaccounts.eu/api";
    private String url;
    private String apikey;
    private String secret;
    private String clientId;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
    private CloseableHttpClient cli = HttpClients.createDefault();

    public SmartAccounts(String apikey, String secret, String clientId, String url){
        this.apikey = apikey;
        this.secret = secret;
        this.clientId = clientId;
        this.url = url;
    }

    public SmartAccounts(String apikey, String secret, String clientId){
        this(apikey, secret, clientId, DEFAULT_URL);
    }

    public SaResponse execPost(String cmd, String json) throws Exception{
        URIBuilder ub = uriBuilder(cmd);
        ub.addParameter("clientId", clientId);
        sign(ub, json);
        HttpPost post = new HttpPost(ub.build());
        post.setEntity(new StringEntity(json));
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("clientId", "2564a1ae-2195-49d3-9acf-6f83868cec1d"));
//        post.setEntity(new UrlEncodedFormEntity(params));
        try (ClassicHttpResponse resp = cli.execute(post)) {
            return new SaResponse(resp.getCode(), resp.getReasonPhrase(), EntityUtils.toString(resp.getEntity()));
        }
    }

    public SaResponse exec(String cmd, Map<String,String> params) throws Exception{
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

        try (ClassicHttpResponse resp = cli.execute(get)) {
            return new SaResponse(resp.getCode(), resp.getReasonPhrase(), EntityUtils.toString(resp.getEntity()));
        }
    }

    public SaResponse execPurchasesalesClientsGet(Map<String, String> params) throws Exception{
        return exec("/purchasesales/clients:get", params);
    }

    public SaResponse execPurchasesalesClientInvoicesAdd(String json) throws Exception{
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
        log.info("now UTC: {}", now);
        log.info("now Tallinn: {}", z);
        return z;
    }

    private void sign(URIBuilder ub, String body) throws URISyntaxException, NoSuchAlgorithmException, InvalidKeyException {
        String query = ub.build().getQuery();
        log.info("query {}, body {}", query, body);
        if (body != null) query += body;
        String signature = HmacSha1Signature.calculateRFC2104HMAC(query, secret);
        ub.addParameter("signature", signature);
    }

    private void sign(URIBuilder ub) throws URISyntaxException, NoSuchAlgorithmException, InvalidKeyException {
        sign(ub, null);
    }
}
