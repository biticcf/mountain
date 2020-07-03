/**
 * 
 */
package com.github.biticcf.mountain.core.common.util;

import java.util.Random;

/**
 * author: DanielCao
 * date:   2020-7-2
 * time:   21:29:06
 * + SnowFlake算法——64bit
 * + SnowFlake算法的缺点：
 * +   依赖于系统时钟的一致性。如果某台机器的系统时钟回拨，有可能造成ID冲突，或者ID乱序。
 * + SnowFlake所生成的ID一共分成四部分：
 * + 1，第一位：占用1bit，其值始终是0，没有实际作用。
 * + 2，时间戳：占用41bit，精确到毫秒，一般实现上不会存储当前的时间戳，
 * +    而是时间戳的差值（当前时间-固定的开始时间），这样可以使产生的ID从更小值开始；
 * +    41位的时间戳可以使用69年
 * + 3，工作机器id：占用10bit，其中高位5bit是数据中心ID（datacenterId），低位5bit是工作
 * +    节点ID（workerId），做多可以容纳1024个节点。
 * + 4，序列号：占用12bit，这个值在同一毫秒同一节点上从0开始不断累加，最多可以累加到4095。
 * + 同一毫秒的ID数量 = 1024 X 4096 = 4194304
 * 
 */
public class SnowFlakeUtils {
	// 起始的时间戳
    private final static long START_STMP = 1593708763695L;
    
    // 每一部分占用的位数，就三个
    private final static int SEQUENCE_BIT = 12;// 序列号占用的位数
    // 含数据中心的配置
    private final static int MACHINE_BIT = 5; // 机器标识占用的位数
    private final static int DATACENTER_BIT = 5;// 数据中心占用的位数
    // 不含数据中心的配置
    private final static int MACHINE_BIT_1 = 10; // 机器标识占用的位数
    
    // 每一部分最大值
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM_1 = -1L ^ (-1L << MACHINE_BIT_1);
    // 每一部分向左的位移
    private final static int MACHINE_LEFT = SEQUENCE_BIT;
    private final static int DATACENTER_LEFT = MACHINE_LEFT + MACHINE_BIT;
    private final static int TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
    private final static int TIMESTMP_LEFT_1 = MACHINE_LEFT + MACHINE_BIT_1;
    
    private int datacenterId = -1; // 数据中心(0 - 7)
    private int machineId = -1; // 机器标识(0 - 127或0 - 1024)
    
    private long sequence = 0L; // 序列号
    private long lastStmp = -1L;// 上一次时间戳
    
    private int type = 0; // 0 - 不含数据中心的配置，1 - 含数据中心的配置
    
    /**
     * + 从系统环境变量中读取参数:
     * + -Dsjdbc.self.id.generator.center.id=1
     * + -Dsjdbc.self.id.generator.worker.id=10
     */
    public SnowFlakeUtils() {
    	String datacenterIdStr = System.getProperty("sjdbc.self.id.generator.center.id", "-1");
    	try {
    		this.datacenterId = Integer.parseInt(datacenterIdStr.trim());
    	} catch (Exception e) {}
    	if (this.datacenterId > MAX_DATACENTER_NUM) {
    		this.datacenterId = this.datacenterId % ((int) MAX_DATACENTER_NUM);
        }
    	
    	int bound = 1;
    	if (this.datacenterId < 0) { // 不含数据中心
			bound = (int) MAX_MACHINE_NUM_1;
		} else { // 含数据中心
			bound = (int) MAX_MACHINE_NUM;
		}
    	
    	String machineIdStr = System.getProperty("sjdbc.self.id.generator.worker.id", "-1");
    	try {
    		this.machineId = Integer.parseInt(machineIdStr.trim());
    	} catch (Exception e) {}
    	if (this.machineId < 0) {
    		this.machineId = new Random().nextInt(bound);
    	} else {
    		if (this.machineId > (int) MAX_MACHINE_NUM) {
    			this.machineId = this.machineId % bound;
    		}
    	}
    }
    
    /**
     * + 不带数据中心的构造方法
     * @param machineId 机器标识(0 - 1023)
     */
    public SnowFlakeUtils(final int machineId) {
        if (machineId > MAX_MACHINE_NUM_1 || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than " + MAX_MACHINE_NUM_1 + " or less than 0");
        }
        
        this.datacenterId = 0;
        this.machineId = machineId;
        
        this.type = 0;
    }
    
    /**
     * + 带有数据中心的构造方法
     * @param datacenterId 数据中心(0 - 31)
     * @param machineId 机器标识(0 - 31)
     */
    public SnowFlakeUtils(final int datacenterId, final int machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than " + MAX_DATACENTER_NUM + " or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than " + MAX_MACHINE_NUM + " or less than 0");
        }
        
        this.datacenterId = datacenterId;
        this.machineId = machineId;
        
        this.type = 1;
    }
    
    /**
     * + 产生下一个ID
     * @return 下一个序列
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过  这个时候应当抛出异常
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }
        
        if (currStmp == lastStmp) {
            //if条件里表示当前调用和上一次调用落在了相同毫秒内，只能通过第三部分，序列号自增来判断为唯一，所以+1.
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大，只能等待下一个毫秒
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else { //时间戳改变，毫秒内序列重置
            //不同毫秒内，序列号置为0
            //执行到这个分支的前提是currTimestamp > lastTimestamp，说明本次调用跟上次调用对比，已经不再同一个毫秒内了，这个时候序号可以重新回置0了。
            sequence = 0L;
        }
        
        lastStmp = currStmp;
        //就是用相对毫秒数、机器ID和自增序号拼接
        //移位  并通过  或运算拼到一起组成64位的ID
        long result = 0L;
        if (this.type == 1) { // 带有数据中心
        	result =  (currStmp - START_STMP) << TIMESTMP_LEFT  //时间戳部分
                    | (1L * datacenterId) << DATACENTER_LEFT    //数据中心部分
                    | (1L * machineId) << MACHINE_LEFT          //机器标识部分
                    | sequence;                                 //序列号部分
        } else { // 不带数据中心
        	result =  (currStmp - START_STMP) << TIMESTMP_LEFT_1 //时间戳部分
                    | (1L * machineId) << MACHINE_LEFT           //机器标识部分
                    | sequence;                                  //序列号部分
        }
        
        return Long.MAX_VALUE & result;
    }
    
    /**
     * + 生成一个带有前缀的字符串id
     * @param prefix id前缀
     * @return id
     */
    public String nextId(String prefix) {
    	String _prefix = "";
    	if (prefix == null || prefix.trim().equals("")) {
    		_prefix = "";
    	} else {
    		_prefix = prefix.trim();
    	}
    	
    	return _prefix + nextId();
    }
    
    /**
     * + 下一毫秒数
     * @return 毫秒数
     */
    private long getNextMill() {
        long mill = getNewstmp();
        //使用while循环等待直到下一毫秒。
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }
    
    /**
     * + 新的时间戳
     * @return 时间戳
     */
    private long getNewstmp() {
        return System.currentTimeMillis();
    }
    
    public static void main(String[] args) {
    	SnowFlakeUtils snow = new SnowFlakeUtils();
    	
		System.out.println(snow.nextId());
	}
}
