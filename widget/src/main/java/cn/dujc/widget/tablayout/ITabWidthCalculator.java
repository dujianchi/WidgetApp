package cn.dujc.widget.tablayout;

/**
 * @author du
 * date: 2019/2/17 5:39 PM
 */
public interface ITabWidthCalculator {

    public int calc(int parentOriginalWidth, int parentShouldBeWidth, int childOriginalWidth, int childCount);

    /**
     * 每个都固定一样都大小
     */
    public static class FixedImpl implements ITabWidthCalculator {

        @Override
        public int calc(int parentOriginalWidth, int parentShouldBeWidth, int childOriginalWidth, int childCount) {
            return parentShouldBeWidth / childCount;
        }
    }

    /**
     * 等比例展开
     */
    public static class ExpandImpl implements ITabWidthCalculator {

        @Override
        public int calc(int parentOriginalWidth, int parentShouldBeWidth, int childOriginalWidth, int childCount) {
            return (int) (parentShouldBeWidth * 1.0 / parentOriginalWidth * childOriginalWidth + 0.5);
        }
    }
}
