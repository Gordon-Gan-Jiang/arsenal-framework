package com.arsenal.framework.model.clone;

/**
 * @author Gordon.Gan
 */
public class CloneSupport<T> implements java.lang.Cloneable {
    @Override
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
