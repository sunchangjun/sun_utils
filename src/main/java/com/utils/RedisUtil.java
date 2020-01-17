
package com.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.io.*;

/**
 * @author sunchangjunn 2018年9月4日下午6:24:06
 */
public class RedisUtil {

	private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

	private static JedisPool jedisPool = null;

	// Redis服务器IP
	private static String ADDR = "192.168.3.11";

	// Redis的端口号
	private static int PORT = 6379;

	// 访问密码
	private static String AUTH = "";

	// 可用连接实例的最大数目，默认值为8；
	// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE = 1024;

	// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 200;

	// 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 10000;

	private static int TIMEOUT = 10000;

	// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = true;

	/**
	 * 初始化Redis连接池
	 */
	static {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			/* 有密码 */
//			jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH);
			/* 无密码 */
			jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 获取资源 */
	public static Jedis getResource() throws JedisException {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
		} catch (JedisException e) {
			logger.warn("getResource.", e);
			if (jedis != null) {
				//jedisPool.returnBrokenResource(jedis);
                jedisPool.getResource();
			}
			throw e;
		}
		return jedis;
	}

	/* 归还资源 */
	public static void returnResource(Jedis jedis) {
		if (jedis != null) {
			//jedisPool.returnResource(jedis);
            jedisPool.getResource();
		}
	}

	/* 检查key是否存在 */
	public static boolean exists(String key) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.exists(key);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			returnResource(jedis);
		}
	}

	/* 获取缓存 */
	public static String get(String key) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.get(key);
			if ("null".equals(result)) {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	


	/**
	 * 获得指定文件的byte数组
	 */
	private static byte[] fileToBytes(File file) {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/* 获取缓存 */
	public static String set(String key, File file) {
		String result = null;
		Jedis jedis = null;
		try {
			byte[] buffer = fileToBytes(file);
			jedis = getResource();
			result = jedis.set(key.getBytes(), buffer);
			if ("null".equals(result)) {
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}
	
	

	/**
	 * 向链表尾部插入元素
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static Long rpush(String key, String value) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.rpush(key, value);

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	
	/**
	 * 移除并返回链表头部的元素
	 * 
	 * @param key
	 * @return
	 */
	public static String lpop(String key) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.lpop(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* 设置缓存 */
	public static String set(String key, Object value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			if (value instanceof String) {
				result = jedis.set(key, (String) value);
			} else {
				result = jedis.set(key, JSONObject.toJSONString(value));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* 删除链表中的元素 */
	public static Long lrem(String key, long count, String value) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.lrem(key, count, value);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* 删除key */
	public static Long del(String key) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.del(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* 查询key的生命周期(毫秒) */
	public static Long pttl(String key) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.pttl(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* 设置缓存并设置过期时间(秒) */
	public static String setex(String key, int seconds, String value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.setex(key, seconds, value);
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* 设置缓存并设置过期时间(毫秒) */
	public static String psetex(String key, int seconds, String value) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			result = jedis.psetex(key, seconds, value);
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* 设置某时间点过期(秒) */
	public static Long expireAt(String key, long timestamp, String value) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.set(key, value);
			result = jedis.expireAt(key, timestamp);
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* 设置某时间点过期(毫秒) */
	public static Long pexpireAt(String key, long timestamp, String value) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.set(key, value);
			result = jedis.pexpireAt(key, timestamp);
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* 移除key的过期时间 */
	public static Long persist(String key) {
		Long result = null;
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.persist(key);

		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/* incr(key)：名称为key的string增1操作 */
	public static boolean incr(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.incr(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * incrby(key, integer)：名称为key的string增加integer
	 */
	public static boolean incrBy(String key, int value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.incrBy(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			returnResource(jedis);
		}
	}

	/** * decr(key)：名称为key的string减1操作 */
	public static boolean decr(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.decr(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * decrby(key, integer)：名称为key的string减少integer
	 */
	public static boolean decrBy(String key, int value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.decrBy(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			returnResource(jedis);
		}
	}
	
	  /**
     * 获取流
     * @param key
     * @return
     */
    public static InputStream getInputStream(String key){
    	InputStream result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            byte[] bytes =  jedis.get(key.getBytes());
            if(null != bytes  &&  bytes.length > 0) {
            	  result = new ByteArrayInputStream(bytes);
            }      
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
        return  result;
    }

}
