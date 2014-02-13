package com.ailk.check.safeguard.safe.random.shuffle;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-22
 * Time: 下午2:23
 */
public class ShuffleListTest {

    @Test
    public void shuffleTest() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(i));
        }

        ShuffleList.reduce(list, ShuffleList.Per.TWO, ShuffleList.Base.TEN);
        ShuffleList.shuffle(list);
        Assert.assertTrue(list.size() == 9 || list.size() == 8);

        for (String s : list) {
            System.out.println(s);
        }
    }
}
