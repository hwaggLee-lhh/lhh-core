package com.lhh.core;


import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:xml/spring-context.xml"})
public abstract class BaseServiceTest extends AbstractJUnit4SpringContextTests {
    protected final Logger log = Logger.getLogger(getClass());
}
