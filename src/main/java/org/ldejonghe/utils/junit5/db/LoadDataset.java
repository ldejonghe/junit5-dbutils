package org.ldejonghe.utils.junit5.db;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoadDataset {
    String value();
}
