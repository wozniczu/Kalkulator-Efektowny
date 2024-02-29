package kalkulator;

import java.awt.Button;

public class DigitButton extends Button
{
Kalkulator cl;

DigitButton(int x,int y, int width,int height,String cap, Kalkulator calc)
{
super(cap);
setBounds(x,y,width,height);
cl=calc;
cl.add(this);
}
}
