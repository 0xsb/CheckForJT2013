package com.ailk.check.safeguard.safe.random.shuffle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: wangpu
 * Date: 13-5-22
 * Time: 上午11:26
 * <p/>
 * 洗牌
 * 对List集合中的元素进行随机重新排列
 * 并可以减少元素个数（根据比率参数随机减少）
 */
public class ShuffleList {
    private static Logger logger = LoggerFactory.getLogger(ShuffleList.class);

    /**
     * 定义减少率常量
     * 取值范围（1-5）
     */
    public enum Per {
        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

        private int value;

        Per(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    /**
     * 定义减少率基数
     * 取值（10, 100, 1000）
     */
    public enum Base {
        TEN(10), HUNDRED(100), THOUSAND(1000);

        private int value;

        Base(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    /**
     * 随机按元素总数和比率参数减少
     *
     * @param list   集合
     * @param maxPer 最大减少比率
     * @param base   比率基数
     * @param <T>    类型
     */
    public static <T> void reduce(List<T> list, Per maxPer, Base base) {
        int per = (int) (Math.random() * maxPer.getValue()) + 1;
        logger.debug("random reduce is : " + per);
        int removeNum = list.size() * per / base.getValue();
        logger.debug("random remove num is : " + removeNum);
        for (int i = 0; i < removeNum; i++) {
            list.remove((int) (Math.random() * list.size()));
        }
    }

    /**
     * 随机重新排序
     *
     * @param list 集合
     * @param <T>  类型
     */
    public static <T> void shuffle(List<T> list) {
        int N = list.size();
        for (int i = 0; i < N; i++) {
            int r = i + (int) (Math.random() * (N - i));
            exchange(list, i, r);
        }
    }

    /**
     * 位置交换
     *
     * @param list 集合
     * @param i    位置索引
     * @param j    位置索引
     * @param <T>  类型
     */
    private static <T> void exchange(List<T> list, int i, int j) {
        T swap = list.get(i);
        list.set(i, list.get(j));
        list.set(j, swap);
    }
}
