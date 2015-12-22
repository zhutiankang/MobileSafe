package com.nl.mobilesafe.test;

import java.util.List;
import java.util.Random;

import com.nl.mobilesafe.db.BlackNumberDBOpenHelper;
import com.nl.mobilesafe.db.dao.BlackNumberDAO;
import com.nl.mobilesafe.domain.BlackNumberInfo;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {
	
	
	public void testCreateDB() throws Exception{
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		helper.getWritableDatabase();
	}
	public void testAdd() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		long baseNumber = 1350000000;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			dao.add(String.valueOf(baseNumber+i), String.valueOf(random.nextInt(3)+1));
		}
	}
	public void testUpdate() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		dao.update("110", "2");
	}
	public void testDelete() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		dao.delete("110");
	}
	public void testFind() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		boolean result = dao.find("110");
		//╤оят
		assertEquals(true, result);
		
	}
	public void testFindAll() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		List<BlackNumberInfo> infos = dao.findAll();
		for (BlackNumberInfo info : infos) {
			System.out.println(info.toString());
		}
		
	}
}
