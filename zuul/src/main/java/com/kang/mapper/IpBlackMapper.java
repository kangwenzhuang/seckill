package com.kang.mapper;

import com.kang.mapper.entity.IpBlack;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface IpBlackMapper {
    @Select("select * from ip_blacklist where ip_address= #{ipAddress} and restriction_type= #{restrictionType}")
    IpBlack findByIp(@Param("ipAddress") String ipAddress, @Param("restrictionType") int restrictionType);
}
