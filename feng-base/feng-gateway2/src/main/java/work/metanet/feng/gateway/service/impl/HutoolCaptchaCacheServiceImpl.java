package work.metanet.feng.gateway.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import cn.hutool.captcha.generator.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.gateway.config.property.HutoolCaptchaProperties;
import work.metanet.feng.gateway.service.HutoolCaptchaCacheService;

import java.awt.Font;
import java.util.concurrent.TimeUnit;

/**
 * @author edison
 * @date 2020/8/27
 * <p>
 * 验证码 缓存提供支持集群,需要通过SPI (使用hutool验证码)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HutoolCaptchaCacheServiceImpl implements HutoolCaptchaCacheService {

	private static final String REDIS = "redis";

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public void set(String key, String value, long expiresInSeconds) {
		stringRedisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
	}

	@Override
	public boolean exists(String key) {
		return stringRedisTemplate.hasKey(key);
	}

	@Override
	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}

	@Override
	public String get(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	@Override
	public String type() {
		return REDIS;
	}

}

