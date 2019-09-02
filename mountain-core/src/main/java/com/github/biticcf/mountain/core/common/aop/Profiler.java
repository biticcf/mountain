/**
 * 
 */
package com.github.biticcf.mountain.core.common.aop;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * author  DanielCao
 * date    2015年4月1日
 * time    下午7:58:58
 *
 */
public final class Profiler {
	private static Logger logger = LoggerFactory.getLogger(Profiler.class);
	
	//private static final ThreadLocal<Entry> ENTRY_STACK = new ThreadLocal<Entry>();
	private static final InheritableThreadLocal<Entry> ENTRY_STACK = new InheritableThreadLocal<Entry>();

	private Profiler() {
		
	}
	
	/**
	 * 起始数据
	 */
    public static void start() {
        start((String) null);
    }

    /**
     * 起始数据
     * @param message 数据信息
     */
    public static void start(String message) {
        startPrivate(message);
    }

    /**
     * 起始数据
     * @param message 数据信息对象
     */
    public static void start(Message message) {
        startPrivate(message);
    }

    /**
     * 起始数据
     * @param message 数据信息对象
     */
    private static void startPrivate(Object message) {
    	Entry entry = (Entry) ENTRY_STACK.get();

    	if (entry == null) {
    		ENTRY_STACK.set(new Entry(message, null, null));
    	} else {
    		enterPrivate(message);
    	}
    }

    /**
     * 重置数据
     */
    public static void reset() {
    	ENTRY_STACK.set(null);
    }

    /**
     * 添加数据
     * @param message 数据信息
     */
    public static void enter(String message) {
    	enterPrivate(message);
    }

    /**
     * 添加数据
     * @param message 数据信息对象
     */
    public static void enter(Message message) {
    	enterPrivate(message);
    }
    
    /**
     * 添加数据
     * @param message 数据信息对象
     */
    private static void enterPrivate(Object message) {
        Entry currentEntry = getCurrentEntry();

        if (currentEntry != null) {
            currentEntry.enterSubEntry(message);
        }
    }

    /**
     * 释放数据
     */
    public static void release() {
        Entry currentEntry = getCurrentEntry();

        if (currentEntry != null) {
            currentEntry.release();
        }
    }

    /**
     * 取得花费时间
     * @return 时间值毫秒数
     */
    public static long getDuration() {
        Entry entry = (Entry) ENTRY_STACK.get();

        if (entry != null) {
            return entry.getDuration();
        } else {
            return -1;
        }
    }
    
    /**
     * 判断是否释放
     * @return true-已经释放;false-未释放
     */
    public static boolean isReleased() {
    	Entry entry = (Entry) ENTRY_STACK.get();
    	
    	if (entry != null) {
    		return entry.isReleased();
    	} else {
    		return true;
    	}
    }

    /**
     * 导出数据
     * @return 数据值字符串
     */
    public static String dump() {
        return dump("", "");
    }

    /**
     * 导出数据
     * @param prefix 数据值前缀
     * @return 数据值字符串
     */
    public static String dump(String prefix) {
        return dump(prefix, prefix);
    }

    /**
     * 导出数据
     * @param prefixF 数据值前缀1
     * @param prefixS 数据值前缀2
     * @return 数据值字符串
     */
    public static String dump(String prefixF, String prefixS) {
        Entry entry = (Entry) ENTRY_STACK.get();

        if (entry != null) {
            return entry.toString(prefixF, prefixS);
        } else {
            return "";
        }
    }

    /**
     * 取得尾条数据信息
     * @return 数据信息对象
     */
    public static Entry getEntry() {
        return (Entry) ENTRY_STACK.get();
    }

    /**
     * 取得首条数据信息
     * @return 数据信息对象
     */
    private static Entry getCurrentEntry() {
        Entry subEntry = (Entry) ENTRY_STACK.get();
        Entry entry = null;

        while (subEntry != null) {
        	entry = subEntry;
            subEntry = entry.getUnreleasedEntry();
        }

        return entry;
    }
    
    /**
     * 信息实体定义
     * author: Daniel.Cao
     * date:   2018年11月19日
     * time:   下午8:13:47
     *
     */
    public static final class Entry {
        private final List<Entry> subEntries = new ArrayList<Entry>(4);
        private final Object      message;
        private final Entry       parentEntry;
        private final Entry       firstEntry;
        private final long        baseTime;
        private final long        startTime;
        private long              endTime;

        private Entry(Object message, Entry parentEntry, Entry firstEntry) {
            this.message = message;
            this.startTime = System.currentTimeMillis();
            this.parentEntry = parentEntry;
            this.firstEntry = (Entry) ObjectUtils.defaultIfNull(firstEntry, this);
            this.baseTime = (firstEntry == null) ? 0 : firstEntry.startTime;
        }
        
        /**
         * 取得消息内容
         * @return 消息内容
         */
        public String getMessage() {
            String messageString = null;

            if (message instanceof String) {
                messageString = (String) message;
            } else if (message instanceof Message) {
                Message messageObject = (Message) message;
                MessageLevel level = MessageLevel.BRIEF_MESSAGE;

                if (isReleased()) {
                    level = messageObject.getMessageLevel(this);
                }

                if (MessageLevel.DETAILED_MESSAGE.equals(level)) {
                    messageString = messageObject.getDetailedMessage();
                } else {
                    messageString = messageObject.getBriefMessage();
                }
            }

            return StringUtils.defaultIfEmpty(messageString, null);
        }

        /**
         * 取得开始时间
         * @return 开始时间
         */
        public long getStartTime() {
            return baseTime > 0 ? startTime - baseTime : 0;
        }

        /**
         * 取得结束时间
         * @return 结束时间
         */
        public long getEndTime() {
            return endTime < baseTime ? -1L : endTime - baseTime;
        }

        /**
         * 取得总消费时间
         * @return 总消费时间
         */
        public long getDuration() {
            return endTime < startTime ? -1L : endTime - startTime;
        }

        /**
         * 取得当前节点的消费时间
         * @return 当前节点的消费时间
         */
        public long getDurationOfSelf() {
            long duration = getDuration();

            if (duration < 0) {
                return -1;
            }
            
            if (subEntries.isEmpty()) {
                return duration;
            }
            
            for (int i = 0; i < subEntries.size(); i++) {
                Entry subEntry = (Entry) subEntries.get(i);
                
                duration -= subEntry.getDuration();
            }
            
            return duration < 0 ? -1L : duration;
        }

        /**
         * 取得百分比
         * @return 百分比
         */
        public double getPecentage() {
            double parentDuration = 0d;
            double duration = 1d * getDuration();

            if ((parentEntry != null) && parentEntry.isReleased()) {
                parentDuration = 1d * parentEntry.getDuration();
            }

            if (duration > 0d && parentDuration > 0d) {
                return duration / parentDuration;
            }
            
            return 0d;
        }

        /**
         * 取得所有百分比
         * @return 所有百分比
         */
        public double getPecentageOfAll() {
            double firstDuration = 0d;
            double duration = 1d * getDuration();

            if (firstEntry != null && firstEntry.isReleased()) {
                firstDuration = 1d * firstEntry.getDuration();
            }

            if (duration > 0d && firstDuration > 0d) {
                return duration / firstDuration;
            }
            
            return 0d;
        }

        /**
         * 取得子信息列表
         * @return 子信息列表
         */
        public List<Entry> getSubEntries() {
            return Collections.unmodifiableList(subEntries);
        }

        /**
         * 释放计时
         */
        private void release() {
            endTime = System.currentTimeMillis();
        }

        /**
         * 判断当前entry是否结束。
         *
         * @return 如果entry已经结束，则返回<code>true</code>
         */
        private boolean isReleased() {
            return endTime > 0;
        }

        /**
         * 添加信息
         * @param message 信息
         */
        private void enterSubEntry(Object message) {
            Entry subEntry = new Entry(message, this, firstEntry);

            subEntries.add(subEntry);
        }

        /**
         * 取得未结束信息
         * @return 未结束信息
         */
        private Entry getUnreleasedEntry() {
            Entry subEntry = null;

            if (subEntries.isEmpty()) {
            	return null;
            }
            
            subEntry = (Entry) subEntries.get(subEntries.size() - 1);
            if (subEntry.isReleased()) {
            	subEntry = null;
            }

            return subEntry;
        }

        /**
         * 生成输出字符串
         * @return 输出的字符串
         */
        public String toString() {
            return toString("", "");
        }

        /**
         * 生成输出字符串
         * @param prefixF 开始行前缀
         * @param prefixS 中间行前缀
         * @return 输出的字符串
         */
        private String toString(String prefixF, String prefixS) {
            StringBuffer buffer = new StringBuffer();

            toString(buffer, prefixF, prefixS);

            return buffer.toString();
        }
        
        /**
         * 生成输出字符串
         * @param buffer 字符串缓存
         * @param prefixF 开始行前缀
         * @param prefixS 中间行前缀
         */
        private void toString(StringBuffer buffer, String prefixF, String prefixS) {
            buffer.append(prefixF);

            String _message = getMessage();
            long _startTime = getStartTime();
            long duration = getDuration();
            long durationOfSelf = getDurationOfSelf();
            double percent = getPecentage();
            double percentOfAll = getPecentageOfAll();

            Object[] params = new Object[] {_message, // {0} - entry信息 
                    Long.valueOf(_startTime), // {1} - 起始时间
                    Long.valueOf(duration), // {2} - 持续总时间
                    Long.valueOf(durationOfSelf), // {3} - 自身消耗的时间
                    Double.valueOf(percent), // {4} - 在父entry中所占的时间比例
                    Double.valueOf(percentOfAll) // {5} - 在总时间中所旧的时间比例
            };

            StringBuffer pattern = new StringBuffer("{1,number} ");

            if (isReleased()) {
                pattern.append("[{2,number}ms");

                if ((durationOfSelf > 0) && (durationOfSelf != duration)) {
                    pattern.append(" ({3,number}ms)");
                }

                if (percent > 0) {
                    pattern.append(", {4,number,##%}");
                }

                if (percentOfAll > 0) {
                    pattern.append(", {5,number,##%}");
                }

                pattern.append("]");
            } else {
                pattern.append("[UNRELEASED]");
            }

            if (_message != null) {
                pattern.append(" - {0}");
            }

            buffer.append(MessageFormat.format(pattern.toString(), params));

            for (int i = 0; i < subEntries.size(); i++) {
                Entry subEntry = (Entry) subEntries.get(i);

                buffer.append('\n');

                if (i == (subEntries.size() - 1)) {
                    subEntry.toString(buffer, prefixS + "`---", prefixS + "    "); // 最后一项
                } else if (i == 0) {
                    subEntry.toString(buffer, prefixS + "+---", prefixS + "|   "); // 第一项
                } else {
                    subEntry.toString(buffer, prefixS + "+---", prefixS + "|   "); // 中间项
                }
            }
        }
    }
    
    /**
     * 消息接口定义
     * author: Daniel.Cao
     * date:   2018年11月19日
     * time:   下午8:32:26
     *
     */
    public interface Message {
    	/**
    	 * 取得消息级别
    	 * @param entry 消息体
    	 * @return 消息级别
    	 */
        MessageLevel getMessageLevel(Entry entry);

        /**
         * 取得消息总览
         * @return 消息总览
         */
        String getBriefMessage();
        
        /**
         * 取得消息详情
         * @return 消息详情
         */
        String getDetailedMessage();
    }
    
    /**
     * 测试用例
     * @param args 命令行参数
     */
    public static void main(String[] args) {
    	Profiler.start("test");
    	
    	try {
			Thread.sleep(30);
			Thread.sleep(30);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Profiler.release();
			logger.info(Profiler.dump("www"));
		}
    	
    }
}
