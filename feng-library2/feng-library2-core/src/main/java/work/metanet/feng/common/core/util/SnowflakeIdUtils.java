package work.metanet.feng.common.core.util;

/**
 * 雪花算法工具类
 * <p>
 * 该类用于生成基于雪花算法的全局唯一 ID。可通过配置 workerId 和 datacenterId 来进行分布式 ID 生成。
 * </p>
 */
public class SnowflakeIdUtils {

    // ============================== Fields ============================================
    /** 开始时间截 (2015-01-01) */
    private final long twepoch = 1420041600000L;

    /** 机器ID所占的位数 */
    private final long workerIdBits = 5L;

    /** 数据标识ID所占的位数 */
    private final long datacenterIdBits = 5L;

    /** 支持的最大机器ID，结果是31 */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 支持的最大数据标识ID，结果是31 */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /** 序列在ID中占的位数 */
    private final long sequenceBits = 12L;

    /** 机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;

    /** 数据标识ID向左移17位(12+5) */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /** 时间截向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 工作机器ID (0~31) */
    private long workerId = 3;

    /** 数据中心ID (0~31) */
    private long datacenterId = 1;

    /** 毫秒内序列 (0~4095) */
    private long sequence = 0L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     * <p>
     * 无入参构造。
     */
    public SnowflakeIdUtils() {
    	// workerId = 3;
    	// datacenterId = 1;
    }
    
    /**
     * 构造函数
     * <p>
     * 初始化工作ID (workerId) 和数据中心ID (datacenterId)，并进行合法性校验。
     * </p>
     * 
     * @param workerId     工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     * @throws IllegalArgumentException 如果传入的 workerId 或 datacenterId 超出范围
     */
    public SnowflakeIdUtils(long workerId, long datacenterId) {
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId < 0 || datacenterId > maxDatacenterId) {
            throw new IllegalArgumentException(String.format("datacenterId can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获得下一个ID (线程安全)
     * <p>
     * 生成一个新的唯一ID，并确保线程安全。
     * </p>
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) { // 毫秒内序列溢出
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else { // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回当前时间的毫秒表示
     *
     * @return 当前时间（毫秒）
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 测试主方法
     * <p>
     * 用于生成一个唯一的ID并打印
     * </p>
     */
    /*public static void main(String[] args) {
        SnowflakeIdUtils idWorker = new SnowflakeIdUtils(3, 1);
        System.out.println(idWorker.nextId());
    }*/
}
