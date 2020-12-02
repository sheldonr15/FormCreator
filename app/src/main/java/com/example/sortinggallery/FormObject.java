package com.example.sortinggallery;

import java.util.Date;

public class FormObject {
    private int id;
    private String username_form;
    private Date from;
    private Date to;
    private String form_title;
    private String form_address;

    public FormObject(){
        this.id = id;
        this.username_form = username_form;
        this.from = from;
        this.to = to;
        this.form_title = form_title;
        this.form_address = form_address;
    }

    public void setIdentity(int passed_id){ this.id = passed_id; }
    public int getIdentity(){ return this.id; }

    public void setUsername(String passed_username){ this.username_form = passed_username; }
    public String getUsername(){ return this.username_form; }

    public void setFromDate(Date passed_from){ this.from = passed_from; }
    public Date getFromDate(){ return this.from; }

    public void setToDate(Date passed_to){ this.to = passed_to; }
    public Date getToDate(){ return this.to; }

    public void setFormTitle(String passed_form_title){ this.form_title = passed_form_title; }
    public String getFormTitle(){ return this.form_title; }

    public void setFormAddress(String passed_form_address){ this.form_address = passed_form_address; }
    public String getFormAddress(){ return this.form_address; }
}
