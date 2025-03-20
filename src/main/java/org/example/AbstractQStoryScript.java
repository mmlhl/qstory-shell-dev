package org.example;

public abstract class AbstractQStoryScript implements QStorySDK {
    @Override public String getMyUin() { return "MyUin"; }
    @Override public String getContext() { return "context"; }
    @Override public String getAppPath() { return "AppPath"; }
    @Override public ClassLoader getLoader() { return null; }
    @Override public String getPluginID() { return "PluginID"; }

    @Override public void onMsg(Msg msg) {}
    @Override public void onTroopEvent(String groupUin, String userUin, String opUin, long time) {}
    @Override public void onTroopEvent(String groupUin, String userUin, int type) {}
    @Override public void onClickFloatingWindow(int type, String uin) {}
    @Override public String getMsg(String msg, String uin, int chatType) { return msg; }

    @Override public native void sendMsg(String groupUin, String userUin, String msg);
    @Override public native void sendPic(String groupUin, String userUin, String path);
    @Override public native void sendCard(String groupUin, String userUin, String card);
    @Override public native void sendReply(String groupUin, Msg msg, String msgText);
    @Override public native void sendFile(String groupUin, String userUin, String path);
    @Override public native void sendVoice(String groupUin, String userUin, String path);
    @Override public native void sendVideo(String groupUin, String userUin, String path);
    @Override public native void sendLike(String userUin, int count);
    @Override public native void sendPai(String groupUin, String userUin);

    @Override public native void setCard(String groupUin, String userUin, String name);
    @Override public native void setTitle(String groupUin, String userUin, String title);
    @Override public native void revokeMsg(Msg msg);
    @Override public native void deleteMsg(Msg msg);
    @Override public native void forbidden(String groupUin, String userUin, int time);
    @Override public native void kick(String groupUin, String userUin, boolean isBlack);

    @Override public native void putString(String configName, String key, String value);
    @Override public native String getString(String configName, String key);
    @Override public native void putInt(String configName, String key, int value);
    @Override public native int getInt(String configName, String key, int def);
    @Override public native void putLong(String configName, String key, long value);
    @Override public native long getLong(String configName, String key, long def);
    @Override public native void putBoolean(String configName, String key, boolean value);
    @Override public native boolean getBoolean(String configName, String key, boolean def);

    @Override public native void toast(Object message);
    @Override public native String addItem(String name, String callbackName);
    @Override public native void addTemporaryItem(String name, String callbackName);
}