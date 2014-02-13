package com.ailk.suits;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.ailk.uap.makefile4new.A4AppAcctDayAddFileTest;
import com.ailk.uap.makefile4new.DbOperateLogDayFileTest;
import com.ailk.uap.makefile4new.MainAcctDayAddFileTest;



@RunWith(Suite.class)
@Suite.SuiteClasses({
	A4AppAcctDayAddFileTest.class,
	DbOperateLogDayFileTest.class,
	MainAcctDayAddFileTest.class
})
public class AbstractMakeDayFileSuitTest {

}
