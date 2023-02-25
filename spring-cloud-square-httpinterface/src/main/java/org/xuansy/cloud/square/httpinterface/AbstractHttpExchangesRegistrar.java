package org.xuansy.cloud.square.httpinterface;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.service.annotation.HttpExchange;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author : xuansy
 * @version : 1.0
 * @email : 1292798418@qq.com
 * @date : 2023/2/25 9:53
 * @project_name : spring-cloud-square
 * @package_name : org.xuansy.cloud.square.httpinterface
 * @name : AbstractRetrofitClientsRegistrar
 * @description :
 */
public abstract class AbstractHttpExchangesRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware,
	BeanClassLoaderAware, EnvironmentAware {

	private Map<String, Object> attributes;

	private ResourceLoader resourceLoader;

	private ClassLoader classLoader;

	private Environment environment;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	protected abstract Class<?> getAnnotationClass();

	protected abstract Class<?> getFactoryBeanClass();

	protected abstract void buildDefinition(BeanDefinitionBuilder definition);

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		this.attributes = metadata.getAnnotationAttributes(getAnnotationClass().getName());
		registerHttpExchanges(metadata, registry);
	}

	protected ClassPathScanningCandidateComponentProvider getScanner() {
		return new ClassPathScanningCandidateComponentProvider(false) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				boolean isCandidate = false;
				if (beanDefinition.getMetadata().isIndependent()) {
					if (!beanDefinition.getMetadata().isAnnotation()) {
						isCandidate = true;
					}
				}
				return isCandidate;
			}
		};
	}

	public void registerHttpExchanges(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		ClassPathScanningCandidateComponentProvider scanner = getScanner();
		scanner.setResourceLoader(this.resourceLoader);

		AnnotationTypeFilter typeFilter = new AnnotationTypeFilter(HttpExchange.class);
		scanner.addIncludeFilter(typeFilter);
		final Class<?>[] httpExchanges = attributes != null ? (Class<?>[]) attributes.get("httpExchanges") : null;
		if (httpExchanges != null && httpExchanges.length > 0) {
			final Set<String> classes = Arrays.stream(httpExchanges)
				.map(ClassUtils::getPackageName)
				.collect(Collectors.toSet());
			AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
				@Override
				protected boolean match(ClassMetadata metadata) {
					String cleaned = metadata.getClassName().replaceAll("\\$", ".");
					return classes.contains(cleaned);
				}
			};
			scanner.addIncludeFilter(filter);
		}

		Set<String> basePackages = getBasePackages(metadata);
		for (String basePackage : basePackages) {
			Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
			for (BeanDefinition candidateComponent : candidateComponents) {
				if (candidateComponent instanceof AnnotatedBeanDefinition beanDefinition) {
					// verify annotated class is an interface
					AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
					Assert.isTrue(annotationMetadata.isInterface(), "@HttpExchange can only be specified on an interface");
					Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(HttpExchange.class.getCanonicalName());
					registerHttpExchange(registry, annotationMetadata, attributes);
				}
			}
		}
	}

	private void registerHttpExchange(BeanDefinitionRegistry registry, AnnotationMetadata metadata,
									  Map<String, Object> attributes) {
		String className = metadata.getClassName();
		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(getFactoryBeanClass());
		definition.addPropertyValue("type", className);
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

		buildDefinition(definition);

		AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
		beanDefinition.setPrimary(false);

		BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className);
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
	}

	/**
	 * find base packages
	 */
	protected Set<String> getBasePackages(AnnotationMetadata metadata) {
		Set<String> basePackages = new HashSet<>();
		if (null != attributes) {
			dealBasePackages(attributes, "value", basePackages::add);
			dealBasePackages(attributes, "basePackages", basePackages::add);
			for (Class<?> clazz : (Class<?>[]) attributes.get("basePackageClasses")) {
				basePackages.add(ClassUtils.getPackageName(clazz));
			}
		}
		if (basePackages.isEmpty()) basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
		return basePackages;
	}

	protected void dealBasePackages(Map<String, Object> attributes, String key, Consumer<String> consumer) {
		for (String pkg : (String[]) attributes.get(key)) {
			if (StringUtils.hasText(pkg)) {
				consumer.accept(pkg);
			}
		}
	}

	private String getClientName(Map<String, Object> client) {
		if (client == null) return null;
		String value = (String) client.get("value");
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("name");
		}
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("serviceId");
		}
		if (StringUtils.hasText(value)) {
			return value;
		}
		throw new IllegalStateException("Either 'name' or 'value' must be provided in @" + HttpExchange.class.getSimpleName());
	}

	private static class HttpExchangeTypeFilter implements TypeFilter {
		private final List<TypeFilter> delegates;

		HttpExchangeTypeFilter(List<TypeFilter> filters) {
			this.delegates = filters;
		}

		@Override
		public boolean match(MetadataReader reader, MetadataReaderFactory factory)
			throws IOException {
			for (TypeFilter filter : this.delegates) {
				if (!filter.match(reader, factory)) {
					return false;
				}
			}
			return true;
		}

	}

}
