package com.ailk.suits;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.ailk.uap.makefile4new.A4ResourceCoverFullFileTest;
import com.ailk.uap.makefile4new.A4SuperAuthorityFullFileTest;
import com.ailk.uap.makefile4new.AppAcctFullFileTest;
import com.ailk.uap.makefile4new.AuthorFullFileTest;
import com.ailk.uap.makefile4new.MainAcctFullFileTest;
import com.ailk.uap.makefile4new.SysResourceAcctFullFileTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	A4SuperAuthorityFullFileTest.class,
	AuthorFullFileTest.class,
	AppAcctFullFileTest.class,
	MainAcctFullFileTest.class,
	SysResourceAcctFullFileTest.class,
	A4ResourceCoverFullFileTest.class
	
})
public class AbstractMakeMonthFileSuitTest {

}
