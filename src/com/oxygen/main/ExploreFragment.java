package com.oxygen.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygen.explore.ExploreAttendanceActivity;
import com.oxygen.explore.ExploreLaunchPlaneActivity;
import com.oxygen.explore.ExploreLaunchPopWindow;
import com.oxygen.explore.ExplorePickPlaneActivity;
import com.oxygen.explore.ExploreSignUpActivity;
import com.oxygen.wall.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @ClassName FavourFragment
 * @Description 探索界面Fragment
 * @author oxygen
 * @email oxygen0106@163.com
 * @date 2014-9-16 下午09:33:01
 */
public class ExploreFragment extends Fragment implements OnClickListener {

	Activity activity;

	private LinearLayout launch;
	private LinearLayout pick;
	private LinearLayout build;
	private LinearLayout register;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.explore_fragment, container,
				false);

		launch = (LinearLayout) view.findViewById(R.id.explore_launch);
		pick = (LinearLayout) view.findViewById(R.id.explore_pick);
		build = (LinearLayout) view.findViewById(R.id.explore_build);
		register = (LinearLayout) view.findViewById(R.id.explore_register);
		launch.setOnClickListener(this);
		pick.setOnClickListener(this);
		build.setOnClickListener(this);
		register.setOnClickListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.explore_launch:
			startActivity(new Intent(activity, ExploreLaunchPlaneActivity.class));
			break;
		case R.id.explore_pick:
			startActivity(new Intent(activity, ExplorePickPlaneActivity.class));
			break;
		case R.id.explore_build:
			startActivity(new Intent(activity, ExploreAttendanceActivity.class));
			break;
		case R.id.explore_register:
			startActivity(new Intent(activity, ExploreSignUpActivity.class));
			break;
		default:
			break;
		}
	}
}
