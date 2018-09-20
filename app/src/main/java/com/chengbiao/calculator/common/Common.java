package com.chengbiao.calculator.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chengbiao.calculator.MainActivity;
import com.chengbiao.calculator.R;
import com.chengbiao.calculator.adapter.ProjectOne;
import com.chengbiao.calculator.ftp.MyFTP;
import com.chengbiao.calculator.update.CheckUpdate;
import com.chengbiao.calculator.utils.SPUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Long on 2018/3/23.
 */

public class Common {


    /**
     * 判断支付宝客户端是否已安装，建议调用转账前检查
     * @return 支付宝客户端是否已安装
     */
    public static   boolean hasInstalledAlipayClient() {
        String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
        PackageManager pm = MyApplication.getContext().getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 支付宝转账
     * @param activity
     * **/
    public static void openALiPay(Activity activity){
        String url1="intent://platformapi/startapp?saId=10000007&" +
                "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Fa6x076306bxhk8outhwdr67%3F_s" +
                "%3Dweb-other&_t=1472443966571#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
        //String url1=activity.getResources().getString(R.string.alipay);
        Intent intent = null;
        Toast.makeText(MyApplication.getContext(),"感谢您的捐赠！٩(๑❛ᴗ❛๑)۶",Toast.LENGTH_SHORT).show();
        if(hasInstalledAlipayClient()){
            try {
                intent = Intent.parseUri(url1 ,Intent.URI_INTENT_SCHEME );
                activity.startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(MyApplication.getContext(),"出错啦",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(MyApplication.getContext(),"您未安装支付宝哦！(>ω･* )ﾉ",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 版本信息提示
     * @param mContext
     * **/
    public static void showNoticeDialog(final Context mContext) {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder.create();
        View view = View.inflate(mContext, R.layout.dialog_about_infor, null);
        Button button=view.findViewById(R.id.check_update);
        TextView version=view.findViewById(R.id.version);
        try {
            version.setText("版本号："+mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName+"  ");
        } catch (PackageManager.NameNotFoundException e) {
            version.setText("null");
            e.printStackTrace();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckUpdate.checkUpdate(mContext);
               // Toast.makeText(mContext,"已是最新！(*/ω＼*)",Toast.LENGTH_SHORT).show();
                //虽然这里的参数是AlertDialog.Builder(Context context)但我们不能使用getApplicationContext()获得的Context,而必须使用Activity.this,因为只有一个Activity才能添加一个窗体。
            }
        });
        WebView webView=view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);//启用javascript支持
        //添加一个js交互接口，方便html布局文件中的javascript代码能与后台java代码直接交互访问
        webView.addJavascriptInterface(new WebPlugin() , "WebPlugin");//new类名，交互访问时使用的别名
        webView.loadUrl("file:///android_asset/about.html");//加载本地的html布局文件
        //其实可以把这个html布局文件放在公网中，这样方便随时更新维护  例如 webview.loadUrl("www.xxxx.com/index.html");
        dialog.setView(view,0,0,0,0);// 设置边距为0,保证在2.x的版本上运行没问题
        dialog.show();
    }
    /**
     * 插件类，在html的js里面直接调用
     */
    private static class WebPlugin {
        @JavascriptInterface
        public String test() {
            return "something";
        }
    }
    /***
     * qq联系我
     * @param activity
     * **/
    public static void contactMe(Activity activity){
        String url ="mqqwpa://im/chat?chat_type=wpa&uin=" + activity.getResources().getString(R.string.qq_number);
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
    /**
     * 保存当前信息记录
     * @param mContext
     * **/
    public static void saveThisDialog(final Context mContext, final List<ProjectOne> list) {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(mContext, R.layout.dialog_save_now, null);
        final Button saveFile=view.findViewById(R.id.saveFile);
        Button cancel=view.findViewById(R.id.cancel);
        final EditText fileName=view.findViewById(R.id.fileName);
        saveFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //新建一个File，传入文件夹目录
                String filePath=MyApplication.getCachePath()+"/"+ SPUtils.getInstance().getString("userName");
                File file = new File(filePath);
                //判断文件夹是否存在，如果不存在就创建，否则不创建
                if (!file.exists()) {
                    //通过file的mkdirs()方法创建<span style="color:#FF0000;">目录中包含却不存在</span>的文件夹
                    file.mkdirs();
                }
                if(saveXML(list,fileName.getText().toString(),filePath)) {
                    Toast.makeText(mContext, "保存成功！", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(mContext,"保存失败！",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }//onclick
        });//LISTNNER
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(mContext,"取消保存！",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setCancelable(false);//设置点击外面消失
        dialog.setView(view,0,0,0,0);// 设置边距为0,保证在2.x的版本上运行没问题

        dialog.show();
    }
    /**
     * 向SD卡写入一个XML文件
     *
     * @param list
     */
    public static boolean saveXML(List<ProjectOne> list,String fileName,String filePath ) {
        boolean result=false;
        // list.add(new ProjectOne("序号","项目名称","计量单位","数量","综合单价","点击各项项目名称显示描述"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        try {
            if(fileName!=null&&fileName!="")
                fileName=simpleDateFormat.format(date)+fileName+".xml";
            else
                fileName=simpleDateFormat.format(date)+"Projects.xml";
            File file = new File(filePath,fileName);
            FileOutputStream fos = new FileOutputStream(file);
            // 获得一个序列化工具
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            // 设置文件头
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "Projects");
            for (int i = 0; i < list.size(); i++) {
                ProjectOne projectOne=list.get(i);
                serializer.startTag(null, "Item");
                serializer.attribute(null, "id", String.valueOf(i));
                // 写项目名称
                serializer.startTag(null, "project");
                serializer.text(projectOne.getProjectName());
                serializer.endTag(null, "project");
                // 写计量单位
                serializer.startTag(null, "unit");
                serializer.text(projectOne.getUnit());
                serializer.endTag(null, "unit");
                // 写数量

                serializer.startTag(null, "num");
                if(i==0)
                    serializer.text("数量");
                else
                    serializer.text(projectOne.getEdit_num());
                serializer.endTag(null, "num");
                //写综合单价
                serializer.startTag(null, "price");
                serializer.text(projectOne.getPrice());
                serializer.endTag(null, "price");
                //写各项项目名称显示描述
                serializer.startTag(null, "description");
                serializer.text(projectOne.getDescription());
                serializer.endTag(null, "description");

                serializer.endTag(null, "Item");
            }
            serializer.endTag(null, "Projects");
            serializer.endDocument();
            fos.close();
            Log.e("Error", "写入成功" );
            result =true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "写入失败"+e.toString() );
        }
        return result;
    }
    public static String getNowTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
    public static String getFileCachePath() {
        //新建一个File，传入文件夹目录
        File filePath = new File(Environment.getExternalStorageDirectory()+File.separator+"ChengBiaoCache");
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!filePath.exists()) {
            //通过file的mkdirs()方法创建<span style="color:#FF0000;">目录中包含却不存在</span>的文件夹
            filePath.mkdirs();
        }
        return  filePath.toString();
    }

    // 使用Pull解析器生成XML文件，并写到本地
//    private void writeXmlToLocal() {
//        List<ProjectOne> personList = getPersonList();
//
//        // 获得序列化对象
//        XmlSerializer serializer = Xml.newSerializer();
////      XmlSerializer serializer = XmlPullParserFactory.newInstance().newSerializer();
//
//        try {
//            File path = new File(Environment.getExternalStorageDirectory(), "persons.xml");
//            FileOutputStream fos = new FileOutputStream(path);
//
//            // 指定序列化对象输出的位置和编码
//            serializer.setOutput(fos, "utf-8");
//
//            // 写开始 <!--?xml version='1.0' encoding='utf-8' standalone='yes' ?-->
//            serializer.startDocument("utf-8", true);
//
//            serializer.startTag(null, "persons");           // <persons>
//
//            for (Person person : personList) {
//
//                // 开始写person
//                serializer.startTag(null, "person");        // <person>
//                serializer.attribute(null, "id", String.valueOf(person.getId()));
//
//                // 写person的name
//                serializer.startTag(null, "name");          // <name>
//                serializer.text(person.getName());
//                serializer.endTag(null, "name");            // </name>
//
//                // 写person的age
//                serializer.startTag(null, "age");           //
//                serializer.text(String.valueOf(person.getAge()));
//                serializer.endTag(null, "age");             // </age>
//
//                serializer.endTag(null, "person");          // </person>
//            }
//
//            serializer.endTag(null, "persons");             // </persons>
//
//            serializer.endDocument();                       // 结束
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
// 使用Pull解析器读取本地的XML文件
    public static  List<ProjectOne> parserXmlFromLocal(String filePath) {
        String[] suffix=filePath.split("\\.");
        if(!"xml".contains(suffix[suffix.length-1]))
        {
            new AlertDialog.Builder(MyApplication.getContext())
                    .setTitle("异常提示")
                    .setMessage("你选择的文件类型不对\n请选择 xml类型的文件！")
                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false) //设置点击外面消失
                    .show();
            return null;
        }
        try {
            File path = new File(filePath);
            FileInputStream fis = new FileInputStream(path);
            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
//          XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");
            // 获得事件类型
            int eventType = parser.getEventType();
            List<ProjectOne> projectOneList = null;
            ProjectOne projectOne = null;
            String id;
            while(eventType != XmlPullParser.END_DOCUMENT) {
                // 获得当前节点的名称
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:               // 当前等于开始节点  <person>
                        if("Projects".equals(tagName)) {         //
                            projectOneList = new ArrayList<>();
                        } else if("Item".equals(tagName)) {   //
                            projectOne = new ProjectOne();
                            id = parser.getAttributeValue(null, "id");
                            projectOne.setSerialNumber(id);
                        } else if("project".equals(tagName)) {     //
                            projectOne.setProjectName(parser.nextText());
                        } else if("unit".equals(tagName)) {      //
                            projectOne.setUnit( parser.nextText());
                        }else if("num".equals(tagName)) {      //
                            projectOne.setEdit_num( parser.nextText());
                        }else if("price".equals(tagName)) {      //
                            projectOne.setPrice( parser.nextText());
                        }else if("description".equals(tagName)) {      //
                            projectOne.setDescription( parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:     //
                        if("Item".equals(tagName)) {
                            // 需要把上面设置好值的person对象添加到集合中
                            projectOneList.add(projectOne);
                        }
                        break;
                    default:
                        break;
                }
                // 获得下一个事件类型
                eventType = parser.next();
            }
            return projectOneList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 模板下载
     *
     */
    public static void downloadDialogModelChoice(final Context context, int fileSize, final ArrayList<String>list) {
        Log.i("ftp", "fileSize="+fileSize);
        final String[] item = context.getResources().getStringArray(R.array.model);
        final boolean[] selected = new boolean[fileSize];
        final String[] items=new String[fileSize];
        while (fileSize>0){
            selected[0]=true;
            items[fileSize-1]=item[fileSize-1];
            fileSize--;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请选择要下载的模板");
        builder.setIcon(R.drawable.app);
        if(items.length!=0){
        builder.setMultiChoiceItems(items, selected,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if(isChecked)
                        {
                            new  AlertDialog.Builder(context)
                                    .setTitle(items[which]+"描述" )
                                    .setMessage(items[which])
                                    .setPositiveButton("我知道了" ,  null )
                                    .show();
                        }
                    }
                });
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // android会自动根据你选择的改变selected数组的值。
                ArrayList<String> temlist=new ArrayList<>();
                for (int i = 0; i < selected.length; i++) {
                    Log.i("md", "onClick: "+selected[i]);
                    if(selected[i]){
                        //todo 服务器路径修改处
                        temlist.add(list.get(i));
                        }
                }
                new MyFTP().ftpMutiDowmload(context,"/gh/Model/",temlist);
            }
        });
        }
        else
        {
            builder.setMessage("暂无模板");
            builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    /****
     * 打开模板选择    定义在这里不可以使用
     * @param context
     * @param result
     * @return
     */
    public static int openDialogModelChoice(final Context context,final int[] result) {
        final String items[] = {"JAVA", "C++", "JavaScript", "MySQL"};
        final boolean selected[] = new boolean[items.length];
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请选择要打开的模板");
        builder.setIcon(R.mipmap.ic_launcher);
        selected[0]=true;
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result[0] =which;
            }
        });
        builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.create().show();
        return result[0];
    }


}

