package performance.locust;

import com.bigsonata.swarm.Cron;
import com.bigsonata.swarm.Props;
import okhttp3.*;

public class ApiCron extends Cron {

    private String url;
    private String jwt;
    private String method;
    private String name;
    private String requestJson = "";
    private OkHttpClient client = new OkHttpClient();

    public ApiCron(String url, String jwt, String method, String name) {
        this(url, jwt, method, name, "");
    }

    public ApiCron(String url, String jwt, String method, String name, String requestJson) {
        super(Props.createAsync().setType(method).setName(name));
        this.url = url;
        this.jwt = jwt;
        this.method = method;
        this.name = name;
        this.requestJson = requestJson;
    }

    @Override
    public void dispose() { }

    @Override
    public void process() {
        RequestBody body = RequestBody.create(MediaType.get("application/json"), getRequestJson());
        Request request;
        if (getRequestJson() == null || getRequestJson().equals("")) {
            request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", jwt)
                .build();
        } else {
            request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", jwt)
                .method(method, body)
                .build();

        }
        long startTime = System.currentTimeMillis();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                long elapsed = System.currentTimeMillis() - startTime;
                recordSuccess(elapsed);
                response.close();
            }
        } catch (Exception ex) {
            long elapsed = System.currentTimeMillis() - startTime;
            recordFailure(elapsed, ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public Cron clone() {
        return new ApiCron(this.url, this.jwt, this.method, this.name, this.requestJson);
    }

    @Override
    public void initialize() {
    }

    public String getRequestJson() {
        return requestJson;
    }
}
