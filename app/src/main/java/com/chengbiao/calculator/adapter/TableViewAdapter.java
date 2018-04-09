package com.chengbiao.calculator.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chengbiao.calculator.MainActivity;
import com.chengbiao.calculator.R;
import com.chengbiao.calculator.common.MyApplication;
import com.chengbiao.calculator.reWrite.MyTableTextView;

import java.util.List;

/**
 * 项目名称：Calculator20180403
 * Created by Long on 2018/4/3.
 * 修改时间：2018/4/3 19:01
 */
public class TableViewAdapter extends  RecyclerView.Adapter<TableViewAdapter.TableViewHolder>implements View.OnClickListener   {
    private Context mContext;
    private List<ProjectOne> list;
    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_adapter_table, parent, false);
        view.setOnClickListener(this);
        TableViewHolder holder = new TableViewHolder(view);
        return holder;
    }

    @Override
    public void onViewRecycled(@NonNull TableViewHolder holder) {
        //销毁之前销毁监听器
        holder.edit_num.removeTextChangedListener(holder.watcher);
        super.onViewRecycled(holder);
    }


    @Override
    public void onBindViewHolder(final TableViewHolder holder, final int position) {
        Log.e("adapter", "onBindViewHolder: "+position);
        final ProjectOne projectOne=list.get(position);
        holder.itemView.setTag(position);
        holder.serialNumber.setText(projectOne.getSerialNumber());
        holder.projectName.setText(projectOne.getProjectName());
        holder.price.setText( projectOne.getPrice() );
        holder.unit.setText(projectOne.getUnit());
        if(position==0)
        {
            holder.num.setVisibility(View.VISIBLE);
            holder.edit_num.setVisibility(View.GONE);
            //少写导致显示不全异常
        }
        else
        {
            holder.num.setVisibility(View.GONE);
            holder.edit_num.setVisibility(View.VISIBLE);
            holder.edit_num.setText(projectOne.getEdit_num());
            //添加editText的监听事件
            holder.edit_num.setTag(position);
            holder.watcher=
                    new  android.text.TextWatcher(){
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                          Log.d("111111111111111", "onTextChanged: 11111111111111111111");

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            Log.d("111111111111111", "afterTextChanged: "+position+" --- " +holder.edit_num.getText().toString());
                            list.get(position).setEdit_num(s.toString());
                        }
                    };
            holder.edit_num.addTextChangedListener(holder.watcher);
        }

        holder.projectName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new  AlertDialog.Builder(mContext)
                        .setTitle("描述"+position )
                        .setMessage(projectOne.getDescription())
                        .setPositiveButton("我知道了" ,  null )
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener!=null){
            itemClickListener.onItemClick(v,(int)v.getTag());
        }
    }


    public TableViewAdapter(List<ProjectOne> list) {
        this.list = list;
    }

    public interface ItemClickListener{
        void onItemClick(View view,int position);
    }
    private ItemClickListener itemClickListener;
    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }

    public class TableViewHolder extends RecyclerView.ViewHolder  {

        TextWatcher watcher;//解决输入内容滑动后消失
        private MyTableTextView serialNumber,projectName,unit,num,price;
        private EditText edit_num;
        public TableViewHolder(View itemView) {
            super(itemView);
            serialNumber=itemView.findViewById(R.id.serialNumber);
            projectName=itemView.findViewById(R.id.projectName);
            unit=itemView.findViewById(R.id.unit);
            num=itemView.findViewById(R.id.num);
            price=itemView.findViewById(R.id.price);
            edit_num=itemView.findViewById(R.id.edit_num);

        }
    }


}
