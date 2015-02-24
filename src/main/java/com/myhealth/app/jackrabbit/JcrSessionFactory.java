package com.myhealth.app.jackrabbit;

import org.springframework.stereotype.Service;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by JIECHEN on 1/26/15.
 */
@Service
public class JcrSessionFactory {

    public Session getSession() throws NamingException, RepositoryException {
        InitialContext ctx = new InitialContext();
        Context env = (Context) ctx.lookup("java:comp/env");
        Repository repo = (Repository) env.lookup("jcr/repository");
        return repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }
}
