/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.hash;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一致性hash算法
 * @author  DanielCao
 * @date    2015年4月1日
 * @time    下午8:01:45
 *
 * @param <T> 节点类型
 */
public class ConsistentHash<T> {
	private static final int DEFAULT_NUMBER_REPLICAS = 100;
	
	protected static Logger logger = LoggerFactory.getLogger(ConsistentHash.class);
	
	private final HashFunction hashFunction; 
	private final int numberOfReplicas; 
	private final SortedMap<Long, T> circle = new TreeMap<Long, T>();
	
	public ConsistentHash(Collection<T> nodes) {
		this(DEFAULT_NUMBER_REPLICAS, nodes);
	}
	
	public ConsistentHash(int numberOfReplicas, Collection<T> nodes) {
		this(new MurMurHashFunction(), numberOfReplicas, nodes);
	}
	
	public ConsistentHash(HashFunction hashFunction, Collection<T> nodes) {
		this(hashFunction, DEFAULT_NUMBER_REPLICAS, nodes);
	}
	
	public ConsistentHash(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes) {
		this.hashFunction = hashFunction;
		if (numberOfReplicas <= 0) {
			this.numberOfReplicas = 1;
		} else {
			this.numberOfReplicas = numberOfReplicas;
		}
		
		for (T node : nodes) {
			add(node);
		}
	}
	/**
	 * 添加节点
	 * @param node 节点值
	 */
	public void add(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.put(hashFunction.hash(node.toString() + i), node);
		}
	}
	
	/**
	 * 删除节点
	 * @param node 节点值
	 */
	public void remove(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.remove(hashFunction.hash(node.toString() + i));
		}
	}
	
	/**
	 * 根据输入值的hash值获取节点
	 * @param key 输入key值
	 * @return 节点值
	 */
	public T get(Object key) {
		if (circle.isEmpty()) {
			return null;
		}
		long hash = hashFunction.hash(key);
		
		if (!circle.containsKey(hash)) {
			SortedMap<Long, T> tailMap = circle.tailMap(hash);
			hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		}
		
		return circle.get(hash);
	}
	/**
	 * 测试用例
	 * @param args 命令行参数 
	 */
	/**
	public static void main(String[] args) {
		String[] myNodes = {"A", "B"};
		List<String> list = Arrays.asList(myNodes);
		ConsistentHash<String> consistentHash = new ConsistentHash<String>(200, list);
		Map<String, Integer> statisticMap = new HashMap<String, Integer>();
		
		for (int i = 0; i < 10; i++) {
			//long lastValue = (long)(Math.random() * System.currentTimeMillis() * (i + 1));
			String key = "" + i;
			String value = consistentHash.get(key);
			logger.info(key + ":" + value);
			int count = 0;
			if (statisticMap.containsKey(value)) {
				count = statisticMap.get(value);
			} else {
				count = 0;
			}
			statisticMap.put(value, count + 1);
		}
		for (String node : myNodes) {
			logger.info(node + ":" + statisticMap.get(node));
		}
	}
	*/
}
