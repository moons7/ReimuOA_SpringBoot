package com.reimu.shiro.filter;

import org.apache.shiro.web.servlet.AdviceFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ReimuAnonymousFilter extends AdviceFilter {


    public static final String FILTER_NAME = "anon";

    @Override
    protected void executeChain(ServletRequest request, ServletResponse response, FilterChain chain) throws Exception {
        response.setCharacterEncoding("UTF-8");
        super.executeChain(request, response, chain);
    }
}
