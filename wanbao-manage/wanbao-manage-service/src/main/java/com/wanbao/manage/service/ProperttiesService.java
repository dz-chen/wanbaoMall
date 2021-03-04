package com.wanbao.manage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProperttiesService {
	//public 修饰,方便子容器直接获取
	
	@Value(value="${REPOSITORY_PATH}")
	public String REPOSITORY_PATH;
	
	@Value(value="${IMAGE_URL}")
	public String IMAGE_URL;
}
