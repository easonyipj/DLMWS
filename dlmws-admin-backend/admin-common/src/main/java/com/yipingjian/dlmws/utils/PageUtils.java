package com.yipingjian.dlmws.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageUtils implements Serializable {
    private int totalCount;
    private int pageSize;
    private int totalPage;
    private int currentPage;
    private List<?> list;

    public PageUtils(List<?> list, int totalCount, int pageSize, int currentPage) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    public PageUtils(IPage<?> page) {
        this.list = page.getRecords();
        this.totalCount = (int) page.getTotal();
        this.pageSize = (int) page.getSize();
        this.currentPage = (int) page.getCurrent();
        this.totalPage = (int) page.getPages();
    }


}
