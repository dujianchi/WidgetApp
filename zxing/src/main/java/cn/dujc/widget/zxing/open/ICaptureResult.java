package cn.dujc.widget.zxing.open;

public interface ICaptureResult {
    /**
     * 处理扫码结果，返回true则需要关闭扫码页
     */
    boolean handleDecode(String result);
}
