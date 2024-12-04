## SmartAccounts API lib for Java

### Quick Start

1. Build jar into local maven repo
   `./gradlew publishToMavenLocal`
2. Include it in your other project's build like `implementation 'eu.mobitrek:smartaccounts-api:3.1.0'`
3. Use in the code
    ```
    var sa = new SmartAccounts(apikey, secret, clientId);
   
    var clientRegCode = "1234567";
    Map params = Map.of("nameOrRegCode", clientRegCode);
   
    var resp = sa.execPurchasesalesClientsGet(params);
   
    var json = EntityUtils.toString(resp.getEntity());
    var jsonObject = new Gson().fromJson(json, JsonObject.class);
    var clients = jsonObject.getAsJsonArray("clients");
   ```

### SmartAccounts documentation
- API https://www.smartaccounts.eu/uuskodu2015/wp-content/uploads/2023/03/SmartAccounts_API_latest-1.pdf
- Postman examples https://www.smartaccounts.eu/uuskodu2015/wp-content/uploads/2024/04/postman_collection.json
