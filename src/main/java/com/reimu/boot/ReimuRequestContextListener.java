package com.reimu.boot;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.annotation.WebListener;

@Configuration
@WebListener
public class ReimuRequestContextListener extends RequestContextListener {
}
