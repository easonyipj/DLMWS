package com.yipingjian.dlmws.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Create by yster@foxmail.com 2018/11/9 0009 22:28
 */
public class ExecuteCmd {

    static class InputStreamRunnable implements Runnable {
        private BufferedReader bReader = null;

        InputStreamRunnable(InputStream is) {
            try {
                bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), StandardCharsets.UTF_8));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            int num = 1;
            try {
                while (bReader.readLine() != null) {
                    System.out.println("---->"+String.format("%02d",num++)+" ");
                }
                bReader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * 执行外部程序,并获取标准输出
     */
    public static String execute(String[] cmd,String... encoding) {
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader;
        try {
            Process p = Runtime.getRuntime().exec(cmd);

            /* 为"错误输出流"单独开一个线程读取之,否则会造成标准输出流的阻塞 */
            Thread t = new Thread(new InputStreamRunnable(p.getErrorStream()));
            t.start();

            /* "标准输出流"就在当前方法中读取 */
            BufferedInputStream bis = new BufferedInputStream(p.getInputStream());

            if (encoding != null && encoding.length != 0) {
                inputStreamReader = new InputStreamReader(bis, encoding[0]);// 设置编码方式
            } else {
                inputStreamReader = new InputStreamReader(bis, StandardCharsets.UTF_8);
            }
            bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            bufferedReader.close();
            p.destroy();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
