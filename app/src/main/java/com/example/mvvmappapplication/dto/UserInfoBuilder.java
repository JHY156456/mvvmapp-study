package com.example.mvvmappapplication.dto;

public final class UserInfoBuilder {
    private String carNumber;
    private String id;
    private String password;

    public UserInfoBuilder setCarNumber(String carNumber) {
        this.carNumber = carNumber;
        return this;
    }

    public UserInfoBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public UserInfoBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserInfo build(){
        return new UserInfo(carNumber,id,password);
    }
}
