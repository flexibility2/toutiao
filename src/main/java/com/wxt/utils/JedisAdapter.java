package com.wxt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private static void print(int index, Object obj) {
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }

    private static void mainx(String[] args)
    {
        Jedis jedis = new Jedis();
        jedis.flushAll();

        // get,set
        jedis.set("hello","w");
        print(1,jedis.get("hello"));
        jedis.rename("hello","h");
        print(1,jedis.get("h"));
        jedis.setex("h2",5,"w");

        // 数值操作
        jedis.set("pv","100");
        jedis.incr("pv");
        jedis.incrBy("pv",5);
        print(2,jedis.get("pv"));
        print(3,jedis.keys("*"));

        // 列表操作, 最近来访, 粉丝列表，消息队列
        String listName = "list";
        jedis.del(listName);
        for (int i=0;i<10;i++)
        {
            jedis.lpush(listName,"a" + String.valueOf(i));
        }
        print(4,jedis.lrange(listName,0,12));  // 最近来访10个id
        print(5,jedis.llen(listName));
        print(6,jedis.lpop(listName));
        print(7,jedis.llen(listName));
        print(8,jedis.lrange(listName,2,6));
        print(9,jedis.lindex(listName,3));
        print(10,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xx1"));
        print(10,jedis.linsert(listName,BinaryClient.LIST_POSITION.BEFORE,"a4","bb"));
        print(11,jedis.lrange(listName,0,12));

        // hash, 可变字段
        String userKey ="userxx";
        jedis.hset(userKey,"name","wxt");
        jedis.hset(userKey,"age","2");
        jedis.hset(userKey,"phone","apple");
        print(12,jedis.hget(userKey,"name"));
        print(13,jedis.hgetAll(userKey));
        jedis.hdel(userKey,"name");
        print(14,jedis.hgetAll(userKey));
        print(15,jedis.hexists(userKey,"name"));
        print(16,jedis.hexists(userKey,"age"));
        print(17,jedis.hkeys(userKey));
        print(18,jedis.hvals(userKey));
        jedis.hsetnx(userKey,"age","33");
        jedis.hsetnx(userKey,"sch","zju");
        print(19,jedis.hgetAll(userKey));

        // 集合，点赞用户群, 共同好友
        String like1 = "like1";
        String like2 = "like2";
        for (int i=0;i<10;i++)
        {
            jedis.sadd(like1,String.valueOf(i));
            jedis.sadd(like2,String.valueOf(i*2));
        }
        print(20,jedis.smembers(like1));
        print(21,jedis.smembers(like2));
        print(22,jedis.sunion(like1,like2));
        print(23,jedis.sdiff(like1,like2));
        print(24,jedis.sinter(like1,like2));
        print(25,jedis.sismember(like1,"4"));
        print(26,jedis.sismember(like2,"5"));
        jedis.srem(like1,"2");
        print(27,jedis.smembers(like1));
        // 从2移动到1
        jedis.smove(like2,like1,"14");
        print(28,jedis.smembers(like1));
        print(29,jedis.smembers(like2));

        // 排序集合，有限队列，排行榜
        String rankKey = "rankKey";
        jedis.zadd(rankKey,1,"x");
        jedis.zadd(rankKey,10,"y");
        jedis.zadd(rankKey,15,"z");
        print(30,jedis.zcard(rankKey));
        print(31,jedis.zcount(rankKey,0,10));
        print(32,jedis.zscore(rankKey,"x"));
        jedis.zincrby(rankKey,2,"x");
        print(33,jedis.zscore(rankKey,"x"));
        print(34,jedis.zcount(rankKey,0,5));

        print(35,jedis.zrange(rankKey,0,2));
        print(36,jedis.zrevrange(rankKey,0,2));

        for (Tuple tuple:jedis.zrangeByScoreWithScores(rankKey,0,10))
        {
            print(38,tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

        JedisPool pool = new JedisPool();
        for(int i=0;i<20;i++)
        {
            Jedis j = pool.getResource();
            print(i,j.get("h"));
            j.close();
        }

    }

    private JedisPool jedisPool = null;


    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool();

    }

    public String get(String Key)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return  jedis.get(Key);
        }catch (Exception e)
        {
            logger.error("发生异常" + e.getMessage());
            return null;
        }finally {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public void set(String Key,String value)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(Key,value);
        }catch (Exception e)
        {
            logger.error("发生异常" + e.getMessage());
        }finally {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public long sadd(String Key,String value)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return  jedis.sadd(Key,value);
        }catch (Exception e)
        {
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public long srem(String Key,String value)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return  jedis.srem(Key,value);
        }catch (Exception e)
        {
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public boolean sismember(String Key,String value)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return  jedis.sismember(Key,value);
        }catch (Exception e)
        {
            logger.error("发生异常" + e.getMessage());
            return false;
        }finally {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public long scard(String Key)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return  jedis.scard(Key);
        }catch (Exception e)
        {
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public long lpush(String Key,String value)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return  jedis.lpush(Key,value);
        }catch (Exception e)
        {
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.brpop(timeout,key);
        }catch (Exception e)
        {
            logger.error("发生异常" + e.getMessage());
            return null;
        }finally {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
    }


}
