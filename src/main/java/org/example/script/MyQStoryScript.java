package org.example.script;

import android.app.Activity;
import android.app.AlertDialog;
import org.example.sdk.AbstractQStoryScript;
import org.example.sdk.GlobalInit;
import android.widget.TextView;
import org.example.sdk.GroupMemberInfo;
import org.example.sdk.Msg;

import java.util.ArrayList;
public class MyQStoryScript extends AbstractQStoryScript {
    @Override
    public void onMsg(Msg msg) {
        String text = msg.MessageContent;
        String qq = msg.UserUin;
        String qun = msg.GroupUin;


        if (text.equals("你也好")) {
            sendMsg(qun, "", "你也好啊");
        }
        if (text.equals("艾特我") && msg.IsGroup && qq.equals(getMyUin())) {
            sendMsg(qun, "", "[AtQQ=" + qq + "] 嗯呐");
        }
        if (text.equals("回复我")) {
            sendReply(qun, msg, "回复了");
        }
        if (text.equals("测试")&&msg.UserUin.equals(getMyUin())&&msg.IsGroup) {
            ArrayList<GroupMemberInfo> l=getGroupMemberList(qun);
            toast(""+l.size());
            TextView textView = new TextView(getContext() );
        }
        if (text.equals("现在时间")) {
            sendReply(qun, msg, TimeUtil.getCustomFormat("yyyy-MM-dd HH:mm:ss"));
        }
    }
    public void dialogTest(String groupUin, String uin, int chatType) {
        Activity activity = GetActivity();
        if (activity == null) return; // 防止 activity 为 null

        TextView textView = new TextView(activity);
        textView.setText("你好");

        // 使用 Material Design 主题
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(textView);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public void 加载提示(String groupUin, String uin, int chatType) {
        if (getString("加载提示", "开关") == null) {
            putString("加载提示", "开关", "关");
            toast("已关闭加载提示");
        } else {
            putString("加载提示", "开关", null);
            toast("已开启加载提示");
        }
    }
    @GlobalInit
    public  void addItem() {
        addItem("开关加载提示", "加载提示");
        addItem("弹窗测试", "dialogTest");
    }
    public void onTroopEvent(String groupUin, String userUin, String opUin, long time){

    }
    //直接调用的代码用GlobalInit修饰,方法名和大括号将被去除

    @GlobalInit
    public void 加载(){
        if (getString("加载提示", "开关") == null) {
            toast("发送 \"菜单\" 查看使用说明");
        }
    }

}