import java.util.Date;

import org.example.sdk.Msg;

import java.text.SimpleDateFormat;

import org.example.sdk.AbstractQStoryScript;


void onMsg(Object msg)
{
    String text = msg.MessageContent;
    String qq = msg.UserUin;
    String qun = msg.GroupUin;
    if (text.equals("菜单") && qq.equals(MyUin)) {
        String reply = "TG频道：https://t.me/QStoryPlugin\n交流群:979938489\n---------\n这是菜单 你可以发送下面的指令来进行测试  \n艾特我\n回复我\n私聊我";
        if (msg.IsGroup) {
            sendMsg(qun, "", reply);
        } else {
            sendMsg("", qq, reply);
        }
    }
    if (text.equals("你也好")) {
        sendMsg(qun, "", "你也好啊");
    }
    if (text.equals("艾特我") && msg.IsGroup && qq.equals(MyUin)) {
        sendMsg(qun, "", "[AtQQ=" + qq + "] 嗯呐");
    }
    if (text.equals("回复我")) {
        sendReply(qun, msg, "回复了");
    }
    if (text.equals("现在时间")) {
        sendReply(qun, msg, TimeUtil2_getCurrentDate());
    }
}

void 加载提示(String groupUin, String uin, int chatType)
{
    if (getString("加载提示", "开关") == null) {
        putString("加载提示", "开关", "关");
        Toast("已关闭加载提示");
    } else {
        putString("加载提示", "开关", null);
        Toast("已开启加载提示");
    }
}

void init()
{
    addItem("开关加载提示", "加载提示");
    if (getString("加载提示", "开关") == null) {
        Toast("发送 \"菜单\" 查看使用说明");
    }
}

String getCurrentDate()
{
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return dateFormat.format(date);
}

String getCustomFormat(String pattern)
{
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    return dateFormat.format(date);
}

String TimeUtil2_getCurrentDate()
{
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return dateFormat.format(date);
}

String TimeUtil2_getCustomFormat(String pattern)
{
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    return dateFormat.format(date);
}

String TimeUtil3_getCurrentDate()
{
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return dateFormat.format(date);
}

String TimeUtil3_getCustomFormat(String pattern)
{
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    return dateFormat.format(date);
}

