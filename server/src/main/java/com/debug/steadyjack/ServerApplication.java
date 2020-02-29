package com.debug.steadyjack;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ImportResource(locations = "classpath:spring/spring-jdbc.xml")
@MapperScan(basePackages = "com.debug.steadyjack.mapper")
public class ServerApplication {

	@Autowired
	private Environment env;

	@Bean
	public CuratorFramework curatorFramework(){
		CuratorFramework curatorFramework=CuratorFrameworkFactory.builder().connectString(env.getProperty("zk.host")).namespace(env.getProperty("zk.namespace"))
				.retryPolicy(new RetryNTimes(5,1000)).build();
		curatorFramework.start();

		return curatorFramework;
	}

	@Bean
	public RedissonClient redissonClient(){
		Config config=new Config();
		config.useSingleServer().setAddress(env.getProperty("redisson.address"));
		RedissonClient client = Redisson.create(config);
		return client;
	}


	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}
}

























