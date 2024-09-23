package com.lifeAIFrontend.LifeAIFrontend.session;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setMaxInactiveInterval(-1); // infinity
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        // Session destroyed logic
    }
}
