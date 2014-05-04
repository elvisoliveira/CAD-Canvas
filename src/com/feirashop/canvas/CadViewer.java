package com.feirashop.canvas;


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

	/**
	 * Estrat�gia para defini��o da regi�o de um {@link Element}: Utiliza os limites do elemento.
	 * A �rea � definida de maneira a conter toda a �rea do elemento.
	 */
	public static final int REGION_STRAT_USE_ELEMENT_BOUNDS = 1;
	
	/**
	 * Estrat�gia para defini��o da regi�o de um {@link Element}: Utiliza um valor fixo para a �rea dos elementos.
	 * O valor padr�o � 30.
	 * O valor pode ser alterado atrav�s do m�todo {@link #setElementRegionRadius(double)}
	 */
	public static final int REGION_STRAT_USE_FIX_VALUE = 2;
	
	private Bitmap bitmap;
	private Paint  entityAreaPaint;
	private float mScaleFactor;
	private int regionStrategy;
	private double elementRegionRadius;
	private double elementRegionScaleFactor;
	private boolean showElementRegionEnabled;
	
	private ScaleGestureDetector mScaleDetector;
	private Rect clipBounds;
	private Coordinates coordinates;
	private Coordinates previousCoordinates;
	
	private float maxScale;
	private float minScale;
	private ElementList elementList;
	private ElementClickListener onElementClickListener;	
		
	private boolean allowMoving;
	private boolean moving;
		
	
	public CadViewer(Context context) {
		super(context);
		this.init(context);
	}
	public CadViewer(Context context, AttributeSet ats){
		super(context, ats);
		this.init(context);
	}
	
	private void init(Context context){
		mScaleDetector = new ScaleGestureDetector(context,new ScaleGestureListener());
		//bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.teste);
		mScaleFactor = 1;
		coordinates = new Coordinates();
		previousCoordinates = new Coordinates();
		maxScale = 5.0f;								  //Por padr�o, o valor m�ximo de escala permitido � 5.0	 
		minScale = 0.1f;								  //Por padr�o, o valor m�nimo de escala permitido � 0.1	
		entityAreaPaint = new Paint();
		entityAreaPaint.setAlpha(50); 					  //Por padr�o, a regi�o clic�vel � exibida com alpha = 50 (transpar�ncia)
		entityAreaPaint.setColor(Color.BLACK); 			  //Por padr�o, utiliza a cor preta para as regi�es clic�veis (elementos)
		regionStrategy = REGION_STRAT_USE_ELEMENT_BOUNDS; //Por padr�o, utiliza a pr�pria dos elementos como limite da regi�o clic�vel 
		elementRegionScaleFactor = 1;					  //Por padr�o, o multiplicador da regi�o � 1
		elementRegionRadius = 30;						  //Por padr�o, o raio da regi�o clic�vel � 30 (Apenas quando utilizar estrat�gia REGION_STRAT_USE_FIX_VALUE 
	}
	
	@Override
	protected void onDraw(Canvas canvas) {	
		super.onDraw(canvas);	
		canvas.save();
		
		//Aplica a escala e o deslocamento sobre o canvas
		canvas.scale(mScaleFactor, mScaleFactor);
		
		//Desenha a imagem no canvas
		if(bitmap != null){
			canvas.drawBitmap(bitmap, coordinates.getX(), coordinates.getY(), null);
		}
		
		canvas.restore();		
		clipBounds = canvas.getClipBounds();
	//	canvas.drawCircle((749.96490f + coordinates.getX())*mScaleFactor, (531.3966993f+ coordinates.getY())*mScaleFactor , 10.0f*mScaleFactor, paint);		
		
		if(showElementRegionEnabled){
			if(this.elementList != null){
				for (Element element : this.elementList.getElementList()) {
					canvas.drawCircle(((float)element.getCenterX() + coordinates.getX())*mScaleFactor, ((float)element.getCenterY()+ coordinates.getY())*mScaleFactor , (float)getElementArea(element) *mScaleFactor, entityAreaPaint);
				}
			}
		}
		
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
	    	moving = false;
	    	allowMoving = true;
	    
	    //Ao movimentar, desloca a imagem pelo valor da diferen�a entre a coordenada atual e a anterior	
	    }else if(event.getAction()==MotionEvent.ACTION_MOVE){
	    	//O deslocamento da imagem fica bloqueado enquanto a imagem est� sendo escalada pelo movimento de "Pin�a"
	    	if((!mScaleDetector.isInProgress())&&(allowMoving)){
	    		coordinates.setX(coordinates.getX()-(previousCoordinates.getX()-touchX));
	    		coordinates.setY(coordinates.getY()-(previousCoordinates.getY()-touchY));
	    		
	    		//Ap�s deslocar a imagem, iguala as coordenadas anteriores �s atuais
	    		previousCoordinates.setX(touchX); 
	    		previousCoordinates.setY(touchY);
	    		moving = true;
	    	}
	    
	    //Ao "levantar" o dedo, desbloqueia o deslocamento da imagem.
	    }else if(event.getAction()==MotionEvent.ACTION_UP){
	    	
	    	if((!mScaleDetector.isInProgress())&&(allowMoving)&&(!moving)){
	    		if(this.elementList!=null){
		    		Element elemento = this.findMatch(touchX,touchY);
		    		if(elemento != null){
		    		//if((previousCoordinates.getX() == touchX)&&(previousCoordinates.getY() == touchY)){
			    		if(onElementClickListener!=null){
			    			onElementClickListener.onElementCLick(elemento);
			    		}
			    	//}
		    		}
	    		}
	    	}
	    	
	    	allowMoving = true;
	    	moving      = false;
	    	previousCoordinates.setX(touchX);
	    	previousCoordinates.setY(touchY);
	    }
	    
	    
	    
	    invalidate();
		return true;
	}
	
	
	private Element findMatch(double x, double y){
		for (Element item : this.elementList.getElementList()) {
			
			double newX = (x - coordinates.getX());
			double newY = (y - coordinates.getY());
			double centerX = item.getCenterX();
			double centerY = item.getCenterY();
			double raio    = getElementArea(item);
			
			double distancia = Math.sqrt(Math.pow((centerX - newX),2) + Math.pow((centerY - newY),2));
			if((distancia < raio)||(distancia==raio)){
				return item;
			}
		}
		return null;
	}
	
	
	private double getElementArea(Element element){
		if(this.regionStrategy == REGION_STRAT_USE_ELEMENT_BOUNDS){
			double width   = element.getWidth();
			double height  = element.getHeight();
			double raio  = width > height ? width : height;
			return (raio/2) * this.elementRegionScaleFactor;
		}else if(this.regionStrategy == REGION_STRAT_USE_FIX_VALUE){
			return this.elementRegionRadius * this.elementRegionScaleFactor;
		}
		return -1;
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
			allowMoving   = false;
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
	 * @return - Um objeto {@link Coordinates} contendo as coordenadas da imagem
	 */
	public Coordinates getCoordinates(){
		return this.coordinates;
	}
	
	/**
	 * Altera as coordenadas da imagem no canvas (Desloca a imagem); A utiliza��o deste m�todo 
	 * implica na chamada imediata ao m�todo {@link #Canvas.invalidate()}, causando o redesenho 
	 * imediato do canvas.
	 * 
	 * param coordinates - Um  objeto {@link Coordinates} contendo as coordenadas da imagem
	 */
	public void setCoordinates(Coordinates coordinates){
		this.coordinates = coordinates;
	}
	
	/**
	 * Altera as coordenadas da imagem no canvas (Desloca a imagem); A utiliza��o deste m�todo 
	 * implica na chamada imediata ao m�todo {@link #Canvas.invalidate()}, causando o redesenho 
	 * imediato do canvas.
	 * 
	 * @param x - A coordenada X da imagem
	 * @param y - A coordenada Y da imagem
	 */
	public void setCoordinates(float x, float y){
		this.coordinates = new Coordinates(x, y);
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
	
	/**
	 * Retorna a lista de elementos a exibidos no canvas. 
	 * @return {@link ElementList} contendo inst�ncias de {@link Element}
	 */
	public ElementList getStandList() {
		return elementList;
	}
	
	/**
	 * Altera a lista de elementos a serem reconhecidos na imagem.
	 * Cada elemento cont�m seu codigo e coordenadas, de forma que,
	 * ao clicar dentro do raio de um desses elementos no canvas, dispara
	 * um evento onElementClick.
	 * 
	 * @param elementList - {@link ElementList} contendo inst�ncias de {@link Element} a serem 
	 * reconhecidos no canvas 
	 */
	public void setElementList(ElementList elementList) {
		this.elementList = elementList;
	}
		
	/**
	 * Registra um listener para eventos de onClick em elementos representados no Canvas
	 * @param onElementClickListener - o {@link ElementClickListener} a ser registrado.
	 * @see ElementClickListener
	 */
	public void setOnElementClickListener(
			ElementClickListener onElementClickListener) {
		this.onElementClickListener = onElementClickListener;
	}
	
	/**
	 * Altera o bitmap a ser desenhado no canvas
	 * @param id - O resource id da imagem.
	 */
	public void setBitmapFromResourceID(int id){
		this.bitmap =  BitmapFactory.decodeResource(getResources(), id);
	}
	
	/**
	 * Altera o bitmap a ser desenhado no canvas
	 * @param id - O bitmap a ser desenhado.
	 */
	public void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
		
	/**
	 * @return o bitmap desenhado no canvas
	 */
	public Bitmap getBitmap(){
		return this.bitmap;
	}
	
	/** 
	 * @return {@link Paint} utilizado para desenhar as �reas em torno do elementos 
	 */
	public Paint getEntityAreaPaint() {
		return entityAreaPaint;
	}
	
	/**
	 * Altera o {@link Paint} utilizado para desenhar as �reas em torno do elementos.
	 * A chamada a este m�todo causa a invalida��o da view atual, for�ando uma nova chamada ao m�todo {@link #OnDrawListener.onDraw()}
	 * @param entityAreaPaint
	 */
	public void setEntityAreaPaint(Paint entityAreaPaint) {
		this.entityAreaPaint = entityAreaPaint;
		invalidate();
	}
	
	/**
	 * @return a estrat�gia a ser utilizada para determinar os a regi�o correspondente a um {@link Element}.
	 * @see {@link #REGION_STRAT_USE_ELEMENT_BOUNDS} e {@link #REGION_STRAT_USE_FIX_VALUE}
	 */
	public int getRegionStrategy() {
		return regionStrategy;
	}
	
	/**
	 * Define a estrat�gia a ser utilizada para determinar os a regi�o correspondente a um {@link Element}.
	 * @see {@link #REGION_STRAT_USE_ELEMENT_BOUNDS} e {@link #REGION_STRAT_USE_FIX_VALUE}
	 * @param regionStrategy - A estrat�gia a ser utilizada.
	 */
	public void setRegionStrategy(int regionStrategy) {
		this.regionStrategy = regionStrategy;
	}
	
	/**
	 * @return O raio de regi�o compreendida por um {@link Element} quando a estrat�gia de defini��o de regi�o estiver definida como
	 * {@link #REGION_STRAT_USE_FIX_VALUE}.
	 * @see {@link #setRegionStrategy(int)}
	 */
	public double getElementRegionRadius() {
		return elementRegionRadius;
	}
	
	/**
	 * Altera o raio da regi�o compreendida por um {@link Element}.
	 * Este valor surge efeito apenas se a estrat�gia de defini��o de regi�o estiver definida como
	 * {@link #REGION_STRAT_USE_FIX_VALUE}.
	 * @see {@link #setRegionStrategy(int)}
	 * @param elementRegionRadius - O valor a ser utilizado como raio para a regi�o do {@link Element}
	 */
	public void setElementRegionRadius(double elementRegionRadius) {
		this.elementRegionRadius = elementRegionRadius;
	}
	
	/**
	 * @return - O fator de escala (multiplicador) sobre a regi�o de todos os {@link Element}s
	 * @see {@link #setElementRegionScaleFactor(double)}
	 */
	public double getElementRegionScaleFactor() {
		return elementRegionScaleFactor;
	}
	
	/**
	 * Aplica um fator de escala (multiplicador) sobre a regi�o de todos os {@link Element}s, independente da estrat�gia
	 * de defini��o de �rea utilizada. O valor padr�o � 1.
	 * @param elementRegionScaleFactor - O valor a ser utilizado como multiplicador.  
	 */
	public void setElementRegionScaleFactor(double elementRegionScaleFactor) {
		this.elementRegionScaleFactor = elementRegionScaleFactor;
	}
	
	/**
	 * Indica se a exibi��o da regi�o clic�vel dos elementos est� ativada.
	 * @return <b>true</b> se estiver ativada e <b>false</b>, caso contr�rio.
	 */
	public boolean isShowElementRegionEnabled() {
		return showElementRegionEnabled;
	}
	
	/**
	 * Ativa/Desativa a exibi��o da regi�o clic�vel dos {@link Element}s no canvas;
	 * @param showElementRegionEnabled - indica se ativa (<b>true</b>) ou desativa (<b>false</b>) a exibi��o.
	 */
	public void setShowElementRegionEnabled(boolean showElementRegionEnabled) {
		this.showElementRegionEnabled = showElementRegionEnabled;
		invalidate();
	}
	
	
}
