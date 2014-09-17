package com.oxygen.my;

import com.oxygen.ar.ARArcMenu;
import com.oxygen.ar.ARArcMenu.OnMenuItemClickListener;
import com.oxygen.wall.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class MyLoginActivity extends Activity {
	
	private  ARArcMenu arcMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.setContentView(R.layout.my_login);
		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.my_setting_title_bar);
		
		arcMenu = (ARArcMenu)findViewById(R.id.id_arcmenu1);
		
		ImageView people = new ImageView(this);
		people.setImageResource(R.drawable.composer_with);
		people.setTag("People");
		arcMenu.addView(people);

	
		arcMenu
				.setOnMenuItemClickListener(new OnMenuItemClickListener()
				{
					@Override
					public void onClick(View view, int pos)
					{
						Toast.makeText(MyLoginActivity.this,
								pos + ":" + view.getTag(), Toast.LENGTH_SHORT)
								.show();
					}
				});
		
		
	}
}
