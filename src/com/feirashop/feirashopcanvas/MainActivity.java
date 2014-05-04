package com.feirashop.feirashopcanvas;

import com.example.feirashopcanvas.R;
import com.feirashop.canvas.CadViewer;
import com.feirashop.canvas.Coordinates;
import com.feirashop.canvas.Element;
import com.feirashop.canvas.ElementClickListener;
import com.feirashop.canvas.ElementList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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
		
		/*
		 * Abaixo segue uma demonstração de utilização do CadViewer, com várias
		 * possibilidades de configurações
		 * 
		 * As únicas configurações indispensáveis ao funcionamento são:
		 * 	a) setBitmapFromResourceID ou setBitmap
		 *  b) setElementList
		 *  c) setOnElementClickListener
		 */
		
		ElementList stands = new ElementList();
		Element e = new Element();
		e.setX(749.96490f);
		e.setY(531.3966993f);
		e.setHeight(10.141688761646833f);
		e.setWidth(24.34005302795333f);
		e.setName("135");
		stands.add(e);
		
		
		Element e1 = new Element();
		e1.setX(421.1219164086223f);
		e1.setY(799.5329703986448f);
		e1.setHeight(10.141688761646833f);
		e1.setWidth(24.34005302795333f);
		e1.setName("319");
		stands.add(e1);
		
		Element e2 = new Element();
		e2.setX(1009.8212105686291f);
		e2.setY(1004.341465219195f);
		e2.setHeight(10.141688761646833f);
		e2.setWidth(24.34005302795333f);
		e2.setName("111");
		stands.add(e2);
		
		cad.setShowElementRegionEnabled(true);  //Não precisava informar, pois este é o valor default. Apenas para demonstrar como funciona. 
												//Se for false, apenas não exibe a região, mas continua detectando os clicques. 
		cad.setBitmapFromResourceID(R.drawable.teste);//Ou, se preferir, pode utilizar setBitmap(Bitmap bmp)
		cad.setElementList(stands);	//Aqui são informadas as coordenadas dos elementos	
		
		cad.setOnElementClickListener(new ElementClickListener(){ //Este evento será chamar toda vez que clicar em um elemento da lista

			@Override
			public void onElementCLick(Element element) {
				Toast t = Toast.makeText(context, element.getName(),Toast.LENGTH_SHORT);
				t.show();
			}
			
		});
		
		cad.setCurrentCanvasScale(0.185f); //Altera a escala inicial do canvas (Como a imagem é maior que a tela, utiliza uma escala menor que 1 para enquadrar a imagem na tela)
		cad.setCoordinates(-250,0); //Desloca a imagem no canvas (Para centralizar)
		cad.setRegionStrategy(CadViewer.REGION_STRAT_USE_ELEMENT_BOUNDS);//Não precisava informar, pois este é o valor default. Apenas para demonstrar como funciona
		cad.setElementRegionScaleFactor(2); //Permite multiplicar o tamanho da região do elemento (região clicável)
		Paint paint = cad.getEntityAreaPaint(); //Permite alterar a forma como a região clicável é desenhada		
		paint.setAlpha(40); //Por exemplo, incluiu maior transparência na cor da região (O valor default é 50)
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
