package jp.linanfine.dsma.value.adapter;

import java.util.ArrayList;

import jp.linanfine.dsma.activity.ManageRivals;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.RivalData;
import jp.linanfine.dsma.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RivalListAdapter extends ArrayAdapter<RivalData> {
		
	 private LayoutInflater layoutInflater_;
	 private int mActiveRival;
	 ManageRivals mManageRivalsActivity;
	 
	 public RivalListAdapter(ManageRivals manageRivalsActivity, ArrayList<RivalData> rivals, int activeRival) {
		 super(manageRivalsActivity, 0, rivals);
		 mManageRivalsActivity = manageRivalsActivity;
		 layoutInflater_ = (LayoutInflater) manageRivalsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 mActiveRival = activeRival;
	 }
	 
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 
		 // 特定の行(position)のデータを得る
		 RivalData rival = (RivalData)getItem(position);
		 
		 final int rivalId = position-2;
		 
		 // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
		 if (null == convertView) {
			 convertView = layoutInflater_.inflate(R.layout.view_rival_list_item, null);
		 }
		 
		 if(null == rival)
		 {
			 ((TextView)convertView.findViewById(R.id.ddrCode)).setVisibility(View.GONE);
			 ((TextView)convertView.findViewById(R.id.dancerName)).setText(convertView.getContext().getResources().getString(R.string.manage_rivals_only_my_score));
			 ImageView act = (ImageView)convertView.findViewById(R.id.imageActive);
			 act.setImageResource((-1 == mActiveRival)?android.R.drawable.btn_star_big_on:android.R.drawable.btn_star_big_off);
			 act.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) {
					FileReader.saveActiveRival(layoutInflater_.getContext(), -1);
					mManageRivalsActivity.initialize();
				}});
		 }
		 else if(rival.Id.equals("00000000"))
		 {
			 ((TextView)convertView.findViewById(R.id.ddrCode)).setVisibility(View.GONE);
			 ((TextView)convertView.findViewById(R.id.dancerName)).setText(convertView.getContext().getResources().getString(R.string.manage_rivals_scores_from_old_ddr_sm));
			 ImageView act = (ImageView)convertView.findViewById(R.id.imageActive);
			 act.setImageResource((-2 == mActiveRival)?android.R.drawable.btn_star_big_on:android.R.drawable.btn_star_big_off);
			 act.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) {
					FileReader.saveActiveRival(layoutInflater_.getContext(), -2);
					mManageRivalsActivity.initialize();
				}});
		 }
		 else
		 {
			 TextView c =(TextView)convertView.findViewById(R.id.ddrCode); 
			 c.setVisibility(View.VISIBLE);
			 c.setText(rival.Id);
			 ((TextView)convertView.findViewById(R.id.dancerName)).setText(rival.Name);
			 ImageView act = (ImageView)convertView.findViewById(R.id.imageActive);
			 act.setImageResource((position == mActiveRival+2)?android.R.drawable.btn_star_big_on:android.R.drawable.btn_star_big_off);
			 act.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) {
					FileReader.saveActiveRival(layoutInflater_.getContext(), rivalId);
					mManageRivalsActivity.initialize();
				}});
		 }
		 
		 return convertView;
	 }
}
