package com.yipingjian.dlmws.dingtalk.entity;

import lombok.Data;

import java.util.List;

@Data
public class ActionCard {
    private String title;
    private String markdown;
    private String btn_orientation;
    private List<BtnJSONList> btn_json_list ;
}
