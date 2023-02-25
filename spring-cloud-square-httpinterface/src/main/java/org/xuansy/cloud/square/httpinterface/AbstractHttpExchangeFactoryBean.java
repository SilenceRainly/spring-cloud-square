package org.xuansy.cloud.square.httpinterface;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author : xuansy
 * @version : 1.0
 * @email : 1292798418@qq.com
 * @date : 2023/2/25 11:03
 * @project_name : spring-cloud-square
 * @package_name : org.xuansy.cloud.square.httpinterface
 * @name : WebClientHttpExchangeFactoryBean
 * @description :
 */
public abstract class AbstractHttpExchangeFactoryBean
	implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

	protected Class<?> type;

	protected ApplicationContext applicationContext;

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	@Override
	public Class<?> getObjectType() {
		return this.type;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.applicationContext = context;
	}

}
