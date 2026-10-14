package it.zucchetti.lab;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@ApplicationScoped
public class AppResource {

    private final String startedAt = Instant.now().toString();

    @ConfigProperty(name = "app.message", defaultValue = "Ciao dalla mia app!")
    String message;

    @ConfigProperty(name = "app.version", defaultValue = "1.0")
    String version;

    @ConfigProperty(name = "app.crash.exit", defaultValue = "true")
    boolean crashExit;

    private String getHostname() {
        String envHostname = System.getenv("HOSTNAME");
        if (envHostname != null && !envHostname.isBlank()) {
            return envHostname;
        }
        String computerName = System.getenv("COMPUTERNAME");
        if (computerName != null && !computerName.isBlank()) {
            return computerName;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getHome() {
        String hostname = getHostname();
        String htmlTemplate = """
            <!DOCTYPE html>
            <html lang="it">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>UFS12 - DevOps Lab</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                        background: #f0f2f5;
                        color: #1c1e21;
                        margin: 0;
                        padding: 40px 20px;
                        display: flex;
                        justify-content: center;
                    }
                    .card {
                        background: #ffffff;
                        padding: 32px;
                        border-radius: 12px;
                        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
                        max-width: 540px;
                        width: 100%;
                    }
                    h1 {
                        margin-top: 0;
                        color: #1976d2;
                        font-size: 24px;
                    }
                    .item {
                        margin: 16px 0;
                        font-size: 16px;
                    }
                    .label {
                        font-weight: 600;
                        color: #4b5563;
                        display: block;
                        margin-bottom: 4px;
                    }
                    .value {
                        display: inline-block;
                        padding: 6px 12px;
                        background: #f3f4f6;
                        border-radius: 6px;
                        font-family: monospace;
                        font-size: 15px;
                        color: #111827;
                    }
                    .message {
                        font-size: 18px;
                        color: #1f2937;
                        padding: 12px;
                        background: #e0f2fe;
                        border-left: 4px solid #0284c7;
                        border-radius: 4px;
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <h1>UFS12 - DevOps Lab</h1>
                    <div class="item">
                        <span class="label">Messaggio:</span>
                        <div class="message">{{MESSAGE}}</div>
                    </div>
                    <div class="item">
                        <span class="label">Hostname:</span>
                        <span class="value">{{HOSTNAME}}</span>
                    </div>
                    <div class="item">
                        <span class="label">Versione:</span>
                        <span class="value">{{VERSION}}</span>
                    </div>
                </div>
            </body>
            </html>
            """;
        return htmlTemplate
                .replace("{{MESSAGE}}", message != null ? message : "")
                .replace("{{HOSTNAME}}", hostname != null ? hostname : "")
                .replace("{{VERSION}}", version != null ? version : "");
    }

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public InfoResponse getInfo() {
        return new InfoResponse(getHostname(), version, startedAt);
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHealth() {
        return Response.ok(Map.of("status", "ok")).build();
    }

    @POST
    @Path("/crash")
    @Produces(MediaType.APPLICATION_JSON)
    public Response crash() {
        if (crashExit) {
            Thread crashThread = new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                System.exit(1);
            }, "crash-thread");
            crashThread.setDaemon(true);
            crashThread.start();
        }
        return Response.status(Response.Status.ACCEPTED)
                .entity(Map.of("status", "crashing"))
                .build();
    }

    public record InfoResponse(String hostname, String version, String startedAt) {
    }
}
