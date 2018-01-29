package com.example.yb.hstt.View.SlidingLib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yb.hstt.R;

import java.util.List;


public class SlideListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;

	private SlideManager slideManager;

	private List<OrderInfoList> lists2;
	private onFunctionListener functionListener;

	public SlideListAdapter(Context mContext,List<OrderInfoList> lists,onFunctionListener functionListener) {
		super();
		this.mContext = mContext;
		this.lists2=lists;
		this.mInflater = LayoutInflater.from(mContext);
		this.functionListener=functionListener;
		slideManager = new SlideManager();
	}
	public void setData(List<OrderInfoList> lists){
		this.lists2=lists;
		notifyDataSetChanged();
	}

	public SlideManager getSlideManager() {
		return slideManager;
	}

	@Override
	public int getCount() {
		return lists2.size();
	}

	@Override
	public Object getItem(int position) {
		return lists2.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	private SlidingContentView.OnItemClickListener itemClickListener;

	public void setOnItemClickListener(SlidingContentView.OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	class ViewHolder {
		public ImageView iv_head,iv_file_tag;
		public TextView tv_device_type,tv_factory,tv_device_id,tv_status;
		public Button mCancelCall;
		public Button mDeleteCell;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		OrderInfoList beans = lists2.get(position);
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item, null, false);
			mHolder.iv_head= (ImageView) convertView.findViewById(R.id.iv_head);
			mHolder.tv_device_type= (TextView) convertView.findViewById(R.id.tv_device_type);
			mHolder.tv_factory= (TextView) convertView.findViewById(R.id.tv_factory);
			mHolder.tv_device_id= (TextView) convertView.findViewById(R.id.tv_device_id);
			mHolder.tv_status= (TextView) convertView.findViewById(R.id.tv_status);
			mHolder.mCancelCall = (Button) convertView.findViewById(R.id.bt_call);
			mHolder.mDeleteCell = (Button) convertView.findViewById(R.id.bt_delete);
			mHolder.iv_file_tag= (ImageView) convertView.findViewById(R.id.iv_file_tag);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		mHolder.iv_head.setImageResource(beans.getResId());
		mHolder.tv_device_type.setText(beans.getDevice_type());
		mHolder.tv_factory.setText(beans.getFactory());
		mHolder.tv_device_id.setText("编号："+beans.getCd_id());
		mHolder.tv_status.setText(beans.getStatus());
		mHolder.tv_status.setTextColor(mContext.getResources().getColor(beans.getTextcolor()));
        mHolder.iv_file_tag.setTag(position);

		int tag = Integer.parseInt(mHolder.iv_file_tag.getTag().toString());
		if (tag==position){
			mHolder.iv_file_tag.setVisibility(lists2.get(tag).getIs_haveFile());
		}
		final SlidingItemLayout view = (SlidingItemLayout) convertView;
		view.closeSlidingLayout(false, false);
		//给我们的滑动视图绑定回调监听（监听生命周期）
		view.setOnSlideItemListener(slideManager.getOnSlideItemListener());
		//一旦你点击了contentView我们立马将原来的已打开的视图关闭
		view.getContentView().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (slideManager.getUnClosedCount()==0){
					if (itemClickListener!=null){
						itemClickListener.onItemClick(position);
					}
					return;
				}
				slideManager.closeAllLayout();
			}
		});
		mHolder.mCancelCall.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				view.close();
				functionListener.edit(position);
			}
		});
		mHolder.mDeleteCell.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				functionListener.delete(position);
			}
		});
		return view;
	}

	public interface onFunctionListener{
		void delete(int pos);
		void edit(int pos);
	}


	//单行更新，
	public void updateSingleRow(ListView listView, String device_id) {

		if (listView != null) {
			int start = listView.getFirstVisiblePosition();
			for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++)
				if (device_id == ((OrderInfoList) listView.getItemAtPosition(i)).getCd_id()) {
					View view = listView.getChildAt(i - start);
					getView(i, view, listView);
					break;
				}
		}
	}

}