package com.yipingjian.dlmws.host.entity;

import lombok.Data;

import java.util.List;

@Data
public class HostDataEntity {
    private List<HostCpu> cpuList;
    private List<HostMem> memList;
}
