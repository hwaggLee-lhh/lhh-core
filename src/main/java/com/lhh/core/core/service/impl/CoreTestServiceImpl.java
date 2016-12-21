package com.lhh.core.core.service.impl;

import com.lhh.core.base.BaseManager;
import com.lhh.core.base.LhhCoreBaseServiceImpl;
import com.lhh.core.core.model.CoreTestModel;
import com.lhh.core.core.service.CoreTestService;

public class CoreTestServiceImpl extends LhhCoreBaseServiceImpl<CoreTestModel> implements CoreTestService {

	@Override
	protected BaseManager<CoreTestModel> getBaseManager() {
		return null;
	}

}
