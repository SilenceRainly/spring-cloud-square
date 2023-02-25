package org.xuansy.cloud.square.httpinterface.webclient;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.xuansy.cloud.square.httpinterface.AbstractHttpExchangesRegistrar;

/**
 * @author : xuansy
 * @version : 1.0
 * @email : 1292798418@qq.com
 * @date : 2023/2/25 9:51
 * @project_name : spring-cloud-square
 * @package_name : org.xuansy.cloud.square.webclient
 * @name : WebClientHttpExchangesRegistrar
 * @description :
 */
public class WebClientHttpExchangesRegistrar extends AbstractHttpExchangesRegistrar {

	@Override
	protected Class<?> getAnnotationClass() {
		return EnableWebclientHttpExchanges.class;
	}

	@Override
	protected Class<?> getFactoryBeanClass() {
		return WebClientHttpExchangeFactoryBean.class;
	}

	@Override
	protected void buildDefinition(BeanDefinitionBuilder definition) {

	}

}
