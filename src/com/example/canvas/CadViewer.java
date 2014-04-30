package com.example.canvas;


import com.example.feirashopcanvas.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;

public class CadViewer extends View{

	private Bitmap bmp1;
	private float mScaleFactor;
	private ScaleGestureDetector mScaleDetector;
	private Rect clipBounds;
	private Coordinates coordinates;
	private Coordinates previousCoordinates;
	
	private float maxScale;
	private float minScale;
	
	/**
	 * Classe utilizada para armazenar coordenadas (X,Y) de elementos
	 * @author William
	 */
	public class Coordinates{
		private float x;
		private float y;
		
		public Coordinates(){
			this.x = -500;
			this.y = -500;
		}
		
		public void setX(float x){
			this.x = x;
		}
		public void setY(float y){
			this.y = y;
		}
		public float getX(){
			return this.x;
		}
		public float getY(){
			return this.y;
		}
	} 
		
	private boolean moving;
	

	
	public CadViewer(Context context) {
		super(context);
		mScaleDetector = new ScaleGestureDetector(context,new ScaleGestureListener());
		bmp1 =  BitmapFactory.decodeResource(getResources(), R.drawable.teste);
		mScaleFactor = 1;
		coordinates = new Coordinates();
		previousCoordinates = new Coordinates();
		maxScale = 5.0f;
		minScale = 0.1f;
	}
	public CadViewer(Context context, AttributeSet ats){
		super(context, ats);
		mScaleDetector = new ScaleGestureDetector(context,new ScaleGestureListener());				
		bmp1 =  BitmapFactory.decodeResource(getResources(), R.drawable.teste);
		mScaleFactor = 1;
		coordinates = new Coordinates();
		previousCoordinates = new Coordinates();
		maxScale = 5.0f;
		minScale = 0.1f;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {	
		super.onDraw(canvas);	
		canvas.save();
		
		//Aplica a escala e o deslocamento sobre o canvas
		canvas.scale(mScaleFactor, mScaleFactor);
		
		//Desenha a imagem no canvas
		canvas.drawBitmap(bmp1, coordinates.getX(), coordinates.getY(), null);
		
		canvas.restore();
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		clipBounds = canvas.getClipBounds();
		//canvas.drawCircle(67.73832f*mScaleFactor, 245.35135f*mScaleFactor, 10.0f, paint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mScaleDetector.onTouchEvent(event);
		//Recalcula as coordenadas atingidas pelo click de acordo com a escala do canvas 
		float touchX = event.getX() / mScaleFactor + clipBounds.left;
	    float touchY = event.getY() / mScaleFactor + clipBounds.top;
	    
	    /*
	     * OBS.: A vari�vel "moving", juntamente com o m�todo  mScaleDetector.isInProgress(),
	     * 		s�o utilizados para implementar um "sem�foro", onde o deslocamento da imagem
	     *      (movendo o dedo sobre a tela) fica bloqueado enquanto o usu�rio estiver escalando
	     *      a mesma (aumentando/diminuindo o zoom com o movimento de "pin�a".
	     *      Isto impede um corpotamento anormal durante a realiza��o do movimento 
	     * */
		
	    //Ao tocar na tela, iguala as coordenadas anteriores com a atual (in�cio do movimento)
	    if(event.getAction()==MotionEvent.ACTION_DOWN){	    	
	    	previousCoordinates.setX(touchX);
	    	previousCoordinates.setY(touchY);
	    	moving = true;
	    
	    //Ao movimentar, desloca a imagem pelo valor da diferen�a entre a coordenada atual e a anterior	
	    }else if(event.getAction()==MotionEvent.ACTION_MOVE){
	    	//O deslocamento da imagem fica bloqueado enquanto a imagem est� sendo escalada pelo movimento de "Pin�a"
	    	if((!mScaleDetector.isInProgress())&&(moving)){
	    		coordinates.setX(coordinates.getX()-(previousCoordinates.getX()-touchX));
	    		coordinates.setY(coordinates.getY()-(previousCoordinates.getY()-touchY));
	    		
	    		//Ap�s deslocar a imagem, iguala as coordenadas anteriores �s atuais
	    		previousCoordinates.setX(touchX); 
	    		previousCoordinates.setY(touchY);
	    	}
	    
	    //Ao "levantar" o dedo, desbloqueia o deslocamento da imagem.
	    }else if(event.getAction()==MotionEvent.ACTION_UP){	    	
	    	moving = true;
	    	previousCoordinates.setX(touchX);
	    	previousCoordinates.setY(touchY);
	    }
	    
	    
	    
	    invalidate();
		return true;
	}
	
	/**
	 * Listener que trata a ocorr�ncia do movimento de "pin�a" para aumentar
	 * ou diminuir o zoon do canvas.
	 * 
	 * @author William
	 */
	public class ScaleGestureListener extends SimpleOnScaleGestureListener{
		
		
		/**
		 * Evento disparado quando a escala � alterada pelo movimento de "Pin�a".
		 * O tamanho do elemento � limitado pela escala m�xima e m�nima, cujos
		 * valores padr�o s�o, respectivamente, 0.1 e 5.
		 * Para valores diferentes, use {@link #CadViewer.setMaxScale(float)} e 
		 * {@link #CadViewer.setMinScale(float)}
		 */
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			moving   = false;
			mScaleFactor *= detector.getScaleFactor();
			
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(minScale, Math.min(mScaleFactor, maxScale));
	        
	        invalidate();

			return true;
		}		
	}
	
	/**
	 * Altera a escala m�xima para o canvas. O valor padr�o � 5.
	 * @param scale - O valor m�ximo para a escala
	 */
	public void setMaxScale(float scale){
		this.maxScale = scale;
	}
	
	/**
	 * @return a escala m�xima para o canvas
	 */
	public float getMaxScale(){
		return this.maxScale;
	}
	
	/**
	 * Altera a escala m�nima para o canvas.
	 * @param scale - O valor m�ximo para a escala
	 */
	public void setMinScale(float scale){
		this.minScale = scale;
	}
	
	/**
	 * @return a escala m�xima para o canvas. O valor padr�o � 0.1.
	 */
	public float getMinScale(){
		return this.minScale;
	}
	
	/**
	 * Altera a escala atual do canvas. A utiliza��o deste m�todo implica na chamada imediata
	 * ao m�todo {@link #Canvas.invalidate()}, causando o redesenho imediato do canvas.
	 * 
	 * @param scale - O valor m�ximo para a escala
	 */
	public void setCurrentCanvasScale(float scale){
		this.mScaleFactor = scale;
		this.invalidate();
	}
	
	/**
	 * @return o fator de escala utilizado no canvas.
	 */
	public float getCurrentCanvasScale(){
		return this.mScaleFactor;
	}
	
	/**
	 * Retorna as coordenadas atuais da imagem no canvas
	 * @return - A {@link Coordinates} object containing the image coordinates
	 */
	public Coordinates getCoordinates(){
		return this.coordinates;
	}
	
	/**
	 * Altera as coordenadas da imagem no canvas (Desloca a imagem); A utiliza��o deste m�todo 
	 * implica na chamada imediata ao m�todo {@link #Canvas.invalidate()}, causando o redesenho 
	 * imediato do canvas.
	 * 
	 * @return - A {@link Coordinates} object containing the image coordinates
	 */
	public void setCoordinates(Coordinates coordinates){
		this.coordinates = coordinates;
	}
	
	/**
	 * Altera as coordenadas da imagem no canvas (Desloca a imagem); A utiliza��o deste m�todo 
	 * implica na chamada imediata ao m�todo {@link #Canvas.invalidate()}, causando o redesenho 
	 * imediato do canvas.
	 * 
	 * @see CadViewer.setCoordinates(Coordinates);
	 * @param x - coordenada X desejada
	 * @param y - coordenada Y desejada
	 */
	public void moveTo(float x, float y){
		this.coordinates.setX(x);
		this.coordinates.setY(y);
	}
}
