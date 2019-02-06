package cn.dujc.widgetapp.address;

/**
 * @author du
 * date: 2019/2/6 3:10 PM
 */
public interface IAddress {

    public String getId();

    public String getName();

    public String getPid();

    public String getExtra();

    public void setId(String id);

    public void setName(String name);

    public void setPid(String pid);

    public void setExtra(String extra);

    public static class Impl implements IAddress {

        private String mId, mName, mPid;
        private String mExtra;

        public Impl() { }

        public Impl(String name) {
            mName = name;
        }

        public Impl(String id, String name, String pid) {
            mId = id;
            mName = name;
            mPid = pid;
        }

        @Override
        public String getId() {
            return mId;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public String getPid() {
            return mPid;
        }

        @Override
        public String getExtra() {
            return mExtra;
        }

        @Override
        public void setId(String id) {
            mId = id;
        }

        @Override
        public void setName(String name) {
            mName = name;
        }

        @Override
        public void setPid(String pid) {
            mPid = pid;
        }

        @Override
        public void setExtra(String extra) {
            mExtra = extra;
        }

        @Override
        public String toString() {
            return mName == null ? "" : mName;
        }
    }

}
