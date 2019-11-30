package com.kang.mapper.entity;


import lombok.Data;

@Data
public class IpBlack {
    private int id;
    private String ipAddress;
    private int restrictionType;
    private int version;
}
