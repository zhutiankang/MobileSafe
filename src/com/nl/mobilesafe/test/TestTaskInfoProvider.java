package com.nl.mobilesafe.test;

import java.util.List;

import com.nl.mobilesafe.domain.TaskInfo;
import com.nl.mobilesafe.engine.TaskInfoProvider;

import android.test.AndroidTestCase;

public class TestTaskInfoProvider extends AndroidTestCase {
	//所有的测试逻辑都要往框架抛异常
	public void testGetTaskInfos() throws Exception{
		
		List<TaskInfo> taskInfos = TaskInfoProvider.getTaskInfos(getContext());
		for (TaskInfo taskInfo : taskInfos) {
			System.out.println(taskInfo.toString());
		}
		
	}
}
