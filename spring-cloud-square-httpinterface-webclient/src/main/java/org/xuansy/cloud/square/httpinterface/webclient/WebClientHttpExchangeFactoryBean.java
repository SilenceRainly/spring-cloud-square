package org.xuansy.cloud.square.httpinterface.webclient;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.xuansy.cloud.square.httpinterface.AbstractHttpExchangeFactoryBean;

/**
 * @author : xuansy
 * @version : 1.0
 * @email : 1292798418@qq.com
 * @date : 2023/2/25 10:09
 * @project_name : spring-cloud-square
 * @package_name : org.xuansy.cloud.square.httpinterface.webclient
 * @name : WebClientHttpExchangeFactoryBean
 * @description :
 */
public class WebClientHttpExchangeFactoryBean extends AbstractHttpExchangeFactoryBean {

	@Override
	public Object getObject() throws Exception {
		WebClient.Builder builder = applicationContext.getBean(WebClient.Builder.class);
		return HttpServiceProxyFactory.builder(WebClientAdapter.forClient(builder.build()))
			.build()
			.createClient(type);
	}

}
