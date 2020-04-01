package com.yipingjian.dlmws.java.service.impl;

import com.yipingjian.dlmws.common.utils.ExecuteCmd;
import com.yipingjian.dlmws.java.entity.JPS;
import com.yipingjian.dlmws.java.service.JavaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JavaServiceImpl implements JavaService {


    @Override
    public List<JPS> getJPSInfo() {
        List<JPS> jpsList = new ArrayList<>();
        String s = ExecuteCmd.execute(new String[]{"jps", "-l", "-v"});
        String[] line = s != null ? s.split("\n") : new String[0];
        for (String aLine : line) {
            String[] one = aLine.split("\\s+");
            //排除sun.tools进程
            if (one[1].contains("sun.tools")){
                continue;
            }
            //格式化控制台输出
            JPS jps = new JPS();
            jps.setPid(Integer.valueOf(one[0]));
            jps.setName(one[1]);
            jpsList.add(jps);
        }
        return jpsList;
    }
}
