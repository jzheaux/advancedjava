package com.sun.faces.test;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class Bean implements Serializable {
    
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public void actionListener() {
        // Poll stock ticker service and get the data for the requested symbol.
        this.info= "" + System.currentTimeMillis();
    }
    
    private String info;
    
    public String getInfo() {
        return info;
    }
}

