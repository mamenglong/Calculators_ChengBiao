package com.chengbiao.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chengbiao.calculator.adapter.ProjectOne;
import com.chengbiao.calculator.adapter.TableViewAdapter;
import com.chengbiao.calculator.common.Common;
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

    //private String[] name={"序号","项目名称","计量单位","数量","综合单价"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniWidget();//初始化控件
        iniData();//初始化数据
//        for(int i=0;i<5;i++)
//        list.add(new ProjectOne("序号","项目名称","计量单位","数量","综合单价","222222222"));
        recyclerView.setAdapter(adapter);


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
            switch (requestCode)
            {
                case 0://历史记录返回文件
                    Uri selectedMediaUri = data.getData();
                    String path =  selectedMediaUri.getPath();
                    Log.e("Error", "onActivityResult: "+path);
                    try{
                    list.clear();
                    list .addAll(Common.parserXmlFromLocal(path));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this,"打开成功，请查看！",Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this,"文件格式不对或已损坏！",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 1://上传文件选择文件路径
                    Uri selectedMediaUriu = data.getData();
                    String filePath =  selectedMediaUriu.getPath();
                    Log.e("MyFTP", "onActivityResult: "+filePath);
                    try{
                       new MyFTP().ftpUpload(this,filePath);
                     //  Toast.makeText(this,"上传完成！",Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        Toast.makeText(this,"文件格式不对！"+e.toString(),Toast.LENGTH_SHORT).show();

                    }
                    break;

                default:

            }
        }
    }
/******************************重写函数分界线*********************************************/

    /****
     * 初始化数据 是活动返回传参数true
     */
    public void iniData() {

        list.clear();
        num = Arrays.asList(getResources().getStringArray(R.array.num));
        for (int i = 0; i < description.size(); i++) {
            if (i == 0)
                list.add(new ProjectOne("序号", "项目名称", "计量单位", "数量", "综合单价", "点击各项项目名称显示描述"));
            list.add(new ProjectOne(String.valueOf(i + 1), project.get(i), unit.get(i), "", price.get(i), description.get(i)));
        }
        adapter = new TableViewAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
        description = Arrays.asList(getResources().getStringArray(R.array.description));
        price = Arrays.asList(getResources().getStringArray(R.array.price));
        project = Arrays.asList(getResources().getStringArray(R.array.project));
        unit = Arrays.asList(getResources().getStringArray(R.array.unit));
    }

    /****
     * 计算
     */
    public double getResult() {
        double result = 0;//i第一个为” 数量“

        for (int i = 1; i < list.size(); i++) {
            int num=list.get(i).getEdit_num()!=""?Integer.parseInt(list.get(i).getEdit_num()):0;
            result += num * Double.parseDouble(list.get(i).getPrice());
        }
        return result;
    }

    public void showResultDialog() {
        Toast.makeText(this, "计算中，请稍后...", Toast.LENGTH_SHORT).show();
        Double result = getResult();
        Log.e("Error", "runOnUiThread");
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("总花费")
                .setMessage(result.toString()+"元")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false) //设置点击外面消失
                .show();
    }



    public class MyListener implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener,BottomNavigationView.OnNavigationItemSelectedListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.icon_mail:
//                    Intent it = new Intent(Intent.ACTION_SENDTO);
//                    it.putExtra(Intent.EXTRA_EMAIL, getResources().getString(R.string.nav_header_email));
//                    //   it.setType("text/plain");
//                    startActivity(Intent.createChooser(it, "Choose Email Client"));
//                    break;
                    Intent intent=new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://www.chengbiaosoft.com/");
                    intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                    intent.setClassName("com.uc.browser", "com.uc.browser.ActivityUpdate");
                    intent.setClassName("com.tencent.mtt", "com.tencent.mtt.MainActivity");
                    intent.setData(content_url);
                    startActivity(Intent.createChooser(intent,"选择浏览器打开网站"));
                    break;
                default:
            }

        }

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            navView.setCheckedItem(item.getItemId());
            switch (item.getItemId()){
                case R.id.nav_home:
                    Toast.makeText(MainActivity.this,"nav_home",Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.nav_history:
                    openExploer(0);
                    break;
                case R.id.uploadToServer:
//                    Intent intentftp=new Intent(MainActivity.this,FTPActivity.class);
//                    startActivity(intentftp);
                    openExploer(1);
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
                    Toast.makeText(MainActivity.this,"历史记录保存地址：/storage/emulated/0/ChengBiaoCache",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this,"清空成功",Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
            drawerLayout.closeDrawers();
            return true;
        }

        public void openExploer(int requestCode){
            String filePath=Common.getFileCachePath();
          //  File parentFile = new File(filePath+File.separator+"20180409.xml");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Uri contentUri = FileProvider.getUriForFile(MainActivity.this,   "com.chengbiao.calculator.fileprovider", null);
//                intent.setDataAndType(contentUri, "*/*");
            }
            else {
                intent.setType("*/*");//无类型限制
//                intent.setDataAndType(Uri.fromFile(null), "*/*");
            }
            intent.setType("*/*");
            startActivityForResult(intent, requestCode);
        }

    }
}
