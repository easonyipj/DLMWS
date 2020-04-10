package com.yipingjian.dlmws.java.service.impl;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.common.utils.ArrayUtils;
import com.yipingjian.dlmws.common.utils.CommonUtils;
import com.yipingjian.dlmws.common.utils.ExecuteCmd;
import com.yipingjian.dlmws.java.entity.*;
import com.yipingjian.dlmws.java.service.JavaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.jvmstat.monitor.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;

@Slf4j
@Service
public class JavaServiceImpl implements JavaService {

    private final static String PREFIX = "java.lang.Thread.State: ";
    private final static String JETBRAINS = "jetbrains";
    private final static List<String> excludeProcess = Lists.newArrayList("org.apache.zookeeper.server.quorum.QuorumPeerMain",
            "sun.tools.jstat.Jstat", "sun.tools.jstack.JStack", "org/netbeans/Main", "kafka.Kafka", "com.yipingjian.dlmws.storm.topology.DLMWSTopology");
    private final static List<String> heapUsed = Lists.newArrayList("S0U", "S1U", "EU", "OU");
    private final static List<String> heapCapacity = Lists.newArrayList("S0C", "S1C", "EC", "OC");
    private final static String LOADED = "Loaded";
    private final static String COMPILED = "Compiled";

    @Override
    public List<JPS> getJPSInfo()  throws Exception{
        List<JPS> jpsList = new ArrayList<>();
        MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
        Set<?> vmList = new HashSet<>(local.activeVms());

        for (Object process : vmList) {

            try {
                MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + process));
                String processName = MonitoredVmUtil.mainClass(vm, true);
                if (!StringUtils.isEmpty(processName) && !excludeProcess.contains(processName) && !processName.contains(JETBRAINS)) {
                    jpsList.add(new JPS((Integer) process, processName));
                }
            } catch (Exception e) {
                log.error("get jps info error", e);
            }
        }
        log.info(jpsList.toString());
        return jpsList;
    }

    @Override
    public JVMClass getJVMClassInfo(Integer pid) throws Exception {
        JVMClass jvmClass = new JVMClass();
        jvmClass.setPid(pid);
        jvmClass.setHostIp(CommonUtils.getHostIp());
        jvmClass.setTime(new Date(System.currentTimeMillis()));
        List<KVEntity> jstatClass = jstat(new String[]{"jstat", "-class", String.valueOf(pid)});
        for (KVEntity kvEntity : jstatClass) {
            if (kvEntity.getKey().equals(LOADED)) {
                jvmClass.setClassLoaded(Integer.parseInt(kvEntity.getValue()));
                break;
            }
        }
        List<KVEntity> jstatCompiler = jstat(new String[]{"jstat", "-compiler", String.valueOf(pid)});
        for (KVEntity kvEntity : jstatCompiler) {
            if (kvEntity.getKey().equals(COMPILED)) {
                jvmClass.setClassLoaded(Integer.parseInt(kvEntity.getValue()));
                break;
            }
        }
        return jvmClass;
    }

    @Override
    public JVMMemory getJVMMemoryInfo(Integer pid) throws Exception {
        List<KVEntity> kvEntityList = jstat(new String[]{"jstat", "-gc", String.valueOf(pid)});
        JVMMemory jvmMemory = generateJVMMemory(kvEntityList);
        jvmMemory.setPid(pid);
        jvmMemory.setHostIp(CommonUtils.getHostIp());
        jvmMemory.setTime(new Date(System.currentTimeMillis()));
        return jvmMemory;
    }

    @Override
    public JVMThread getJVMThreadInfo(Integer pid) {

        JVMThread jvmThread = new JVMThread();
        String s = ExecuteCmd.execute(new String[]{"jstack", String.valueOf(pid)});
        jvmThread.setPid(pid);
        jvmThread.setHostIp(CommonUtils.getHostIp());
        jvmThread.setTotal(ArrayUtils.appearNumber(s, "nid="));
        jvmThread.setRunnable(ArrayUtils.appearNumber(s, PREFIX + "RUNNABLE"));
        jvmThread.setTimeWaiting(ArrayUtils.appearNumber(s, PREFIX + "TIMED_WAITING"));
        jvmThread.setWaiting(ArrayUtils.appearNumber(s, PREFIX + "WAITING"));
        jvmThread.setTime(new Date(System.currentTimeMillis()));
        return jvmThread;
    }

    private JVMMemory generateJVMMemory(List<KVEntity> list) {
        JVMMemory jvmMemory = new JVMMemory();
        float totalUsed = 0;
        float totalCapacity = 0;
        for (KVEntity kvEntity : list) {
            if (heapUsed.contains(kvEntity.getKey())) {
                totalUsed += Float.parseFloat(kvEntity.getValue());
            }
            if (heapCapacity.contains(kvEntity.getKey())) {
                totalCapacity += Float.parseFloat(kvEntity.getValue());
            }
        }
        jvmMemory.setMemoryCapacity(totalCapacity / 1024);
        jvmMemory.setMemoryUsed(totalUsed / 1024);
        return jvmMemory;
    }

    private static List<KVEntity> jstat(String[] strings) throws Exception {
        List<KVEntity> list = new ArrayList<>();
        String s = ExecuteCmd.execute(strings);
        assert s != null;
        BufferedReader reader = new BufferedReader(new StringReader(s));
        String[] keys = ArrayUtils.trim(reader.readLine().split("\\s+|\t"));
        String[] values = ArrayUtils.trim(reader.readLine().split("\\s+|\t"));
        // 特殊情况
        if (strings[1].equals("-compiler")) {
            for (int i = 0; i < 4; i++) {
                list.add(new KVEntity(keys[i], values[i]));
            }
            return list;
        }
        // 正常流程
        for (int i = 0; i < keys.length; i++) {
            list.add(new KVEntity(keys[i], values[i]));
        }
        return list;
    }

}
