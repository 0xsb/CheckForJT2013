package com.ailk.check.safeguard.safe.random.shuffle;

import org.junit.Test;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-22
 * Time: 下午5:33
 */
public class ShuffleTimeTest {

    @Test
    public void shuffleTest() {
        for (int i = 0; i < 10; i++) {
            Calendar calendar = Calendar.getInstance();
            System.out.println(calendar.get(Calendar.HOUR_OF_DAY) +
                    ":" + calendar.get(Calendar.MINUTE) +
                    ":" + calendar.get(Calendar.SECOND));

            calendar = ShuffleTime.shuffle(calendar, ShuffleTime.getChangeValue());

            System.out.println(calendar.get(Calendar.HOUR_OF_DAY) +
                    ":" + calendar.get(Calendar.MINUTE) +
                    ":" + calendar.get(Calendar.SECOND));

            System.out.println("----------------------------------");
        }
    }
}
