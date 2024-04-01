package com.arsenal.framework.config.spring;

import com.arsenal.framework.config.CmdLineConfigImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Gordon.Gan
 */
public class ArsenalConfigBeanFactory<T extends Object> implements FactoryBean<T>, ApplicationContextAware {
    private Class<T> clazz;

    public ArsenalConfigBeanFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    private ApplicationContext context;

    @Override
    public T getObject() throws Exception {
        final CmdLineConfigImpl config = context.getBean(CmdLineConfigImpl.class);
       return (T) new ArsenalConfigurationProvider(config.getProfile(), config.getRegion(), clazz.newInstance(), clazz,
                context.getEnvironment()).get();
    }

    @Override
    public Class<?> getObjectType() {
        return clazz;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = context;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
