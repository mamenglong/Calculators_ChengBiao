package com.chengbiao.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chengbiao.calculator.adapter.ProjectOne;
import com.chengbiao.calculator.adapter.TableViewAdapter;
import com.chengbiao.calculator.common.Common;
import com.chengbiao.calculator.common.MyApplication;
import com.chengbiao.calculator.ftp.MyFTP;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;//侧滑根标签
    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新
    private NavigationView navView;//侧滑菜单按钮
    private BottomNavigationView bottomNavigationView;//底部
    private RecyclerView recyclerView;//列表控件
    private List<ProjectOne> list = new ArrayList<>();

    private List<String> description, price, project, unit, num;
    private TableViewAdapter adapter;
    private   int[] openChoice = {0,0};//0是选择打开时的标志，1是远程文件的书面
    //private String[] name={"序号","项目名称","计量单位","数量","综合单价"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniWidget();//初始化控件
        if (isHaveModel())
            openModel();
        else {
            getRemoteFileSize();
        }
    }

    /**
     * 加载menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * 菜单按钮点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.backup:
                Toast.makeText(this, "backup", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.delete:
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.setting:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0://历史记录返回文件
                    Uri selectedMediaUri = data.getData();
                    String path = selectedMediaUri.getPath();
                    Log.e("Error", "onActivityResult: " + path);
                    try {
                        list.clear();
                        list.addAll(Common.parserXmlFromLocal(path));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "打开成功，请查看！", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "文件格式不对或已损坏！", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 1://上传文件选择文件路径
                    Uri selectedMediaUriu = data.getData();
                    String filePath = selectedMediaUriu.getPath();
                    Log.e("MyFTP", "onActivityResult: " + filePath);
                    try {
                        new MyFTP().ftpUpload(this, filePath);
                        //  Toast.makeText(this,"上传完成！",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "文件格式不对！" + e.toString(), Toast.LENGTH_SHORT).show();

                    }
                    break;

                default:

            }
        }
    }

    /******************************重写函数分界线*********************************************/

    /****
     * 打开模板，并加载数据
     */
    public void openModel() {
        Log.i("openChoice", "onCreate: " + openChoice[0]);
        openDialogModelChoice();
        //   Common.openDialogModelChoice(this,openChoice);
        Log.i("openChoice", "onCreate: " + openChoice[0]);
        //  iniData(openChoice[0]);
        Log.i("openChoice", "onCreate: " + openChoice[0]);
    }

    /***
     * 是否存在model
     * **/
    private String modelPath = MyApplication.getCachePath() + File.separator + "Model";

    public boolean isHaveModel() {
        File file = new File(modelPath);
        if (!file.exists())
            file.mkdirs();
        else if (file.listFiles().length != 0)
            return true;
        return false;
    }

    /****
     *
     * 获取远程文件夹下文件数目，并显示下载多选按钮
     */
    private ArrayList<String> remoteFileList=new ArrayList();
    public void getRemoteFileSize(){
    final ProgressDialog progressDialog=new ProgressDialog(this);
    progressDialog.setTitle("加载中...");
    progressDialog.show();
    Log.i("ftp", "fileSize="+openChoice[1]);
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                 new MyFTP().getFileSize("/gh/Model/",openChoice,remoteFileList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Common.downloadDialogModelChoice(MainActivity.this, openChoice[1],remoteFileList);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }).start();
}

    /****
     * 初始化数据 是活动返回传参数true
     */
    public void iniData() {
        ProgressBar progressBar = new ProgressBar(this);
        list.clear();
        List<ProjectOne> list1=Common.parserXmlFromLocal(modelPath + File.separator +"model_"+openChoice[0]+".xml");
        if(list1==null)
        Toast.makeText(this,"文件错误！",Toast.LENGTH_SHORT).show();
        else
        list.addAll(list1);
        adapter = new TableViewAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);
    }
    /*****
     * 初始化控件
     */
    public void iniWidget() {
        MyListener listener = new MyListener();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //显示导航
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置导航图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        drawerLayout = findViewById(R.id.drawerLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        navView = (NavigationView) findViewById(R.id.nav_view);//侧栏
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        navView.setCheckedItem(R.id.nav_home);
        //点击邮箱发邮件
        View headerView = navView.inflateHeaderView(R.layout.nav_header);
        LinearLayout linearLayout = headerView.findViewById(R.id.icon_mail);
        linearLayout.setOnClickListener(listener);
        navView.setNavigationItemSelectedListener(listener);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

    }

    /****
     * 计算
     */
    public double getResult() {
        double result = 0;//i第一个为” 数量“

        for (int i = 1; i < list.size(); i++) {
            int num = list.get(i).getEdit_num() != "" ? Integer.parseInt(list.get(i).getEdit_num()) : 0;
            result += num * Double.parseDouble(list.get(i).getPrice());
        }
        return result;
    }

    /***8
     * 计算结果
     */
    public void showResultDialog() {
        Toast.makeText(this, "计算中，请稍后...", Toast.LENGTH_SHORT).show();
        Double result = getResult();
        Log.e("Error", "runOnUiThread");
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("总花费")
                .setMessage(result.toString() + "元")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false) //设置点击外面消失
                .show();
    }

    /***
     * 选择打开模板
     */
    public void openDialogModelChoice() {
        int fileSize=0;
        String path=MyApplication.getCachePath()+File.separator+"Model";
        File file=new File(path);
        fileSize=file.listFiles().length;
        Log.i("fileSize", "fileSize="+fileSize);
        final String item[] = getResources().getStringArray(R.array.model);//{"模板一", "模板二", "模板三", "模板四"};
        final boolean selected[] = new boolean[fileSize];
        final String items[]=new String[fileSize];
        while (fileSize>0){
            selected[0] = true;
            items[fileSize-1]=item[fileSize-1];
            fileSize--;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请选择要打开的模板");
        builder.setIcon(R.drawable.app);
        if(items.length!=0) {
            builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openChoice[0] = which;
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(items[openChoice[0]] + "描述")
                            .setMessage(items[openChoice[0]])
                            .setPositiveButton("我知道了", null)
                            .show();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Log.i("setSingleChoiceItems", which + "  " + openChoice[0]);
                    iniData();
                }
            });
        }
        else
        {
            builder.setMessage("暂无模板,请先下载！");
            builder.setNegativeButton("点我去下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getRemoteFileSize();
                }
            });
        }
        builder.create().show();
    }


    public class MyListener implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.icon_mail:
//                    Intent it = new Intent(Intent.ACTION_SENDTO);
//                    it.putExtra(Intent.EXTRA_EMAIL, getResources().getString(R.string.nav_header_email));
//                    //   it.setType("text/plain");
//                    startActivity(Intent.createChooser(it, "Choose Email Client"));
//                    break;
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://www.chengbiaosoft.com/");
                    intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    intent.setClassName("com.uc.browser", "com.uc.browser.ActivityUpdate");
                    intent.setClassName("com.tencent.mtt", "com.tencent.mtt.MainActivity");
                    intent.setData(content_url);
                    startActivity(Intent.createChooser(intent, "选择浏览器打开网站"));
                    break;
                default:
            }

        }

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            navView.setCheckedItem(item.getItemId());
            switch (item.getItemId()) {
                case R.id.nav_home:
                    openModel();
                    break;
                case R.id.nav_history:
                    openExploer(0);
                    break;
                case R.id.uploadToServer:
//                    Intent intentftp=new Intent(MainActivity.this,FTPActivity.class);
//                    startActivity(intentftp);
                    openExploer(1);
                    break;
                case R.id.dowmLoadModel:
                    getRemoteFileSize();
                    // Common.downloadDialogModelChoice(MainActivity.this,openChoice[1]);
                    break;
                case R.id.nav_save:
                    Common.saveThisDialog(MainActivity.this, list);
                    break;
                case R.id.contactMe:
                    Common.contactMe(MainActivity.this);
                    break;
                case R.id.about:
                    Common.showNoticeDialog(MainActivity.this);
                    //虽然这里的参数是AlertDialog.Builder(Context context)但我们不能使用getApplicationContext()获得的Context,而必须使用Activity.this,因为只有一个Activity才能添加一个窗体。
                    break;
                case R.id.tips:
                    Toast.makeText(MainActivity.this, "历史记录保存地址：/storage/emulated/0/ChengBiaoCache", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_bottom_count:
                    showResultDialog();
                    break;
                case R.id.nav_bottom_save:
                    Common.saveThisDialog(MainActivity.this, list);
                    break;
                case R.id.nav_bottom_clear:
                    iniData();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "清空成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
            drawerLayout.closeDrawers();
            return true;
        }

        public void openExploer(int requestCode) {
            String filePath = Common.getFileCachePath();
            //  File parentFile = new File(filePath+File.separator+"20180409.xml");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Uri contentUri = FileProvider.getUriForFile(MainActivity.this,   "com.chengbiao.calculator.fileprovider", null);
//                intent.setDataAndType(contentUri, "*/*");
            } else {
                intent.setType("*/*");//无类型限制
//                intent.setDataAndType(Uri.fromFile(null), "*/*");
            }
            intent.setType("*/*");
            startActivityForResult(intent, requestCode);
        }


    }


}
