package com.microservicio.usuarios_backend.model.dto;

public class ResponseModel {
    private boolean status;
    private String message;

    public ResponseModel(boolean status, String message){
        this.status = status;
        this.message = message;
    }

    public boolean getStatus(){
        return status;
    }

    public void setMessage(boolean status){
        this.status = status;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
