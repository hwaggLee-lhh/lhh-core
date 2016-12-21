package com.lhh.core.dict;

import javax.annotation.Resource;

import org.junit.Test;

import com.lhh.core.BaseServiceTest;
import com.lhh.core.core.model.LhhCoreDictionary;
import com.lhh.core.core.service.impl.DictionaryFactoryDB;

public class TestDict extends BaseServiceTest {
	@Resource
	private DictionaryFactoryDB dictionaryFactoryDB;
	@Test
	public void test() {
		try {
			System.out.println("OK");
			LhhCoreDictionary dict = dictionaryFactoryDB.getDictionary("001");
			System.out.println(dict);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
