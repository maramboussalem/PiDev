package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;

public class MapViewController {
    @FXML
    private WebView webView;

    @FXML
    public void initialize() {
        try {
            WebEngine webEngine = webView.getEngine();
            webEngine.setJavaScriptEnabled(true);
            webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
            URL url = getClass().getResource("/map.html");
            if (url != null) {
                webEngine.load(url.toExternalForm());
                webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == javafx.concurrent.Worker.State.FAILED) {
                        System.err.println("Failed to load map.html: " + webEngine.getLoadWorker().getException());
                        webEngine.loadContent("<h1>Error: Failed to load map</h1><p>Check console for details.</p>");
                    } else if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        System.out.println("Map loaded successfully.");
                        String content = (String) webEngine.executeScript("document.documentElement.outerHTML");
                        System.out.println("Page content: " + content);
                    }
                });
                // Force resize when WebView size changes
                webView.widthProperty().addListener((obs, old, newVal) -> {
                    System.out.println("WebView width changed to: " + newVal);
                    webEngine.executeScript("if (typeof resizeMap === 'function') resizeMap();");
                });
                webView.heightProperty().addListener((obs, old, newVal) -> {
                    System.out.println("WebView height changed to: " + newVal);
                    webEngine.executeScript("if (typeof resizeMap === 'function') resizeMap();");
                });
                // Delayed resize to ensure map loads correctly
                webEngine.executeScript("setTimeout(function() { if (typeof resizeMap === 'function') resizeMap(); }, 500);");
            } else {
                System.err.println("Error: Couldn't find map.html!");
                webEngine.loadContent("<h1>Error: Map file not found</h1>");
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize map: " + e.getMessage());
        }
    }
}