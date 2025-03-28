import android.webkit.WebView;
import android.widget.Button;
import android.content.Context;
import android.view.ViewGroup;
import android.net.http.SslError;
import java.util.ArrayList;
import android.webkit.WebViewClient;
import java.util.Date;
import android.view.WindowManager;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.R;
import java.text.SimpleDateFormat;
import android.widget.LinearLayout;
import android.app.Activity;
import android.content.DialogInterface;

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
    boolean showDialog = true;
    // 防止 activity 为 null
    if (activity == null)
        return;
    WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
    LinearLayout layout = new LinearLayout(activity);
    layout.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 0, 0, WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE, -3));
    layout.setOrientation(LinearLayout.VERTICAL);
    WebView webView = new WebView(activity);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    Button closeButton = new Button(activity);
    closeButton.setText("close");
    closeButton.setOnClickListener(new View.OnClickListener() {

        public void onClick(View view) {
            wm.removeView(layout);
        }
    });
    webView.getSettings().setAllowFileAccess(true);
    layout.addView(closeButton);
    layout.addView(webView);
    wm.addView(layout, layout.getLayoutParams());
    String path = AppPath;
    webView.setWebViewClient(new WebViewClient() {

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    });
    webView.setOnKeyListener(new View.OnKeyListener() {

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                webView.goBack();
                return false;
            }
            return true;
        }
    });
    webView.loadUrl("file://" + path + "/dist/index.html");
    /*TextView textView = new TextView(activity);
        textView.setText("你好");

        // 使用 Material Design 主题
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Theme_Material_NoActionBar_Fullscreen);

        builder.setTitle("title");
        builder.setNegativeButton("哈哈", new android.content.DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {

            }

        });
        AlertDialog alertDialog = builder.create();

//        alertDialog.setView(textView);
        alertDialog.setCancelable(true);
        alertDialog.show();*/
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

