import android.widget.TextView;
import android.app.AlertDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.app.Activity;
import java.util.Date;

public void onMsg(Object msg)
{
    String text = msg.MessageContent;
    String qq = msg.UserUin;
    String qun = msg.GroupUin;
    if (text.equals("你也好")) {
        sendMsg(qun, "", "你也好啊");
    }
    if (text.equals("艾特我") && msg.IsGroup && qq.equals(MyUin)) {
        sendMsg(qun, "", "[AtQQ=" + qq + "] 嗯呐");
    }
    if (text.equals("回复我")) {
        sendReply(qun, msg, "回复了");
    }
    if (text.equals("测试") && msg.UserUin.equals(MyUin) && msg.IsGroup) {
        ArrayList l = getGroupMemberList(qun);
        Toast("" + l.size());
        TextView textView = new TextView(context);
    }
    if (text.equals("现在时间")) {
        sendReply(qun, msg, getCustomFormat("yyyy-MM-dd HH:mm:ss"));
    }
}

public void dialogTest(String groupUin, String uin, int chatType)
{
    Activity activity = GetActivity();
    // 防止 activity 为 null
    if (activity == null)
        return;
    TextView textView = new TextView(activity);
    textView.setText("你好");
    // 使用 Material Design 主题
    AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
    AlertDialog alertDialog = builder.create();
    alertDialog.setView(textView);
    alertDialog.setCancelable(true);
    alertDialog.show();
}

public void 加载提示(String groupUin, String uin, int chatType)
{
    if (getString("加载提示", "开关") == null) {
        putString("加载提示", "开关", "关");
        Toast("已关闭加载提示");
    } else {
        putString("加载提示", "开关", null);
        Toast("已开启加载提示");
    }
}

addItem("开关加载提示", "加载提示");
addItem("弹窗测试", "dialogTest");

public void onTroopEvent(String groupUin, String userUin, String opUin, long time)
{
}

if (getString("加载提示", "开关") == null) {
Toast("发送 \"菜单\" 查看使用说明");
}

public String getCurrentDate()
{
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return dateFormat.format(date);
}

public String getCustomFormat(String pattern)
{
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    return dateFormat.format(date);
}

