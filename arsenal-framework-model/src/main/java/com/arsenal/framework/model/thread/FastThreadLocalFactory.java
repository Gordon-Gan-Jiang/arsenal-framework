// Copyright 2022 Kingsoft Office Software Inc. All rights reserved.

package com.arsenal.framework.model.thread;

/**
 * @author Gordon.Gan
 */
@FunctionalInterface
public interface FastThreadLocalFactory<T> {
    T getObject() ;
}
