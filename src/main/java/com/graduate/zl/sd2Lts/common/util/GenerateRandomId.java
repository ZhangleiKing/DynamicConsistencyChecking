package com.graduate.zl.sd2Lts.common.util;

import java.util.HashMap;
import java.util.Map;

public class GenerateRandomId {

    //开始时间戳，由配置文件获取
    private long startTimeStamp;

    //毫秒数位数
    private int millisBits;

    //序列号位数
    private int sequenceBits;

    //存储Id中间字段<字段名称，位数>
    private Map<String, Integer> idMidFactors;

    //Id中间字段传入值（例如机房号：1，机架号：8，机器号：413）
    private Map<String, Long> midFactorsValue;

    //当前序列号
    private long sequence = 0L;

    //序列号掩码（如12位序列号的掩码为0b11111111111=0xfff=4095）
    private long sequenceMask;

    //上次生成Id的时间戳
    private long lastTimeStamp = -1L;

    private void init() {
        idMidFactors = new HashMap<>();
        startTimeStamp = 1420041600000L; //2015-01-01
        millisBits = 41;
        sequenceBits = 12;
        sequenceMask = -1L ^ (-1L << sequenceBits);
        idMidFactors.put("engineRoom", 2);
        idMidFactors.put("frame", 3);
        idMidFactors.put("worker", 5);
    }

    public GenerateRandomId() {
        init();
    }

    public GenerateRandomId(Map<String, Long> midFactorsValue) {
        init();
        this.midFactorsValue = midFactorsValue;
    }

    public synchronized long getNextId() {
        //返回当前时间（毫秒）
        long timeStamp = System.currentTimeMillis();

        //如果当前时间小于上一次生成Id的时间戳，说明系统时钟的问题
        if(timeStamp < lastTimeStamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", 1));
        }

        if(timeStamp == lastTimeStamp) {
            sequence = (sequence +1) & sequenceMask;
            //毫秒内序列溢出
            if(sequence == 0) {
                //获得新的（更大的）时间戳
                timeStamp = getNextMillis(timeStamp);
            }
        }
        else {
            //时间戳发生变化，该毫秒内序列重置
            sequence = 0L;
        }

        //更新上次生成Id的时间戳
        lastTimeStamp = timeStamp;

        long ret = sequence;

        int offset = sequenceBits;
        //注意，采用HashMap后，无法保证中间字段是符合原来的顺序（但是并不影响）
        for(Map.Entry<String, Integer> entry : idMidFactors.entrySet()) {
            int bits = entry.getValue();
            ret = ret | (midFactorsValue.get(entry.getKey()) << offset);
            offset += bits;
        }
        ret = ret | (((long)(timeStamp - startTimeStamp)) << offset);

        return ret;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param timeStamp 上次生成Id的时间戳
     * @return 当前时间戳
     */
    protected long getNextMillis(long timeStamp) {
        long currTimeStamp = System.currentTimeMillis();
        while(currTimeStamp <= timeStamp) {
            currTimeStamp = System.currentTimeMillis();
        }
        return currTimeStamp;
    }

    public static void main(String[] args) {
        System.out.println("hahah");
    }
}
