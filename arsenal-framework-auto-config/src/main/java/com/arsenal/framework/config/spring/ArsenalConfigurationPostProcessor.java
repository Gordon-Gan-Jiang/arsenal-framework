package com.arsenal.framework.config.spring;

import com.arsenal.framework.config.annotations.ArsenalConfiguration;
import com.arsenal.framework.config.annotations.ArsenalConfigurationDelegate;
import com.arsenal.framework.model.utility.ObjectHelper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.MethodMetadata;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * [BeanDefinitionRegistryPostProcessor] to create [ArsenalConfiguration] and
 * [ArsenalConfigurationDelegate] objects.
 * @author Gordon.Gan
 */
@Slf4j
@Component
public class ArsenalConfigurationPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        final List<String> configurationBeans = new ArrayList<>();
        final Map<String, String> configurationPrefixMap = new HashMap();
        final Map<Class<?>, String> originalConfigurations = new HashMap();
        final Map<Class<?>, Map<String, ArsenalConfigurationDelegate>> delegateConfigurations = new HashMap();

        // Process original configurations.
        for (String beanName : registry.getBeanDefinitionNames()) {
            final BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
                final String className = findClassName(annotatedBeanDefinition);
                final Class<?> clazz = ObjectHelper.classNameToClass(className);
                if (clazz == null) {
                    continue;
                }
                final ArsenalConfiguration annotation = clazz.getAnnotation(ArsenalConfiguration.class);
                if (annotation != null) {
                    // Check configuration prefix: not use the same prefix.
                    final String prefix = annotation.prefix().toLowerCase();
                    if (configurationPrefixMap.containsKey(prefix)) {
                        throw new RuntimeException("Two ArsenalConfiguration beans use the same prefix" + prefix + ": "
                                + configurationPrefixMap.get(prefix) + ", " + className);
                    } else {
                        configurationPrefixMap.put(prefix, className);
                    }

                    // Replace bean definition.
                    GenericBeanDefinition newDefinition = new GenericBeanDefinition();
                    newDefinition.setBeanClassName(ArsenalConfigBeanFactory.class.getName());
                    ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                    // Add argument, to invoke the right constructor.
                    constructorArgumentValues.addGenericArgumentValue(className);
                    newDefinition.setConstructorArgumentValues(constructorArgumentValues);
                    newDefinition.setPrimary(true);
                    registry.removeBeanDefinition(beanName);
                    registry.registerBeanDefinition(beanName, newDefinition);
                    configurationBeans.add(className);
                    originalConfigurations.put(clazz, beanName);
                }
            }
        }

        // Process configuration delegates.
        for (String beanName : registry.getBeanDefinitionNames()) {
            final BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
                final String className = findClassName(annotatedBeanDefinition);
                final Class<?> clazz = ObjectHelper.classNameToClass(className);
                if (clazz == null) {
                    continue;
                }
                final ArsenalConfigurationDelegate annotation = clazz.getAnnotation(ArsenalConfigurationDelegate.class);
                if (annotation == null) {
                    continue;
                }
                // Replace bean definition.
                GenericBeanDefinition newDefinition = new GenericBeanDefinition();
                newDefinition.setBeanClassName(ArsenalConfigBeanFactory.class.getName());
                ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                // Add argument, to invoke the right constructor.
                constructorArgumentValues.addGenericArgumentValue(className);
                newDefinition.setConstructorArgumentValues(constructorArgumentValues);
                newDefinition.setPrimary(true);
                // Remove configuration delegate bean.
                registry.removeBeanDefinition(beanName);
                final String originalConfigurationName = originalConfigurations.get(annotation.value());
                if (originalConfigurationName != null) {
                    // Remove original configuration.
                    registry.removeBeanDefinition(originalConfigurationName);
                }
                // Check multiple delegate orders.
                final Map<String, ArsenalConfigurationDelegate> oldDelegate = delegateConfigurations.get(
                        annotation.value());
                // todo use Pair instead o Map
                if (MapUtils.isNotEmpty(oldDelegate)) {
                    final Entry<String, ArsenalConfigurationDelegate> entry =
                            oldDelegate.entrySet().stream().findFirst().get();
                    if (entry.getValue().order() > annotation.order()) {
                        continue;
                    } else if (entry.getValue().order() == annotation.order()) {
                        throw new RuntimeException(
                                "Two delegates for +" + annotation.value() + " have the same order.");
                    }
                    // remove old delegate.
                    registry.removeBeanDefinition(entry.getKey());
                }
                // register new bean definition.
                String newBeanName = StringUtils.isNotEmpty(originalConfigurationName) ? originalConfigurationName
                        : beanName;
                registry.registerBeanDefinition(newBeanName, newDefinition);
                delegateConfigurations.put(annotation.value(), ImmutableMap.of(newBeanName, annotation));
                configurationBeans.add(className);
            }
        }
        log.info("found @ArsenalConfiguration beans: {}", configurationBeans.stream().collect(Collectors.joining(", ")));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Do nothing.
    }

    private static String findClassName(AnnotatedBeanDefinition annotatedBeanDefinition) {
        final MethodMetadata factoryMethodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
        return factoryMethodMetadata != null ? factoryMethodMetadata.getReturnTypeName()
                : annotatedBeanDefinition.getMetadata().getClassName();
    }
}
