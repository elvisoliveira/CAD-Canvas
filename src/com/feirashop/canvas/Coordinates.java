package com.feirashop.canvas;

/**
 * Classe utilizada para armazenar coordenadas (X,Y) de elementos
 * @author William
 */
public class Coordinates{
	private float x;
	private float y;
	
	public Coordinates(){
		this.x = 0;
		this.y = 0;
	}
	public Coordinates(float x, float y){
		this.x = x;
		this.y = y;
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
