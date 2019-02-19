package com.example.parse.core.data;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;


/**
 * class for sharing task status it's not best decision, but simple and fast
 */
@Getter
public class TaskStatus {
    private final AtomicInteger allListCount = new AtomicInteger();
    private final AtomicInteger completeListCount = new AtomicInteger();
}
