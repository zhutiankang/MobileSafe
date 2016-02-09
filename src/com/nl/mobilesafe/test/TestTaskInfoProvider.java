package com.nl.mobilesafe.test;

import java.util.List;

import com.nl.mobilesafe.domain.TaskInfo;
import com.nl.mobilesafe.engine.TaskInfoProvider;

import android.test.AndroidTestCase;

public class TestTaskInfoProvider extends AndroidTestCase {
	//���еĲ����߼���Ҫ��������쳣
	public void testGetTaskInfos() throws Exception{
		
		List<TaskInfo> taskInfos = TaskInfoProvider.getTaskInfos(getContext());
		for (TaskInfo taskInfo : taskInfos) {
			System.out.println(taskInfo.toString());
		}
		
	}
}
