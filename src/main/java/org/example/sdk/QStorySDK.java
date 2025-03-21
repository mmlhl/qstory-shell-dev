package org.example.sdk;

import android.app.Activity;
import android.content.Context;
import java.util.ArrayList;

public interface QStorySDK {
    String getMyUin();
    Context getContext();
    String getAppPath();
    ClassLoader getLoader();
    String getPluginID();

    void onMsg(Msg msg);
    void onTroopEvent(String groupUin, String userUin, String opUin, long time);
    void onTroopEvent(String groupUin, String userUin, int type);
    void onClickFloatingWindow(int type, String uin);
    String getMsg(String msg, String uin, int chatType);

    void sendMsg(String groupUin, String userUin, String msg);
    void sendPic(String groupUin, String userUin, String path);
    void sendCard(String groupUin, String userUin, String card);
    void sendReply(String groupUin, Msg msg, String msgText);
    void sendFile(String groupUin, String userUin, String path);
    void sendVoice(String groupUin, String userUin, String path);
    void sendVideo(String groupUin, String userUin, String path);
    void sendLike(String userUin, int count);
    void sendPai(String groupUin, String userUin);

    void setCard(String groupUin, String userUin, String name);
    void setTitle(String groupUin, String userUin, String title);
    void revokeMsg(Msg msg);
    void deleteMsg(Msg msg);
    void forbidden(String groupUin, String userUin, int time);
    void kick(String groupUin, String userUin, boolean isBlack);

    void putString(String configName, String key, String value);
    String getString(String configName, String key);
    void putInt(String configName, String key, int value);
    int getInt(String configName, String key, int def);
    void putLong(String configName, String key, long value);
    long getLong(String configName, String key, long def);
    void putBoolean(String configName, String key, boolean value);
    boolean getBoolean(String configName, String key, boolean def);

    void toast(Object message);
    String addItem(String name, String callbackName);
    void addTemporaryItem(String name, String callbackName);

    // 已添加的群组方法
    ArrayList<GroupInfo> getGroupList();
    ArrayList<GroupMemberInfo> getGroupMemberList(String groupUin);

    // 新增方法
    Activity GetActivity(); // 注意文档中大写 G，可能需与实现一致
    void load(String Path);
}