package com.urzaizcoding.subscriber.persistence.datasource;

import com.urzaizcoding.subscriber.utils.file.SubscriberFileProvider;

import org.junit.Before;

public class SubscriberFileProviderTest {
    private SubscriberFileProvider underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new SubscriberFileProvider();
    }
}