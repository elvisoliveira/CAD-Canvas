package com.feirashop.feirashopcanvas;

import com.example.feirashopcanvas.R;
import com.feirashop.canvas.CadViewer;
import com.feirashop.canvas.Element;
import com.feirashop.canvas.ElementClickListener;
import com.feirashop.canvas.ElementList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	private CadViewer cad;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		
		cad = (CadViewer) findViewById(R.id.cadViewer1);
		
		ElementList stands = new ElementList();
		Element e = new Element();
		e.setX(749.96490f);
		e.setY(531.3966993f);
		e.setHeight(28.7999999f);
		e.setWidth(69.1200000f);
		e.setName("135");
		stands.add(e);
		
		
		Element e1 = new Element();
		e1.setX(421.1219164086223f);
		e1.setY(799.5329703986448f);
		e1.setHeight(28.799999999999272f);
		e1.setWidth(69.1200000000008f);
		e1.setName("319");
		stands.add(e1);
		
		Element e2 = new Element();
		e2.setX(1009.8212105686291f);
		e2.setY(1004.341465219195f);
		e2.setHeight(28.799999999999272f);
		e2.setWidth(69.1200000000008f);
		e2.setName("111");
		stands.add(e2);
		
		
		cad.setElementList(stands);
		
		cad.setOnElementClickListener(new ElementClickListener(){

			@Override
			public void onElementCLick(String element) {
				Toast t = Toast.makeText(context, element,Toast.LENGTH_SHORT);
				t.show();
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
